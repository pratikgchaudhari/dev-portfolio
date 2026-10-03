package dev.portfolio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeoService {
    private final String origin;
    private final boolean indexingEnabled;
    private final String name;
    private final String role;
    private final ObjectMapper json;

    public SeoService(@Value("${portfolio.site-url:}") String siteUrl,
                      @Value("${portfolio.indexing-enabled:true}") boolean indexingEnabled,
                      @Value("${portfolio.name}") String name,
                      @Value("${portfolio.role}") String role, ObjectMapper json) {
        this.origin = validateOrigin(siteUrl);
        this.indexingEnabled = indexingEnabled;
        this.name = name;
        this.role = role;
        this.json = json;
    }

    static String validateOrigin(String value) {
        String trimmed = value.strip();
        if (trimmed.isEmpty()) return "";
        try {
            URI uri = URI.create(trimmed);
            if (!("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                    || uri.getHost() == null || uri.getRawUserInfo() != null
                    || uri.getRawQuery() != null || uri.getRawFragment() != null
                    || !(uri.getRawPath().isEmpty() || uri.getRawPath().equals("/"))) {
                throw new IllegalArgumentException();
            }
            return trimmed.replaceFirst("/$", "");
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("SITE_URL must be an http(s) origin without a path, credentials, query, or fragment", exception);
        }
    }

    public boolean isIndexable() {
        return indexingEnabled && !origin.isEmpty();
    }

    public String absoluteUrl(String path) {
        return origin.isEmpty() ? null : origin + path;
    }

    public String blogPath(int page) {
        return page == 1 ? "/blog" : "/blog?page=" + page;
    }

    public String blogPath(int page, String query) {
        if (query.isEmpty()) return blogPath(page);
        return "/blog?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + (page > 1 ? "&page=" + page : "");
    }

    public Metadata search(String query, int currentPage) {
        String title = "Search: " + query + (currentPage > 1 ? " — Page " + currentPage : "") + " — " + name;
        return new Metadata(title, "Search results for “" + query + "” in " + name + "'s articles.",
                absoluteUrl(blogPath(currentPage, query)), "noindex, follow", "website",
                absoluteUrl("/images/social-card.png"), absoluteUrl("/#about"), null, null);
    }

    public Metadata home() {
        String title = name + " — " + role;
        String description = name + "'s portfolio: Java and Spring Boot engineering, experience across backend systems and the web, and practical software development articles.";
        var profile = page("ProfilePage", "/", title, description);
        profile.put("mainEntity", person());
        return metadata(title, description, "/", "website", null, profile);
    }

    public Metadata writing(int currentPage, int totalPages, List<Post> posts) {
        String title = (currentPage > 1 ? "Writing — Page " + currentPage : "Writing") + " — " + name;
        String description = "Software engineering articles by " + name + ": Java, Spring Boot, React, Vue.js and Linux."
                + (currentPage > 1 ? " Page " + currentPage + " of " + totalPages + "." : "");
        var collection = page("CollectionPage", blogPath(currentPage), title, description);
        var entries = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            entries.add(object("@type", "ListItem", "position", i + 1, "name", post.title(),
                    "url", absoluteUrl("/blog/" + post.slug())));
        }
        collection.put("mainEntity", object("@type", "ItemList", "itemListElement", entries));
        return metadata(title, description, blogPath(currentPage), "website", null, collection);
    }

    public Metadata books() {
        String title = "Bookshelf — " + name;
        String description = "Explore " + name + "'s bookshelf: books currently in progress and finished reads on software, business, technology and fiction.";
        return metadata(title, description, "/books", "website", null,
                page("CollectionPage", "/books", title, description));
    }

    public Metadata article(Post post) {
        String path = "/blog/" + post.slug();
        var article = page("BlogPosting", path, post.title(), post.summary());
        article.put("headline", post.title());
        article.put("datePublished", post.date().toString());
        article.put("articleSection", post.tag());
        article.put("author", person());
        article.put("mainEntityOfPage", absoluteUrl(path));
        return metadata(post.title() + " — " + name, post.summary(), path, "article", post.date(), article);
    }

    private Metadata metadata(String title, String description, String path, String type,
                              LocalDate published, Map<String, Object> page) {
        String data = null;
        if (!origin.isEmpty()) {
            var website = object("@type", "WebSite", "@id", absoluteUrl("/#website"),
                    "url", absoluteUrl("/"), "name", name, "inLanguage", "en");
            data = scriptJson(object("@context", "https://schema.org", "@graph", List.of(website, page)));
        }
        return new Metadata(title, description, absoluteUrl(path),
                isIndexable() ? "index, follow, max-image-preview:large" : "noindex, follow",
                type, absoluteUrl("/images/social-card.png"), absoluteUrl("/#about"), published, data);
    }

    private Map<String, Object> page(String type, String path, String title, String description) {
        var page = object("@type", type, "@id", absoluteUrl(path + "#webpage"), "url", absoluteUrl(path),
                "name", title, "description", description, "inLanguage", "en");
        page.put("isPartOf", object("@id", absoluteUrl("/#website")));
        return page;
    }

    private Map<String, Object> person() {
        return object("@type", "Person", "@id", absoluteUrl("/#person"), "name", name,
                "url", absoluteUrl("/#about"), "jobTitle", role,
                "sameAs", List.of("https://x.com/pratikc_89", "https://github.com/pratikgchaudhari",
                        "https://www.linkedin.com/in/pratik-chaudhari-71ba37a1/"));
    }

    private String scriptJson(Object value) {
        try {
            // Script text needs JSON escapes: metadata must never terminate the script element.
            return json.writeValueAsString(value).replace("<", "\\u003c").replace(">", "\\u003e")
                    .replace("&", "\\u0026").replace("\u2028", "\\u2028").replace("\u2029", "\\u2029");
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Cannot serialize SEO metadata", exception);
        }
    }

    private static Map<String, Object> object(Object... pairs) {
        var result = new LinkedHashMap<String, Object>();
        for (int i = 0; i < pairs.length; i += 2) result.put((String) pairs[i], pairs[i + 1]);
        return result;
    }

    public record Metadata(String title, String description, String canonicalUrl, String robots,
                           String type, String imageUrl, String authorUrl, LocalDate published,
                           String structuredData) {}
}
