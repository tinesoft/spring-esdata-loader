package com.github.spring.esdata.loader.core;

/**
 * Enum defining the data formats supported by the tool to load data into Elasticsearch.
 */
public enum EsDataFormat {
  /***
   * Format of data as dumped from an existing Elasticsearch server (using tools like `elasticdump` for e.g).
   */
  DUMP,
  /**
   * Format of data as manually created by User. Must be an array of JSON objects, each representing the actual content to put into related ES index.
   */
  MANUAL,

  UNKNOWN
}
