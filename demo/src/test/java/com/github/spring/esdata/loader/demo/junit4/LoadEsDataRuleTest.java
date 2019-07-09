package com.github.spring.esdata.loader.demo.junit4;

import com.github.spring.esdata.loader.core.IndexData;
import com.github.spring.esdata.loader.demo.DemoTestPropertyValues;
import com.github.spring.esdata.loader.demo.model.AuthorEsEntity;
import com.github.spring.esdata.loader.demo.model.BookEsEntity;
import com.github.spring.esdata.loader.demo.repository.AuthorEsRepository;
import com.github.spring.esdata.loader.demo.repository.BookEsRepository;
import com.github.spring.esdata.loader.junit4.LoadEsDataRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
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


@ContextConfiguration(initializers = LoadEsDataRuleTest.ExposeDockerizedElasticsearchServer.class)//for this test setup only, not required in general
public class LoadEsDataRuleTest {

	@ClassRule // helper to easily start a dockerized Elasticsearch server to run our tests against (not required to use this library)
	public final static ElasticsearchContainer ES_CONTAINER = new ElasticsearchContainer(DemoTestPropertyValues.ES_DOCKER_IMAGE_VERSION);

	@Rule
	public final LoadEsDataRule esLoaderRule = new LoadEsDataRule(//
			IndexData.of(AuthorEsEntity.class, "/data/authors.json"), //
			IndexData.of(BookEsEntity.class, "/data/books.json.gz")// built-in support for gzipped files
	// ... add as many data as you want, simple rule: one type of data by file
	);

	@Autowired
	private AuthorEsRepository esAuthorRepository;

	@Autowired
	private BookEsRepository esBookRepository;

	@Test
	public void testLoader() {

		Iterable<AuthorEsEntity> authors = this.esAuthorRepository.findAll();
		Iterable<BookEsEntity> books = this.esBookRepository.findAll();

		assertThat(books).hasSize(10);
		assertThat(authors).hasSize(10);
	}

	static class ExposeDockerizedElasticsearchServer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		@Override
		public void initialize(ConfigurableApplicationContext cac) {
			DemoTestPropertyValues.using(ES_CONTAINER).applyTo(cac.getEnvironment());
		}
	}
}
