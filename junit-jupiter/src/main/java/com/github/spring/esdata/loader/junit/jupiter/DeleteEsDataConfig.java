package com.github.spring.esdata.loader.junit.jupiter;

import com.github.spring.esdata.loader.core.DeleteEsData;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * {@code @DeleteEsDataConfig} is a <em>composed annotation</em> that combines
 * {@link ExtendWith @ExtendWith(DeleteEsDataExtension.class)} from JUnit Jupiter
 * with the ability to directly define {@link DeleteEsData @DeleteEsData} to load.
 * <br><br>
 * It is a shortcut for having both:
 * <pre>
 *  &#064;ExtendWith(DeleteEsDataExtension.class)
 *  &#064;DeleteEsData(esEntityClass = BookEsEntity.class, location = "/data/books.json")
 *  &#064;DeleteEsData(esEntityClass = AuthorEsEntity.class, location = "/data/authors.json.gz")
 *  public class MyTest{
 *
 *  }
 * </pre>
 *
 * @author tinesoft
 * @see ExtendWith
 * @see DeleteEsDataExtension
 * @see DeleteEsData
 */
@ExtendWith(DeleteEsDataExtension.class)
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DeleteEsDataConfig {
  /**
   * Alias for {@link DeleteEsDataConfig#value}.
   *
   * @return mapping classes of the data to be deleted from Elasticsearch
   */
  @AliasFor("value")
  Class<?>[] esEntityClasses() default {};

  /**
   * Alias for {@link DeleteEsDataConfig#esEntityClasses}.
   *
   * @return mapping classes of the data to be deleted from Elasticsearch
   */
  @AliasFor("esEntityClasses")
  Class<?>[] value() default {};
}
