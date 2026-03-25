package main;

import entity.Entity;
import entity.Player;
import graphics.WorldRenderer;
import object.ObjectFactory;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3; // to scale 16x16 x 3 = 48x48

    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    // FPS
    int FPS = 60;

    // SYSTEM
    TileManager tileManager = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    Sound soundMusic = new Sound();
    Sound soundSE = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public FontManager fontManager = new FontManager(); // Initialize FontManager before UI
    public UI ui = new UI(this);
    public Thread gameThread;
    
    // --- RENDERER ---
    public WorldRenderer worldRenderer = new WorldRenderer(this);

    // ENTITY AND OBJECT
    public Player player = new Player(this, keyH);
    public Entity[] obj = new Entity[10]; // Use Entity array for objects
    public Entity[] npc = new Entity[10];
    public ObjectFactory objFactory = new ObjectFactory(this);
    
    // --- INTERACTION ---
    public Entity interactingEntity = null; // The entity the player is currently interacting with

    // GAME STATE
    public int gameState;
    public static final int titleState = 0;
    public static final int playState = 1;
    public static final int pauseState = 2;
    public static final int dialogueState = 3;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // Better rendering performance
        this.addKeyListener(keyH); // Allow GamePanel to recognize key input
        this.setFocusable(true); // GamePanel will be focused to receive key input
    }

    public void setUpGame() {
        aSetter.setObject(); // Set objects
        aSetter.setNPC(); // Set NPCs
        gameState = titleState; // Set game state
        playMusic(0);
    }

    public void startGameThread() {
        gameThread = new Thread(this); // pass GamePanel into this thread
        gameThread.start(); // will auto call the run method
    }

    public void run() {
        double drawInterval = (double) 1000000000 /FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;
            if (delta >= 1) {
                // UPDATE: update info such as character position
                update();
                // DRAW: draw the screen with the updated info
                repaint(); // call paintComponent
                delta--;
                drawCount++;
            }

            if(timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        if (gameState == playState) {
            // PLAYER
            player.update();
            // NPC
            for (Entity entity : npc) {
                if (entity != null) {
                    entity.update();
                }
            }
        } else if (gameState == pauseState) {
            // when paused, do nothing
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g); // use paintComponent on JPanel
        Graphics2D g2 = (Graphics2D)g; // convert to use 2D
        
        // DEBUG
        long drawStart = 0;
        if (keyH.checkDrawTime) {
            drawStart = System.nanoTime();
        }

        // MAIN MENU TITLE SCREEN
        if (gameState == titleState) {
            ui.draw(g2);
        } else {
            // TILE
            tileManager.draw(g2);
            
            // --- WORLD RENDERER ---
            // The renderer handles drawing all entities (player, NPCs, objects) in the correct order.
            worldRenderer.draw(g2);

            // UI
            ui.draw(g2);

            // DEBUG
            if (keyH.checkDrawTime) {
                long drawEnd = System.nanoTime();
                long drawTime = drawEnd - drawStart;
                g2.setColor(Color.black);
                g2.drawString("DrawTime: " + drawTime, 10, 96);
                System.out.println("DrawTime: " + drawTime);
            }
        }
        g2.dispose(); // good practice to save memories
    }
    public void playMusic(int index) {
        soundMusic.setClip(index);
        if (index == 5) {
            soundMusic.setVolume(0.5f);
        } else {
            soundMusic.setVolume(0.1f);
        }
        soundMusic.play();
        soundMusic.loop();
    }
    public void stopMusic() {
        soundMusic.stop();
    }
    public void playSound(int index) {
        soundSE.setClip(index);
        soundSE.setVolume(1.0f);
        soundSE.play();
    }
}
