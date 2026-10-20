package dev.portfolio;

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
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "portfolio.site-url=https://notnullpratik.dev")
@AutoConfigureMockMvc
class ArticlePreviewTests extends DatabaseTestSupport {
    @TempDir static Path postsDirectory;
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @DynamicPropertySource
    static void posts(DynamicPropertyRegistry registry) {
        registry.add("portfolio.content-dir", () -> postsDirectory.toString());
    }

    @BeforeEach
    void seedPosts() throws Exception {
        try (var files = Files.list(postsDirectory)) {
            for (var file : files.toList()) Files.delete(file);
        }
        writePost("first", "Java without the guesswork", "Java", "2020-01-01", false);
        writePost("second", "Understanding Vue watchers", "Vue.js", "2020-01-02", false);
    }

    @Test
    void articleMetadataUsesItsOwnAbsolutePreviewAndDescriptiveAltText() throws Exception {
        String html = articleHtml("first");
        String url = imageUrl(html);
        assertThat(url).matches("https://notnullpratik.dev/images/articles/first\\.png\\?v=[a-f0-9]{64}");
        assertThat(html).contains("name=\"twitter:image\" content=\"" + url + "\"",
                "property=\"og:image:alt\" content=\"Java without the guesswork — Pratik Chaudhari · Java\"",
                "name=\"twitter:image:alt\" content=\"Java without the guesswork — Pratik Chaudhari · Java\"")
                .doesNotContain("/images/social-card.png");
        var schema = Pattern.compile("<script type=\"application/ld\\+json\">(.*?)</script>", Pattern.DOTALL).matcher(html);
        assertThat(schema.find()).isTrue();
        var image = json.readTree(schema.group(1)).get("@graph").get(1).get("image");
        assertThat(image.get("url").asText()).isEqualTo(url);
        assertThat(image.get("width").asInt()).isEqualTo(1200);
        assertThat(image.get("height").asInt()).isEqualTo(630);
    }

    @Test
    void publishedArticlesReturnDifferentValidPngsWithoutReaderCookies() throws Exception {
        byte[] first = png("first");
        byte[] second = png("second");
        assertThat(first).isNotEqualTo(second);
        for (byte[] bytes : new byte[][]{first, second}) {
            var image = ImageIO.read(new ByteArrayInputStream(bytes));
            assertThat(image).isNotNull();
            assertThat(image.getWidth()).isEqualTo(1200);
            assertThat(image.getHeight()).isEqualTo(630);
            assertThat(bytes.length).isLessThan(1_000_000);
        }
    }

    @Test
    void imagesRevalidateAndMetadataEditsChangeTheUrlAndPixels() throws Exception {
        String beforeUrl = imageUrl(articleHtml("first"));
        var before = mvc.perform(get("/images/articles/first.png")).andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-cache, public"))
                .andReturn().getResponse();
        String etag = before.getHeader("ETag");
        assertThat(etag).isNotBlank();
        mvc.perform(get("/images/articles/first.png").header("If-None-Match", etag))
                .andExpect(status().isNotModified()).andExpect(content().bytes(new byte[0]));

        writePost("first", "A new title with new trade-offs", "Systems", "2020-02-02", false);
        assertThat(imageUrl(articleHtml("first"))).isNotEqualTo(beforeUrl);
        var after = mvc.perform(get(beforeUrl.replace("https://notnullpratik.dev", ""))
                        .header("If-None-Match", etag))
                .andExpect(status().isOk()).andReturn().getResponse();
        assertThat(after.getHeader("ETag")).isNotEqualTo(etag);
        assertThat(after.getContentAsByteArray()).isNotEqualTo(before.getContentAsByteArray());
    }

    @Test
    void unpublishedAndRemovedArticlesNeverServeCachedPreviews() throws Exception {
        png("first");
        writePost("first", "Java without the guesswork", "Java", "2020-01-01", true);
        mvc.perform(get("/images/articles/first.png")).andExpect(status().isNotFound());
        writePost("first", "Java without the guesswork", "Java", "2999-01-01", false);
        mvc.perform(get("/images/articles/first.png")).andExpect(status().isNotFound());
        Files.delete(postsDirectory.resolve("first.md"));
        mvc.perform(get("/images/articles/first.png")).andExpect(status().isNotFound());
        mvc.perform(get("/images/articles/missing.png")).andExpect(status().isNotFound());
    }

    @Test
    void headRequestsExposeImageMetadataWithoutABodyOrCookie() throws Exception {
        var response = mvc.perform(head("/images/articles/first.png"))
                .andExpect(status().isOk()).andExpect(content().contentType("image/png"))
                .andExpect(header().doesNotExist("Set-Cookie"))
                .andExpect(content().bytes(new byte[0])).andReturn().getResponse();
        assertThat(response.getHeader("ETag")).isNotBlank();
        assertThat(response.getContentLength()).isGreaterThan(0);
    }

    @Test
    void longTitlesUnicodeAndMarkupRenderAsTextAndMetadataStaysEscaped() throws Exception {
        writePost("long-title", "<Teleport> & café: “framework internals” " + "LongIdentifier".repeat(80),
                "Vue.js " + "LongTag".repeat(30), "2020-01-01", false);
        byte[] bytes = png("long-title");
        assertThat(ImageIO.read(new ByteArrayInputStream(bytes)).getWidth()).isEqualTo(1200);
        assertThat(articleHtml("long-title")).contains("&lt;Teleport&gt; &amp; café")
                .doesNotContain("content=\"<Teleport>");
    }

    private byte[] png(String slug) throws Exception {
        return mvc.perform(get("/images/articles/" + slug + ".png"))
                .andExpect(status().isOk()).andExpect(content().contentType("image/png"))
                .andExpect(header().doesNotExist("Set-Cookie"))
                .andReturn().getResponse().getContentAsByteArray();
    }

    private String articleHtml(String slug) throws Exception {
        return mvc.perform(get("/blog/" + slug).header("Host", "untrusted.example"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    private String imageUrl(String html) {
        var match = Pattern.compile("property=\"og:image\" content=\"([^\"]+)\"").matcher(html);
        assertThat(match.find()).isTrue();
        return match.group(1);
    }

    private void writePost(String slug, String title, String tag, String date, boolean draft) throws Exception {
        Files.writeString(postsDirectory.resolve(slug + ".md"), """
                ---
                title: %s
                date: %s
                summary: A useful article.
                tag: %s
                draft: %s
                ---
                Article body.
                """.formatted(title, date, tag, draft));
    }
}
