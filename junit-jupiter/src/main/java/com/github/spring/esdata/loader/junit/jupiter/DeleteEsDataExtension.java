package com.github.spring.esdata.loader.junit.jupiter;

import com.github.spring.esdata.loader.core.DeleteEsData;
import com.github.spring.esdata.loader.core.EsDataLoader;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * JUnit {@link Extension} to delete data from Elasticsearch either before all tests, or before each test.
 *
 * @author tinesoft
 */
public class DeleteEsDataExtension extends AbstractEsDataExtension
  implements TestInstancePostProcessor, BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteEsDataExtension.class);

  private final static Namespace NAMESPACE = Namespace.create(DeleteEsDataExtension.class);

  // This constructor is invoked by JUnit Jupiter via reflection or ServiceLoader
  public DeleteEsDataExtension() {
    this(null);
  }

  /**
   * Constructor
   *
   * @param loader the loader
   */
  public DeleteEsDataExtension(final EsDataLoader loader) {
    super(loader);
  }

  @Override
  public void beforeAll(final ExtensionContext context) throws Exception {

    this.loader = this.getDataLoader(context);

    // ES data can be specified either by:
    // - using one or many @DeleteEsData on the test class (in conjunction with @ExtendWith(DeleteEsDataExtension.class)
    // - using the convenient @DeleteEsDataConfig that combines the two annotations above
    // - or using both
    Stream.concat(
      findMergedAnnotation(context.getRequiredTestClass(), DeleteEsData.class)//
        .flatMap(d -> Stream.of(d.esEntityClasses())),
      findMergedAnnotation(context.getRequiredTestClass(), DeleteEsDataConfig.class)//
        .flatMap(d -> Stream.of(d.esEntityClasses()))
    ).forEach(c -> this.getDataLoader(context).delete(c));
  }

  @Override
  public void afterAll(final ExtensionContext context) throws Exception {
    this.getStore(context).remove(LOADER);
  }

  @Override
  public void beforeEach(final ExtensionContext context) throws Exception {
    findMergedAnnotation(context.getRequiredTestMethod(), DeleteEsData.class)//
      .flatMap(d -> Stream.of(d.esEntityClasses()))//
      .forEach(c -> this.getDataLoader(context).delete(c));
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
