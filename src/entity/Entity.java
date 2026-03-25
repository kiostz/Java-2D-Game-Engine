package entity;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.ArrayList;
import java.util.logging.Level;
import graphics.Animation;

public class Entity {
    GamePanel gp;
    public double worldX, worldY; // World coordinates of this entity
    public int speed;

    // --- PROPERTIES FOR OBJECT ---
    public BufferedImage image; // For static objects
    public String name;
    public boolean collision = false; // Default collision property for objects

    // ANIMATIONS
    public Animation up, down, left, right;
    public Animation idleUp, idleDown, idleLeft, idleRight;
    public Animation currentAnim;
    
    public String direction = "down";   // Stores current facing direction
    public boolean moving = false;      // Indicate if the entity is moving

    public int solidAreaDefaultX = 8;
    public int solidAreaDefaultY = 16;
    public Rectangle solidArea = new Rectangle(solidAreaDefaultX, solidAreaDefaultY, 28, 30); // Default HitBox for all entity
    public boolean collisionOn = false;
    public int actionLockCounter = 0;
    String dialogues[] = new String[20];
    int dialogueIndex = 0;
    
    // CHARACTER STATUS
    public int maxLife;
    public int life;
    
    // --- INTERACTION STATE ---
    public boolean isInteracting = false; // Flag to check if the entity is in an interaction
    
    // SOUND
    private int interactionSound = -1; // Default to no sound

    // --- DIALOGUE SYSTEM ---
    public ArrayList<ChatBubble> chatBubbles = new ArrayList<>();
    public Animation continuePrompt;

    public static class ChatBubble {
        public String text;
        public int timer;
        public ChatBubble(String text) {
            this.text = text;
            this.timer = 180; // 3 seconds * 60 FPS
        }
    }

    public Entity(GamePanel gp) {
        this.gp = gp;
        loadContinuePrompt();
    }
    
    private void loadContinuePrompt() {
        try {
            BufferedImage sheet = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/ui/UI_PressF.png")));
            BufferedImage[] frames = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                frames[i] = sheet.getSubimage(i * 32, 0, 32, 32);
            }
            continuePrompt = new Animation(frames, 15); // Frame delay of 15
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setAction() {}
    
    public void setInteractionSound(int soundIndex) {
        this.interactionSound = soundIndex;
    }

    public void speak() {
        // If already interacting, advance the dialogue. Otherwise, start it.
        if (isInteracting) {
            // If there are no more dialogues, end the interaction.
            if (dialogues[dialogueIndex] == null) {
                endInteraction();
                return;
            }
        } else {
            // Start the interaction.
            isInteracting = true;
        }
        
        // Add the current dialogue to the chat bubble list.
        chatBubbles.add(new ChatBubble(dialogues[dialogueIndex]));
        dialogueIndex++;

        // Make the NPC face the player.
        switch (gp.player.direction) {
            case "up":
                direction = "down";
                break;
            case "down":
                direction = "up";
                break;
            case "left":
                direction = "right";
                break;
            case "right":
                direction = "left";
                break;
        }
        
        // Force update the current animation to match the new direction immediately
        if ("up".equals(direction)) currentAnim = idleUp;
        else if ("down".equals(direction)) currentAnim = idleDown;
        else if ("left".equals(direction)) currentAnim = idleLeft;
        else if ("right".equals(direction)) currentAnim = idleRight;
        
        // Play the interaction sound if one has been set.
        if (interactionSound != -1) {
            gp.playSound(interactionSound);
        }
    }
    
    /**
     * Ends the current interaction, allowing the entity to move again.
     */
    public void endInteraction() {
        this.isInteracting = false;
        this.dialogueIndex = 0; // Reset dialogue for the next conversation.
    }

    public void update() {
        // Always update dialogue so text fades out even during interaction
        updateDialogue();

        // If the entity is currently interacting, it should not move or update its AI.
        if (isInteracting) {
            moving = false; // Ensure the entity is visually in an idle state.

            // Update the prompt animation
            continuePrompt.update();
            
            // Also update the entity's idle animation so they don't freeze completely
            if (currentAnim != null) {
                currentAnim.update();
            }
            
            return; // Skip the rest of the update logic to pause the entity.
        }
        
        setAction();

        // CHECK TILE COLLISION
        collisionOn = false;
        gp.cChecker.checkTile(this);
        //CHECK OBJECT COLLISION
        gp.cChecker.checkObject(this, false);
        // CHECK PLAYER COLLISION
        gp.cChecker.checkPlayer(this);
        //IF COLLISION IS FALSE, ENTITY CAN MOVE

        if(!collisionOn) {
            // Only update moving state if we are actually free to move
            // (Note: Player overrides update, so this is mostly for NPCs)
            moving = true;
            switch (direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        } else {
            moving = false;
        }

        // ANIMATION UPDATE
        // Set the current animation based on state
        if (moving) {
            if ("up".equals(direction)) currentAnim = up;
            else if ("down".equals(direction)) currentAnim = down;
            else if ("left".equals(direction)) currentAnim = left;
            else if ("right".equals(direction)) currentAnim = right;
        } else {
            if ("up".equals(direction)) currentAnim = idleUp;
            else if ("down".equals(direction)) currentAnim = idleDown;
            else if ("left".equals(direction)) currentAnim = idleLeft;
            else if ("right".equals(direction)) currentAnim = idleRight;
        }
        
        if (currentAnim != null) {
            currentAnim.update();
        }
    }

    public void updateDialogue() {
        for (int i = 0; i < chatBubbles.size(); i++) {
            chatBubbles.get(i).timer--;
            if (chatBubbles.get(i).timer <= 0) {
                chatBubbles.remove(i);
                i--;
            }
        }
    }
    
    public BufferedImage setupSheet(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
    
    public void loadAnimations(String walkPath, String idlePath) {
        // Utility for scaling
        UtilityTool uTool = new UtilityTool();
        // Load sheets using the method we created earlier
        BufferedImage walkSheet = setupSheet(walkPath);
        BufferedImage idleSheet = setupSheet(idlePath);

        BufferedImage[] upFrames = new BufferedImage[8];
        BufferedImage[] downFrames = new BufferedImage[8];
        BufferedImage[] rightFrames = new BufferedImage[8];
        BufferedImage[] leftFrames = new BufferedImage[8];
        BufferedImage[] upIdleFrames = new BufferedImage[4];
        BufferedImage[] downIdleFrames = new BufferedImage[4];
        BufferedImage[] rightIdleFrames = new BufferedImage[4];
        BufferedImage[] leftIdleFrames = new BufferedImage[4];

        for (int i = 0; i < 8; i++) {
            rightFrames[i] = uTool.scaleImage(walkSheet.getSubimage(i * 16, 0, 16, 16), gp.tileSize, gp.tileSize);
            leftFrames[i] = flipImage(rightFrames[i]);
            downFrames[i] = uTool.scaleImage(walkSheet.getSubimage(i * 16, 16, 16, 16), gp.tileSize, gp.tileSize);
            upFrames[i] = uTool.scaleImage(walkSheet.getSubimage(i * 16, 32, 16, 16), gp.tileSize, gp.tileSize);
        }

        // Extraction loop for idle frames
        for (int i = 0; i < 4; i++) {
            rightIdleFrames[i] = uTool.scaleImage(idleSheet.getSubimage(i * 16, 0, 16, 16), gp.tileSize, gp.tileSize);
            leftIdleFrames[i] = flipImage(rightIdleFrames[i]);
            downIdleFrames[i] = uTool.scaleImage(idleSheet.getSubimage(i * 16, 16, 16, 16), gp.tileSize, gp.tileSize);
            upIdleFrames[i] = uTool.scaleImage(idleSheet.getSubimage(i * 16, 32, 16, 16), gp.tileSize, gp.tileSize);
        }
        
        // Initialize Animation objects
        // 3 is the frame delay (speed). Adjust as needed.
        up = new Animation(upFrames, 5);
        down = new Animation(downFrames, 5);
        left = new Animation(leftFrames, 5);
        right = new Animation(rightFrames, 5);
        
        idleUp = new Animation(upIdleFrames, 10);
        idleDown = new Animation(downIdleFrames, 10);
        idleLeft = new Animation(leftIdleFrames, 10);
        idleRight = new Animation(rightIdleFrames, 10);
    }

    public BufferedImage flipImage(BufferedImage img) {
        // Get dimensions of original image
        int w = img.getWidth();
        int h = img.getHeight();
        // Create new buffer
        BufferedImage flipped = new BufferedImage(w, h, img.getType());
        // Create graphics context for drawing
        Graphics2D g = flipped.createGraphics();
        // Draw mirrored image (negative width flips it)
        g.drawImage(img, w, 0, -w, h, null);
        // Release resources
        g.dispose();
        // Return result
        return flipped;
    }
}
