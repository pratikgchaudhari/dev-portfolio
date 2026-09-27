package dev.portfolio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.gfm.tables.TablesExtension;

import java.nio.file.*;
import java.io.IOException;
import java.time.*;
import java.util.*;

@Service
public class BlogService {
    private final Path directory;
    private final Parser parser = Parser.builder().extensions(List.of(TablesExtension.create())).build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().extensions(List.of(TablesExtension.create())).escapeHtml(true).sanitizeUrls(true).build();

    public BlogService(@Value("${portfolio.content-dir:content/posts}") String directory) {
        this.directory = Path.of(directory);
    }

    public List<Post> posts() {
        if (!Files.isDirectory(directory))
            throw new IllegalStateException("Blog directory missing: " + directory.toAbsolutePath());
        try (var files = Files.list(directory)) {
            return files.filter(p -> p.getFileName().toString().matches("[a-z0-9]+(?:-[a-z0-9]+)*\\.md"))
                    .map(this::read).filter(Objects::nonNull).sorted(Comparator.comparing(Post::date).reversed().thenComparing(Post::slug)).toList();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read blog directory", e);
        }
    }

    private Post read(Path file) {
        try {
            return parse(file.getFileName().toString().replaceFirst("\\.md$", ""), Files.readString(file));
        } catch (Exception e) {
            throw new IllegalStateException("Invalid blog post: " + file.getFileName(), e);
        }
    }

    Post parse(String slug, String source) {
        String normalized = source.replace("\r\n", "\n");
        if (!normalized.startsWith("---\n")) throw new IllegalArgumentException("Front matter required");
        int end = normalized.indexOf("\n---\n", 4);
        if (end < 0) throw new IllegalArgumentException("Closing front matter delimiter required");
        Map<String, String> meta = new HashMap<>();
        for (String line : normalized.substring(4, end).split("\n")) {
            if (line.isBlank() || line.startsWith("#")) continue;
            int colon = line.indexOf(':');
            if (colon < 1) throw new IllegalArgumentException("Use key: value metadata");
            String value = line.substring(colon + 1).strip();
            if (value.length() > 1 && ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))))
                value = value.substring(1, value.length() - 1);
            meta.put(line.substring(0, colon).strip(), value);
        }
        LocalDate date = LocalDate.parse(required(meta, "date"));
        if (Boolean.parseBoolean(meta.getOrDefault("draft", "false")) || date.isAfter(LocalDate.now(ZoneOffset.UTC)))
            return null;
        String body = normalized.substring(end + 5);
        return new Post(slug, required(meta, "title"), date, required(meta, "summary"), meta.getOrDefault("tag", "Notes"), Math.max(1, (body.split("\\s+").length + 199) / 200), renderer.render(parser.parse(body)));
    }

    private String required(Map<String, String> meta, String key) {
        String v = meta.get(key);
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Missing " + key);
        return v;
    }
}
