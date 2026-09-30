package dev.portfolio;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EngagementControllerTests extends DatabaseTestSupport {
    @TempDir
    static Path content;
    @Autowired
    MockMvc mvc;
    @Autowired
    JdbcTemplate jdbc;

    @DynamicPropertySource
    static void content(DynamicPropertyRegistry registry) {
        registry.add("portfolio.content-dir", () -> content.toString());
    }

    @BeforeEach
    void setup() throws Exception {
        jdbc.update("DELETE FROM article_visits");
        jdbc.update("DELETE FROM article_likes");
        jdbc.update("DELETE FROM article_stats");
        Files.writeString(content.resolve("article.md"), "---\ntitle: Article\ndate: 2020-01-01\nsummary: Summary\n---\nBody");
        Files.writeString(content.resolve("draft.md"), "---\ntitle: Draft\ndate: 2020-01-01\nsummary: Secret\ndraft: true\n---\nBody");
        Files.writeString(content.resolve("future.md"), "---\ntitle: Future\ndate: 2999-01-01\nsummary: Secret\n---\nBody");
    }

    @Test
    void pageSetsPrivateCookieAndOnlyVisibleViewRequestIncrementsCount() throws Exception {
        var response = mvc.perform(get("/blog/article").secure(true)).andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "private, no-store"))
                .andReturn().getResponse();
        var cookie = response.getCookie(ReaderIdentity.COOKIE);
        assertThat(cookie).isNotNull();
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSecure()).isTrue();
        assertThat(response.getHeader("Set-Cookie")).contains("SameSite=Lax", "Max-Age=");
        mvc.perform(get("/api/articles/article/stats")).andExpect(jsonPath("$.views").value(0));
        mvc.perform(head("/blog/article")).andExpect(status().isOk());
        for (int i = 0; i < 2; i++) {
            mvc.perform(post("/api/articles/article/views").cookie(cookie).header("X-Portfolio-Request", "1"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.views").value(1));
        }
        mvc.perform(post("/api/articles/article/views").cookie(reader()).header("X-Portfolio-Request", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.views").value(2));
    }

    @Test
    void likesToggleIdempotentlyAndPersistAcrossPageLoads() throws Exception {
        var reader = reader();
        for (int i = 0; i < 2; i++) {
            mvc.perform(put("/api/articles/article/like").cookie(reader).header("X-Portfolio-Request", "1")
                            .contentType("application/json").content("{\"liked\":true}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.likes").value(1))
                    .andExpect(jsonPath("$.liked").value(true));
        }
        var html = mvc.perform(get("/blog/article").cookie(reader)).andReturn().getResponse().getContentAsString();
        assertThat(html).contains("aria-pressed=\"true\"", "aria-label=\"Unlike this article\"", "1 like");
        mvc.perform(get("/api/articles/article/stats").cookie(reader()))
                .andExpect(jsonPath("$.likes").value(1)).andExpect(jsonPath("$.liked").value(false));
        assertThat(mvc.perform(get("/blog")).andReturn().getResponse().getContentAsString()).contains("1 like", "0 views");
        for (int i = 0; i < 2; i++) {
            mvc.perform(put("/api/articles/article/like").cookie(reader).header("X-Portfolio-Request", "1")
                            .contentType("application/json").content("{\"liked\":false}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.likes").value(0))
                    .andExpect(jsonPath("$.liked").value(false));
        }
    }

    @Test
    void unpublishedAndMissingArticlesCannotBeCountedOrLiked() throws Exception {
        for (var slug : new String[]{"draft", "future", "missing"}) {
            mvc.perform(get("/api/articles/" + slug + "/stats")).andExpect(status().isNotFound());
            mvc.perform(post("/api/articles/" + slug + "/views").cookie(reader()).header("X-Portfolio-Request", "1"))
                    .andExpect(status().isNotFound());
            mvc.perform(put("/api/articles/" + slug + "/like").cookie(reader()).header("X-Portfolio-Request", "1")
                            .contentType("application/json").content("{\"liked\":true}"))
                    .andExpect(status().isNotFound());
        }
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM article_stats", Long.class)).isZero();
    }

    @Test
    void rejectsCrossSiteWritesAndInvalidReaderOrPayload() throws Exception {
        mvc.perform(post("/api/articles/article/views").cookie(reader())).andExpect(status().isForbidden());
        mvc.perform(post("/api/articles/article/views").cookie(reader()).header("X-Portfolio-Request", "1")
                .header("Sec-Fetch-Site", "cross-site")).andExpect(status().isForbidden());
        mvc.perform(post("/api/articles/article/views").header("X-Portfolio-Request", "1"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/articles/article/views").cookie(new Cookie(ReaderIdentity.COOKIE, "invalid"))
                .header("X-Portfolio-Request", "1")).andExpect(status().isBadRequest());
        mvc.perform(put("/api/articles/article/like").cookie(reader()).header("X-Portfolio-Request", "1")
                        .contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/api/articles/article/like").cookie(reader()).contentType("application/json").content("{\"liked\":true}"))
                .andExpect(status().isForbidden());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM article_stats", Long.class)).isZero();
    }

    @Test
    void invalidCookieIsReplacedOnArticlePage() throws Exception {
        var response = mvc.perform(get("/blog/article").cookie(new Cookie(ReaderIdentity.COOKIE, "invalid")))
                .andExpect(status().isOk()).andReturn().getResponse();
        assertThat(UUID.fromString(response.getCookie(ReaderIdentity.COOKIE).getValue())).isNotNull();
    }

    private Cookie reader() {
        return new Cookie(ReaderIdentity.COOKIE, UUID.randomUUID().toString());
    }
}
