package dev.portfolio;

public record ArticleStats(long views, long likes, long shares, boolean liked) {
    public static final ArticleStats EMPTY = new ArticleStats(0, 0, 0, false);
}
