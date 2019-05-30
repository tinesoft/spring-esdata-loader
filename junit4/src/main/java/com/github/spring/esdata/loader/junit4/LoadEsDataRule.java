package com.github.spring.esdata.loader.junit4;

import com.github.spring.esdata.loader.core.IndexData;
import com.github.spring.esdata.loader.core.LoadEsData;
import com.github.spring.esdata.loader.core.LoadMultipleEsData;
import com.github.spring.esdata.loader.core.SpringUtils;
import org.junit.ClassRule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.TestContextManager;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A JUnit {@link MethodRule} to load ES data only once (during first test run),
 * unless specified otherwise (using <code>@LoadEsData</code> on each individual
 * test).
 * 
  * <br>
 * This is a workaround to have a <b>non-static</b> {@link ClassRule}
 * equivalent, because we need to access the tested class instance in order to
 * perform the data loading (using the {@link ElasticsearchTemplate} from with
 * its context)
 * 
 * @author tinesoft
 *
 */
public class LoadEsDataRule implements MethodRule {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoadEsDataRule.class);

	private Consumer<IndexData<?>> loader;
	private IndexData<?>[] initialData;
	private static AtomicBoolean firstLoad = new AtomicBoolean(true);// need to be static for class level

	/**
	 * Cache of {@code TestContextManagers} keyed by test class.
	 */
	private static final Map<Class<?>, TestContextManager> testContextManagerCache = new ConcurrentHashMap<>(64);


	/**
	 * @param data
	 *            the data to be loaded before each test method (unless overwriten
	 *            with a {@literal @}LoadEsData)
	 */
	public LoadEsDataRule(final IndexData<?>... data) {
		this(null, data);
	}

	/**
	 * @param loader
	 *            the code that will actually load the data into Elasticsearch
	 * @param data
	 *            the data to be loaded before each test method (unless overwritten
	 *            with a {@literal @}LoadEsData)
	 */
	public LoadEsDataRule(final Consumer<IndexData<?>> loader, final IndexData<?>... data) {
		this.loader = loader;
		this.initialData = data;
	}

	@Override
	public Statement apply(final Statement base, final FrameworkMethod method, final Object testInstance) {

		List<LoadEsData> loadEsDataAnnotations = Arrays.stream(method.getAnnotations())//
				.filter(a -> a instanceof LoadMultipleEsData)// repeatable annotations are contained in their parent
				.map(LoadMultipleEsData.class::cast)//
				.flatMap(m -> Arrays.stream(m.value()))// retrieve each single LoadEsData annotation
				.collect(Collectors.toList());

		Statement statement = base;
		statement = withLoadEsData(statement, loadEsDataAnnotations,testInstance);
		statement = withTestContextManagerCacheEvictor(statement, testInstance);

		return statement;
	}

	private static ApplicationContext getApplicationContext(Object testInstance){
		Class<?> testClass = testInstance.getClass();
		TestContextManager testContextManager = getTestContextManager(testClass);
		return testContextManager.getTestContext().getApplicationContext();
	}

	/**
	 * Get the {@link TestContextManager} associated with the supplied test class.
	 * @param testClass the test class to be managed; never {@code null}
	 */
	private static TestContextManager getTestContextManager(Class<?> testClass) {
		Assert.notNull(testClass, "Test Class must not be null");
		return testContextManagerCache.computeIfAbsent(testClass, TestContextManager::new);
	}

	/**
	 * Wrap the supplied {@link Statement} with a {@code LoadEsDataStatement} statement.
	 * @see LoadEsDataStatement
	 */
	private Statement withLoadEsData(Statement next, List<LoadEsData> loadEsDataAnnotations, Object testInstance) {
		return new LoadEsDataStatement(next, loadEsDataAnnotations,testInstance);
	}

	/**
	 * Wrap the supplied {@link Statement} with a {@code TestContextManagerCacheEvictorStatement} statement.
	 * @see TestContextManagerCacheEvictorStatement
	 */
	private Statement withTestContextManagerCacheEvictor(Statement next, Object testInstance) {
		Class<?> testClass = testInstance.getClass();
		return new TestContextManagerCacheEvictorStatement(next, testClass);
	}


	private static class TestContextManagerCacheEvictorStatement extends Statement {

		private final Statement next;

		private final Class<?> testClass;

		TestContextManagerCacheEvictorStatement(Statement next, Class<?> testClass) {
			this.next = next;
			this.testClass = testClass;
		}

		@Override
		public void evaluate() throws Throwable {
			try {
				this.next.evaluate();
			}
			finally {
				testContextManagerCache.remove(this.testClass);
			}
		}
	}

	private class LoadEsDataStatement extends Statement {
		private final Statement base;
		private final List<LoadEsData> loadEsDataAnnotations;
		private final Object testInstance;

		public LoadEsDataStatement(Statement base, List<LoadEsData> loadEsDataAnnotations, Object testInstance) {
			this.base = base;
			this.loadEsDataAnnotations = loadEsDataAnnotations;
			this.testInstance = testInstance;
		}

		@Override
		public void evaluate() throws Throwable {

			boolean shouldReloadEsData = !loadEsDataAnnotations.isEmpty();
			boolean hasInitialData = initialData.length > 0;

			loader = SpringUtils.getDataLoader(getApplicationContext(testInstance));

			// before test: (re)load test data
			if (firstLoad.get() && hasInitialData || shouldReloadEsData) {

				if (shouldReloadEsData) { // load the specified es data
					for (LoadEsData a : loadEsDataAnnotations) {
						IndexData<?> d = IndexData.of((Class<?>) a.esEntityClass(), a.location(), a.nbMaxItems(),
								a.nbSkipItems());
						loader.accept(d);
					}
				} else { // reload the same initial data

					for (IndexData<?> d : initialData)
						loader.accept(d);
				}

				firstLoad.set(shouldReloadEsData);
			} else
				LOGGER.debug(
						"Skipping data load because isFirstLoad={} or hasInitialData={} and shouldReloadEsData={} ",
						firstLoad.get(), hasInitialData, shouldReloadEsData);

			base.evaluate();// run the actual test
		}
	}
}