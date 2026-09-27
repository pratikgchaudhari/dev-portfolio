package dev.portfolio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.*;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PortfolioTests {
    @Autowired
    MockMvc mvc;
    @TempDir
    Path temp;

    String source(String extra, String body) {
        return "---\ntitle: Hello\ndate: 2020-01-01\nsummary: Test summary\n" + extra + "---\n" + body;
    }

    @Test
    void rendersMarkdownSafely() {
        var post = new BlogService(temp.toString()).parse("hello", source("", "## Heading\n\n**Bold**\n\n<script>alert(1)</script>\n\n[x](javascript:alert)"));
        assertThat(post.html()).contains("<h2>Heading</h2>", "<strong>Bold</strong>").doesNotContain("<script>", "href=\"javascript:");
    }

    @Test
    void hidesDraftsAndFuturePosts() {
        var service = new BlogService(temp.toString());
        assertThat(service.parse("draft", source("draft: true\n", "Secret"))).isNull();
        assertThat(service.parse("future", source("", "Later").replace("2020-01-01", "2999-01-01"))).isNull();
    }

    @Test
    void reloadsFilesAndSortsByDate() throws Exception {
        var service = new BlogService(temp.toString());
        assertThat(service.posts()).isEmpty();
        Files.writeString(temp.resolve("old.md"), source("", "Old"));
        Files.writeString(temp.resolve("new.md"), source("", "New").replace("2020-01-01", "2021-01-01"));
        assertThat(service.posts()).extracting(Post::slug).containsExactly("new", "old");
        Files.writeString(temp.resolve("new.md"), source("draft: true\n", "New"));
        assertThat(service.posts()).extracting(Post::slug).containsExactly("old");
    }

    @Test
    void validatesMetadata() {
        assertThatThrownBy(() -> new BlogService(temp.toString()).parse("bad", "No front matter")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void homepageShowsAboutAndLinksToWritingWithoutRecentPosts() throws Exception {
        var html = mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(html).contains("id=\"about\"", "id=\"journey-heading\"")
                .doesNotContain("Recent writing", "class=\"post-list\"", "class=\"post-row\"");
        assertThat(java.util.regex.Pattern.compile("<a\\b[^>]*href=\"/blog\"[^>]*>\\s*Writing\\s*</a>")
                .matcher(html).find()).isTrue();
        assertThat(java.util.regex.Pattern.compile("<a\\b[^>]*href=\"/blog\"[^>]*>Read the blog")
                .matcher(html).find()).isTrue();
    }

    @Test
    void socialProfilesAppearOncePerPageAndOpenInNewTabs() throws Exception {
        var urls = new String[] {
                "https://x.com/pratikc_89",
                "https://github.com/pratikgchaudhari",
                "https://www.linkedin.com/in/pratik-chaudhari-71ba37a1/"
        };
        for (var route : new String[] {"/", "/blog", "/books", "/blog/keeping-software-simple"}) {
            var html = mvc.perform(get(route)).andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            assertThat(html).contains("Social profiles", "aria-label=\"My social profiles\"");
            for (var url : urls) {
                assertThat(html).containsOnlyOnce("href=\"" + url + "\"");
                assertThat(html).contains("href=\"" + url + "\" target=\"_blank\" rel=\"noopener noreferrer\"");
                if (route.equals("/")) {
                    assertThat(html.indexOf("href=\"" + url + "\""))
                            .isLessThan(html.indexOf("id=\"about\""));
                }
            }
        }
    }

    @Test
    void pagesAndMissingPosts() throws Exception {
        mvc.perform(get("/blog")).andExpect(status().isOk());
        mvc.perform(get("/blog/keeping-software-simple")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("<table>")));
        mvc.perform(get("/blog/does-not-exist")).andExpect(status().isNotFound());
    }
}
