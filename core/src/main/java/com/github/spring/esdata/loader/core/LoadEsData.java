package com.github.spring.esdata.loader.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
	 * class of the ES data to be loaded
	 * 
	 * @return class of the ES data to be loaded
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

}
