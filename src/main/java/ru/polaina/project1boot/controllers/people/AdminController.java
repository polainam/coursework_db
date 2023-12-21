package ru.polaina.project1boot.controllers.people;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Journal;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.services.BooksService;
import ru.polaina.project1boot.services.JournalService;
import ru.polaina.project1boot.services.PeopleService;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.*;

@Controller
@RequestMapping("/people/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final PeopleService peopleService;
    private final JournalService journalService;
    private final BooksService booksService;
    private static final int NUMBER_OF_READING_DAYS_ORDINARY_BOOK = 60;
    private static final int NUMBER_OF_READING_DAYS_RARE_BOOK = 21;
    private static final int NUMBER_OF_READING_DAYS_UNIQUE_BOOK = 7;

    @Autowired
    public AdminController(PeopleService peopleService, JournalService journalService, BooksService booksService) {
        this.peopleService = peopleService;
        this.journalService = journalService;
        this.booksService = booksService;
    }

    @GetMapping()
    public String listOfPeople(Model model,
                               @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                               @RequestParam(value = "people_per_page", required = false, defaultValue = "10") Integer peoplePerPage) {
        List<Person> users = peopleService.findAllUsers("ROLE_USER");
        int totalPeople = users.size();
        int totalPages = (int) Math.ceil((double) totalPeople / peoplePerPage) - 1;
        Page<Person> people = peopleService.findUsersWithRole(page, peoplePerPage);
        model.addAttribute("people", people);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("peoplePerPage", peoplePerPage);

        return "people/admin/listOfPeople";
    }

    @GetMapping("/{id}")
    public String pagePersonForAdmin(@PathVariable("id") int id, Model model) {
        getInfoAboutPersonForAdmin(id, model);
        return "people/admin/pagePerson";
    }

    private void getInfoAboutPersonForAdmin(int id, Model model) {
        Person person = peopleService.findOne(id);
        model.addAttribute("infoAboutPerson", person);
        List<Journal> borrowedBooks = journalService.findByPersonIdAndDateRetNull(person.getPersonId());
        model.addAttribute("borrowedBooks", borrowedBooks);

        List<Book> books = booksService.findAll();
        for (Journal borrowedBook : borrowedBooks) {
            Book book = borrowedBook.getBook();
            books.remove(book);
        }
        model.addAttribute("books", books);
        Date currentDate = new Date();
        Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
        model.addAttribute("currentDate", currentTimestamp);
        Integer totalFine = journalService.calculateTotalFine(id);
        if (totalFine != null) {
            model.addAttribute("totalFine", totalFine);
        }
        Integer numberOfBooks = peopleService.getNumberOfBooksForPerson(id);
        model.addAttribute("numberOfBooks", numberOfBooks);
    }

    @PutMapping("/assign")
    public String assignBook(@RequestParam int bookId, @RequestParam int personId, Journal journal, Model model) {
        Book book = booksService.findOne(bookId);
        Person person = peopleService.findOne(personId);

        Date currentDate = new Date();
        Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
        journal.setDateBegin(currentTimestamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTimestamp);
        journal.setDateEnd(getDateEnd(calendar, book));

        journal.setBook(book);
        journal.setPerson(person);
        try {
            journalService.save(journal);
            book.reduceNumberOfCopies();
            booksService.update(bookId, book);
        } catch (JpaSystemException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Клиент достиг лимита в 10 книг")) {
                getMenuInfo(model, 0, 10);
                model.addAttribute("exceededTenBooks", true);
            } else if (errorMessage.contains("Нельзя вставить запись в журнал. Количество экземпляров книги равно 0")) {
                getMenuInfo(model, 0, 10);
                model.addAttribute("bookIsOutOfStock", true);
            } else if (errorMessage.contains("Клиент уже читает эту книгу")) {
                getMenuInfo(model, 0, 10);
                model.addAttribute("duplicateBooks", true);
            }
            return "/menu/journal";
        }
        getMenuInfo(model, 0, 10);
        return "/menu/journal";
    }

    private Timestamp getDateEnd(Calendar calendar, Book book) {
        switch (book.getTypeBook().getTypeName()) {
            case "ordinary" -> calendar.add(Calendar.DAY_OF_MONTH, NUMBER_OF_READING_DAYS_ORDINARY_BOOK);
            case "rare" -> calendar.add(Calendar.DAY_OF_MONTH, NUMBER_OF_READING_DAYS_RARE_BOOK);
            case "unique" -> calendar.add(Calendar.DAY_OF_MONTH, NUMBER_OF_READING_DAYS_UNIQUE_BOOK);
        }
        Date dateEnd = calendar.getTime();
        return new Timestamp(dateEnd.getTime());
    }

    @PutMapping("/return")
    public String returnBook(@RequestParam int bookId, @RequestParam int personId, Model model) {
        Optional<Journal> journalEntry = journalService.findIdByBookIdAndPersonId(bookId, personId);
        if (journalEntry.isPresent()) {
            journalService.returnBook(journalEntry.get());
        } else {
            model.addAttribute("clientRecordNotFound", true);
        }
        getMenuInfo(model, 0, 10);
        return "/menu/journal";
    }


    @GetMapping("/search")
    public String searchPeople(@RequestParam String query, Model model) {
        if (query.isEmpty()) {
            return "redirect:/people/admin";
        }
        List<Person> searchPeople = peopleService.findByTitleIsStartingWith(query);
        model.addAttribute("people", searchPeople);

        return "people/admin/listOfPeople";
    }


    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable("id") int id, Model model) {
        try {
            peopleService.delete(id);
        } catch (JpaSystemException e) {
            model.addAttribute("hasThisPersonBeenDeleted", false);
            model.addAttribute("cause", "Not all books have been returned");
            getInfoAboutPersonForAdmin(id, model);
            return "people/admin/pagePerson";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("hasThisPersonBeenDeleted", false);
            model.addAttribute("cause", "Foreign key constraint");
            getInfoAboutPersonForAdmin(id, model);
            return "people/admin/pagePerson";
        }
        return "redirect:/people/admin";
    }

    @DeleteMapping("/entries/{id}")
    public String deleteJournalEntriesByPersonId(@PathVariable("id") int id, Model model) {
        journalService.deleteByPersonId(id);
        getInfoAboutPersonForAdmin(id, model);
        return "people/admin/pagePerson";
    }

    @GetMapping("/menu")
    public String pageMenuWIthJournal(Model model,
                                      @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                      @RequestParam(value = "journal_per_page", required = false, defaultValue = "10") Integer journalPerPage) {

        getMenuInfo(model, page, journalPerPage);

        return "/menu/journal";
    }

    private void getMenuInfo(Model model, Integer page, Integer journalPerPage) {
        List<Journal> journalEntries = journalService.findAll();
        model.addAttribute("journalEntries", journalEntries);
        int totalEntries = journalEntries.size();
        int totalPages = (int) Math.ceil((double) totalEntries / journalPerPage) - 1;
        Page<Journal> journalPage = journalService.findAllWithPagination(page, journalPerPage);
        model.addAttribute("journalPage", journalPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("journalPerPage", journalPerPage);

        List<Book> books = booksService.findAll();
        model.addAttribute("books", books);

        List<Person> people = peopleService.findAllUsers("ROLE_USER");
        model.addAttribute("people", people);

        model.addAttribute("bookId", 0);
        model.addAttribute("personId", 0);
        model.addAttribute("exceededTenBooks", false);
        model.addAttribute("bookIsOutOfStock", false);
        model.addAttribute("duplicateBooks", false);
    }

    @GetMapping("/queryHistory")
    public String queryHistory(Model model) throws IOException {
        Path logFilePath = Path.of("query_history.log");

        try (Scanner scanner = new Scanner(logFilePath)) {
            List<String> sqlQueries = new ArrayList<>();
            boolean foundSql = false;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (foundSql) {
                    sqlQueries.add(line);
                } else if (line.contains("org.hibernate.SQL")) {
                    foundSql = true;
                    sqlQueries.add(line);
                }
            }
            model.addAttribute("logLines", sqlQueries);
        }
        return "/menu/queryHistory";
    }
}
