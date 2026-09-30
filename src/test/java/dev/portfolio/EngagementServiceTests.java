package dev.portfolio;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.nio.file.Path;
import java.time.*;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

class EngagementServiceTests {
    @TempDir
    Path temp;
    JdbcTemplate jdbc;
    EngagementService service;
    MutableClock clock;

    @BeforeEach
    void setup() {
        clock = new MutableClock();
        service = create("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
    }

    private EngagementService create(String url) {
        var source = new DriverManagerDataSource(url, "sa", "");
        new ResourceDatabasePopulator(new ClassPathResource("schema.sql")).execute(source);
        jdbc = new JdbcTemplate(source);
        return new EngagementService(jdbc, new DataSourceTransactionManager(source), clock);
    }

    @AfterEach
    void closeDatabase() {
        jdbc.execute("SHUTDOWN");
    }

    @Test
    void likesAreIdempotentAndIsolatedByReaderAndArticle() {
        var first = UUID.randomUUID();
        var second = UUID.randomUUID();
        assertThat(service.stats("article", first)).isEqualTo(ArticleStats.EMPTY);
        assertThat(service.setLiked("article", first, true)).isEqualTo(new ArticleStats(0, 1, true));
        assertThat(service.setLiked("article", first, true).likes()).isEqualTo(1);
        assertThat(service.stats("article", second).liked()).isFalse();
        assertThat(service.setLiked("article", second, true).likes()).isEqualTo(2);
        assertThat(service.setLiked("article", first, false).likes()).isEqualTo(1);
        assertThat(service.setLiked("article", first, false).likes()).isEqualTo(1);
        assertThat(service.stats("another", second)).isEqualTo(ArticleStats.EMPTY);
    }

    @Test
    void viewsAreDeduplicatedForThirtyMinutesAndDoNotResetLikes() {
        var reader = UUID.randomUUID();
        service.setLiked("article", reader, true);
        assertThat(service.recordView("article", reader)).isEqualTo(new ArticleStats(1, 1, true));
        clock.advance(Duration.ofMinutes(29));
        assertThat(service.recordView("article", reader).views()).isEqualTo(1);
        clock.advance(Duration.ofMinutes(1));
        assertThat(service.recordView("article", reader).views()).isEqualTo(2);
        assertThat(service.recordView("article", UUID.randomUUID()).views()).isEqualTo(3);
        assertThat(service.setLiked("article", reader, false)).isEqualTo(new ArticleStats(3, 0, false));
        assertThat(service.recordView("another", reader).views()).isEqualTo(1);
    }

    @Test
    void concurrentRequestsFromOneReaderCountOnce() throws Exception {
        var reader = UUID.randomUUID();
        var pool = Executors.newFixedThreadPool(8);
        try {
            var tasks = IntStream.range(0, 32).<Callable<Void>>mapToObj(i -> () -> {
                service.recordView("article", reader);
                service.setLiked("article", reader, true);
                return null;
            }).toList();
            for (var future : pool.invokeAll(tasks)) future.get();
        } finally {
            pool.shutdownNow();
        }
        assertThat(service.stats("article", reader)).isEqualTo(new ArticleStats(1, 1, true));
    }

    @Test
    void concurrentReadersDoNotLoseUpdates() throws Exception {
        var pool = Executors.newFixedThreadPool(8);
        try {
            var tasks = IntStream.range(0, 24).<Callable<Void>>mapToObj(i -> () -> {
                var reader = UUID.randomUUID();
                service.recordView("article", reader);
                service.setLiked("article", reader, true);
                return null;
            }).toList();
            for (var future : pool.invokeAll(tasks)) future.get();
        } finally {
            pool.shutdownNow();
        }
        assertThat(service.stats("article", null)).isEqualTo(new ArticleStats(24, 24, false));
    }

    @Test
    void fileDatabaseKeepsCountsAndReaderStateWhenReopened() {
        jdbc.execute("SHUTDOWN");
        String url = "jdbc:h2:file:" + temp.resolve("engagement");
        var reader = UUID.randomUUID();
        var original = create(url);
        original.recordView("article", reader);
        original.setLiked("article", reader, true);
        var reopened = create(url);
        assertThat(reopened.stats("article", reader)).isEqualTo(new ArticleStats(1, 1, true));
        assertThat(reopened.recordView("article", reader).views()).isEqualTo(1);
        assertThat(reopened.setLiked("article", reader, true).likes()).isEqualTo(1);
    }

    @Test
    void failedViewUpdateRollsBackDeduplicationEntry() {
        var reader = UUID.randomUUID();
        jdbc.execute("ALTER TABLE article_stats ADD CONSTRAINT no_views CHECK (views = 0)");
        assertThatThrownBy(() -> service.recordView("article", reader))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
        assertThat(service.stats("article", reader).views()).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM article_visits", Long.class)).isZero();
        jdbc.execute("ALTER TABLE article_stats DROP CONSTRAINT no_views");
        assertThat(service.recordView("article", reader).views()).isEqualTo(1);
    }

    static class MutableClock extends Clock {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");

        void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now;
        }
    }
}
