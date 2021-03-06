package com.github.spring.esdata.loader.junit.jupiter;

import com.github.spring.esdata.loader.core.EsDataLoader;
import com.github.spring.esdata.loader.core.IndexData;
import com.github.spring.esdata.loader.core.LoadEsData;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.platform.commons.support.AnnotationSupport.findRepeatableAnnotations;

/**
 * JUnit {@link Extension} to load data into Elasticsearch either before all tests, or before each test.
 *
 * @author tinesoft
 */
public class LoadEsDataExtension extends AbstractEsDataExtension
  implements TestInstancePostProcessor, BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoadEsDataExtension.class);

  private final static Namespace NAMESPACE = Namespace.create(LoadEsDataExtension.class);

  // This constructor is invoked by JUnit Jupiter via reflection or ServiceLoader
  public LoadEsDataExtension() {
    this(null);
  }

  /**
   * Constructor
   *
   * @param loader the loader
   */
  public LoadEsDataExtension(final EsDataLoader loader) {
    super(loader);
  }

  @Override
  public void beforeAll(final ExtensionContext context) throws Exception {

    this.loader = this.getDataLoader(context);

    // ES data can be specified either by:
    // - using one or many @LoadEsData on the test class (in conjunction with @ExtendWith(LoadEsDataExtension.class)
    // - using the convenient @LoadEsDataConfig that combines the two annotations above
    // - or using both
    Stream.concat(
      findRepeatableAnnotations(context.getRequiredTestClass(), LoadEsData.class).stream(),
      findMergedAnnotation(context.getRequiredTestClass(), LoadEsDataConfig.class)
        .flatMap(c -> Stream.of(c.data())))
      .map(IndexData::of)//
      .forEach(d -> this.loader.load(d));

  }

  @Override
  public void afterAll(final ExtensionContext context) throws Exception {
    this.getStore(context).remove(LOADER);
  }

  @Override
  public void beforeEach(final ExtensionContext context) throws Exception {
    findRepeatableAnnotations(context.getRequiredTestMethod(), LoadEsData.class)//
      .stream()//
      .map(IndexData::of)//
      .forEach(d -> this.getDataLoader(context).load(d));
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
    this.getStore(context).put(TEST_INSTANCE, testInstance);
  }

  @Override
  protected Namespace getNamespace() {
    return NAMESPACE;
  }

}
