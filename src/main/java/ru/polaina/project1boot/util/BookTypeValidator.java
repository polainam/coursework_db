package ru.polaina.project1boot.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.polaina.project1boot.models.TypeBook;
import ru.polaina.project1boot.services.TypeBookService;

@Component
public class BookTypeValidator implements Validator {

    private final TypeBookService typeBookService;

    public BookTypeValidator(TypeBookService typeBookService) {
        this.typeBookService = typeBookService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return TypeBook.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        TypeBook typeBook = (TypeBook) target;
        if (typeBookService.findByName(typeBook.getTypeName()).isPresent()) {
            errors.rejectValue("typeName", "", "This type of book already exists");
        }
    }
}
