package dev.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class BooksService {
    public record Book(String title, String url) {
    }

    public record Shelf(List<Book> currentlyReading, List<Book> finished) {
    }

    private final ObjectMapper mapper;
    private final Path file;

    public BooksService(ObjectMapper mapper,
                        @Value("${portfolio.books-file:content/books.json}") String file) {
        this.mapper = mapper;
        this.file = Path.of(file);
    }

    public Shelf shelf() {
        try {
            return mapper.readValue(file.toFile(), Shelf.class);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read bookshelf: " + file.toAbsolutePath(), e);
        }
    }
}
