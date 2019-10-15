package com.example.demo;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.demo.model.Book;
import com.example.demo.service.BookServiceImpl;
import com.example.demo.web.BookController;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author suyashsingh01
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = BookController.class)
public class BookControllerTest {
	private static final Logger log = LoggerFactory.getLogger(BookControllerTest.class);

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private BookServiceImpl bookService;
	@Autowired
	private ObjectMapper objectMapper;

	 
	
	@Before
	public void setup() {
		Mockito.when(bookService.getAllBooks()).thenReturn(getAllBookMockReturn());
	}

	List<Book> getAllBookMockReturn() {
		Book b = new Book();
		b.setId(1L);
		b.setIsbn("ISBN 978-0-596-52068-7");
		b.setTitle("blind are welcome option to find");
		b.setAuthor("author1");
		b.setPrice(1);
		b.setCopies(0);
		List<Book> bl = new ArrayList<>();// ('ISBN 978-0-596-52068-7', 'blind are welcome option to find',
											// 'author1',1,DEFAULT),
		bl.add(b);
		return bl;

	}

	@Test
	public void get_allbooks() throws Exception {
		final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
				MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
		/*
		 * mockMvc.perform(post("/api/v1/books")
		 * .contentType(APPLICATION_JSON_UTF8)).andExpect(status().isOk());
		 */
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/books").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].isbn", is("ISBN 978-0-596-52068-7")));

	}
}
