package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

	Book findFirstByIsbn(String isbn);

	List<Book> findAllByIsbn(String isbn);

	List<Book> findAllByAuthorIgnoreCaseContaining(String author);

	List<Book> findAllByTitleIgnoreCaseContaining(String title);

	Book findByIdAndCopiesGreaterThan(Long id, int book);

	List<Book> findAllByIsbnIgnoreCaseContaining(String isbn);

}
