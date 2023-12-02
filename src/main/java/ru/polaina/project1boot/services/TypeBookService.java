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

/*    public Optional<TypeBook> getTypeBookByBookId(int typeId) {
        return typeBookRepository.findById(typeId);
    }*/
}
