package dev.portfolio;

public record ArticleStats(long views, long likes, boolean liked) {
    public static final ArticleStats EMPTY = new ArticleStats(0, 0, false);
}
