package ru.polaina.project1boot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

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

    @NotEmpty(message = "Type name should not be empty")
    @Column(name = "name")
    private String typeName;

    @Min(value = 0, message = "Count should be greater than or equal to 0")
    @Column(name = "cnt")
    private int countOfTypes;

    @Min(value = 0, message = "Fine should be greater than or equal to 0")
    @Column(name = "fine")
    private int fine;

    @Min(value = 0, message = "Count of days should be greater than or equal to 0")
    @Column(name = "day_count")
    private int countOfDaysToRead;

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
    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public int getCountOfTypes() {
        return countOfTypes;
    }

    public void setCountOfTypes(int countOfTypes) {
        this.countOfTypes = countOfTypes;
    }

    public int getFine() {
        return fine;
    }

    public void setFine(int fine) {
        this.fine = fine;
    }

    public int getCountOfDaysToRead() {
        return countOfDaysToRead;
    }

    public void setCountOfDaysToRead(int countOfDaysToRead) {
        this.countOfDaysToRead = countOfDaysToRead;
    }
}
