package ru.polaina.project1boot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Journal;
import ru.polaina.project1boot.repositories.JournalRepository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public List<Journal> findAllByBookId(int bookId) {
        return journalRepository.findAllByBookId(bookId);
    }

    public void setDateReserve(Date dateReserve) {

    }

    public Optional<Journal> findIdByBookIdAndPersonId(int bookId, int personId) {
        return journalRepository.findIdByBookIdAndPersonIdAndDateReturnNull(bookId, personId);
    }

 /*   public boolean isBookReserved(int bookId, int personId) {
        Optional<Journal> journalEntry = findIdByBookIdAndPersonId(bookId, personId);

        if (journalEntry.isPresent()) {
            Journal journal = journalRepository.findById(journalEntry.get().getId()).orElse(null); // null никогда не будет
            return journal != null && journal.getDateReserve() != null;
        }

        return false;
    }*/

    public Journal getJournalEntry(int bookId, int personId) {
        Optional<Journal> journalEntry = findIdByBookIdAndPersonId(bookId, personId);
        return journalEntry.orElse(null);
    }

    @Transactional
    public void save(Journal journal) {
        journalRepository.save(journal);
    }

/*    @Transactional
    public void deleteExpiredReservations(Date currentDate) {
        journalRepository.deleteByDateEndReserveBefore(currentDate);
    }

    @Transactional
    public void delete(Journal journalEntry) {
        journalRepository.delete(journalEntry);
    }

    public List<Journal> findByPersonIdAndDateReserveNotNull(int personId) {
        return journalRepository.findByPersonIdAndDateReserveNotNull(personId);
    }*/

    public List<Journal> findByPersonIdAndDateRetNull(int personId) {
        return journalRepository.findByPersonIdAndDateReturnNull(personId);
    }

    public boolean isBookBorrowed(int bookId, int personId) {
        Optional<Journal> journalEntry = findIdByBookIdAndPersonId(bookId, personId);

        if (journalEntry.isPresent()) {
            Journal journal = journalRepository.findById(journalEntry.get().getId()).orElse(null); // null никогда не будет
            return journal != null && journal.getDateBegin() != null;
        }
        return false;
    }

    public List<Journal> findAllByPersonId(int personId) {
        return journalRepository.findAllByPersonId(personId);
    }

    public Integer countAllByPersonId(int personId) {
        return journalRepository.countAllByPersonId(personId);
    }

    @Transactional
    public void returnBooks(List<Integer> returnedBooksId) {
        for (Integer id:returnedBooksId) {
            /*Date dateReturn = new Timestamp(System.currentTimeMillis());*/
            Date currentDate = new Date();
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
            Optional<Journal> journal = journalRepository.findByBookIdAndDateReturnNull(id);
            journal.get().setDateReturn(currentTimestamp);
            Book book = booksService.findOne(id);
            book.increaseNumberOfCopies();
            booksService.update(id, book);
        }
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

/*    public List<Journal> getExpiredBooks(Date date, List<Journal> borrowedBooks) {
        List<Journal> expiredBooks = new ArrayList<>();
        for (Journal journalEntry:borrowedBooks) {
            if (journalEntry.getDateEnd().compareTo(date) < 0) {
                expiredBooks.add(journalEntry);
            }
        }
        return expiredBooks;
    }*/
}
