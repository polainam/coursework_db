package ru.polaina.project1boot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Journal;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.models.TypeBook;
import ru.polaina.project1boot.security.PersonDetails;
import ru.polaina.project1boot.services.BooksService;
import ru.polaina.project1boot.services.JournalService;
import ru.polaina.project1boot.services.TypeBookService;
import ru.polaina.project1boot.util.BookValidator;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BooksService bookService;
    private  final JournalService journalService;
    private final BookValidator bookValidator;

    private final TypeBookService typeBookService;

    private static final int NUMBER_OF_DAYS_OF_RESERVE = 3;

    @Autowired
    public BooksController(BooksService bookService, JournalService journalService, BookValidator bookValidator, TypeBookService typeBookService) {
        this.bookService = bookService;
        this.journalService = journalService;
        this.bookValidator = bookValidator;
        this.typeBookService = typeBookService;
    }

    @GetMapping()
    public String listOfBooks(Model model,
                              @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                              @RequestParam(value = "books_per_page", required = false, defaultValue = "10") Integer booksPerPage,
                              @RequestParam(value = "sort_by_year", required = false) String sortByYear,
                              Authentication authentication) {
        int totalBooks = bookService.countAll();

        int totalPages = (int) Math.ceil((double) totalBooks / booksPerPage) - 1;

        List<Book> books = bookService.findAll(page, booksPerPage);

        model.addAttribute("booksPerPage", booksPerPage);
        model.addAttribute("books", books);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        Person person = ((PersonDetails) authentication.getPrincipal()).getPerson();
        model.addAttribute("personId", person.getPersonId());

        return "books/listOfBooks";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/new")
    public String newBook(@ModelAttribute("newBook") Book book) {
        return "books/admin/newBook";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/new")
    public String insertNewBook(@ModelAttribute("newBook") @Valid Book book, BindingResult bindingResult) {
        bookValidator.validate(book, bindingResult);
        bookValidator.validateCountOfBooks(book.getNumberOfCopies(), bindingResult);
        if (bindingResult.hasErrors()) {
            return "books/admin/newBook";
        }
        bookService.save(book);
        return "redirect:/books";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/user/{id}")
    public String pageBookForUser(@PathVariable("id") int id, Authentication authentication, Model model) {
        Person person = ((PersonDetails) authentication.getPrincipal()).getPerson();
        //model.addAttribute("infoAboutPerson", person);
        getInfoAboutBook(person.getPersonId(), id, model);

        return "books/user/pageBook";
    }

    private void getInfoAboutBook(int personId, int bookId, Model model) {
        Book book = bookService.findOne(bookId);
        model.addAttribute("infoAboutBook", book);
        model.addAttribute("typeBook", book.getTypeBook());
        Journal journal = journalService.getJournalEntry(bookId, personId);
        if (journal  != null) {
            model.addAttribute("isBookBorrowed", true);
            Date dateEnd = journal.getDateEnd();
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //String formattedDate = sdf.format(dateEnd);
            model.addAttribute("dateEnd", dateEnd);
            /*LocalDate localDate = LocalDate.now();
            Date currentDate = java.sql.Date.valueOf(localDate);*/
            Date currentDate = new Date();
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
            model.addAttribute("currentDate", currentTimestamp);
            Integer fine = journalService.calculateFine(bookId, personId);
            if (fine != null) {
                model.addAttribute("fine", fine);
            }
        }
        else {
            model.addAttribute("isBookBorrowed", false);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/{id}")
    public String pageBookForAdmin(@PathVariable("id") int id, Model model) {
        getInfoAboutBookForAdmin(id, model);

        return "books/admin/pageBook";
    }

    private void getInfoAboutBookForAdmin(int id, Model model) {
        Book book = bookService.findOne(id);
        int countBooks = book.getNumberOfCopies();
        if (countBooks == 1) {
            model.addAttribute("isOne", true);
        }
        else {
            model.addAttribute("isOne", false);
        }
        model.addAttribute("infoAboutBook", book);
        model.addAttribute("countBooks", countBooks);
        model.addAttribute("typeBook", book.getTypeBook());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/{id}/edit")
    public String bookEditPage(@PathVariable("id") int id, Model model) {
        Book editBook = bookService.findOne(id);
        switch (editBook.getTypeBook().getTypeId()) {
            case 1 -> {
                editBook.setTypeName("ordinary");
            }
            case 2 -> {
                editBook.setTypeName("rare");
            }
            case 3 -> {
                editBook.setTypeName("unique");
            }
        }
        model.addAttribute("editBook", editBook);

        return "books/admin/editBook";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public String updateInfoAboutBook(@ModelAttribute("editBook") @Valid Book editBook, BindingResult bindingResult, @PathVariable("id") int id) {
        bookValidator.validate(editBook, bindingResult);
        bookValidator.validateCountOfBooks(editBook.getNumberOfCopies(), bindingResult);
        if (bindingResult.hasErrors()) {
            return "books/admin/editBook";
        }
        bookService.update(id, editBook);
        return "redirect:/books/admin/" + id;
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam String query, Model model) {
        if (query.isEmpty()) {
            return "redirect:/books";
        }
        List<Book> searchBooks = bookService.findByTitleIsStartingWith(query);
        model.addAttribute("books", searchBooks);
        return "books/listOfBooks";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") int id, Model model) {
        try {
            bookService.delete(id);
        } catch (JpaSystemException e) {
            model.addAttribute("hasThisBookBeenDeleted", false);
            getInfoAboutBookForAdmin(id, model);
            return "books/admin/pageBook";
        }
        return "redirect:/books";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/pageBookForPerson/{person_id}/{book_id}")
    public String pageBookForPerson(@PathVariable("person_id") int personId, @PathVariable("book_id") int bookId, Model model) {
        getInfoAboutBook(personId, bookId, model);

        return "/books/admin/pageBookForPerson";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/bookTypes")
    public String pageBookTypes(Model model) {
        List<TypeBook> bookTypes = typeBookService.findAll();
        model.addAttribute("bookTypes", bookTypes);
        return "/books/bookTypes";
    }

}
