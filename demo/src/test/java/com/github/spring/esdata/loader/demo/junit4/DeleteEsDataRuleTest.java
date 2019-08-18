package com.github.spring.esdata.loader.demo.junit4;

import com.github.spring.esdata.loader.core.DeleteEsData;
import com.github.spring.esdata.loader.demo.DemoTestPropertyValues;
import com.github.spring.esdata.loader.demo.model.AuthorEsEntity;
import com.github.spring.esdata.loader.demo.model.BookEsEntity;
import com.github.spring.esdata.loader.demo.model.LibraryEsEntity;
import com.github.spring.esdata.loader.junit4.DeleteEsDataRule;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Simple integration test to illustrate how to use the library when testing with JUnit 4.
 *
 * @author tinesoft
 */
@RunWith(SpringRunner.class) // required to run JUnit 4 tests with Spring magic support
@SpringBootTest

// the following data will be removed before all tests are executed
@DeleteEsData({AuthorEsEntity.class, BookEsEntity.class})

//(for this test setup only) not required in general
@ContextConfiguration(initializers = DeleteEsDataRuleTest.ExposedDockerizedEsConfiguration.class)

@FixMethodOrder(MethodSorters.NAME_ASCENDING)// because of the @SpyBean below, we need to control test order
public class DeleteEsDataRuleTest {

  //@ClassRule
  // helper to easily start a dockerized Elasticsearch server to run our tests against (not required to use this library)
  public static final ElasticsearchContainer ES_CONTAINER = new ElasticsearchContainer(DemoTestPropertyValues.ES_DOCKER_IMAGE_VERSION);

  //@ClassRule
  // this rule allows to remove data at class level (using @LoadEsData annotations on the test class)
  public static final DeleteEsDataRule ES_DATA_REMOVER_RULE = new DeleteEsDataRule();

  @Rule // this rule allows to remove data at test method level (using @LoadEsData annotations on the test method)
  public final DeleteEsDataRule esDataRemoverRule = ES_DATA_REMOVER_RULE;

  @ClassRule
  // (for this test setup only) we need to make sure that ES_CONTAINER rule is loaded BEFORE
  // our ES_DATA_LOADER_RULE, to make sure that the dockerized Elasticsearch server is started BEFORE
  // we try to load data into it via our ES_DATA_LOADER_RULE
  public final static TestRule RUlE = RuleChain.outerRule(ES_CONTAINER).around(ES_DATA_REMOVER_RULE);

  @SpyBean(reset = MockReset.NONE)
  private ElasticsearchTemplate esMockTemplate;

  @Test
  public void dataDeletedAtClassLevel() {

    verify(this.esMockTemplate).deleteIndex(AuthorEsEntity.class);
    verify(this.esMockTemplate).deleteIndex(BookEsEntity.class);
    verify(this.esMockTemplate, never()).deleteIndex(LibraryEsEntity.class);//removed at method level, see below #dataDeleteedAtMethodLevel()
  }

  @Test
  // the following data will be removed for this test only
  @DeleteEsData(esEntityClasses = {LibraryEsEntity.class})
  public void dataDeletedAtMethodLevel() {

    verify(this.esMockTemplate).deleteIndex(AuthorEsEntity.class);//removed at class level, before all tests
    verify(this.esMockTemplate).deleteIndex(BookEsEntity.class);//removed at class level, before all tests
    verify(this.esMockTemplate).deleteIndex(LibraryEsEntity.class);
  }

  public static class ExposedDockerizedEsConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext cac) {
      DemoTestPropertyValues.using(ES_CONTAINER).applyTo(cac.getEnvironment());
    }
  }
}
