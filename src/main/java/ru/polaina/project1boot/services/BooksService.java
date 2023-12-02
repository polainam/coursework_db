package ru.polaina.project1boot.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Journal;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.models.TypeBook;
import ru.polaina.project1boot.repositories.BooksRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll() {
        return booksRepository.findAll();
    }

    public List<Book> findAll(Integer page, Integer booksPerPage) {
        return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    public List<Book> findAll(String yearOfPublishing) {
        return booksRepository.findAll(Sort.by(yearOfPublishing));
    }

    public List<Book> findAll(Integer page, Integer booksPerPage, String yearOfPublishing) {
        return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by(yearOfPublishing))).getContent();
    }

/*    public List<Book> findByPersonId(Person personId) {
        //return journalRepository.findByPersonId(personId);
        return booksRepository.findByPersonId(personId);
    }*/

    @Transactional
    public void save(Book newBook) {
        booksRepository.saveNewBook(newBook.getTitle(), newBook.getNumberOfCopies(), newBook.getTypeBook().getTypeId());
    }

    public Book findOne(int id) {
        Optional<Book> foundBook = booksRepository.findById(id);
        return foundBook.orElse(null);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        booksRepository.update(id, updatedBook.getTitle(), updatedBook.getNumberOfCopies(), updatedBook.getTypeBook().getTypeId());
    }

    public Optional<Book> findByTitle(String title) {
        return booksRepository.findByTitle(title);
    }


    @Transactional
    public void delete(int id) {
        booksRepository.deleteBook(id);
    }

/*    @Transactional
    public void update(int bookId) {
        Book updatedBook = findOne(bookId);
        updatedBook.setPersonId(null);
        booksRepository.save(updatedBook);
    }*/

/*    @Transactional
    public void update(Person personId, int bookId) { //personId был int
        Book updatedBook = findOne(bookId);
        updatedBook.setPersonId(personId);

        updatedBook.setDateOfReceiving(new Date()); //человек в это время взял книгу

        booksRepository.save(updatedBook);
    }*/



    public List<Book> findByTitleIsStartingWith(String query) {
        return booksRepository.findByTitleIsStartingWith(query);
    }

    public int countAll() {
        return findAll().size();
    }
}
