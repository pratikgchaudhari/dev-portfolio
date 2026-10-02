package dev.portfolio;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;

@Controller
public class PageController {
    private final BlogService blog;
    private final BooksService books;
    private final int postsPerPage;
    private final EngagementService engagement;
    private final ReaderIdentity readers;
    private final SeoService seo;
    @Value("${portfolio.name}")
    private String name;
    @Value("${portfolio.role}")
    private String role;
    @Value("${portfolio.bio}")
    private String bio;

    public PageController(BlogService blog, BooksService books,
                          EngagementService engagement, ReaderIdentity readers, SeoService seo,
                          @Value("${portfolio.posts-per-page:6}") int postsPerPage) {
        if (postsPerPage < 1) {
            throw new IllegalArgumentException("portfolio.posts-per-page must be at least 1");
        }
        this.blog = blog;
        this.books = books;
        this.postsPerPage = postsPerPage;
        this.engagement = engagement;
        this.readers = readers;
        this.seo = seo;
    }

    @ModelAttribute
    void profile(Model model) {
        model.addAttribute("name", name);
        model.addAttribute("role", role);
        model.addAttribute("bio", bio);
    }

    @GetMapping("/")
    String home(Model model) {
        model.addAttribute("page", "home");
        model.addAttribute("seo", seo.home());
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
        model.addAttribute("articleStats", engagement.statsFor(posts.subList(from, to)));
        model.addAttribute("currentPage", requestedPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("firstPost", totalPosts == 0 ? 0 : from + 1);
        model.addAttribute("lastPost", to);
        model.addAttribute("page", "blog");
        model.addAttribute("seo", seo.writing(requestedPage, totalPages, posts.subList(from, to)));
        return "blog";
    }

    @GetMapping("/about")
    ResponseEntity<Void> about() {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header("Location", "/#about").build();
    }

    @GetMapping("/books")
    String books(Model model) {
        model.addAttribute("shelf", books.shelf());
        model.addAttribute("page", "books");
        model.addAttribute("seo", seo.books());
        return "books";
    }

    @GetMapping("/blog/{slug}")
    String post(@PathVariable String slug, Model model, HttpServletRequest request, HttpServletResponse response) {
        var post = blog.post(slug);
        response.setHeader("Cache-Control", "private, no-store");
        var reader = readers.ensure(request, response);
        model.addAttribute("stats", engagement.stats(slug, reader));
        model.addAttribute("post", post);
        model.addAttribute("page", "blog");
        model.addAttribute("seo", seo.article(post));
        return "post";
    }
}
