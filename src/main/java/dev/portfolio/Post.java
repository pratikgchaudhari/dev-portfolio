package dev.portfolio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record Post(String slug, String title, LocalDate date, String summary, String tag, int minutes, String html,
                   List<String> relatedSlugs) {
    public Post {
        relatedSlugs = List.copyOf(relatedSlugs);
    }

    public String displayDate() {
        return date.format(DateTimeFormatter.ofPattern("MMM d, yyyy", java.util.Locale.ENGLISH));
    }
}
