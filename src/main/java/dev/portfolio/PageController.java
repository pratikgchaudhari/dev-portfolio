package dev.portfolio;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;

@Controller
public class PageController {
    private final BlogService blog;
    private final BooksService books;
    @Value("${portfolio.name}")
    private String name;
    @Value("${portfolio.role}")
    private String role;
    @Value("${portfolio.bio}")
    private String bio;

    public PageController(BlogService blog, BooksService books) {
        this.blog = blog;
        this.books = books;
    }

    @ModelAttribute
    void profile(Model model) {
        model.addAttribute("name", name);
        model.addAttribute("role", role);
        model.addAttribute("bio", bio);
    }

    @GetMapping("/")
    String home(Model model) {
        model.addAttribute("posts", blog.posts().stream().limit(3).toList());
        model.addAttribute("page", "home");
        return "home";
    }

    @GetMapping("/blog")
    String blog(Model model) {
        model.addAttribute("posts", blog.posts());
        model.addAttribute("page", "blog");
        return "blog";
    }

    @GetMapping("/about")
    String about() {
        return "redirect:/#about";
    }

    @GetMapping("/books")
    String books(Model model) {
        model.addAttribute("shelf", books.shelf());
        model.addAttribute("page", "books");
        return "books";
    }

    @GetMapping("/blog/{slug}")
    String post(@PathVariable String slug, Model model) {
        var post = blog.posts().stream().filter(p -> p.slug().equals(slug)).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("post", post);
        model.addAttribute("page", "blog");
        return "post";
    }
}
