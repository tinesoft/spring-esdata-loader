package com.github.spring.esdata.loader.core;

/**
 * Represents data to be inserted into Elasticsearch. <br>
 * <br>
 * Data is basically represented by:
 * <ul>
 *      <li><code>esEntityClass</code>: mapping class, used to create the corresponding index and mapping</li>
 *      <li><code>location</code>: path to the JSON file that contains the actual data to import (can be gzipped)</li>
 *      <li><code>nbMaxItems</code> (<i>optional</i>): how many max items to load (<code>all</code> <i>by default</i> )</li>
 *      <li><code>nbSkipItems</code> (<i>optional</i>): how many items to skip (<code>0</code> <i>by default</i> )</li>
 *      <li><code>format</code> (<i>optional</i>): format of the data to import (<code>null</code> <i>by default</i>, will be detected from JSON file content )</li>
 * </ul>
 *
 * @author tinesoft
 */
public class IndexData {

  final Class<?> esEntityClass;
  final String location;
  final boolean gzipped;
  final Long nbMaxItems;
  final Long nbSkipItems;
  final EsDataFormat format;

  /**
   * @param esEntityClass mapping class of the data to be indexed in ES
   * @param location      path to the file that contains data (as JSON) to be indexed
   * @param gzipped       whether or not the data is gzipped (true by default)
   * @param nbMaxItems    maximum number of items to load
   * @param nbSkipItems   number of items to skip
   * @param format        format of the data to load
   */
  public IndexData(final Class<?> esEntityClass, final String location, final boolean gzipped, final Long nbMaxItems,
                   final Long nbSkipItems, final EsDataFormat format) {
    this.esEntityClass = esEntityClass;
    this.location = location;
    this.gzipped = gzipped;
    this.nbMaxItems = nbMaxItems;
    this.nbSkipItems = nbSkipItems;
    this.format = format;
  }

  /**
   * Builds a new {@link IndexData} using provided parameters.
   *
   * @param esEntityClass mapping class of the data to be indexed in ES
   * @param location      path to the file that contains data (as JSON) to be indexed
   * @return a new {@link IndexData}
   */
  public static IndexData of(final Class<?> esEntityClass, final String location) {
    return of(esEntityClass, location, Long.MAX_VALUE, 0L, null);
  }

  /**
   * Builds a new {@link IndexData} using provided parameters.
   *
   * @param esEntityClass mapping class of the data to be indexed in ES
   * @param location      path to the file that contains data (as JSON) to be indexed
   * @param nbMaxItems    maximum number of items to load
   * @return a new {@link IndexData}
   */
  public static IndexData of(final Class<?> esEntityClass, final String location, final Long nbMaxItems) {
    return of(esEntityClass, location, nbMaxItems, 0L, null);
  }

  /**
   * Builds a new {@link IndexData} using provided parameters.
   *
   * @param esEntityClass mapping class of the data to be indexed in ES
   * @param location      path to the file that contains data (as JSON) to be indexed
   * @param nbMaxItems    maximum number of items to load
   * @param nbSkipItems   number of items to skip
   * @param format        format of the data to load
   * @return a new {@link IndexData}
   */
  public static IndexData of(final Class<?> esEntityClass, final String location, final Long nbMaxItems,
                             final Long nbSkipItems, final EsDataFormat format) {
    boolean gzipped = location.toLowerCase().endsWith(".gz");
    return new IndexData(esEntityClass, location, gzipped, nbMaxItems, nbSkipItems, format);
  }

  /**
   * Builds a new {@link IndexData} using provided parameter.
   *
   * @param a {@link LoadEsData} to construct the data from
   * @return a new {@link IndexData}
   */
  public static IndexData of(final LoadEsData a) {
    return of(a.esEntityClass(), a.location(), a.nbMaxItems(), a.nbSkipItems(), a.format());
  }

  public Class<?> getEsEntityClass() {
    return this.esEntityClass;
  }

  public String getLocation() {
    return this.location;
  }

  public boolean isGzipped() {
    return this.gzipped;
  }

  public Long getNbMaxItems() {
    return this.nbMaxItems;
  }

  public Long getNbSkipItems() {
    return this.nbSkipItems;
  }

  public EsDataFormat getFormat() {
    return this.format;
  }
}
