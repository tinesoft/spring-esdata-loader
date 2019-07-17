package com.github.spring.esdata.loader.demo.repository;

import com.github.spring.esdata.loader.demo.model.LibraryEsEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface LibraryEsRepository extends ElasticsearchCrudRepository<LibraryEsEntity, String> {
}
