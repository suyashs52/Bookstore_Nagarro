package com.example.demo.web;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BookDTO;
import com.example.demo.error.RecordNotFoundException;
import com.example.demo.model.Book;
import com.example.demo.service.BookService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("api/v1/books")
@Api(value = "Book Rest API", description = "Create , Update , Delete, Search, Media Coverage of Book")
public class BookController {

	@Autowired
	BookService service;

	
	@ApiOperation(value = "View a list of available book", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),

			@ApiResponse(code = 415, message = "Media type not supported"),
			@ApiResponse(code = 500, message = "Server Error") })

	@GetMapping
	public ResponseEntity<List<BookDTO>> getAllBooks() {
		List<BookDTO> list = service.getAllBooks().stream().map(b -> convertToDTO(b)).collect(Collectors.toList());

		return new ResponseEntity<List<BookDTO>>(list, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "View a book by Id", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved a book"),

			@ApiResponse(code = 400, message = "Bad Request - Invalid Arguments, Type Mismatch,Argument Pass is invalid"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 405, message = "The resource you were trying to reach by invalid method"),
			@ApiResponse(code = 415, message = "Media type not supported"),
			@ApiResponse(code = 500, message = "Server Error") })
	@GetMapping("/{id}")
	public ResponseEntity<BookDTO> getBookById(
			@ApiParam(value = "Book id from which book object will retrieve", required = true) @PathVariable("id") Long id)
			throws RecordNotFoundException {
		BookDTO entity = convertToDTO(service.getBookById(id));

		return new ResponseEntity<BookDTO>(entity, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "Add/Update a book by id, throw 404 for invalid request")
	@PutMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<Book> createOrUpdateBook(
			@ApiParam(value = " Id to update book object", required = true) @PathVariable("id") long id,
			@ApiParam(value = "DTO object", required = true) @Valid @RequestBody BookDTO bookDTO)
			throws RecordNotFoundException {
		Book book = convertToEntity(bookDTO);
		book.setId(id);
		Book updated = service.createOrUpdateBook(book);
		return new ResponseEntity<Book>(updated, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "Add/Update a book by isbn, if duplicated copies will increase by 1")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Book> createOrUpdateBooByIsbn(
			@ApiParam(value = "Book object store in database table", required = true) @Valid @RequestBody BookDTO bookDTO)
			throws RecordNotFoundException {
		Book updated = service.createOrUpdateBookByIsbn(convertToEntity(bookDTO));
		return new ResponseEntity<Book>(updated, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "Search book by isbn/title/author, unique record will be displayed using executer service")
	@PostMapping("/search")
	public ResponseEntity<List<Book>> search(
			@ApiParam(value = "Book object to search for book", required = true) @RequestBody Book book)
			throws RecordNotFoundException, InterruptedException, ExecutionException {
		List<Book> search = service.search(book);
		return new ResponseEntity<List<Book>>(search, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "Purchase a book by id")

	@GetMapping("/buy/{id}")
	public ResponseEntity<Book> buyBookById(
			@ApiParam(value = "Book id to get book", required = true) @PathVariable("id") Long id)
			throws RecordNotFoundException {
		Book entity = service.buyBookById(id);

		return new ResponseEntity<Book>(entity, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "Check media coverage , url in property file using paralle stream, cache")

	@PostMapping("/coverage")
	public ResponseEntity<Set<String>> bookCoverage(
			@ApiParam(value = "Right now with isbn of book but can be scale in future", required = true) @RequestBody Book book)
			throws RecordNotFoundException, InterruptedException, ExecutionException {
		Set<String> titles = service.getBookCoverage(book);

		return new ResponseEntity<Set<String>>(titles, new HttpHeaders(), HttpStatus.OK);
	}

	/*
	 * @DeleteMapping("/{id}") public HttpStatus deleteBookById(@PathVariable("id")
	 * Long id) throws RecordNotFoundException { service.deleteBookById(id); return
	 * HttpStatus.FORBIDDEN; }
	 */
	@Autowired
	private ModelMapper modelMapper;

	private BookDTO convertToDTO(Book book) {
		BookDTO bookdto = modelMapper.map(book, BookDTO.class);
		return bookdto;
	}

	private Book convertToEntity(BookDTO bookDTO) {
		return modelMapper.map(bookDTO, Book.class);
	}
}
