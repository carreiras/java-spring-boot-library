package com.ewecarreira.library.api.resource.controller;

import java.util.List;

import javax.validation.Valid;

import com.ewecarreira.library.api.exception.ApiErrors;
import com.ewecarreira.library.api.resource.dto.BookDTO;
import com.ewecarreira.library.model.entity.Book;
import com.ewecarreira.library.service.BookService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    BookService bookService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO request) {
        Book book = modelMapper.map(request, Book.class);
        Book bookSaved = bookService.save(book);
        BookDTO bookDTO = modelMapper.map(bookSaved, BookDTO.class);
        
        return bookDTO;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        
        return new ApiErrors(bindingResult);        
    }
}
