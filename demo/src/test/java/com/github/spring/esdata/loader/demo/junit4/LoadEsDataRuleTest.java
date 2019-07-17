package com.github.spring.esdata.loader.demo.junit4;

import com.github.spring.esdata.loader.core.LoadEsData;
import com.github.spring.esdata.loader.demo.DemoTestPropertyValues;
import com.github.spring.esdata.loader.demo.model.AuthorEsEntity;
import com.github.spring.esdata.loader.demo.model.BookEsEntity;
import com.github.spring.esdata.loader.demo.model.LibraryEsEntity;
import com.github.spring.esdata.loader.demo.repository.AuthorEsRepository;
import com.github.spring.esdata.loader.demo.repository.BookEsRepository;
import com.github.spring.esdata.loader.demo.repository.LibraryEsRepository;
import com.github.spring.esdata.loader.junit4.LoadEsDataRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple integration test to illustrate how to use the library when testing with JUnit 4.
 *
 * @author tinesoft
 *
 */
@RunWith(SpringRunner.class) // required to run JUnit 4 tests with Spring magic support
@SpringBootTest

// the following data will be loaded before all tests are executed
@LoadEsData(esEntityClass = AuthorEsEntity.class, location = "/data/authors.json")//
@LoadEsData(esEntityClass = BookEsEntity.class, location = "/data/books.json.gz") // built-in support for gzipped files


@ContextConfiguration(initializers = LoadEsDataRuleTest.ExposeDockerizedElasticsearchServer.class)//for this test setup only, not required in general
public class LoadEsDataRuleTest {

	//@ClassRule // helper to easily start a dockerized Elasticsearch server to run our tests against (not required to use this library)
	public final static ElasticsearchContainer ES_CONTAINER = new ElasticsearchContainer(DemoTestPropertyValues.ES_DOCKER_IMAGE_VERSION);

	//@Rule
	//@ClassRule
	public static final LoadEsDataRule ES_DATA_LOADER_RULE = new LoadEsDataRule();

	@Rule// this rule allows to load data at method level
	public final LoadEsDataRule esDataLoaderRule = ES_DATA_LOADER_RULE;

	@ClassRule
	public final static TestRule RUlE = RuleChain.outerRule(ES_CONTAINER).around(ES_DATA_LOADER_RULE);

	@Autowired
	private AuthorEsRepository esAuthorRepository;

	@Autowired
	private BookEsRepository esBookRepository;

	@Autowired
	private LibraryEsRepository esLibraryRepository;

	@Test
	public void dataLoadedAtClassLevel() {

		Iterable<AuthorEsEntity> authors = this.esAuthorRepository.findAll();
		Iterable<BookEsEntity> books = this.esBookRepository.findAll();
		Iterable<LibraryEsEntity> libraries = this.esLibraryRepository.findAll();

		assertThat(books).hasSize(10);
		assertThat(authors).hasSize(10);
		assertThat(libraries).isEmpty();//loaded at method level, see below #dataLoadedAtMethodLevel()
	}

	@Test
	// the following data will be loaded for this test only. You can even specify how many data to load and how many to skip
	@LoadEsData(esEntityClass = LibraryEsEntity.class, location = "/data/libraries.json.gz", nbMaxItems = 5, nbSkipItems = 2)
	public void dataLoadedAtMethodLevel() {

		Iterable<AuthorEsEntity> authors = this.esAuthorRepository.findAll();
		Iterable<BookEsEntity> books = this.esBookRepository.findAll();
		Iterable<LibraryEsEntity> libraries = this.esLibraryRepository.findAll();

		assertThat(books).hasSize(10);//loaded at class level, before all tests
		assertThat(authors).hasSize(10);//loaded at class level, before all tests
		assertThat(libraries).hasSize(5);
	}

	static class ExposeDockerizedElasticsearchServer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		@Override
		public void initialize(ConfigurableApplicationContext cac) {
			DemoTestPropertyValues.using(ES_CONTAINER).applyTo(cac.getEnvironment());
		}
	}
}
