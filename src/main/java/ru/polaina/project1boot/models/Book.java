package ru.polaina.project1boot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookId;

    @OneToMany(mappedBy = "book")
    private List<Journal> journalEntries;

    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private TypeBook typeBook;

    @NotEmpty(message = "Type should not be empty")
    @Column(name = "type_id", insertable = false, updatable = false)
    private String typeName;
    @NotEmpty(message = "Title should not be empty")
    @Column(name = "name")
    private String title;

    @Min(value = 0, message = "Count should be greater than or equal to 0")
    @Column(name = "cnt")
    private Integer numberOfCopies;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public List<Journal> getJournalEntries() {
        return journalEntries;
    }

    public void setJournalEntries(List<Journal> journalEntries) {
        this.journalEntries = journalEntries;
    }

    public Integer getNumberOfCopies() {
        return numberOfCopies;
    }

    public void setNumberOfCopies(Integer numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }

    public void reduceNumberOfCopies() {
        setNumberOfCopies(getNumberOfCopies() - 1);
    }

    public void increaseNumberOfCopies() {
        setNumberOfCopies(getNumberOfCopies() + 1);
    }

    public TypeBook getTypeBook() {
        return typeBook;
    }

    public void setTypeBook(TypeBook typeBook) {
        this.typeBook = typeBook;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
