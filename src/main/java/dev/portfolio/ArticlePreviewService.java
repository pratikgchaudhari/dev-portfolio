package dev.portfolio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ArticlePreviewService {
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 630;
    // Increment when changing the layout so social crawlers see a new image URL.
    private static final String DESIGN_VERSION = "1";
    private static final int MAX_CACHED_IMAGES = 128;
    private static final int TEXT_WIDTH = 1048;
    private static final Color PAPER = new Color(0xfbfcfe);
    private static final Color INK = new Color(0x17202c);
    private static final Color MUTED = new Color(0x606b7b);
    private static final Color BLUE = new Color(0x264beb);
    private final String name;
    private final String brand;
    private final Map<String, Preview> cache = new LinkedHashMap<>(16, .75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Preview> eldest) {
            return size() > MAX_CACHED_IMAGES;
        }
    };

    public ArticlePreviewService(@Value("${portfolio.name}") String name,
                                 @Value("${portfolio.site-url:}") String siteUrl) {
        this.name = name;
        String origin = SeoService.validateOrigin(siteUrl);
        this.brand = origin.isEmpty() ? name : URI.create(origin).getHost();
    }

    public String path(Post post) {
        return "/images/articles/" + post.slug() + ".png?v=" + version(post);
    }

    public String alt(Post post) {
        return post.title() + " — " + name + " · " + post.tag();
    }

    private String version(Post post) {
        // Only inputs visible on the card affect its URL, not the request Host or cookies.
        return digest(String.join("\0", DESIGN_VERSION, name, brand, post.title(), post.tag(),
                post.date().toString(), Integer.toString(post.minutes())).getBytes(StandardCharsets.UTF_8));
    }

    public synchronized Preview image(Post post) {
        // Bound memory use and serialize rendering; a bot burst cannot allocate unlimited canvases.
        return cache.computeIfAbsent(version(post), ignored -> {
            byte[] png = render(post);
            return new Preview(png, "\"" + digest(png) + "\"");
        });
    }

    private byte[] render(Post post) {
        var image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setColor(PAPER);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(BLUE);
            g.fillRect(0, 0, 10, HEIGHT);

            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 27));
            g.setColor(INK);
            String wordmark = fitLine(brand, g, 860);
            g.drawString(wordmark, 76, 85);
            g.setColor(BLUE);
            g.fillRect(82 + g.getFontMetrics().stringWidth(wordmark), 61, 3, 29);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 19));
            g.setColor(MUTED);
            g.drawString("ARTICLE", 1038, 83);
            g.setColor(new Color(0xdde3eb));
            g.drawLine(76, 122, 1124, 122);

            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 21));
            String tag = fitLine(clean(post.tag()).toUpperCase(Locale.ROOT), g, TEXT_WIDTH - 32);
            g.setColor(new Color(0xeef2ff));
            g.fillRoundRect(76, 152, g.getFontMetrics().stringWidth(tag) + 32, 40, 12, 12);
            g.setColor(BLUE);
            g.drawString(tag, 92, 179);

            drawTitle(g, post.title());

            g.setColor(new Color(0xdde3eb));
            g.drawLine(76, 518, 1124, 518);
            g.setColor(INK);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
            g.drawString(fitLine(name, g, 560), 76, 571);
            g.setColor(MUTED);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
            String details = post.displayDate() + "  ·  " + post.minutes() + " min read";
            g.drawString(details, 1124 - g.getFontMetrics().stringWidth(details), 571);
        } finally {
            g.dispose();
        }
        try (var bytes = new ByteArrayOutputStream(); var output = new MemoryCacheImageOutputStream(bytes)) {
            ImageIO.write(image, "png", output);
            output.flush();
            return bytes.toByteArray();
        } catch (IOException exception) {
            throw new UncheckedIOException("Cannot render article preview", exception);
        }
    }

    private void drawTitle(Graphics2D g, String title) {
        String text = clean(title);
        List<String> lines = List.of();
        for (int size = 68; size >= 38; size -= 2) {
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size));
            lines = wrap(text, g);
            if (lines.size() <= 4 && lines.size() * g.getFontMetrics().getHeight() <= 274) break;
        }
        int lineHeight = g.getFontMetrics().getHeight();
        int maxLines = Math.min(4, 274 / lineHeight);
        if (lines.size() > maxLines) {
            lines = new ArrayList<>(lines.subList(0, maxLines));
            // Exceptionally long titles stay inside the card; the HTML metadata keeps the full title.
            lines.set(maxLines - 1, fitLine(lines.get(maxLines - 1) + "…", g, TEXT_WIDTH));
        }
        float y = 218 + (274 - lines.size() * lineHeight) / 2f + g.getFontMetrics().getAscent();
        g.setColor(INK);
        for (String line : lines) {
            g.drawString(line, 76, y);
            y += lineHeight;
        }
    }

    private static List<String> wrap(String text, Graphics2D g) {
        var attributed = new AttributedString(text);
        attributed.addAttribute(TextAttribute.FONT, g.getFont());
        var measurer = new LineBreakMeasurer(attributed.getIterator(), g.getFontRenderContext());
        var lines = new ArrayList<String>();
        while (measurer.getPosition() < text.length()) {
            int start = measurer.getPosition();
            measurer.nextLayout(TEXT_WIDTH);
            lines.add(text.substring(start, measurer.getPosition()).strip());
            // Five lines are enough to know that this size will not fit.
            if (lines.size() == 5) break;
        }
        return lines;
    }

    private static String fitLine(String text, Graphics2D g, int width) {
        String result = clean(text);
        if (g.getFontMetrics().stringWidth(result) <= width) return result;
        while (!result.isEmpty() && g.getFontMetrics().stringWidth(result + "…") > width) {
            result = result.substring(0, result.offsetByCodePoints(result.length(), -1));
        }
        return result.stripTrailing() + "…";
    }

    private static String clean(String value) {
        String text = value.replaceAll("[\\p{Z}\\s]+", " ").strip();
        return text.isEmpty() ? "Notes" : text;
    }

    private static String digest(byte[] value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public record Preview(byte[] png, String etag) {}
}
