package tile;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TileManager {
    private static final Logger logger = Logger.getLogger(TileManager.class.getName()); // Create a logger specific to this class

    GamePanel gp;
    public Tile[] tile;
    public int[][] mapTileNum;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();
        loadMap("/maps/world01.txt");
    }

    public void getTileImage() {
        setup(0, "stone.png", false);
        setup(1, "wood.png", false);
        setup(2, "brick.png", true);
        setup(3, "Grass_Middle.png", false);
        setup(4, "Path_Middle.png", false);
        setup(5, "Water_Middle.png", true);
    }
    public void setup(int index, String imagePath, boolean collision) {
        UtilityTool util = new UtilityTool();
        try {
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/" + imagePath)));
            tile[index].image = util.scaleImage(tile[index].image, gp.tileSize, gp.tileSize);
            tile[index].collision = collision;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void loadMap(String filepath) {
        try {
            InputStream is = getClass().getResourceAsStream(filepath);
            assert is != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0, row = 0;

            while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
                String line = br.readLine(); // Read the entire line

                while(col < gp.maxWorldCol) {
                    String[] numbers = line.split(" "); // Split line by spaces into an array
                    int num = Integer.parseInt(numbers[col]); // Convert string "0" to int 0

                    mapTileNum[col][row] = num; // Store in our 2D array
                    col++;
                }
                // Once the column loop finishes, reset for the next row
                if (col == gp.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load map at: " + filepath, e);
        }
    }
    public void draw(Graphics2D g2) {
        // PRE-CALCULATE BOUNDARIES ONCE
        int leftBoundary = (int) gp.player.worldX - gp.player.screenX;
        int rightBoundary = (int) gp.player.worldX + gp.player.screenX;
        int topBoundary = (int) gp.player.worldY - gp.player.screenY;
        int bottomBoundary = (int) gp.player.worldY + gp.player.screenY;

        // Standard nested for-loop is highly optimized by the JVM for grid traversal
        for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++) {
            for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++) {

                // Access the tile index from the 2D array
                int tileNum = mapTileNum[worldCol][worldRow];

                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;
                int screenX = (int) (worldX - gp.player.worldX + gp.player.screenX);
                int screenY = (int) (worldY - gp.player.worldY + gp.player.screenY);

                // Calculate coordinates: (column * size) for X, (row * size) for Y
                int x = worldCol * gp.tileSize;
                int y = worldRow * gp.tileSize;

                if (worldX + gp.tileSize > leftBoundary &&
                        worldX - gp.tileSize < rightBoundary &&
                        worldY + gp.tileSize > topBoundary &&
                        worldY - gp.tileSize < bottomBoundary) {
                    if (tile[tileNum] != null) { // Only draw if the tile object exists to avoid NullPointerException
                        g2.drawImage(tile[tileNum].image, screenX, screenY, null);
                    }
                }
            }
        }
    }
}
