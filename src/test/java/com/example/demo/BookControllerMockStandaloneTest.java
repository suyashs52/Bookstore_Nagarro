package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import com.example.demo.dto.BookDTO;
import com.example.demo.error.RecordNotFoundException;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.BookService;
import com.example.demo.service.BookServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class BookControllerMockStandaloneTest {

	private static BookServiceImpl bookService;

	private static BookRepository bookRepository;

	private static Book book;

	@BeforeClass
	public static void init() {
		bookRepository = mock(BookRepository.class);
		bookService = mock(BookServiceImpl.class);
		book = new Book("ISBN 978-0-596-52068-8", "blind are welcome", "author2");
		Book newbook = new Book("ISBN 978-0-596-52068-8", "blind are welcome", "author2");
		newbook.setCopies(5);
		when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
		when(bookService.createOrUpdateBookByIsbn(book)).thenReturn(newbook);
		when(bookService.createOrUpdateBook(book)).thenReturn(newbook);
		
		
	}

	@Test
	public void getBookByIdRepoTest_IfExists() throws RecordNotFoundException {
		// given
		Optional<Book> book = bookRepository.findById(2L);
		// bookService.getBookById(2L);
		assertNotNull(book.get());
		assertEquals("ISBN 978-0-596-52068-8", book.get().getIsbn());
		assertEquals("author2", book.get().getAuthor());

	}

	@Test
	public void getBookByIdRepoTest_IfNotExists() throws RecordNotFoundException {
		// given
		Optional<Book> book = bookRepository.findById(3L);
		// bookService.getBookById(2L);
		assertFalse(book.isPresent());

	}

	/*
	 * @Rule public ExpectedException thrown = ExpectedException.none();
	 */
	@Test
	public void whenExceptionThrown_thenExpectationSatisfied() throws RecordNotFoundException {

		try {
			bookService.getBookById(10L);

		} catch (RecordNotFoundException e) {
			assertEquals(e.getMessage(), "No book record exist for given id:10");
		}
	}

	@Test
	public void createOrUpdateBook_existingBookPut() {
		book.setCopies(5);
		book.setTitle("provident");
		Book entity = bookService.createOrUpdateBook(book);
		assertEquals(book.getIsbn(), entity.getIsbn());
		assertEquals(book.getId(), entity.getId());
		assertEquals(book.getCopies(), entity.getCopies());
	}

	@Test
	public void createOrUpdateBook_existingBookByIsbn() {

		Book entity = bookService.createOrUpdateBookByIsbn(book);
		assertEquals(book.getIsbn(), entity.getIsbn());
		assertTrue(entity.getCopies() > 0);
	}
 
	
	@Test
	public void convertBookEntityToDTO() {
		ModelMapper mm=new ModelMapper();
		Book b=new Book();
		b.setTitle("test");
		BookDTO bt=mm.map(b, BookDTO.class);
		assertEquals(bt.getTitle(), b.getTitle());
	}

}
