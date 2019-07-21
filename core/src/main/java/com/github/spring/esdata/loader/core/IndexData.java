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
	 *            mapping class of the data to be indexed in ES
	 * @param location
	 *            path to the file that contains data (as JSON) to be indexed
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

	/**
	 * Builds a new {@link IndexData} using provided parameters.
	 *
	 * @param esEntityClass mapping class of the data to be indexed in ES
	 * @param location      path to the file that contains data (as JSON) to be indexed
	 * @return a new {@link IndexData}
	 */
	public static IndexData of(final Class<?> esEntityClass, final String location) {
		return of(esEntityClass, location, Long.MAX_VALUE, 0L);
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
		return of(esEntityClass, location, nbMaxItems, 0L);
	}

	/**
	 * Builds a new {@link IndexData} using provided parameters.
	 *
	 * @param esEntityClass mapping class of the data to be indexed in ES
	 * @param location      path to the file that contains data (as JSON) to be indexed
	 * @param nbMaxItems    maximum number of items to load
	 * @param nbSkipItems   number of items to skip
	 * @return a new {@link IndexData}
	 */
	public static IndexData of(final Class<?> esEntityClass, final String location, final Long nbMaxItems,
							   final Long nbSkipItems) {
		boolean gzipped = location.toLowerCase().endsWith(".gz");
		return new IndexData(esEntityClass, location, gzipped, nbMaxItems, nbSkipItems);
	}

	/**
	 * Builds a new {@link IndexData} using provided parameter.
	 *
	 * @param a {@link LoadEsData} to construct the data from
	 * @return a new {@link IndexData}
	 */
	public static IndexData of(final LoadEsData a) {
		return of(a.esEntityClass(), a.location(), a.nbMaxItems(), a.nbSkipItems());
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

}