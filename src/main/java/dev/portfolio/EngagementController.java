package dev.portfolio;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/articles/{slug}")
public class EngagementController {
    private final BlogService blog;
    private final EngagementService engagement;
    private final ReaderIdentity readers;

    public EngagementController(BlogService blog, EngagementService engagement, ReaderIdentity readers) {
        this.blog = blog;
        this.engagement = engagement;
        this.readers = readers;
    }

    @GetMapping("/stats")
    ResponseEntity<ArticleStats> stats(@PathVariable String slug, HttpServletRequest request) {
        blog.post(slug);
        return result(engagement.stats(slug, readers.find(request)));
    }

    @PostMapping("/views")
    ResponseEntity<ArticleStats> view(@PathVariable String slug, HttpServletRequest request) {
        blog.post(slug);
        return result(engagement.recordView(slug, requireReader(request)));
    }

    @PutMapping(value = "/like", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ArticleStats> like(@PathVariable String slug, @RequestBody LikeRequest body, HttpServletRequest request) {
        blog.post(slug);
        UUID reader = requireReader(request);
        if (body.liked() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        return result(engagement.setLiked(slug, reader, body.liked()));
    }

    private UUID requireReader(HttpServletRequest request) {
        // A required custom header prevents cross-origin form submissions; CORS is not enabled.
        if (!"1".equals(request.getHeader("X-Portfolio-Request"))
                || "cross-site".equals(request.getHeader("Sec-Fetch-Site"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        UUID reader = readers.find(request);
        if (reader == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cookies are required");
        return reader;
    }

    private ResponseEntity<ArticleStats> result(ArticleStats stats) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(stats);
    }

    record LikeRequest(Boolean liked) {
    }
}
