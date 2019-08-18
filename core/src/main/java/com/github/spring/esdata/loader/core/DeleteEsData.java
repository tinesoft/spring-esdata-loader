package com.github.spring.esdata.loader.core;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * {@code @LoadEsData} is a {@linkplain Repeatable repeatable} annotation
 * that is used to define which data to delete from Elasticsearch index.
 *
 * @author tinesoft
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DeleteEsData {
  /**
   * Alias for {@link DeleteEsData#value}.
   *
   * @return mapping classes of the data to be deleted from Elasticsearch
   */
  @AliasFor("value")
  Class<?>[] esEntityClasses() default {};


  /**
   * Alias for {@link DeleteEsData#esEntityClasses}.
   *
   * @return mapping classes of the data to be deleted from Elasticsearch
   */
  @AliasFor("esEntityClasses")
  Class<?>[] value() default {};

}
