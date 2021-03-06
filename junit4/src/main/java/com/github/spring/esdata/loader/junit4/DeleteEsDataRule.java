package com.github.spring.esdata.loader.junit4;

import com.github.spring.esdata.loader.core.DeleteEsData;
import com.github.spring.esdata.loader.core.EsDataLoader;
import com.github.spring.esdata.loader.core.SpringUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.TestContextManager;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * JUnit 4 {@link TestRule} to remove data from Elasticsearch either before all tests, or before each test.
 *
 * @author tinesoft
 */
public class DeleteEsDataRule implements JunitJupiterExtensionLikeTestRule {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteEsDataRule.class);

  private EsDataLoader loader;

  /**
   * Cache of {@code TestContextManagers} keyed by test class.
   */
  private static final Map<Class<?>, TestContextManager> testContextManagerCache = new ConcurrentHashMap<>(64);


  /**
   * Constructor
   */
  public DeleteEsDataRule() {
    this(null);
  }

  /**
   * Constructor using the given loader.
   *
   * @param loader the loader
   */
  public DeleteEsDataRule(final EsDataLoader loader) {
    this.loader = loader;
  }

  @Override
  public void beforeAll(Statement base, Description description) throws Exception {
    this.loader = SpringUtils.getDataLoader(getApplicationContext(description.getTestClass()));

    findMergedAnnotation(description.getTestClass(), DeleteEsData.class)
      .flatMap(d -> Stream.of(d.esEntityClasses()))//
      .forEach(c -> this.loader.delete(c));
  }

  @Override
  public void before(Statement base, Description description) throws Exception {
    this.loader = SpringUtils.getDataLoader(getApplicationContext(description.getTestClass()));

    Method testMethod = description.getTestClass().getDeclaredMethod(description.getMethodName());

    findMergedAnnotation(testMethod, DeleteEsData.class)
      .flatMap(d -> Stream.of(d.esEntityClasses()))//
      .forEach(c -> this.loader.delete(c));
  }

  @Override
  public void afterAll(Statement base, Description description) throws Exception {
    testContextManagerCache.remove(description.getTestClass());
  }

  private static ApplicationContext getApplicationContext(Class<?> testClass) {
    TestContextManager testContextManager = getTestContextManager(testClass);
    return testContextManager.getTestContext().getApplicationContext();
  }

  /**
   * Get the {@link TestContextManager} associated with the supplied test class.
   *
   * @param testClass the test class to be managed; never {@code null}
   */
  private static TestContextManager getTestContextManager(Class<?> testClass) {
    Assert.notNull(testClass, "Test Class must not be null");
    return testContextManagerCache.computeIfAbsent(testClass, TestContextManager::new);
  }

  protected static <A extends Annotation> Stream<A> findMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
    A annotation = AnnotatedElementUtils.findMergedAnnotation(element, annotationType);
    return annotation != null ? Stream.of(annotation) : Stream.empty();
  }
}
