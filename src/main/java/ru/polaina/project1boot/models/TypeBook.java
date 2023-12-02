package ru.polaina.project1boot.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "book_types")
public class TypeBook {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int typeId;

    @OneToMany(mappedBy = "typeBook")
    private List<Book> books;

    @Column(name = "name")
    private String typeName;

    @Column(name = "cnt")
    private String countOfTypes;

    @Column(name = "fine")
    private String fine;

    @Column(name = "day_count")
    private String countOfDaysToRead;

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCountOfTypes() {
        return countOfTypes;
    }

    public void setCountOfTypes(String countOfTypes) {
        this.countOfTypes = countOfTypes;
    }

    public String getFine() {
        return fine;
    }

    public void setFine(String fine) {
        this.fine = fine;
    }

    public String getCountOfDaysToRead() {
        return countOfDaysToRead;
    }

    public void setCountOfDaysToRead(String countOfDaysToRead) {
        this.countOfDaysToRead = countOfDaysToRead;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
