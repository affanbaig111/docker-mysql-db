package com.onlinelearning.dockerlearning;

import com.onlinelearning.dockerlearning.dao.BookRepo;
import com.onlinelearning.dockerlearning.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RequestMapping("/book")
@RestController
public class SpringbootMongodbDockerApplication {

    @Autowired
    private BookRepo bookRepo;
    public static void main(String[] args) {
        SpringApplication.run(SpringbootMongodbDockerApplication.class, args);
    }

    @PostMapping
    public Book saveBook(@RequestBody Book book) {
        return  bookRepo.save(book);
    }

    @GetMapping
    public List<Book> getBooks() {
        return bookRepo.findAll();
    }
}
