package dev.portfolio;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticlePreviewController {
    private final BlogService blog;
    private final ArticlePreviewService previews;

    public ArticlePreviewController(BlogService blog, ArticlePreviewService previews) {
        this.blog = blog;
        this.previews = previews;
    }

    @GetMapping(value = "/images/articles/{slug}.png", produces = MediaType.IMAGE_PNG_VALUE)
    ResponseEntity<byte[]> image(@PathVariable String slug, HttpServletRequest request) {
        // Check publication on every request, even when the rendered image is cached.
        var image = previews.image(blog.post(slug));
        var response = ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.noCache().cachePublic()).eTag(image.etag())
                .contentLength(image.png().length);
        return request.getMethod().equals("HEAD") ? response.build() : response.body(image.png());
    }
}
