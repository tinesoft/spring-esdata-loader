package com.github.spring.esdata.loader.demo.junit.jupiter;

import com.github.spring.esdata.loader.core.DeleteEsData;
import com.github.spring.esdata.loader.demo.DemoTestPropertyValues;
import com.github.spring.esdata.loader.demo.model.AuthorEsEntity;
import com.github.spring.esdata.loader.demo.model.BookEsEntity;
import com.github.spring.esdata.loader.demo.model.LibraryEsEntity;
import com.github.spring.esdata.loader.junit.jupiter.DeleteEsDataConfig;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


/**
 * Simple integration test to illustrate how to use the library when testing with JUnit Jupiter.
 *
 * @author tinesoft
 */

// helper to easily start a dockerized Elasticsearch server to run our tests against (not required to use this library)
@Testcontainers

// The following annotation registers the @DeleteEsDataExtention with JUnit Jupiter
// and specifies which data to remove from the underlying Elasticsearch Server.
// The data will be removed only once, before all tests (i.e in the beforeAll() phase)
@DeleteEsDataConfig({AuthorEsEntity.class, BookEsEntity.class})
@SpringBootTest

//for this test setup only, not required in general
@ContextConfiguration(initializers = DeleteEsDataExtensionTest.ExposedDockerizedEsConfiguration.class)

@TestMethodOrder(MethodOrderer.Alphanumeric.class)// because of the @SpyBean below, we need to control test order
public class DeleteEsDataExtensionTest {

  @Container
  // helper to easily start a dockerized Elasticsearch server to run our tests against (not required to use this library)
  public static final ElasticsearchContainer ES_CONTAINER = new ElasticsearchContainer(DemoTestPropertyValues.ES_DOCKER_IMAGE_VERSION);

  @SpyBean(reset = MockReset.NONE)
  private ElasticsearchOperations esMockOperations;

  @Test
  public void dataDeletedAtClassLevel() {

    verify(this.esMockOperations).deleteIndex(AuthorEsEntity.class);
    verify(this.esMockOperations).deleteIndex(BookEsEntity.class);
    verify(this.esMockOperations, never()).deleteIndex(LibraryEsEntity.class);//removed at method level, see below #dataDeleteedAtMethodLevel()
  }

  @Test
  // the following data will be removed for this test only
  @DeleteEsData(esEntityClasses = {LibraryEsEntity.class})
  public void dataDeletedAtMethodLevel() {

    verify(this.esMockOperations).deleteIndex(AuthorEsEntity.class);//removed at class level, before all tests
    verify(this.esMockOperations).deleteIndex(BookEsEntity.class);//removed at class level, before all tests
    verify(this.esMockOperations).deleteIndex(LibraryEsEntity.class);
  }

  public static class ExposedDockerizedEsConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext cac) {
      DemoTestPropertyValues.using(ES_CONTAINER).applyTo(cac.getEnvironment());
    }
  }

}
