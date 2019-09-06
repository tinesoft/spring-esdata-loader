package com.github.spring.esdata.loader.demo.junit.jupiter;

import com.github.spring.esdata.loader.core.LoadEsData;
import com.github.spring.esdata.loader.demo.DemoTestPropertyValues;
import com.github.spring.esdata.loader.demo.model.AuthorEsEntity;
import com.github.spring.esdata.loader.demo.repository.AuthorEsRepository;
import com.github.spring.esdata.loader.junit.jupiter.LoadEsDataExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Simple integration test to illustrate how to use the library when testing with JUnit Jupiter.
 *
 * @author tinesoft
 */
@Testcontainers
// helper to easily start a dockerized Elasticsearch server to run our tests against (not required to use this library)
@SpringBootTest
@ExtendWith(LoadEsDataExtension.class)
//for this test setup only, not required in general
@ContextConfiguration(initializers = EsDataFormatTest.ExposedDockerizedEsConfiguration.class)
public class EsDataFormatTest {

  @Container
  // helper to easily start a dockerized Elasticsearch server to run our tests against (not required to use this library)
  public static final ElasticsearchContainer ES_CONTAINER = new ElasticsearchContainer(DemoTestPropertyValues.ES_DOCKER_IMAGE_VERSION);

  @Autowired
  private AuthorEsRepository esAuthorRepository;

  @Test
  @LoadEsData(esEntityClass = AuthorEsEntity.class, location = "/data/authors-MANUAL.json", nbMaxItems = 5, nbSkipItems = 2)
  public void dataLoadedWithManualFormat() {

    Iterable<AuthorEsEntity> authors = this.esAuthorRepository.findAll();

    assertThat(authors).hasSize(5);
    assertThat(authors).extracting(AuthorEsEntity::getFirstName).containsOnly("firstName3", "firstName4", "firstName5", "firstName6", "firstName7");
  }

  public static class ExposedDockerizedEsConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext cac) {
      DemoTestPropertyValues.using(ES_CONTAINER).applyTo(cac.getEnvironment());
    }
  }

}

