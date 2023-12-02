package ru.polaina.project1boot.models;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookId;

    /////////////////////////////////////////////
    @OneToMany(mappedBy = "book")
    private List<Journal> journalEntries;
    ///////////////////////////////////////////////////

    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private TypeBook typeBook;

    @NotEmpty(message = "Type should not be empty")
    @Column(name = "type_id", insertable = false, updatable = false)
    private String typeName;
    @NotEmpty(message = "Title should not be empty")
    @Column(name = "name")
    private String title;

    @Column(name = "cnt")
    private Integer numberOfCopies;

    //сделать отношение
/*    @Column(name = "type_id")
    private String typeId;*/

/*    @Column(name = "date_of_receiving")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateOfReceiving;*/

/*    @Transient
    private boolean isReturnTimeOverdue;*/

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

/*    public Person getPersonId() {
        return personId;
    }

    public void setPersonId(Person personId) {
        this.personId = personId;
    }*/

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

    /*    public Date getDateOfReceiving() {
        return dateOfReceiving;
    }

    public void setDateOfReceiving(Date dateOfReceiving) {
        this.dateOfReceiving = dateOfReceiving;
    }

    public boolean isReturnTimeOverdue() {
        Date currentTime = new Timestamp(System.currentTimeMillis());
        long differenceInMillis = currentTime.getTime() - dateOfReceiving.getTime();
        long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis);

        return differenceInDays > 10;
    }*/

/*    public void setReturnTimeOverdue(boolean returnTimeOverdue) {
        isReturnTimeOverdue = returnTimeOverdue;
    }*/

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

/*    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }*/

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
