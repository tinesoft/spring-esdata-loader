package com.github.spring.esdata.loader.demo.junit.jupiter;

import com.github.spring.esdata.loader.core.LoadEsData;
import com.github.spring.esdata.loader.demo.DemoTestPropertyValues;
import com.github.spring.esdata.loader.demo.model.AuthorEsEntity;
import com.github.spring.esdata.loader.demo.model.BookEsEntity;
import com.github.spring.esdata.loader.demo.model.LibraryEsEntity;
import com.github.spring.esdata.loader.demo.repository.AuthorEsRepository;
import com.github.spring.esdata.loader.demo.repository.BookEsRepository;
import com.github.spring.esdata.loader.demo.repository.LibraryEsRepository;
import com.github.spring.esdata.loader.junit.jupiter.LoadEsDataConfig;
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


/**
 * Simple integration test to illustrate how to use the library when testing with JUnit Jupiter.
 *
 * @author tinesoft
 *
 */
@Testcontainers // helper to easily start a dockerized Elasticsearch server to run our tests against (not required to use this library)

// The following annotation registers the @LoadEsDataExtention with JUnit Jupiter
// and specifies which data to load into the underlying Elasticsearch Server.
// The data will be loaded only once, before all tests (i.e in the beforeAll() phase)
@LoadEsDataConfig({ //
		@LoadEsData(esEntityClass = AuthorEsEntity.class, location = "/data/authors.json"),//
		@LoadEsData(esEntityClass = BookEsEntity.class, location = "/data/books.json.gz"), // built-in support for gzipped files
})
@SpringBootTest
//for this test setup only, not required in general
@ContextConfiguration(initializers = LoadEsDataExtensionTest.ExposedDockerizedEsConfiguration.class)
public class LoadEsDataExtensionTest {

  @Container
  // helper to easily start a dockerized Elasticsearch server to run our tests against (not required to use this library)
  public static final ElasticsearchContainer ES_CONTAINER = new ElasticsearchContainer(DemoTestPropertyValues.ES_DOCKER_IMAGE_VERSION);

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

  public static class ExposedDockerizedEsConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext cac) {
      DemoTestPropertyValues.using(ES_CONTAINER).applyTo(cac.getEnvironment());
    }
  }

}

