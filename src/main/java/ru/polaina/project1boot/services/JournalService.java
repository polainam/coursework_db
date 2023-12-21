package ru.polaina.project1boot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Journal;
import ru.polaina.project1boot.repositories.JournalRepository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class JournalService {

    private final JournalRepository journalRepository;
    private final BooksService booksService;

    @Autowired
    public JournalService(JournalRepository journalRepository, BooksService booksService) {
        this.journalRepository = journalRepository;
        this.booksService = booksService;
    }

    public Optional<Journal> findIdByBookIdAndPersonId(int bookId, int personId) {
        return journalRepository.findIdByBookIdAndPersonIdAndDateReturnNull(bookId, personId);
    }

    public Journal getJournalEntry(int bookId, int personId) {
        Optional<Journal> journalEntry = findIdByBookIdAndPersonId(bookId, personId);
        return journalEntry.orElse(null);
    }

    @Transactional
    public void save(Journal journal) {
        journalRepository.save(journal);
    }

    public List<Journal> findByPersonIdAndDateRetNull(int personId) {
        return journalRepository.findByPersonIdAndDateReturnNull(personId);
    }

    @Transactional
    public void returnBook(Journal journalEntry) {
            Date currentDate = new Date();
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
            journalEntry.setDateReturn(currentTimestamp);
            journalRepository.save(journalEntry);//update?
            int bookId = journalEntry.getBookId();
            Book book = booksService.findOne(bookId);
            book.increaseNumberOfCopies();
            booksService.update(book.getBookId(), book); //update?
    }

    @Transactional
    public void deleteByPersonId(int personId) {
        journalRepository.deleteEntriesByPersonId(personId);
    }

    public Integer calculateFine(int bookId, int personId) {
        return journalRepository.calculateFine(bookId, personId);
    }

    public Integer calculateTotalFine(int id) {
        return journalRepository.calculateTotalFine(id);
    }

    public List<Journal> findAll() {
        return journalRepository.findAll();
    }

    public Page<Journal> findAllWithPagination(Integer page, Integer journalPerPage) {
        Pageable pageable = PageRequest.of(page, journalPerPage);

        return journalRepository.findAll(pageable);
    }

    @Transactional
    public void deleteByBookId(int id) {
        journalRepository.deleteByBookId(id);
    }
}
