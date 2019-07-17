package com.github.spring.esdata.loader.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Objects;

@Document(indexName = "library", type = "Library")
public class LibraryEsEntity {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String address;

    @Field(type = FieldType.Long)
    private long nbBooks;

    public LibraryEsEntity() {// for serialization only
    }

    public LibraryEsEntity(String id, String name, String address, long nbBooks) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.nbBooks = nbBooks;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getNbBooks() {
        return this.nbBooks;
    }

    public void setNbBooks(long nbBooks) {
        this.nbBooks = nbBooks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        LibraryEsEntity that = (LibraryEsEntity) o;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
