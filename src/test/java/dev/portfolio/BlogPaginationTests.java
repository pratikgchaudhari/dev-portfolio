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
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "portfolio.posts-per-page=2")
@AutoConfigureMockMvc
class BlogPaginationTests {
    @TempDir
    static Path postsDirectory;

    @Autowired
    MockMvc mvc;

    @DynamicPropertySource
    static void contentDirectory(DynamicPropertyRegistry registry) {
        registry.add("portfolio.content-dir", () -> postsDirectory.toString());
    }

    @BeforeEach
    void clearTestPosts() throws Exception {
        try (var files = Files.list(postsDirectory)) {
            for (var file : files.toList()) {
                Files.delete(file);
            }
        }
    }

    @Test
    void firstPageShowsNewestPublishedPostsAndOnlyNextLink() throws Exception {
        seedPosts(5);
        writePost("draft", "2021-01-01", true);
        writePost("future", "2999-01-01", false);

        var result = assertPage(null, "post-5", "post-4");
        assertThat(result.getModelAndView().getModel())
                .containsEntry("currentPage", 1).containsEntry("totalPages", 3)
                .containsEntry("totalPosts", 5);
        assertThat(html(result)).contains("1–2 of 5 articles", "Page 1 of 3", "rel=\"next\"", "href=\"/blog?page=2\"")
                .doesNotContain("rel=\"prev\"", "/blog/draft", "/blog/future");
        assertThat(html(assertPage("1", "post-5", "post-4"))).isEqualTo(html(result));
    }

    @Test
    void middlePageHasCorrectRangeAndBothNavigationLinks() throws Exception {
        seedPosts(5);

        var result = assertPage("2", "post-3", "post-2");
        assertThat(html(result)).contains("3–4 of 5 articles", "Page 2 of 3", "Writing — Page 2 —",
                "href=\"/blog?page=3\"").doesNotContain("/blog/post-5", "/blog/post-4", "/blog/post-1");
        assertThat(Pattern.compile("<a\\b[^>]*rel=\"prev\"[^>]*href=\"/blog\"")
                .matcher(html(result)).find()).isTrue();
        assertThat(html(result)).contains("rel=\"next\"");
    }

    @Test
    void lastPageShowsRemainingArticleAndOnlyPreviousLink() throws Exception {
        seedPosts(5);

        var result = assertPage("3", "post-1");
        assertThat(html(result)).contains("5–5 of 5 articles", "Page 3 of 3", "rel=\"prev\"", "href=\"/blog?page=2\"")
                .doesNotContain("rel=\"next\"");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void hidesPaginationWhenAllArticlesFitOnOnePage(int count) throws Exception {
        seedPosts(count);
        var result = mvc.perform(get("/blog")).andExpect(status().isOk()).andReturn();

        assertThat(html(result)).contains(count + (count == 1 ? " article" : " articles"))
                .doesNotContain("aria-label=\"Article pagination\"");
        assertThat(Pattern.compile("class=\"post-row\"").matcher(html(result)).results().count()).isEqualTo(count);
        if (count == 0) {
            assertThat(html(result)).contains("No articles yet.");
        }
        mvc.perform(get("/blog").param("page", "2")).andExpect(status().isNotFound());
    }

    @Test
    void exactMultipleDoesNotAddAnEmptyLastPage() throws Exception {
        seedPosts(4);
        var result = assertPage("2", "post-2", "post-1");
        assertThat(html(result)).contains("3–4 of 4 articles", "Page 2 of 2").doesNotContain("rel=\"next\"");
        mvc.perform(get("/blog").param("page", "3")).andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "abc", "1.5", "2147483648"})
    void rejectsInvalidPageNumbers(String page) throws Exception {
        seedPosts(5);
        mvc.perform(get("/blog").param("page", page)).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"4", "2147483647"})
    void missingPagesReturnNotFound(String page) throws Exception {
        seedPosts(5);
        mvc.perform(get("/blog").param("page", page)).andExpect(status().isNotFound());
    }

    @Test
    void postsWithSameDateHaveStableOrderAcrossPages() throws Exception {
        writePost("charlie", "2020-01-01", false);
        writePost("alpha", "2020-01-01", false);
        writePost("bravo", "2020-01-01", false);
        assertPage("1", "alpha", "bravo");
        assertPage("2", "charlie");
    }

    @Test
    void updatesPagesWhenMarkdownFilesChange() throws Exception {
        seedPosts(2);
        assertPage("1", "post-2", "post-1");

        writePost("post-3", "2020-01-03", false);
        assertThat(html(assertPage("1", "post-3", "post-2"))).contains("Page 1 of 2");
        assertPage("2", "post-1");

        writePost("post-3", "2020-01-03", true);
        assertThat(html(assertPage("1", "post-2", "post-1")))
                .doesNotContain("aria-label=\"Article pagination\"");
        mvc.perform(get("/blog").param("page", "2")).andExpect(status().isNotFound());
    }

    private MvcResult assertPage(String page, String... expectedSlugs) throws Exception {
        var request = get("/blog");
        if (page != null) {
            request.param("page", page);
        }
        var result = mvc.perform(request).andExpect(status().isOk()).andExpect(view().name("blog")).andReturn();
        var posts = (List<?>) result.getModelAndView().getModel().get("posts");
        assertThat(posts).extracting(post -> ((Post) post).slug()).containsExactly(expectedSlugs);
        assertThat(Pattern.compile("class=\"post-row\"").matcher(html(result)).results().count())
                .isEqualTo(expectedSlugs.length);
        for (var slug : expectedSlugs) {
            assertThat(html(result)).contains("href=\"/blog/" + slug + "\"");
        }
        return result;
    }

    private void seedPosts(int count) throws Exception {
        for (int i = 1; i <= count; i++) {
            writePost("post-" + i, "2020-01-%02d".formatted(i), false);
        }
    }

    private void writePost(String slug, String date, boolean draft) throws Exception {
        Files.writeString(postsDirectory.resolve(slug + ".md"), """
                ---
                title: %s
                date: %s
                summary: Pagination fixture
                draft: %s
                ---
                Article body.
                """.formatted(slug, date, draft));
    }

    private String html(MvcResult result) throws Exception {
        return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
}
