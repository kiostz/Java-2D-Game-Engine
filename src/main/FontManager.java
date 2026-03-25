package main;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class FontManager {

    private Font customFont;

    public FontManager() {
        loadFonts();
    }

    private void loadFonts() {
        // Attempt to load the custom font
        try (InputStream is = getClass().getResourceAsStream("/fonts/ArkPixelFont_CN.ttf")) {
            if (is != null) {
                // Create the font from the stream
                customFont = Font.createFont(Font.TRUETYPE_FONT, is);
            } else {
                // Fallback if file not found
                System.out.println("Font file not found. Using default Dialog font.");
                customFont = new Font("Dialog", Font.PLAIN, 24);
            }
        } catch (FontFormatException | IOException e) {
            // Log error and use fallback
            System.err.println("Error loading font. Using default Dialog font.");
            e.printStackTrace();
            customFont = new Font("Dialog", Font.PLAIN, 24);
        }
    }

    // Getter to provide the font to other classes
    public Font getFont() {
        return customFont;
    }
}