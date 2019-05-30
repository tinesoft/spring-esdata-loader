package com.github.spring.esdata.loader.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.github.spring.esdata.loader.demo.repository.NoOpEsRepository;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackageClasses = NoOpEsRepository.class)
public class DemoApplication {

	public static void main(final String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
