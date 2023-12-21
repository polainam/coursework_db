package ru.polaina.project1boot.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.polaina.project1boot.models.TypeBook;
import ru.polaina.project1boot.repositories.TypeBookRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TypeBookService {
    private final TypeBookRepository typeBookRepository;

    public TypeBookService(TypeBookRepository typeBookRepository) {
        this.typeBookRepository = typeBookRepository;
    }

    public List<TypeBook> findAll() {
        return typeBookRepository.findAll();
    }

    public Optional<TypeBook> findByName(String name) {
        return typeBookRepository.findByTypeName(name);
    }

    @Transactional
    public void save(TypeBook typeBook) {
        typeBookRepository.save(typeBook);
    }

    public Optional<TypeBook> findById(int typeId) {
        return typeBookRepository.findById(typeId);
    }

    public boolean isNewNameTheSame(int id, TypeBook typeBookForEdit) {
        TypeBook oldTypeBook = typeBookRepository.findById(id).get();
        return oldTypeBook.getTypeName().equals(typeBookForEdit.getTypeName());
    }

    @Transactional
    public void update(int id, TypeBook typeBookForEdit) {
        typeBookForEdit.setTypeId(id);
        typeBookRepository.save(typeBookForEdit);
    }

    @Transactional
    public void delete(int id) {
        typeBookRepository.deleteById(id);
    }
}
