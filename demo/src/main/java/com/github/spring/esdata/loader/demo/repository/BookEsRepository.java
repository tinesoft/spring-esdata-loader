package com.github.spring.esdata.loader.demo.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

import com.github.spring.esdata.loader.demo.model.BookEsEntity;

public interface BookEsRepository extends ElasticsearchCrudRepository<BookEsEntity, String> {
}
