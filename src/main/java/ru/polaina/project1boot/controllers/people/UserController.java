package ru.polaina.project1boot.controllers.people;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.polaina.project1boot.models.Journal;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.services.JournalService;
import ru.polaina.project1boot.services.PeopleService;
import ru.polaina.project1boot.util.PersonValidator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/people/user")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {
    private final PeopleService peopleService;
    private final JournalService journalService;
    private final PersonValidator personValidator;

    public UserController(PeopleService peopleService, JournalService journalService, PersonValidator personValidator) {
        this.peopleService = peopleService;
        this.journalService = journalService;
        this.personValidator = personValidator;
    }

    @GetMapping("/{id}")
    public String pagePersonForUser(@PathVariable("id") int id, Model model) {
        Person person = peopleService.findOne(id);
        model.addAttribute("infoAboutPerson", person);
        List<Journal> borrowedBooks = journalService.findByPersonIdAndDateRetNull(person.getPersonId());
        model.addAttribute("borrowedBooks", borrowedBooks);
        /*Date currentDate = Calendar.getInstance().getTime();
        model.addAttribute("currentDate", currentDate);*/
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedCurrentDate = dateFormat.format(currentDate);
        model.addAttribute("currentDate", formattedCurrentDate);
        int numberOfBooksForPerson = peopleService.getNumberOfBooksForPerson(id);
        model.addAttribute("numberOfBooksForPerson", numberOfBooksForPerson);
        Integer totalFine = journalService.calculateTotalFine(id);
        if (totalFine != null) {
            model.addAttribute("totalFine", totalFine);
        }

        return "people/user/pagePerson";
    }

    @GetMapping("/{id}/edit")
    public String personEditPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("editPerson", peopleService.findOne(id));
        return "people/user/editPerson";
    }

    @PatchMapping("/{id}")
    public String updateInfoAboutPerson(@ModelAttribute("editPerson") @Valid Person editPerson, BindingResult bindingResult, @PathVariable("id") int id, Model model) {
        if (!peopleService.isNewUsernameTheSame(id, editPerson)) {
            personValidator.validate(editPerson, bindingResult);
        }
        if (!peopleService.isNewPassportNumTheSame(id, editPerson)) {
            personValidator.validatePassportNum(editPerson, bindingResult);
        }
        if (bindingResult.hasErrors()) {
            editPerson.setPersonId(id);
            model.addAttribute("editPerson", editPerson);
            return "people/user/editPerson";
        }
        peopleService.update(id, editPerson);
        return "redirect:/people/user/" + id;
    }

    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable("id") int id, Model model) {
        journalService.deleteByPersonId(id);
        peopleService.delete(id);
        return "redirect:/auth/registration";
    }
}
