package entity;

import main.GamePanel;
import main.KeyHandler;
import main.UtilityTool;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public class Player extends Entity{
    private static final Logger logger = Logger.getLogger(Player.class.getName()); // Create a logger specific to this class

    KeyHandler keyH;

    public final int screenX, screenY; // Indicate where we draw player on the screen
    public int hasKey = 0;

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);
        this.gp = gp;
        this.keyH = keyH;

        // Put player in middle of screen, screen / 2 = half, tileSize / 2 = exactly half
        screenX = gp.screenWidth / 2 - (gp.tileSize / 2); // Subtract half tile length
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle(8, 16, 28, 30);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        loadAnimations("/player/walk.png", "/player/idle.png");
    }
    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
        
        // PLAYER STATUS
        maxLife = 6;
        life = 3;
    }
    public void update() {
        // Check if any NPC is currently in an interaction state.
        boolean isAnyNpcInteracting = false;
        for (Entity npc : gp.npc) {
            if (npc != null && npc.isInteracting) {
                isAnyNpcInteracting = true;
                break;
            }
        }
        
        // If not interacting, allow player movement.
        // Also check if key is pressed to move player
        if (!isAnyNpcInteracting && (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed)) {
            // --- NORMALIZED MOVEMENT HANDLING ---
            double moveX = 0;
            double moveY = 0;

            // Determine base movement vector from key presses
            if (keyH.upPressed) { moveY -= 1; }
            if (keyH.downPressed) { moveY += 1; }
            if (keyH.leftPressed) { moveX -= 1; }
            if (keyH.rightPressed) { moveX += 1; }

            // --- SET ANIMATION DIRECTION (Horizontal priority) ---
            // We determine the final direction for the sprite animation here, before any movement happens.
            // This ensures that if moving diagonally, the left/right animation takes precedence.
            // The result is stored in a local variable to be applied after all movement logic is complete.
            String finalDirection = direction; // Default to the current direction to avoid standing still animation
            if (keyH.leftPressed) {
                finalDirection = "left";
            } else if (keyH.rightPressed) {
                finalDirection = "right";
            } else if (keyH.upPressed) {
                finalDirection = "up";
            } else if (keyH.downPressed) {
                finalDirection = "down";
            }

            // If moving diagonally, normalize the vector to prevent faster speed
            if (moveX != 0 && moveY != 0) {
                // The length of a (1,1) vector is sqrt(2)
                moveX /= Math.sqrt(2);
                moveY /= Math.sqrt(2);
            }

            // Apply speed to the final normalized vector
            moveX *= speed;
            moveY *= speed;

            // Apply movement and check for collisions on each axis separately
            handleMovement(moveX, 0); // Handle horizontal movement
            handleMovement(0, moveY); // Handle vertical movement

            // After collision checks are done, set the final direction for the animation frame.
            direction = finalDirection;

            moving = true; // Set moving flag for animation
        } else {
            moving = false; // Player is not moving
        }
        
        // --- NEW INTERACTION LOGIC ---
        // Check if the interaction key is pressed
        if (gp.keyH.interactPressed) {
            interact(); // Call the new interaction method
            gp.keyH.interactPressed = false; // Consume the key press to prevent repeated interactions
        }
        
        // Update dialogue bubbles for player (if player ever speaks)
        updateDialogue();
        
        // --- UPDATE ANIMATION ---
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

    public void pickUpObject(int i) {
        if (i != 999) {
            String objectName = gp.obj[i].name;

            switch (objectName) {
                case "Key":
                    gp.playSound(2);
                    hasKey++;
                    gp.obj[i] = null;
                    gp.ui.showMessage("You got a key!");
                    break;
                case "Door":
                    if (hasKey > 0) {
                        gp.playSound(3);
                        gp.obj[i] = null;
                        hasKey--;
                        gp.ui.showMessage("You opened the door!");
                    } else {
                        gp.ui.showMessage("You need a key!");
                    }
                    break;
                case "Chest":
                    gp.ui.gameFinished = true;
                    gp.stopMusic();
                    gp.playSound(4);
                    break;
                case "Coin":
                    gp.playSound(1);
                    gp.obj[i] = null;
                    gp.ui.showMessage("You got a coin!");
                    break;
            }
        }
    }
    
    /**
     * A refactored method to apply movement on a single axis and check for collisions.
     * @param moveX The amount to move on the X-axis for this check.
     * @param moveY The amount to move on the Y-axis for this check.
     */
    private void handleMovement(double moveX, double moveY) {
        if (moveX == 0 && moveY == 0) {
            return; // No movement to process
        }

        // Set direction based on movement for animations and collision checks
        if (moveX > 0) direction = "right";
        if (moveX < 0) direction = "left";
        if (moveY > 0) direction = "down";
        if (moveY < 0) direction = "up";

        // Temporarily apply movement to check for future collision
        worldX += moveX;
        worldY += moveY;

        // Reset collision flag and perform all collision checks for the new potential position
        collisionOn = false;
        gp.cChecker.checkTile(this);
        int objIndex = gp.cChecker.checkObject(this, true);
        pickUpObject(objIndex);
        gp.cChecker.checkEntity(this, gp.npc);

        // If a collision was detected, revert the movement on this axis
        if (collisionOn) {
            worldX -= moveX;
            worldY -= moveY;
        }
    }

    /**
     * Handles interaction with the closest NPC or object the player is facing.
     */
    public void interact() {
        // If an interaction is already happening, the key press is for that interaction.
        if (gp.interactingEntity != null) {
            gp.interactingEntity.speak();
            // If the interaction ended, clear the tracking field.
            if (!gp.interactingEntity.isInteracting) {
                gp.interactingEntity = null;
            }
            return;
        }
        
        // Otherwise, find a new entity to interact with.
        Rectangle interactionArea = getInteractionArea();
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        // Find the closest NPC in the interaction area.
        for (Entity npc : gp.npc) {
            if (npc != null) {
                // Create a temporary rectangle for the NPC's world position hitbox
                Rectangle npcSolidAreaWorld = new Rectangle(
                    (int)npc.worldX + npc.solidArea.x, // Cast worldX to int for Rectangle
                    (int)npc.worldY + npc.solidArea.y, // Cast worldY to int for Rectangle
                    npc.solidArea.width,
                    npc.solidArea.height
                );
                if (interactionArea.intersects(npcSolidAreaWorld)) {
                    // Calculate the distance between the player and the NPC
                    double distance = getDistance(npc);
                    // If this NPC is closer than the previous closest one, update it
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestEntity = npc;
                    }
                }
            }
        }
        
        // If an entity was found, start the interaction.
        if (closestEntity != null) {
            gp.interactingEntity = closestEntity; // Track the interacting entity.
            closestEntity.speak();
        }
    }

    /**
     * Creates a rectangle in front of the player to detect interactions.
     * @return The interaction area as a Rectangle.
     */
    public Rectangle getInteractionArea() {
        // Start with the player's position
        int interactionX = (int)worldX; // Cast double to int
        int interactionY = (int)worldY; // Cast double to int
        
        // The size of the interaction area is one tile
        int interactionWidth = gp.tileSize;
        int interactionHeight = gp.tileSize;

        // Offset the area based on the player's direction
        switch (direction) {
            case "up":
                interactionY -= gp.player.solidAreaDefaultY - 6; // Place area above the player
                interactionX += 8; // Move the area to the center of player hitbox;
                interactionHeight = gp.tileSize / 2; // Half the height
                interactionWidth = gp.player.solidArea.width; // Full width of the player's hitbox
                break;
            case "down":
                interactionY += gp.tileSize; // Place area below the player
                interactionX += 8; // Move the area to the center of player hitbox;
                interactionHeight = gp.tileSize / 2; // Half the height
                interactionWidth = gp.player.solidArea.width; // Full width of the player's hitbox
                break;
            case "left":
                interactionX -= 16; // Place area to the left of the player
                interactionY += 16;
                interactionWidth = gp.tileSize / 2; // Half the width
                interactionHeight = gp.player.solidArea.height;
                break;
            case "right":
                interactionX += 36; // Place area to the right of the player
                interactionY += 16;
                interactionWidth = gp.tileSize / 2; // Half the width
                interactionHeight = gp.player.solidArea.height;
                break;
        }
        // Return the final calculated interaction area
        return new Rectangle(interactionX, interactionY, interactionWidth, interactionHeight);
    }
    
    /**
     * Calculates the distance between the player and another entity.
     * @param entity The entity to measure the distance to.
     * @return The distance as a double.
     */
    private double getDistance(Entity entity) {
        // Use the Pythagorean theorem to calculate the distance
        return Math.sqrt(Math.pow(worldX - entity.worldX, 2) + Math.pow(worldY - entity.worldY, 2));
    }
}
