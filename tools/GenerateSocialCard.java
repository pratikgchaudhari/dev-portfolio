import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

// Run from the project directory: java tools/GenerateSocialCard.java [name] [domain]
class GenerateSocialCard {
    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        String name = args.length > 0 ? args[0] : "Pratik Chaudhari";
        String domain = args.length > 1 ? args[1] : "notnullpratik.dev";
        var image = new BufferedImage(1200, 630, BufferedImage.TYPE_INT_RGB);
        var canvas = image.createGraphics();
        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        canvas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        canvas.setColor(new Color(0xfbfcfe));
        canvas.fillRect(0, 0, 1200, 630);
        canvas.setColor(new Color(0x264beb));
        canvas.fillRect(0, 0, 12, 630);
        canvas.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        canvas.drawString(domain, 84, 90);
        canvas.setColor(new Color(0x17202c));
        canvas.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 68));
        canvas.drawString(name, 80, 222);
        canvas.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 52));
        canvas.drawString("Building software.", 84, 320);
        canvas.setColor(new Color(0x264beb));
        canvas.setFont(new Font(Font.SERIF, Font.ITALIC, 58));
        canvas.drawString("Sharing what I learn.", 82, 390);
        canvas.setColor(new Color(0xdde3eb));
        canvas.drawLine(84, 472, 1116, 472);
        canvas.setColor(new Color(0x606b7b));
        canvas.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
        canvas.drawString("Java  ·  Spring Boot  ·  Web development", 84, 536);
        canvas.dispose();
        Path output = Path.of("src/main/resources/static/images/social-card.png");
        Files.createDirectories(output.getParent());
        ImageIO.write(image, "png", output.toFile());
        System.out.println(output.toAbsolutePath());
    }
}
