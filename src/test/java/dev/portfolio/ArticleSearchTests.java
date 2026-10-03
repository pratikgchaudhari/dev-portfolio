package dev.portfolio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.HtmlUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {"portfolio.posts-per-page=2", "portfolio.site-url=https://notnullpratik.dev",
        "portfolio.indexing-enabled=true"})
@AutoConfigureMockMvc
class ArticleSearchTests extends DatabaseTestSupport {
    @TempDir static Path postsDirectory;
    @Autowired MockMvc mvc;

    @DynamicPropertySource
    static void contentDirectory(DynamicPropertyRegistry registry) {
        registry.add("portfolio.content-dir", () -> postsDirectory.toString());
    }

    @BeforeEach
    void clearPosts() throws Exception {
        try (var files = Files.list(postsDirectory)) {
            for (var file : files.toList()) Files.delete(file);
        }
    }

    @Test
    void matchesEveryTermAcrossFieldsIgnoringCaseAccentsAndExtraWhitespace() throws Exception {
        writePost("complete", "Café collections", "2020-01-01", "A practical reference", "Java",
                "Concurrent queues with `Map.compute`.", false);
        writePost("partial", "Café collections", "2020-01-02", "A practical reference", "Java",
                "Sequential lists only.", false);

        assertPosts(search("  CAFE\t java  \n PRACT  concur  map.compute  "), "complete");
        assertPosts(search("CAFE concurrent missing"));
    }

    @Test
    void treatsPunctuationLiterallyAndIncludesCodeInSearchableContent() throws Exception {
        writePost("code", "Pattern examples", "2020-01-01", "Language examples", "Programming", """
                ```cpp
                C++ uses a literal a.* pattern here.
                ```
                """, false);
        writePost("other", "Pattern alternatives", "2020-01-02", "Language examples", "Programming",
                "CCCC and abc are different strings.", false);

        assertPosts(search("C++ a.*"), "code");
        assertPosts(search("[unterminated"));
    }

    @Test
    void searchesVisibleMarkdownWithoutMatchingLinkDestinationsOrMarkup() throws Exception {
        writePost("linked", "Useful reading", "2020-01-01", "Reference material", "Notes", """
                ## A **readable** heading
                [Documentation](https://hiddenhost.example/destinationtoken)
                ![Helpful diagram](https://images.example/hiddenimage.png)
                """, false);

        assertPosts(search("readable Documentation"), "linked");
        assertPosts(search("destinationtoken"));
        assertPosts(search("hiddenimage"));
        assertPosts(search("href"));
        assertPosts(search("**"));
    }

    @Test
    void prioritizesTitlesThenUsesNewestDateAndSlugToKeepResultsStable() throws Exception {
        writePost("bravo", "Java basics", "2020-01-01", "A reference", "Notes", "Article body.", false);
        writePost("alpha", "Java types", "2020-01-01", "A reference", "Notes", "Article body.", false);
        writePost("zulu", "Java collections", "2020-02-01", "A reference", "Notes", "Article body.", false);
        writePost("body", "Another language", "2021-01-01", "A reference", "Notes", "Compared with Java.", false);

        assertPosts(search("java"), "zulu", "alpha");
        assertPosts(search("java", 2), "bravo", "body");
    }

    @Test
    void filtersPublishedMatchesBeforePaginationAndReflectsMarkdownChanges() throws Exception {
        for (int i = 1; i <= 3; i++) {
            writePost("match-" + i, "Queue example " + i, "2020-01-0" + i,
                    "Useful examples", "Java", "Article body.", false);
        }
        writePost("unrelated", "An unrelated title", "2022-01-01", "Other work", "Notes", "Article body.", false);
        writePost("draft", "Queue draft", "2023-01-01", "Useful examples", "Java", "Article body.", true);
        writePost("future", "Queue scheduled", "2999-01-01", "Useful examples", "Java", "Article body.", false);

        var first = search("queue");
        assertPosts(first, "match-3", "match-2");
        assertThat(first.getModelAndView().getModel()).containsEntry("totalPosts", 3).containsEntry("totalPages", 2);
        assertPosts(search("queue", 2), "match-1");

        writePost("match-3", "Changed topic", "2020-01-03", "Other work", "Notes", "Different body.", false);
        assertPosts(search("queue"), "match-2", "match-1");
        mvc.perform(get("/blog").param("q", "queue").param("page", "2")).andExpect(status().isNotFound());
        writePost("added", "Queue update", "2020-03-01", "Useful examples", "Java", "Article body.", false);
        assertPosts(search("queue"), "added", "match-2");
    }

    @Test
    void blankQueriesPreserveNewestFirstListingAndIndexableCanonical() throws Exception {
        writePost("older", "Java", "2020-01-01", "Java reference", "Java", "Java body.", false);
        writePost("newer", "Something else", "2021-01-01", "Other work", "Notes", "Article body.", false);
        var unfiltered = mvc.perform(get("/blog")).andExpect(status().isOk()).andReturn();
        assertPosts(unfiltered, "newer", "older");

        for (String blank : List.of("", " \t  \n ")) {
            var result = search(blank);
            assertPosts(result, "newer", "older");
            assertThat(canonical(html(result))).isEqualTo("https://notnullpratik.dev/blog");
            assertThat(html(result)).contains("name=\"robots\" content=\"index, follow, max-image-preview:large\"");
        }
    }

    @Test
    void noMatchesHaveAnEmptyStateAndNoPaginationWhileInvalidPagesAreRejected() throws Exception {
        writePost("first", "Java basics", "2020-01-01", "A reference", "Java", "Article body.", false);
        var empty = search("unfindable");
        assertPosts(empty);
        assertThat(empty.getModelAndView().getModel()).containsEntry("totalPosts", 0);
        assertThat(html(empty)).containsIgnoringCase("No articles")
                .doesNotContain("No articles yet.", "aria-label=\"Article pagination", "class=\"post-row\"");
        assertThat(searchInput(html(empty)).get("value")).isEqualTo("unfindable");

        mvc.perform(get("/blog").param("q", "unfindable").param("page", "2")).andExpect(status().isNotFound());
        mvc.perform(get("/blog").param("q", "java").param("page", "0")).andExpect(status().isBadRequest());
        mvc.perform(get("/blog").param("q", "java").param("page", "abc")).andExpect(status().isBadRequest());
    }

    @Test
    void acceptsTheQueryLengthLimitAndRejectsLongerQueries() throws Exception {
        String boundary = "x".repeat(200);
        writePost("boundary", boundary, "2020-01-01", "A reference", "Notes", "Article body.", false);
        assertPosts(search(boundary), "boundary");
        mvc.perform(get("/blog").param("q", boundary + "x")).andExpect(status().isBadRequest());
    }

    @Test
    void escapesTheQueryAndKeepsReservedCharactersInBothPaginationControls() throws Exception {
        String query = "C++ & <script>alert(1)</script> # ?";
        for (int i = 1; i <= 5; i++) {
            writePost("special-" + i, query, "2020-01-0" + i, "A reference", "Notes", "Article body.", false);
        }
        var result = search(query, 2);
        assertPosts(result, "special-3", "special-2");
        String rendered = html(result);
        assertThat(rendered).doesNotContain("<script>alert(1)</script>");
        assertThat(searchInput(rendered)).containsEntry("type", "search").containsEntry("value", query);

        var forms = Pattern.compile("<form\\b[^>]*>(.*?)</form>", Pattern.DOTALL).matcher(rendered).results()
                .filter(match -> match.group(1).contains("name=\"q\"")).toList();
        assertThat(forms).hasSize(1);
        assertThat(attributes(forms.get(0).group())).containsEntry("method", "get").containsEntry("action", "/blog");
        assertThat(forms.get(0).group(1)).doesNotContain("name=\"page\"");

        var links = Pattern.compile("<a\\b[^>]*>").matcher(rendered).results().map(match -> attributes(match.group()))
                .filter(attrs -> List.of("prev", "next").contains(attrs.getOrDefault("rel", ""))).toList();
        assertThat(links).hasSize(4);
        assertThat(links.stream().filter(link -> "prev".equals(link.get("rel"))).count()).isEqualTo(2);
        for (var link : links) {
            var uri = URI.create(link.get("href"));
            assertThat(uri.getPath()).isEqualTo("/blog");
            assertThat(uri.getFragment()).isEqualTo("articles");
            var params = queryParameters(uri);
            assertThat(params).containsEntry("q", query);
            if ("prev".equals(link.get("rel"))) assertThat(params).doesNotContainKey("page");
            else assertThat(params).containsEntry("page", "3");
        }
    }

    @Test
    void searchPagesHaveNormalizedSelfCanonicalsAreNoindexAndStayOutOfTheSitemap() throws Exception {
        for (int i = 1; i <= 3; i++) {
            writePost("result-" + i, "Café C++ &", "2020-01-0" + i,
                    "A reference", "Notes", "Article body.", false);
        }
        for (int page : List.of(1, 2)) {
            String rendered = html(search("  café   C++ &  ", page));
            assertThat(rendered).contains("name=\"robots\" content=\"noindex, follow\"");
            var uri = URI.create(canonical(rendered));
            assertThat(uri.getScheme()).isEqualTo("https");
            assertThat(uri.getHost()).isEqualTo("notnullpratik.dev");
            assertThat(uri.getPath()).isEqualTo("/blog");
            var params = queryParameters(uri);
            assertThat(params).containsEntry("q", "café C++ &");
            if (page == 1) assertThat(params).doesNotContainKey("page");
            else assertThat(params).containsEntry("page", "2");
            assertThat(searchInput(rendered).get("value")).isEqualTo("café C++ &");
        }
        String sitemap = mvc.perform(get("/sitemap.xml")).andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(sitemap).doesNotContain("?q=", "&amp;q=", "noindex")
                .contains("https://notnullpratik.dev/blog?page=2", "https://notnullpratik.dev/blog/result-1");
    }

    private MvcResult search(String query) throws Exception {
        return search(query, 1);
    }

    private MvcResult search(String query, int page) throws Exception {
        return mvc.perform(get("/blog").param("q", query).param("page", Integer.toString(page)))
                .andExpect(status().isOk()).andExpect(view().name("blog")).andReturn();
    }

    private void assertPosts(MvcResult result, String... slugs) {
        var posts = (List<?>) result.getModelAndView().getModel().get("posts");
        assertThat(posts).extracting(post -> ((Post) post).slug()).containsExactly(slugs);
    }

    private String html(MvcResult result) throws Exception {
        return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    private Map<String, String> searchInput(String html) {
        return Pattern.compile("<input\\b[^>]*>").matcher(html).results().map(match -> attributes(match.group()))
                .filter(attrs -> "q".equals(attrs.get("name"))).findFirst().orElseThrow();
    }

    private String canonical(String html) {
        return Pattern.compile("<link\\b[^>]*>").matcher(html).results().map(match -> attributes(match.group()))
                .filter(attrs -> "canonical".equals(attrs.get("rel"))).findFirst().orElseThrow().get("href");
    }

    private Map<String, String> attributes(String html) {
        Map<String, String> result = new LinkedHashMap<>();
        Pattern.compile("([\\w:-]+)=\"([^\"]*)\"").matcher(html).results()
                .forEach(match -> result.put(match.group(1), HtmlUtils.htmlUnescape(match.group(2))));
        return result;
    }

    private Map<String, String> queryParameters(URI uri) {
        Map<String, String> result = new LinkedHashMap<>();
        if (uri.getRawQuery() != null) {
            for (String pair : uri.getRawQuery().split("&")) {
                String[] parts = pair.split("=", 2);
                result.put(URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(parts.length == 2 ? parts[1] : "", StandardCharsets.UTF_8));
            }
        }
        return result;
    }

    private void writePost(String slug, String title, String date, String summary, String tag,
                           String body, boolean draft) throws Exception {
        Files.writeString(postsDirectory.resolve(slug + ".md"), """
                ---
                title: %s
                date: %s
                summary: %s
                tag: %s
                draft: %s
                ---
                %s
                """.formatted(title, date, summary, tag, draft, body));
    }
}
