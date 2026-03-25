package main;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GameLogger {
    // We target the root logger so it affects all classes in the project
    private static final Logger logger = Logger.getLogger("");

    public static void setup() {
        try {
            // Create a FileHandler that saves to "game_log.txt"
            // The 'true' means it will append to the file instead of overwriting it
            FileHandler fileHandler = new FileHandler("game_log.txt", true);
            // Set the format to simple text (otherwise it saves as messy XML)
            fileHandler.setFormatter(new SimpleFormatter());

            logger.addHandler(fileHandler);

            // Optional: Set the level to only log important errors and warnings
            logger.setLevel(Level.WARNING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
