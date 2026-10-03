package dev.portfolio;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

final class ArticleSearch {
    static final int MAX_QUERY_LENGTH = 200;
    private static final Pattern WHITESPACE = Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern ACCENTS = Pattern.compile("\\p{M}+");

    private ArticleSearch() {
    }

    static String normalizeQuery(String query) {
        return WHITESPACE.matcher(query).replaceAll(" ").strip();
    }

    static List<Post> find(List<Post> published, String query) {
        if (query.isEmpty()) return published;
        String normalized = searchable(query);
        List<String> terms = Arrays.stream(normalized.split(" ")).filter(term -> !term.isEmpty()).distinct().toList();
        if (terms.isEmpty()) return List.of();
        return published.stream().map(post -> new Match(post, score(post, normalized, terms)))
                .filter(match -> match.score() > 0)
                .sorted(Comparator.comparingInt(Match::score).reversed()
                        .thenComparing(match -> match.post().date(), Comparator.reverseOrder())
                        .thenComparing(match -> match.post().slug()))
                .map(Match::post).toList();
    }

    private static int score(Post post, String query, List<String> terms) {
        String title = searchable(post.title());
        String tag = searchable(post.tag());
        String summary = searchable(post.summary());
        String body = searchable(post.searchText());
        int score = title.contains(query) ? 20 : 0;
        for (String term : terms) {
            // Treat query terms literally, including programming punctuation such as C++.
            if (title.contains(term)) score += 4;
            else if (tag.contains(term)) score += 3;
            else if (summary.contains(term)) score += 2;
            else if (body.contains(term)) score += 1;
            else return 0;
        }
        return score;
    }

    private static String searchable(String value) {
        return normalizeQuery(ACCENTS.matcher(Normalizer.normalize(value, Normalizer.Form.NFD))
                .replaceAll("").toLowerCase(Locale.ROOT));
    }

    private record Match(Post post, int score) {
    }
}
