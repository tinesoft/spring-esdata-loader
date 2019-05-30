package com.github.spring.esdata.loader.demo.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

import com.github.spring.esdata.loader.demo.model.AuthorEsEntity;

public interface AuthorEsRepository extends ElasticsearchCrudRepository<AuthorEsEntity, String> {
}
