package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.error.RecordNotFoundException;
import com.example.demo.model.Book;
import com.example.demo.model.BookCoverage;
import com.example.demo.repository.BookRepository;

/**
 * @author suyashsingh01
 * @see RecordNotFoundException
 * 
 */

@Service
public class BookServiceImpl implements BookService {

	 

	@Autowired
	BookRepository repository;

	@Value("${media.coverage.url}")
	private String mediaCoverageURL;

	private final ReentrantLock lock = new ReentrantLock();

	private static List<BookCoverage> coverage = null;

	@Override
	public List<Book> getAllBooks() {
		List<Book> bookList = repository.findAll();

		if (bookList.size() > 0) {
			return bookList;
		} else {
			return new ArrayList<Book>();
		}
	}

	/**
	 * @param long id to search book id
	 * @return book entity
	 * @throws RecordNotFoundException when no record exist
	 *                                 {@link RecordNotFoundException}
	 */
	@Override
	public Book getBookById(Long id) throws RecordNotFoundException {
		Optional<Book> book = repository.findById(id);

		if (book.isPresent()) {
			return book.get();
		} else {
			throw new RecordNotFoundException("No book record exist for given id:" + id);
		}
	}

	@Override
	public Book createOrUpdateBook(Book entity) {
		Optional<Book> book = repository.findById(entity.getId());

		if (book.isPresent()) {
			Book newEntity = book.get();
			newEntity.setTitle(entity.getTitle());
			newEntity.setAuthor(entity.getAuthor());
			newEntity.setPrice(entity.getPrice());
			newEntity.setCopies(entity.getCopies());
			newEntity = repository.save(newEntity);

			return newEntity;
		} else {
			entity = repository.save(entity);

			return entity;
		}
	}

	@Override
	public Book createOrUpdateBookByIsbn(Book entity) {
		Book book = repository.findFirstByIsbn(entity.getIsbn());
		// repository.findById(entity.getId());

		if (book != null) {
			Book newEntity = book;
			newEntity.setTitle(entity.getTitle());
			newEntity.setAuthor(entity.getAuthor());
			newEntity.setPrice(entity.getPrice());
			newEntity.setCopies(book.getCopies() == null ? 1 : book.getCopies() + 1);
			newEntity = repository.save(newEntity);

			return newEntity;
		} else {
			entity = repository.save(entity);

			return entity;
		}
	}

	@Override
	public List<Book> search(Book book) throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub

		Set<Book> set = new HashSet<>();
		CompletableFuture<List<Book>> isbn = getBooksByISBN(book);

		CompletableFuture<List<Book>> title = getBooksByTitle(book);

		CompletableFuture<List<Book>> author = getBooksByAuthor(book);

		CompletableFuture.allOf(isbn, title, author).join();

		set.addAll(isbn.get());
		set.addAll(title.get());
		set.addAll(author.get());

		return new ArrayList<>(set);
	}

	@Override
	@Async("asyncBookExecutor")
	public CompletableFuture<List<Book>> getBooksByISBN(Book book) {
		if (isNullOrEmpty(book.getIsbn())) {
			return CompletableFuture.completedFuture(new ArrayList<>());
		}
		return CompletableFuture.completedFuture(repository.findAllByIsbn(book.getIsbn().trim()));
	}

	@Override
	@Async("asyncBookExecutor")
	public CompletableFuture<List<Book>> getBooksByTitle(Book book) {
		if (isNullOrEmpty(book.getTitle())) {
			return CompletableFuture.completedFuture(new ArrayList<>());
		}
		return CompletableFuture.completedFuture(repository.findAllByTitleIgnoreCaseContaining(book.getTitle().trim()));
	}

	@Override
	@Async("asyncBookExecutor")
	public CompletableFuture<List<Book>> getBooksByAuthor(Book book) {
		if (isNullOrEmpty(book.getAuthor())) {
			return CompletableFuture.completedFuture(new ArrayList<>());
		}
		return CompletableFuture
				.completedFuture(repository.findAllByAuthorIgnoreCaseContaining(book.getAuthor().trim()));
	}

	public static boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;
		return true;
	}

	@Override
	public Book buyBookById(Long id) throws RecordNotFoundException {
		lock.lock();
		try {
			Book book = repository.findByIdAndCopiesGreaterThan(id, 0);

			if (book != null) {
				Book newEntity = book;

				newEntity.setCopies(book.getCopies() - 1);
				newEntity = repository.save(newEntity);

				return newEntity;
			} else {

				throw new RecordNotFoundException("No book available for given id: " + id);
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public Set<String> getBookCoverage(Book entiy)
			throws RecordNotFoundException, InterruptedException, ExecutionException {

		Book book = repository.findFirstByIsbn(entiy.getIsbn());

		if (book != null) {

			CompletableFuture<List<BookCoverage>> coverage = getBookCoverage();
			CompletableFuture.allOf(coverage).join();

			Set<String> title = coverage.get().parallelStream()
					.filter(f -> f.getTitle().contains(book.getTitle()) || f.getBody().contains(book.getTitle()))
					.map(m -> m.getTitle()).collect(Collectors.toSet());
			return title;
		} else {
			throw new RecordNotFoundException(
					"No media coverage available for given book has isbn: " + entiy.getIsbn());
		}

	}

	@Override
	@Cacheable(value = "coverage", key = "#title")
	@Async("asyncBookExecutor")
	public CompletableFuture<List<BookCoverage>> getBookCoverage() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		synchronized (BookServiceImpl.class) {
			if (coverage == null) {
				synchronized (BookServiceImpl.class) {
					RestTemplate restTemplate = new RestTemplate();
					ResponseEntity<List<BookCoverage>> resp = restTemplate.exchange(mediaCoverageURL, HttpMethod.GET,
							null, new ParameterizedTypeReference<List<BookCoverage>>() {
							});
					coverage = resp.getBody();
					return CompletableFuture.completedFuture(coverage);
				}

			}
			return CompletableFuture.completedFuture(coverage);
		}

	}

}
