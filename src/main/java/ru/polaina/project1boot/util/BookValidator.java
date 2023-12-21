package ru.polaina.project1boot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.TypeBook;
import ru.polaina.project1boot.services.BooksService;
import ru.polaina.project1boot.services.TypeBookService;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookValidator implements Validator {

    private final TypeBookService typeBookService;

    @Autowired
    public BookValidator(TypeBookService typeBookService) {
        this.typeBookService = typeBookService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Book book = (Book) target;
        List<TypeBook> types = typeBookService.findAll();
        for (TypeBook type: types) {
            if (type.getTypeName().equals(book.getTypeName())) {
                book.setTypeBook(type);
            }
        }
        if (book.getTypeBook() == null) {
            errors.rejectValue("typeName", "", "Choose one of these: " + getTypeBooks());
        }
    }

    private List<String> getTypeBooks() {
        List<String> types = new ArrayList<>();
        for (TypeBook type: typeBookService.findAll()) {
            types.add(type.getTypeName());
        }
        return types;
    }
}
