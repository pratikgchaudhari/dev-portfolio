package dev.portfolio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.StringWriter;
import java.util.ArrayList;

@RestController
public class CrawlController {
    private final BlogService blog;
    private final SeoService seo;
    private final int postsPerPage;

    public CrawlController(BlogService blog, SeoService seo,
                           @Value("${portfolio.posts-per-page:6}") int postsPerPage) {
        this.blog = blog;
        this.seo = seo;
        this.postsPerPage = postsPerPage;
    }

    @GetMapping(value = "/robots.txt", produces = "text/plain;charset=UTF-8")
    String robots() {
        // Allow HTML crawling so preview pages' noindex metadata can be read.
        return "User-agent: *\nAllow: /\nDisallow: /api/\n"
                + (seo.isIndexable() ? "\nSitemap: " + seo.absoluteUrl("/sitemap.xml") + "\n" : "");
    }

    @GetMapping(value = "/sitemap.xml", produces = "application/xml;charset=UTF-8")
    ResponseEntity<String> sitemap() throws XMLStreamException {
        if (!seo.isIndexable()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        var posts = blog.posts();
        var paths = new ArrayList<String>();
        paths.add("/");
        paths.add("/books");
        int pageCount = posts.isEmpty() ? 1 : 1 + (posts.size() - 1) / postsPerPage;
        for (int page = 1; page <= pageCount; page++) paths.add(seo.blogPath(page));
        for (var post : posts) paths.add("/blog/" + post.slug());

        var text = new StringWriter();
        var xml = XMLOutputFactory.newFactory().createXMLStreamWriter(text);
        xml.writeStartDocument("UTF-8", "1.0");
        xml.writeStartElement("urlset");
        xml.writeDefaultNamespace("http://www.sitemaps.org/schemas/sitemap/0.9");
        for (String path : paths) {
            xml.writeStartElement("url");
            xml.writeStartElement("loc");
            xml.writeCharacters(seo.absoluteUrl(path));
            xml.writeEndElement();
            xml.writeEndElement();
        }
        xml.writeEndElement();
        xml.writeEndDocument();
        xml.close();
        // Do not invent modification dates from deployment or filesystem timestamps.
        return ResponseEntity.ok().header("Cache-Control", "no-cache").body(text.toString());
    }
}
