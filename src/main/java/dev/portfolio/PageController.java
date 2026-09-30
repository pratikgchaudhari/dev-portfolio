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
    private final int postsPerPage;
    @Value("${portfolio.name}")
    private String name;
    @Value("${portfolio.role}")
    private String role;
    @Value("${portfolio.bio}")
    private String bio;

    public PageController(BlogService blog, BooksService books,
                          @Value("${portfolio.posts-per-page:6}") int postsPerPage) {
        if (postsPerPage < 1) {
            throw new IllegalArgumentException("portfolio.posts-per-page must be at least 1");
        }
        this.blog = blog;
        this.books = books;
        this.postsPerPage = postsPerPage;
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
    String blog(@RequestParam(name = "page", defaultValue = "1") int requestedPage, Model model) {
        if (requestedPage < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        var posts = blog.posts();
        int totalPosts = posts.size();
        int totalPages = totalPosts == 0 ? 1 : 1 + (totalPosts - 1) / postsPerPage;
        if (requestedPage > totalPages) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        int from = (requestedPage - 1) * postsPerPage;
        int to = from + Math.min(postsPerPage, totalPosts - from);
        model.addAttribute("posts", posts.subList(from, to));
        model.addAttribute("currentPage", requestedPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("firstPost", totalPosts == 0 ? 0 : from + 1);
        model.addAttribute("lastPost", to);
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
