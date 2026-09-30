package dev.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EngagementService {
    private static final long VIEW_WINDOW = Duration.ofMinutes(30).toMillis();
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transactions;
    private final Clock clock;

    @Autowired
    public EngagementService(JdbcTemplate jdbc, PlatformTransactionManager manager) {
        this(jdbc, manager, Clock.systemUTC());
    }

    EngagementService(JdbcTemplate jdbc, PlatformTransactionManager manager, Clock clock) {
        this.jdbc = jdbc;
        this.transactions = new TransactionTemplate(manager);
        this.clock = clock;
    }

    public ArticleStats stats(String slug, UUID reader) {
        return jdbc.queryForObject("""
                        SELECT COALESCE((SELECT views FROM article_stats WHERE slug = ?), 0) AS views,
                               (SELECT COUNT(*) FROM article_likes WHERE slug = ?) AS likes,
                               EXISTS(SELECT 1 FROM article_likes WHERE slug = ? AND reader_id = ?) AS liked
                        """, (rs, row) -> new ArticleStats(rs.getLong("views"), rs.getLong("likes"), rs.getBoolean("liked")),
                slug, slug, slug, reader);
    }

    public Map<String, ArticleStats> statsFor(List<Post> posts) {
        Map<String, ArticleStats> stats = new HashMap<>();
        posts.forEach(post -> stats.put(post.slug(), ArticleStats.EMPTY));
        if (posts.isEmpty()) return stats;
        new NamedParameterJdbcTemplate(jdbc).query("""
                SELECT s.slug, s.views, COUNT(l.reader_id) AS likes
                FROM article_stats s LEFT JOIN article_likes l ON l.slug = s.slug
                WHERE s.slug IN (:slugs) GROUP BY s.slug, s.views
                """, Map.of("slugs", stats.keySet()), rs -> {
            stats.put(rs.getString("slug"), new ArticleStats(rs.getLong("views"), rs.getLong("likes"), false));
        });
        return stats;
    }

    public ArticleStats recordView(String slug, UUID reader) {
        return transactions.execute(transaction -> {
            lockArticle(slug);
            long now = clock.millis();
            jdbc.update("DELETE FROM article_visits WHERE slug = ? AND viewed_at <= ?", slug, now - VIEW_WINDOW);
            boolean seen = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM article_visits WHERE slug = ? AND reader_id = ?", Long.class, slug, reader) > 0;
            if (!seen) {
                jdbc.update("INSERT INTO article_visits (slug, reader_id, viewed_at) VALUES (?, ?, ?)", slug, reader, now);
                jdbc.update("UPDATE article_stats SET views = views + 1 WHERE slug = ?", slug);
            }
            return stats(slug, reader);
        });
    }

    public ArticleStats setLiked(String slug, UUID reader, boolean liked) {
        return transactions.execute(transaction -> {
            lockArticle(slug);
            if (liked) {
                jdbc.update("MERGE INTO article_likes (slug, reader_id) KEY (slug, reader_id) VALUES (?, ?)", slug, reader);
            } else {
                jdbc.update("DELETE FROM article_likes WHERE slug = ? AND reader_id = ?", slug, reader);
            }
            return stats(slug, reader);
        });
    }

    private void lockArticle(String slug) {
        // Only the slug is updated: existing view totals remain intact.
        jdbc.update("MERGE INTO article_stats (slug) KEY (slug) VALUES (?)", slug);
        // Serialize changes for one article, including simultaneous requests from different tabs.
        jdbc.queryForObject("SELECT views FROM article_stats WHERE slug = ? FOR UPDATE", Long.class, slug);
    }
}
