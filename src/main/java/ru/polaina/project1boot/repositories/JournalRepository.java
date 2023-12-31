package ru.polaina.project1boot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import ru.polaina.project1boot.models.Journal;

import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Integer> {

    List<Journal> findAllByBookId(int bookId);

    Optional<Journal> findIdByBookIdAndPersonIdAndDateReturnNull(int bookId, int personId);

    List<Journal> findByPersonIdAndDateReturnNull(int personId);
    List<Journal> findAllByPersonId(int personId);

    Integer countAllByPersonId(int personId);

    void deleteByBookId(int id);

    @Procedure("delete_journal_entries_for_client")
    void deleteEntriesByPersonId(int personId);

    @Procedure("calculate_fine_for_book")
    Integer calculateFine(int bookId, int personId);

    @Procedure("calculate_total_fine")
    Integer calculateTotalFine(int id);
}

