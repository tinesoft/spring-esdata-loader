package com.github.spring.esdata.loader.junit.jupiter;

import com.github.spring.esdata.loader.core.LoadEsData;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * {@code @LoadEsDataConfig} is a <em>composed annotation</em> that combines
 * {@link ExtendWith @ExtendWith(LoadEsDataExtension.class)} from JUnit Jupiter
 * with the ability to directly define {@link LoadEsData @LoadEsData} to load.
 * <br><br>
 * It is a shortcut for having both:
 * <pre>
 *  &#064;ExtendWith(LoadEsDataExtension.class)
 *  &#064;LoadEsData(esEntityClass = BookEsEntity.class, location = "/data/books.json")
 *  &#064;LoadEsData(esEntityClass = AuthorEsEntity.class, location = "/data/authors.json.gz")
 *  public class MyTest{
 *
 *  }
 * </pre>
 *
 *
 * @author tinesoft
 * @see ExtendWith
 * @see LoadEsDataExtension
 * @see LoadEsData
 */
@ExtendWith(LoadEsDataExtension.class)
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoadEsDataConfig {

	/**
	 * Alias for {@link LoadEsDataConfig#data}.
   * @return the data to load
	 */
	@AliasFor("data")
	LoadEsData[] value() default {};

	/**
	 * Alias for {@link LoadEsDataConfig#value}.
   * @return the data to load
	 */
	@AliasFor("value")
	LoadEsData[] data() default {};

}
