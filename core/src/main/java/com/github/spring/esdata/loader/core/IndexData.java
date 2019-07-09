package com.github.spring.esdata.loader.core;

/**
 * Represents data to be inserted into Elasticsearch. <br>
 * <br>
 * Data is basically represented by:
 * <ul>
 *      <li><code>esEntityClass</code>: mapping class, used to create the corresponding index and mapping</li>
 *      <li><code>location</code>: path to the JSON file that contains the actual data to import</li>
 *      <li><code>nbMaxItems</code> (<i>optional</i>): how many max items to load (<code>all</code> <i>by default</i> )</li>
 *      <li><code>nbSkipItems</code> (<i>optional</i>): how many items to skip (<code>0</code> <i>by default</i> )</li>
 * </ul>
 *
 * @author tinesoft
 *
 * @param <T>
 */
public class IndexData {

	final Class<?> esEntityClass;
	final String location;
	final boolean gzipped;
	final Long nbMaxItems;
	final Long nbSkipItems;

	/**
	 *
	 * @param esEntityClass
	 *            the mapping class of the data to be indexed in ES
	 * @param location
	 *            path to the file that contains a dump (generated with
	 *            'elasticdump' for i.e)
	 * @param gzipped
	 *            whether or not the data is gzipped (true by default)
	 * @param nbMaxItems
	 *            maximum number of items to load
	 * @param nbSkipItems
	 *            number of items to skip
	 */
	public IndexData(final Class<?> esEntityClass, final String location, final boolean gzipped, final Long nbMaxItems,
			final Long nbSkipItems) {
		this.esEntityClass = esEntityClass;
		this.location = location;
		this.gzipped = gzipped;
		this.nbMaxItems = nbMaxItems;
		this.nbSkipItems = nbSkipItems;
	}

	public static IndexData of(final Class<?> esEntityClass, final String location) {
		return of(esEntityClass, location, Long.MAX_VALUE, 0L);
	}

	public static IndexData of(final Class<?> clazz, final String location, final Long nbMaxItems) {
		return of(clazz, location, nbMaxItems, 0L);
	}

	public static IndexData of(final Class<?> clazz, final String location, final Long nbMaxItems,
			final Long nbSkipItems) {
		boolean gzipped = location.toLowerCase().endsWith(".gz");
		return new IndexData(clazz, location, gzipped, nbMaxItems, nbSkipItems);
	}

	public static IndexData of(final LoadEsData a) {
		return of(a.esEntityClass(), a.location(), a.nbMaxItems(), a.nbSkipItems());
	}

	public Class<?> getEsEntityClass() {
		return esEntityClass;
	}

	public String getLocation() {
		return location;
	}

	public boolean isGzipped() {
		return gzipped;
	}

}