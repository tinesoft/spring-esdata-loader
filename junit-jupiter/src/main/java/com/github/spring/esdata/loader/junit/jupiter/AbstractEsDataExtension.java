package com.github.spring.esdata.loader.junit.jupiter;

import com.github.spring.esdata.loader.core.EsDataLoader;
import com.github.spring.esdata.loader.core.SpringUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.TestContextManager;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.stream.Stream;

/**
 * Base class for ES Data extensions
 */
public abstract class AbstractEsDataExtension {
  protected final static String TEST_INSTANCE = "testInstance";
  protected static final String LOADER = "loader";
  protected EsDataLoader loader;

  public AbstractEsDataExtension(final EsDataLoader loader) {
    this.loader = loader;
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
  protected ApplicationContext getApplicationContext(final ExtensionContext context) {
    return this.getTestContextManager(context).getTestContext().getApplicationContext();
  }

  /**
   * Get the {@link TestContextManager} associated with the supplied {@code ExtensionContext}.
   * @param context the {@code ExtensionContext}
   * @return the {@code TestContextManager} (never {@code null})
   */
  protected TestContextManager getTestContextManager(final ExtensionContext context) {
    Assert.notNull(context, "ExtensionContext must not be null");
    Class<?> testClass = context.getRequiredTestClass();
    ExtensionContext.Store store = this.getStore(context);
    return store.getOrComputeIfAbsent(testClass, TestContextManager::new, TestContextManager.class);
  }

  /**
   * Get the {@link EsDataLoader} associated with the supplied {@code ExtensionContext}.
   * @param context the {@code ExtensionContext}
   * @return the {@link EsDataLoader} (never {@code null})
   */
  protected EsDataLoader getDataLoader(final ExtensionContext context) {
    Assert.notNull(context, "ExtensionContext must not be null");
    ExtensionContext.Store store = this.getStore(context);
    ApplicationContext appContext = this.getApplicationContext(context);
    return store.getOrComputeIfAbsent(LOADER, (k) -> SpringUtils.getDataLoader(appContext), EsDataLoader.class);
  }

  protected ExtensionContext.Store getStore(final ExtensionContext context) {
    return context.getRoot().getStore(this.getNamespace());
  }

  protected static <A extends Annotation> Stream<A> findMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
    A annotation = AnnotatedElementUtils.findMergedAnnotation(element, annotationType);
    return annotation != null ? Stream.of(annotation) : Stream.empty();
  }

  protected abstract ExtensionContext.Namespace getNamespace();
}
