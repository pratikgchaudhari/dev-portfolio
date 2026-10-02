package dev.portfolio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"portfolio.site-url=https://notnullpratik.dev/", "portfolio.indexing-enabled=true",
        "portfolio.posts-per-page=2"})
@AutoConfigureMockMvc
class SeoTests extends DatabaseTestSupport {
    @TempDir static Path postsDirectory;
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @DynamicPropertySource
    static void contentDirectory(DynamicPropertyRegistry registry) {
        registry.add("portfolio.content-dir", () -> postsDirectory.toString());
    }

    @BeforeEach
    void seedPosts() throws Exception {
        try (var files = Files.list(postsDirectory)) {
            for (var file : files.toList()) Files.delete(file);
        }
        writePost("first", "First article", "2020-01-01", false);
        writePost("second", "Second article", "2020-01-02", false);
        writePost("third", "Third article", "2020-01-03", false);
        writePost("draft", "Unpublished draft", "2020-01-04", true);
        writePost("future", "Scheduled post", "2999-01-01", false);
    }

    @Test
    void pagesHaveCanonicalMetadataAndSocialPreviews() throws Exception {
        for (String path : List.of("/", "/books", "/blog", "/blog/first")) {
            String html = html(path);
            assertThat(html).containsOnlyOnce("rel=\"canonical\"")
                    .contains("href=\"https://notnullpratik.dev" + path + "\"",
                            "name=\"robots\" content=\"index, follow, max-image-preview:large\"",
                            "property=\"og:url\" content=\"https://notnullpratik.dev" + path + "\"",
                            "name=\"twitter:card\" content=\"summary_large_image\"",
                            "https://notnullpratik.dev/images/social-card.png");
            assertThat(structuredData(html).get("@graph").get(0).get("@type").asText()).isEqualTo("WebSite");
        }
        var profile = structuredData(html("/")).get("@graph").get(1);
        assertThat(profile.get("@type").asText()).isEqualTo("ProfilePage");
        assertThat(profile.get("mainEntity").get("name").asText()).isEqualTo("Pratik Chaudhari");
        assertThat(profile.get("mainEntity").get("sameAs").size()).isEqualTo(3);
    }

    @Test
    void paginationUsesItsOwnCanonicalAndDiscardsTrackingParametersAndHostHeaders() throws Exception {
        var response = mvc.perform(get("/blog").param("page", "2").param("utm_source", "test")
                        .header("Host", "untrusted.example").header("X-Forwarded-Host", "untrusted.example"))
                .andExpect(status().isOk()).andReturn().getResponse();
        String html = response.getContentAsString(StandardCharsets.UTF_8);
        assertThat(html).contains("rel=\"canonical\" href=\"https://notnullpratik.dev/blog?page=2\"",
                        "Writing — Page 2 — Pratik Chaudhari")
                .doesNotContain("untrusted.example", "utm_source");
        assertThat(html("/blog?page=1")).contains("rel=\"canonical\" href=\"https://notnullpratik.dev/blog\"");
        var items = structuredData(html).get("@graph").get(1).get("mainEntity").get("itemListElement");
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0).get("url").asText()).isEqualTo("https://notnullpratik.dev/blog/first");
    }

    @Test
    void articleSchemaUsesActualMetadataAndEscapesScriptTerminators() throws Exception {
        String title = "Java </script><script>alert(1)</script> & \"types\"";
        writePost("special", title, "2020-02-01", false);
        String html = html("/blog/special");
        var article = structuredData(html).get("@graph").get(1);
        assertThat(article.get("@type").asText()).isEqualTo("BlogPosting");
        assertThat(article.get("headline").asText()).isEqualTo(title);
        assertThat(article.get("datePublished").asText()).isEqualTo("2020-02-01");
        assertThat(article.get("author").get("url").asText()).isEqualTo("https://notnullpratik.dev/#about");
        assertThat(article.has("dateModified")).isFalse();
        assertThat(html).doesNotContain("<script>alert(1)</script>")
                .contains("\\u003c/script\\u003e", "property=\"og:type\" content=\"article\"",
                        "href=\"/#about\" rel=\"author\"");
    }

    @Test
    void sitemapIncludesOnlyPublishedCanonicalUrlsAndReloadsContent() throws Exception {
        assertThat(sitemapUrls()).containsExactlyInAnyOrder(
                "https://notnullpratik.dev/", "https://notnullpratik.dev/books",
                "https://notnullpratik.dev/blog", "https://notnullpratik.dev/blog?page=2",
                "https://notnullpratik.dev/blog/first", "https://notnullpratik.dev/blog/second",
                "https://notnullpratik.dev/blog/third");
        writePost("fourth", "New article", "2020-01-05", false);
        assertThat(sitemapUrls()).contains("https://notnullpratik.dev/blog/fourth")
                .doesNotContain("https://notnullpratik.dev/blog/draft", "https://notnullpratik.dev/blog/future");
        mvc.perform(get("/blog/draft")).andExpect(status().isNotFound());
        mvc.perform(get("/blog/future")).andExpect(status().isNotFound());
    }

    @Test
    void robotsAdvertisesSitemapAndAboutRedirectIsPermanent() throws Exception {
        mvc.perform(get("/robots.txt")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/plain"))
                .andExpect(content().string("User-agent: *\nAllow: /\nDisallow: /api/\n\nSitemap: https://notnullpratik.dev/sitemap.xml\n"));
        mvc.perform(get("/about")).andExpect(status().isMovedPermanently()).andExpect(header().string("Location", "/#about"));
        mvc.perform(get("/api/articles/first/stats")).andExpect(status().isOk())
                .andExpect(header().string("X-Robots-Tag", "noindex"));
    }

    @Test
    void socialCardIsServedAtTheAdvertisedDimensionsAndImagesRemainAccessible() throws Exception {
        var bytes = mvc.perform(get("/images/social-card.png")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("image/png")).andReturn().getResponse().getContentAsByteArray();
        var image = ImageIO.read(new ByteArrayInputStream(bytes));
        assertThat(image.getWidth()).isEqualTo(1200);
        assertThat(image.getHeight()).isEqualTo(630);
        var post = new BlogService(postsDirectory.toString()).parse("image", """
                ---
                title: Image test
                date: 2020-01-01
                summary: Image example
                ---
                ![Helpful diagram](/images/example.png)
                """);
        assertThat(post.html()).contains("alt=\"Helpful diagram\"", "loading=\"lazy\"", "decoding=\"async\"", "src=\"/images/example.png\"");
    }

    private List<String> sitemapUrls() throws Exception {
        byte[] xml = mvc.perform(get("/sitemap.xml")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/xml"))
                .andReturn().getResponse().getContentAsByteArray();
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        var doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
        assertThat(doc.getDocumentElement().getNamespaceURI()).isEqualTo("http://www.sitemaps.org/schemas/sitemap/0.9");
        assertThat(doc.getElementsByTagName("lastmod").getLength()).isZero();
        var locations = doc.getElementsByTagNameNS("http://www.sitemaps.org/schemas/sitemap/0.9", "loc");
        var result = new ArrayList<String>();
        for (int i = 0; i < locations.getLength(); i++) result.add(locations.item(i).getTextContent());
        return result;
    }

    private JsonNode structuredData(String html) throws Exception {
        var match = Pattern.compile("<script type=\"application/ld\\+json\">(.*?)</script>", Pattern.DOTALL).matcher(html);
        assertThat(match.find()).isTrue();
        return json.readTree(match.group(1));
    }

    private String html(String path) throws Exception {
        return mvc.perform(get(path)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    private void writePost(String slug, String title, String date, boolean draft) throws Exception {
        Files.writeString(postsDirectory.resolve(slug + ".md"), """
                ---
                title: %s
                date: %s
                summary: A useful Java article.
                tag: Java
                draft: %s
                ---
                Article body.
                """.formatted(title, date, draft));
    }
}
