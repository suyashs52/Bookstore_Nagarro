package com.example.demo.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel(description = "All details about the Book. ")
public class BookDTO {

	@ApiModelProperty(notes = "ISBN , should be valid format ISBN 10,13")
	@NotNull
	@Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$")
	private String isbn;
	@ApiModelProperty(notes = "Book title must not be blank and max size is 100")
	@NotNull
	@NotBlank(message = "Title  must not be blank!")
	@Size(min = 1, max = 100, message = "Book Title '${validatedValue}' must be between {min} and {max} characters long")
	private String title;

	@NotNull
	@ApiModelProperty(notes = "Author title must not be blank and max size is 100")
	@Size(min = 1, max = 100, message = "Book Author '${validatedValue}' must be between {min} and {max} characters long")
	private String author;

	@NotNull
	@ApiModelProperty(notes = "Book price must not be blank and greater than 0")
	@Min(value = 1, message = "There must be at least {value} value of price")
	private Integer price;

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

}
