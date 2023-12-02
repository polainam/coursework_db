package ru.polaina.project1boot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.services.PeopleService;

@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        if (peopleService.findByName(person.getUserName()).isPresent()) {
            errors.rejectValue("userName", "", "This username is already taken");
        }
    }

    public void validatePassportNum(Object target, Errors errors) {
        Person person = (Person) target;
        if (peopleService.findByPassportNum(person.getPassportNum()).isPresent()) {
            errors.rejectValue("passportNum", "", "Incorrect passport number");
        }
    }
}
