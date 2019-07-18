package com.github.spring.esdata.loader.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Containing annotation to allow repeating {@literal @}LoadEsData, no need to use it directly in code.
 *
 * @see <a href=
 *      "https://docs.oracle.com/javase/tutorial/java/annotations/repeating.html">Repeating
 *      Annotations</ap>
 * @author tinesoft
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface LoadMultipleEsData {
	LoadEsData[] value();
}