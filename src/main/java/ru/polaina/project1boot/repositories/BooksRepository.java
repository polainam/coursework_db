package ru.polaina.project1boot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.TypeBook;

import java.util.List;
import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {

    Optional<Book> findByTitle(String title);
    List<Book> findByTitleIsStartingWith(String query);
    @Procedure("create_book")
    void saveNewBook(String title, Integer cnt, Integer typeId);

    @Procedure("update_book")
    void update(Integer id, String title, Integer cnt, Integer typeId);
    @Procedure("delete_book")
    void deleteBook(Integer id);

    List<Book> findByTypeBook(TypeBook typeBook);
}
