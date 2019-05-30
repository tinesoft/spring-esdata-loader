package com.github.spring.esdata.loader.core;

/**
 * Represents data to be inserted into Elastic Search. <br>
 * <br>
 * Data is basically represented by:
 * <ul>
 * <li><code>esEntityClass</code>, to create corresponding index and
 * mappings</li>
 * <li><code>location</code>: path to the file that contains a dump of data to
 * import(generated with 'elasticdump')</li>
 * <li><code>nbMaxItems</code> (optional): how many max items to load</li>
 * <li><code>nbSkipItems</code> (optional): how many items to skip</li>
 * </ul>
 * 
 * @author tinesoft
 *
 * @param <T>
 */
public class IndexData<T> {

	final Class<T> esEntityClass;
	final String location;
	final boolean gzipped;
	final Long nbMaxItems;
	final Long nbSkipItems;

	/**
	 * 
	 * @param esEntityClass
	 *            the class of the data to be index in ES
	 * @param location
	 *            path to the file that contains a dump (generated with
	 *            'elasticdump' for i.e)
	 * @param gzipped
	 *            whether or not the data is gzipped true by default)
	 * @param nbMaxItems
	 *            maximum number of items to load
	 * @param nbSkipItems
	 *            number of items to skip
	 */
	public IndexData(final Class<T> esEntityClass, final String location, final boolean gzipped, final Long nbMaxItems,
			final Long nbSkipItems) {
		this.esEntityClass = esEntityClass;
		this.location = location;
		this.gzipped = gzipped;
		this.nbMaxItems = nbMaxItems;
		this.nbSkipItems = nbSkipItems;
	}

	public static <T> IndexData<T> of(final Class<T> esEntityClass, final String location) {
		return of(esEntityClass, location, Long.MAX_VALUE, 0L);
	}

	public static <T> IndexData<T> of(final Class<T> clazz, final String location, final Long nbMaxItems) {
		return of(clazz, location, nbMaxItems, 0L);
	}

	public static <T> IndexData<T> of(final Class<T> clazz, final String location, final Long nbMaxItems,
			final Long nbSkipItems) {
		boolean gzipped = location.toLowerCase().endsWith(".gz");
		return new IndexData<>(clazz, location, gzipped, nbMaxItems, nbSkipItems);
	}

	public static <T> IndexData<T> of(final LoadEsData a) {
		return of((Class<T>) a.esEntityClass(), a.location(), a.nbMaxItems(), a.nbSkipItems());
	}

	public Class<T> getEsEntityClass() {
		return esEntityClass;
	}

	public String getLocation() {
		return location;
	}

	public boolean isGzipped() {
		return gzipped;
	}

}