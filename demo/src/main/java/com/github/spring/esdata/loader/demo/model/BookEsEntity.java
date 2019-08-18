package com.github.spring.esdata.loader.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Objects;

@Document(indexName = "book", type = "Book")
public class BookEsEntity {

	@Id
	private String id;

	@Field(type = FieldType.Keyword)
	private String isbn;

	@Field(type = FieldType.Text)
	private String title;

	@Field(type = FieldType.Nested)
	private AuthorEsEntity author;

	public BookEsEntity() {// for serialization only
	}

	public BookEsEntity(String id, String isbn, String title, AuthorEsEntity author) {
		this.id = id;
		this.isbn = isbn;
		this.title = title;
		this.author = author;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIsbn() {
		return this.isbn;
	}

	public void setIsbn(final String isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public AuthorEsEntity getAuthor() {
		return this.author;
	}

	public void setAuthor(final AuthorEsEntity author) {
		this.author = author;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
    BookEsEntity that = (BookEsEntity) o;
		return this.id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}
}
