package com.example.demo.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.example.demo.error.RecordNotFoundException;
import com.example.demo.model.Book;
import com.example.demo.model.BookCoverage;

public interface BookService {

	public List<Book> getAllBooks();

	public Book getBookById(Long id) throws RecordNotFoundException;

	public Book createOrUpdateBook(Book entity);

	public Book createOrUpdateBookByIsbn(Book entity);

	public List<Book> search(Book book) throws InterruptedException, ExecutionException;

	public CompletableFuture<List<Book>> getBooksByISBN(Book book);

	public CompletableFuture<List<Book>> getBooksByTitle(Book book);

	public CompletableFuture<List<Book>> getBooksByAuthor(Book book);

	public Book buyBookById(Long id) throws RecordNotFoundException;

	public Set<String> getBookCoverage(Book entiy)
			throws RecordNotFoundException, InterruptedException, ExecutionException;

	public CompletableFuture<List<BookCoverage>> getBookCoverage();

}
