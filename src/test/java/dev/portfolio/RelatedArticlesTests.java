package dev.portfolio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RelatedArticlesTests extends DatabaseTestSupport {
    @TempDir
    static Path postsDirectory;
    @Autowired
    MockMvc mvc;

    @DynamicPropertySource
    static void contentDirectory(DynamicPropertyRegistry registry) {
        registry.add("portfolio.content-dir", () -> postsDirectory.toString());
    }

    @BeforeEach
    void seedPosts() throws Exception {
        try (var files = Files.list(postsDirectory)) {
            for (var file : files.toList()) Files.delete(file);
        }
        writePost("first", "First article", "2020-01-01", "");
        writePost("second", "Second <script>alert(1)</script>", "2020-01-02", "");
        writePost("draft", "Unpublished draft", "2020-01-03", "draft: true\n");
        writePost("future", "Scheduled article", "2999-01-01", "");
    }

    @Test
    void rendersSelectedPublishedArticlesInAuthorOrderWithEscapedMetadata() throws Exception {
        writePost("source", "Source article", "2020-02-01", """
                related: " first, second, first, source, missing, draft, future, ../../private, https://example.com, "
                """);

        String html = html("source");
        String section = relatedSection(html);
        assertThat(Pattern.compile("href=\"/blog/([^\"]+)\"").matcher(section).results()
                .map(match -> match.group(1)).toList()).containsExactly("first", "second");
        assertThat(section).contains("Also see", "First article", "Second &lt;script&gt;alert(1)&lt;/script&gt;",
                        "Summary for first.", "Java", "1 min read")
                .doesNotContain("<script>", "Unpublished draft", "Scheduled article", "private", "example.com", "target=\"_blank\"");
        assertThat(html.indexOf(section)).isGreaterThan(html.indexOf("Article body."));
        mvc.perform(get("/blog/draft")).andExpect(status().isNotFound());
        mvc.perform(get("/blog/future")).andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "related: \n", "related: missing, source, draft, future\n"})
    void omitsTheSectionWhenNoRelatedArticlesCanBeShown(String metadata) throws Exception {
        writePost("source", "Source article", "2020-02-01", metadata);
        assertThat(html("source")).doesNotContain("id=\"also-see-heading\"", "class=\"related-list\"");
    }

    @Test
    void reflectsChangesToLinksAndTargetMetadataWithoutRestarting() throws Exception {
        writePost("source", "Source article", "2020-02-01", "related: first\n");
        assertThat(relatedSection(html("source"))).contains("First article").doesNotContain("/blog/second");

        writePost("source", "Source article", "2020-02-01", "related: second\n");
        writePost("second", "Updated article title", "2020-01-02", "");
        assertThat(relatedSection(html("source"))).contains("Updated article title").doesNotContain("/blog/first");

        writePost("second", "Updated article title", "2020-01-02", "draft: true\n");
        assertThat(html("source")).doesNotContain("id=\"also-see-heading\"");

        writePost("second", "Republished article", "2020-01-02", "");
        assertThat(relatedSection(html("source"))).contains("Republished article");
        Files.delete(postsDirectory.resolve("second.md"));
        assertThat(html("source")).doesNotContain("id=\"also-see-heading\"");
    }

    @Test
    void supportsReciprocalLinksWithoutRecursingOrAddingImplicitBacklinks() throws Exception {
        writePost("first", "First article", "2020-01-01", "related: second\n");
        assertThat(relatedSection(html("first"))).contains("href=\"/blog/second\"");
        assertThat(html("second")).doesNotContain("id=\"also-see-heading\"");

        writePost("second", "Second article", "2020-01-02", "related: first\n");
        assertThat(relatedSection(html("second"))).contains("href=\"/blog/first\"");
        assertThat(relatedSection(html("first"))).contains("href=\"/blog/second\"");
    }

    private String html(String slug) throws Exception {
        return mvc.perform(get("/blog/" + slug)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    private String relatedSection(String html) {
        var match = Pattern.compile("<section class=\"related-articles\"[^>]*>(.*?)</section>", Pattern.DOTALL).matcher(html);
        assertThat(match.find()).isTrue();
        return match.group(1);
    }

    private void writePost(String slug, String title, String date, String metadata) throws Exception {
        Files.writeString(postsDirectory.resolve(slug + ".md"), """
                ---
                title: %s
                date: %s
                summary: Summary for %s.
                tag: Java
                %s---
                Article body.
                """.formatted(title, date, slug, metadata));
    }
}
