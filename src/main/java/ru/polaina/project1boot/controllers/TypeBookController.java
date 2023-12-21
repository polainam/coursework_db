package ru.polaina.project1boot.controllers;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.TypeBook;
import ru.polaina.project1boot.services.BooksService;
import ru.polaina.project1boot.services.TypeBookService;
import ru.polaina.project1boot.util.BookTypeValidator;

import java.util.List;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@Controller
@RequestMapping("/bookTypes")
public class TypeBookController {

    private final TypeBookService typeBookService;
    private final BooksService booksService;

    private final BookTypeValidator bookTypeValidator;

    public TypeBookController(TypeBookService typeBookService, BooksService booksService, BookTypeValidator bookTypeValidator) {
        this.typeBookService = typeBookService;
        this.booksService = booksService;
        this.bookTypeValidator = bookTypeValidator;
    }

    @GetMapping()
    public String pageBookTypes(Model model) {
        List<TypeBook> bookTypes = typeBookService.findAll();
        model.addAttribute("bookTypes", bookTypes);
        return "bookTypes/bookTypes";
    }

    @GetMapping("/new")
    public String newBookType(@ModelAttribute("newBookType") TypeBook typeBook) {
        return "bookTypes/newTypeBook";
    }

    @PostMapping("/new")
    public String insertNewBookType(@ModelAttribute("newBookType") @Valid TypeBook typeBook, BindingResult bindingResult) {
        bookTypeValidator.validate(typeBook, bindingResult);
        if (bindingResult.hasErrors()) {
            return "bookTypes/newTypeBook";
        }
        typeBookService.save(typeBook);
        return "redirect:/bookTypes";
    }

    @GetMapping("/{id}")
    public String pageTypeOfBook(@PathVariable("id") int typeId, Model model,
                                 @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                 @RequestParam(value = "typeBook_per_page", required = false, defaultValue = "10") Integer typeBookPerPage) {
        TypeBook typeBook = typeBookService.findById(typeId).get();
        List<Book> booksWithType = booksService.findByTypeBook(typeBook);
        int totalBooks = booksWithType.size();
        int totalPages = (int) Math.ceil((double) totalBooks / typeBookPerPage) - 1;

        List<Book> books = booksService.paginateBooks(booksWithType, page, typeBookPerPage);

        model.addAttribute("typeBook", typeBook);
        model.addAttribute("booksWithType", books);
        model.addAttribute("booksPerPage", typeBookPerPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "bookTypes/pageBookType";
    }

    @GetMapping("/{id}/edit")
    public String typeBookEditPage(@PathVariable("id") int id, Model model) {
        TypeBook typeBookForEdit = typeBookService.findById(id).get();
        model.addAttribute("typeBookForEdit", typeBookForEdit);
        return "bookTypes/editTypeBook";
    }

    @PatchMapping("/{id}")
    public String updateInfoAboutTypeBook(@ModelAttribute("typeBookForEdit") @Valid TypeBook typeBookForEdit, BindingResult bindingResult, @PathVariable("id") int id, Model model) {
        if (!typeBookService.isNewNameTheSame(id, typeBookForEdit)) {
            bookTypeValidator.validate(typeBookForEdit, bindingResult);
        }
        if (bindingResult.hasErrors()) {
            typeBookForEdit.setTypeId(id);
            model.addAttribute("typeBookForEdit", typeBookForEdit);
            return "bookTypes/editTypeBook";
        }
        typeBookService.update(id, typeBookForEdit);
        return "redirect:/bookTypes/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") int id, Model model) {
        try {
            typeBookService.delete(id);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("hasThisTypeBookBeenDeleted", false);
            model.addAttribute("cause", "Foreign key constraint");
            TypeBook typeBook = typeBookService.findById(id).get();
            List<Book> booksWithType = booksService.findByTypeBook(typeBook);
            int totalBooks = booksWithType.size();
            int totalPages = (int) Math.ceil((double) totalBooks / 10) - 1;

            List<Book> books = booksService.paginateBooks(booksWithType, 0, 10);

            model.addAttribute("typeBook", typeBook);
            model.addAttribute("booksWithType", books);
            model.addAttribute("booksPerPage", 10);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", totalPages);
            return "bookTypes/pageBookType";
        }
        return "redirect:/bookTypes";
    }
}
