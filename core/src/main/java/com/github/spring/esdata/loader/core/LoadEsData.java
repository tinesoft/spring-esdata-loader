package com.github.spring.esdata.loader.core;

import java.lang.annotation.*;

/**
 * {@code @LoadEsData} is a {@linkplain Repeatable repeatable} annotation
 * that is used to define which Elasticsearch data to load into which index and how.
 *
 * @author tinesoft
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Repeatable(LoadMultipleEsData.class)
public @interface LoadEsData {

	/**
	 * mapping class of the data to be loaded into Elasticsearch
	 *
	 * @return mapping class of the data to be loaded into Elasticsearch
	 */
	Class<?> esEntityClass();

	/**
	 * path to the file that contains the data
	 *
	 * @return path to the file that contains the data
	 */
	String location();

	/**
	 * maximum number of items to load
	 *
	 * @return maximum number of items to load
	 */
	long nbMaxItems() default Long.MAX_VALUE;

	/**
	 * number of items to skip
	 *
	 * @return number of items to skip
	 */
	long nbSkipItems() default 0;

  /**
   * Format of the data to load. If unspecified the library will try to guess the right format from the content of file at {@link #location()}.
   *
   * @return format of the data to load
   */
  EsDataFormat format() default EsDataFormat.UNKNOWN;

}
