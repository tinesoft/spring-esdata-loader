package com.github.spring.esdata.loader.demo.junit5;

import com.github.spring.esdata.loader.core.LoadEsData;
import com.github.spring.esdata.loader.demo.DemoTestPropertyValues;
import com.github.spring.esdata.loader.demo.model.AuthorEsEntity;
import com.github.spring.esdata.loader.demo.model.BookEsEntity;
import com.github.spring.esdata.loader.demo.repository.AuthorEsRepository;
import com.github.spring.esdata.loader.demo.repository.BookEsRepository;
import com.github.spring.esdata.loader.junit5.LoadEsDataConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers

// The following annotation registers the @LoadEsDataExtention with JUnit Jupiter
// and defines which data to load into the underlying Elasticsearch Server.
// The data will be loaded only once before all tests (i.e in the beforeAll() phase)
@LoadEsDataConfig({ //
		@LoadEsData(esEntityClass = AuthorEsEntity.class, location = "/data/authors.json"),//
		@LoadEsData(esEntityClass = BookEsEntity.class, location = "/data/books.json.gz"), // built-in support for gzipped files
})

@SpringBootTest
@ContextConfiguration(initializers = LoadEsDataExtensionTest.ExposeDockerizedEsServer.class)
public class LoadEsDataExtensionTest {

	@Container
	private static final ElasticsearchContainer ES_CONTAINER = new ElasticsearchContainer(DemoTestPropertyValues.ES_DOCKER_IMAGE_VERSION);

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

	static class ExposeDockerizedEsServer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(ConfigurableApplicationContext cac) {
			DemoTestPropertyValues.using(ES_CONTAINER).applyTo(cac.getEnvironment());
		}
	}
}

