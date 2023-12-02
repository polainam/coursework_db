package ru.polaina.project1boot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.TypeBook;
import ru.polaina.project1boot.services.BooksService;

@Component
public class BookValidator implements Validator {

    private final BooksService booksService;

    @Autowired
    public BookValidator(BooksService booksService) {
        this.booksService = booksService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Book book = (Book) target;
        TypeBook typeBook = new TypeBook();
        switch (book.getTypeName()) {
            case "ordinary" -> {
                typeBook.setTypeId(1);
                book.setTypeBook(typeBook);
            }
            case "rare" -> {
                typeBook.setTypeId(2);
                book.setTypeBook(typeBook);
            }
            case "unique" -> {
                typeBook.setTypeId(3);
                book.setTypeBook(typeBook);
            }
            default -> errors.rejectValue("typeName", "", "Choose one of three types: ordinary, rare, unique");
        }
    }

    public void validateCountOfBooks(Object target, Errors errors) {
        Integer numberOfCopies = (Integer) target;
        if (numberOfCopies == null) {
            errors.rejectValue("numberOfCopies", "", "Count of copies should not be empty");
        }
    }
}
