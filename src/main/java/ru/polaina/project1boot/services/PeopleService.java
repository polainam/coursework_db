package ru.polaina.project1boot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import ru.polaina.project1boot.models.Journal;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.repositories.PeopleRepository;
import ru.polaina.project1boot.security.PersonDetails;
import ru.polaina.project1boot.util.PersonValidator;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService implements UserDetailsService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    @Transactional
    public void save(Person person) {
        String encodedPassword = passwordEncoder.encode(person.getPassword());
        person.setPassword(encodedPassword);
        person.setRole("ROLE_USER");
        peopleRepository.save(person.getUserName(), person.getFirstName(), person.getLastName(),
                person.getPartherName(), person.getPassportSeria(), person.getPassportNum(), person.getPassword(), person.getRole());
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        String encodedPassword = passwordEncoder.encode(updatedPerson.getPassword());
        updatedPerson.setPassword(encodedPassword);
        peopleRepository.update(id, updatedPerson.getUserName(), updatedPerson.getFirstName(), updatedPerson.getLastName(), updatedPerson.getPartherName(),
                 updatedPerson.getPassportSeria(), updatedPerson.getPassportNum(), updatedPerson.getPassword());
    }

    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElse(null);
    }

    public Optional<Person> findByName(String name) {
        return peopleRepository.findByUserName(name);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deletePerson(id);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByUserName(s);
        if (person.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new PersonDetails(person.get());
    }

    public List<Person> findAllUsers(String role) {
        return peopleRepository.findAllByRole(role);
    }

/*    public List<Person> findAll(Integer page, Integer peoplePerPage) {
        return peopleRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }*/

    public Page<Person> findUsersWithRole(Integer page, Integer peoplePerPage) {
        String roleName = "ROLE_USER";
        Pageable pageable = PageRequest.of(page, peoplePerPage);

        return peopleRepository.findByRole(roleName, pageable);
    }

    public List<Person> findByTitleIsStartingWith(String query) {
        return peopleRepository.findByUserNameIsStartingWith(query);
    }
    public Optional<Person> findByPassportNum(String passportNum) {
        return peopleRepository.findByPassportNum(passportNum);
    }

    public boolean isNewUsernameTheSame(int id, Person editPerson) {
        Person oldPerson = findOne(id);
        return oldPerson.getUserName().equals(editPerson.getUserName());
    }

    public boolean isNewPassportNumTheSame(int id, Person editPerson) {
        Person oldPerson = findOne(id);
        return oldPerson.getPassportNum().equals(editPerson.getPassportNum());
    }

    public Integer getNumberOfBooksForPerson(int id) {
        return peopleRepository.getNumberOfBooksForPerson(id);
    }
}
