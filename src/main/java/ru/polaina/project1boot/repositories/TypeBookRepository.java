package ru.polaina.project1boot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.polaina.project1boot.models.TypeBook;

import java.util.Optional;

@Repository
public interface TypeBookRepository extends JpaRepository<TypeBook, Integer> {
    Optional<TypeBook> findByTypeName(String name);
}
