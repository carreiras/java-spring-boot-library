package com.ewecarreira.library.api.resource.controller;

import com.ewecarreira.library.api.resource.dto.BookDTO;
import com.ewecarreira.library.exception.BusinessException;
import com.ewecarreira.library.model.entity.Book;
import com.ewecarreira.library.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class BookControllerTest {

        static String BOOK_API = "/api/books";

        @Autowired
        MockMvc mockMvc;

        @MockBean
        BookService bookService;

        @Test
        @DisplayName("Deve criar um livro com sucesso")
        public void createBookTest() throws Exception {
                BookDTO bookDTO = createNewBook();

                Book savedBook = Book.builder()
                                .id(1L)
                                .title("Titulo")
                                .autor("Autor")
                                .isbn("123456789")
                                .build();

                BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(savedBook);

                String json = new ObjectMapper().writeValueAsString(bookDTO);

                MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(json);

                mockMvc.perform(request)
                                .andExpect(MockMvcResultMatchers.status().isCreated())
                                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
                                .andExpect(MockMvcResultMatchers.jsonPath("title").value(bookDTO.getTitle()))
                                .andExpect(MockMvcResultMatchers.jsonPath("autor").value(bookDTO.getAutor()))
                                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(bookDTO.getIsbn()));
        }

        @Test
        @DisplayName("Deve lançar um erro de validação quando não houver dados suficientes para a criação do livro")
        public void createInvalidBookTest() throws Exception {
                String json = new ObjectMapper().writeValueAsString(new BookDTO());

                MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(json);

                mockMvc.perform(request)
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
        }

        @Test
        @DisplayName("Deve lançar um erro ao tentar cadastrar um livro com isbn já tulizado por outro")
        public void createBookWithDuplicatedIsbn() throws Exception {
                BookDTO bookDTO = createNewBook();

                String json = new ObjectMapper().writeValueAsString(bookDTO);
                String errorMessage = "Isbn já cadastrado.";
                BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                                .willThrow(new BusinessException(errorMessage));

                MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(json);

                mockMvc.perform(request)
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(errorMessage));
        }

        private BookDTO createNewBook() {
                return BookDTO.builder()
                                .title("Titulo")
                                .autor("Autor")
                                .isbn("123456789")
                                .build();
        }
}