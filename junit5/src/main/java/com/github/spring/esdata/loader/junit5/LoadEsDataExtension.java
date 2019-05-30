package com.github.spring.esdata.loader.junit5;

import com.github.spring.esdata.loader.core.IndexData;
import com.github.spring.esdata.loader.core.LoadEsData;
import com.github.spring.esdata.loader.core.SpringUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static org.junit.platform.commons.support.AnnotationSupport.findRepeatableAnnotations;

/**
 * A JUnit {@link Extension} to load ES data before all tests, or before each
 * test
 *
 * @author tinesoft
 */
public class LoadEsDataExtension
        implements TestInstancePostProcessor, BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadEsDataExtension.class);

    private final static Namespace NAMESPACE = Namespace.create(LoadEsDataExtension.class);
    private final static String TEST_INSTANCE = "testInstance";
    private static final String LOADER = "loader";

    private Consumer<IndexData<?>> loader;

    // This constructor is invoked by JUnit Jupiter via reflection or ServiceLoader
    public LoadEsDataExtension() {
        this(null);
    }

    /**
     * @param loader the code that will actually load the data into Elasticsearch
     */
    public LoadEsDataExtension(final Consumer<IndexData<?>> loader) {
        this.loader = loader;
    }

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {

        this.loader = getDataLoader(context);

        // es data can either be specified by:
        // - using one or many @LoadEsData on the test class (in conjunction with @ExtendWith(LoadEsDataExtension.class)
        // - using the convenient @LoadEsDataConfig that combines the two annotations above

        List<IndexData<?>> classData = Stream.concat(
                findRepeatableAnnotations(context.getRequiredTestClass(), LoadEsData.class).stream(),
                findAnnotation(context.getRequiredTestClass(), LoadEsDataConfig.class)
                        .map(c -> Stream.of(c.data(), c.value()).flatMap(Arrays::stream)).orElse(Stream.empty()))
                .map(IndexData::of)//
                .collect(Collectors.toList());

        for (IndexData<?> d : classData)
            this.loader.accept(d);

    }

    @Override
    public void afterAll(final ExtensionContext context) throws Exception {
        getStore(context).remove(LOADER);
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        List<IndexData<?>> methodData = findRepeatableAnnotations(context.getRequiredTestMethod(), LoadEsData.class)//
                .stream()//
                .map(IndexData::of)//
                .collect(Collectors.toList());

        for (IndexData<?> d : methodData)
            getDataLoader(context).accept(d);
    }

    /**
     * <p>
     * Callback for post-processing the supplied test instance.
     *
     * <p>
     * <strong>Note</strong>: the {@code ExtensionContext} supplied to a
     * {@code TestInstancePostProcessor} will always return an empty
     * {@link Optional} value from {@link ExtensionContext#getTestInstance()
     * getTestInstance()}. A {@code TestInstancePostProcessor} should therefore only
     * attempt to process the supplied {@code testInstance}.
     *
     * @param testInstance the instance to post-process; never {@code null}
     * @param context      the current extension context; never {@code null}
     */
    @Override
    public void postProcessTestInstance(final Object testInstance, final ExtensionContext context) {
        getStore(context).put(TEST_INSTANCE, testInstance);
    }

    /**
     * Get the {@link ApplicationContext} associated with the supplied
     * {@code ExtensionContext}.
     *
     * @param context the current {@code ExtensionContext} (never {@code null})
     * @return the application context
     * @throws IllegalStateException if an error occurs while retrieving the application context
     * @see org.springframework.test.context.TestContext#getApplicationContext()
     */
    public static ApplicationContext getApplicationContext(final ExtensionContext context) {
        return getTestContextManager(context).getTestContext().getApplicationContext();
    }

    /**
     * Get the {@link TestContextManager} associated with the supplied
     * {@code ExtensionContext}.
     *
     * @return the {@code TestContextManager} (never {@code null})
     */
    private static TestContextManager getTestContextManager(final ExtensionContext context) {
        Assert.notNull(context, "ExtensionContext must not be null");
        Class<?> testClass = context.getRequiredTestClass();
        Store store = getStore(context);
        return store.getOrComputeIfAbsent(testClass, TestContextManager::new, TestContextManager.class);
    }

    /**
     * Get the {@link com.github.spring.esdata.loader.core.SpringEsDataLoader} associated with the supplied
     * {@code ExtensionContext}.
     *
     * @return the {@code Consumer<IndexData<?>>} (never {@code null})
     */
    private static Consumer<IndexData<?>> getDataLoader(final ExtensionContext context) {
        Assert.notNull(context, "ExtensionContext must not be null");
        Store store = getStore(context);
        ApplicationContext appContext = getApplicationContext(context);
        return store.getOrComputeIfAbsent(LOADER, (k) -> SpringUtils.getDataLoader(appContext), Consumer.class);
    }

    private static Store getStore(final ExtensionContext context) {
        return context.getRoot().getStore(NAMESPACE);
    }

}