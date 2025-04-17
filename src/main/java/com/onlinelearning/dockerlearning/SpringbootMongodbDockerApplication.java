package com.onlinelearning.dockerlearning;

import com.onlinelearning.dockerlearning.dao.BookRepo;
import com.onlinelearning.dockerlearning.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@RequestMapping("/book")
@RestController
public class SpringbootMongodbDockerApplication {

    @Autowired
    private BookRepo bookRepo;
    public static void main(String[] args) {
        SpringApplication.run(SpringbootMongodbDockerApplication.class, args);
    }

    // Create a new book
    @PostMapping
    public Book saveBook(@RequestBody Book book) {
        return bookRepo.save(book);
    }

    // Get all books
    @GetMapping
    public List<Book> getBooks() {
        return bookRepo.findAll();
    }

    // Get a single book by ID
    @GetMapping("/{id}")
    public  Book getBookById(@PathVariable Integer id) {
        Book book;
        book = bookRepo.findById(id).orElse(null);
         System.out.println(book);


        return book;

    }

    // Update an entire book
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Integer id, @RequestBody Book updatedBook) {
        return bookRepo.findById(id)
                .map(book -> {
                    book.setBookName(updatedBook.getBookName());
                    book.setAuthorName(updatedBook.getAuthorName());
                    return ResponseEntity.ok().body(bookRepo.save(book));
                }).orElse(ResponseEntity.notFound().build());
    }

    // Partially update a book
    @PatchMapping("/{id}")
    public ResponseEntity<Book> patchBook(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        return bookRepo.findById(id)
                .map(book -> {
                    if (updates.containsKey("bookName")) {
                        book.setBookName((String) updates.get("bookName"));
                    }
                    if (updates.containsKey("authorName")) {
                        book.setAuthorName((String) updates.get("authorName"));
                    }
                    return ResponseEntity.ok().body(bookRepo.save(book));
                }).orElse(ResponseEntity.notFound().build());
    }

    // Delete a book by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        if (bookRepo.existsById(id)) {
            bookRepo.deleteById(id);
            return ResponseEntity.noContent().build();

        }
        return ResponseEntity.notFound().build();
    }
}
