package entity;

import main.GamePanel;
import main.UtilityTool;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class NPC_OldMan extends Entity {

    public NPC_OldMan(GamePanel gp) {
        super(gp);
        direction = "down";
        speed = 1;
        
        // NPC STATUS
        maxLife = 10;
        life = maxLife;

        loadAnimations("/npc/oldman_walk.png", "/npc/oldman_idle.png");
        setDialogue();
        
        // Set the sound to be played when this NPC speaks
        setInteractionSound(1);
    }

    public void setAction() {
        // Call the parent's setAction first to handle interaction pause
        super.setAction();
        
        actionLockCounter++; // Increment the counter every frame (60 times per second)

        if (actionLockCounter == 120) {
            Random rand = new Random();
            int i = rand.nextInt(100)+1; // random num between 1 - 100

            if (i <= 25) {
                direction = "up";
            } else if (i <= 50) {
                direction = "down";
            } else if (i <= 75) {
                direction = "left";
            } else if (i <= 100) {
                direction = "right";
            }

            actionLockCounter = 0; // Reset the counter to 0 so it can start counting another 2 seconds
        }
    }
    public void setDialogue() {
        dialogues[0] = "GOGO 我要LV BAG";
        dialogues[1] = "我不理 我要LV BAG";
        dialogues[2] = "我就要LV BAG";
        dialogues[3] = "gorgor 我要LV BAG";
    }
}
