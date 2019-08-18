package com.github.spring.esdata.loader.core;

public interface EsDataLoader {
  void delete(Class<?> esEntityClass);

  void load(IndexData d);
}
