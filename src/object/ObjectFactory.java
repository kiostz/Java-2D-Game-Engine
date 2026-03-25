package object;

import entity.Entity;
import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ObjectFactory {

    GamePanel gp;
    // Cache object definitions so we don't re-read JSON every time
    private final Map<String, ObjectData> objectDataMap = new HashMap<>();

    public ObjectFactory(GamePanel gp) {
        this.gp = gp;
        loadObjectData();
    }

    // Inner class to hold the data from JSON
    private static class ObjectData {
        String name;
        String imagePath;
        boolean collision;
    }

    private void loadObjectData() {
        try {
            // Get JSON from resources
            InputStream is = getClass().getResourceAsStream("/object/objects.json");
            if (is == null) {
                System.err.println("Could not find objects.json!");
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonBuilder.append(line);
            }
            br.close();

            // Parse JSON
            parseJson(jsonBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJson(String json) {
        // Remove brackets and split by objects
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

        // Split by "}," to separate objects (very basic parser)
        String[] objects = json.split("},");

        for (String objStr : objects) {
            ObjectData data = new ObjectData();
            
            // Extract Name
            if (objStr.contains("\"name\":")) {
                int start = objStr.indexOf("\"name\":") + 7; // "name": length is 7
                int quoteStart = objStr.indexOf("\"", start);
                int quoteEnd = objStr.indexOf("\"", quoteStart + 1);
                data.name = objStr.substring(quoteStart + 1, quoteEnd);
            }
            
            // Extract Image Path
            if (objStr.contains("\"imagePath\":")) {
                int start = objStr.indexOf("\"imagePath\":") + 12; // "imagePath": length is 12
                int quoteStart = objStr.indexOf("\"", start);
                int quoteEnd = objStr.indexOf("\"", quoteStart + 1);
                data.imagePath = objStr.substring(quoteStart + 1, quoteEnd);
            }

            // Extract Collision
            if (objStr.contains("\"collision\":")) {
                int start = objStr.indexOf("\"collision\":") + 12;
                int end = objStr.indexOf(",", start);
                if (end == -1) end = objStr.indexOf("}", start); // Handle last element
                if (end == -1) end = objStr.length();
                
                String boolStr = objStr.substring(start, end).trim();
                // Remove any trailing brace if present
                if (boolStr.endsWith("}")) boolStr = boolStr.substring(0, boolStr.length() - 1).trim();
                
                data.collision = Boolean.parseBoolean(boolStr);
            }

            if (data.name != null) {
                objectDataMap.put(data.name, data);
            }
        }
    }

    public Entity createObject(String objectName) {
        ObjectData data = objectDataMap.get(objectName);
        if (data == null) {
            System.err.println("Object not found in JSON: " + objectName);
            return null;
        }

        Entity obj = new Entity(gp);
        obj.name = data.name;
        obj.collision = data.collision;
        
        // Load and scale image
        try {
            UtilityTool uTool = new UtilityTool();
            // Ensure path starts with /
            String path = data.imagePath;
            if (!path.startsWith("/")) path = "/" + path;
            
            BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path)));
            obj.image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (Exception e) {
            System.err.println("Failed to load image for " + objectName + " at " + data.imagePath);
            e.printStackTrace();
        }

        return obj;
    }
}
