CREATE TABLE IF NOT EXISTS article_stats (
    slug VARCHAR(255) PRIMARY KEY,
    views BIGINT NOT NULL DEFAULT 0 CHECK (views >= 0)
);

CREATE TABLE IF NOT EXISTS article_likes (
    slug VARCHAR(255) NOT NULL REFERENCES article_stats(slug),
    reader_id UUID NOT NULL,
    PRIMARY KEY (slug, reader_id)
);

CREATE TABLE IF NOT EXISTS article_visits (
    slug VARCHAR(255) NOT NULL REFERENCES article_stats(slug),
    reader_id UUID NOT NULL,
    viewed_at BIGINT NOT NULL,
    PRIMARY KEY (slug, reader_id)
);
CREATE INDEX IF NOT EXISTS visits_expiry ON article_visits(viewed_at);
