package graphics;

import entity.Entity;
import entity.Player;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;

public class WorldRenderer {

    private final GamePanel gp;

    public WorldRenderer(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        // --- Y-SORTING RENDER PIPELINE ---
        // 1. Create a temporary list to hold all entities for sorting.
        ArrayList<Entity> entityList = new ArrayList<>();

        // 2. Add all entities (Player, NPCs, Objects) to the list.
        entityList.add(gp.player);
        for (Entity npc : gp.npc) {
            if (npc != null) entityList.add(npc);
        }
        for (Entity obj : gp.obj) {
            if (obj != null) entityList.add(obj);
        }

        // 3. Sort the list by the entity's worldY coordinate.
        entityList.sort(Comparator.comparingDouble(e -> e.worldY));

        // 4. Draw all entities in the sorted order.
        for (Entity entity : entityList) {
            drawEntity(g2, entity);
        }

        // 5. Clear the list for the next frame.
        entityList.clear();
        
        // --- IMMEDIATE REDRAW FOR INTERACTION ---
        // If an entity has just started interacting, its direction may have changed.
        // We redraw it one more time this frame to ensure the change is visible instantly.
        if (gp.interactingEntity != null) {
            drawEntity(g2, gp.interactingEntity);
        }
    }

    private void drawEntity(Graphics2D g2, Entity entity) {
        // Calculate the entity's position on the screen relative to the player.
        int screenX = (int) (entity.worldX - gp.player.worldX + gp.player.screenX);
        int screenY = (int) (entity.worldY - gp.player.worldY + gp.player.screenY);

        // Only draw entities that are within the screen boundaries to save resources.
        if (entity.worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
            entity.worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            entity.worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
            entity.worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            // --- Draw Sprite ---
            BufferedImage imageToDraw = null;
            if (entity.image != null) { // For static objects like keys or chests
                imageToDraw = entity.image;
            } else if (entity.currentAnim != null) { // For animated entities
                imageToDraw = entity.currentAnim.getFrame();
            }

            if (imageToDraw != null) {
                g2.drawImage(imageToDraw, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }

            // --- Draw UI Elements Attached to Entity ---
            drawLifeBar(g2, entity, screenX, screenY);
            drawDialogue(g2, entity, screenX, screenY);

            // --- Draw Debug Info ---
            if (gp.keyH.debugMode) {
                drawDebug(g2, entity, screenX, screenY);
            }
        }
    }

    private void drawLifeBar(Graphics2D g2, Entity entity, int screenX, int screenY) {
        if (entity.maxLife > 0) {
            double oneScale = (double) gp.tileSize / entity.maxLife;
            double hpBarValue = oneScale * entity.life;

            g2.setColor(new Color(35, 35, 35));
            g2.fillRect(screenX - 1, screenY - 16, gp.tileSize + 2, 8);

            g2.setColor(new Color(255, 0, 30));
            g2.fillRect(screenX, screenY - 15, (int) hpBarValue, 6);
        }
    }

    private void drawDialogue(Graphics2D g2, Entity entity, int screenX, int screenY) {
        if (entity.chatBubbles.isEmpty()) {
            return;
        }

        g2.setFont(gp.fontManager.getFont().deriveFont(Font.BOLD, 12F));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int yOffset = -25;

        // Iterate backwards to draw the newest message at the bottom.
        for (int i = entity.chatBubbles.size() - 1; i >= 0; i--) {
            Entity.ChatBubble cb = entity.chatBubbles.get(i);

            float alpha = 1.0f;
            if (cb.timer < 60) {
                alpha = cb.timer / 60f;
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            int length = (int) g2.getFontMetrics().getStringBounds(cb.text, g2).getWidth();
            int x = screenX + (gp.tileSize / 2) - (length / 2);
            int y = screenY + yOffset;

            g2.setColor(Color.BLACK);
            g2.drawString(cb.text, x + 1, y + 1);
            g2.setColor(Color.WHITE);
            g2.drawString(cb.text, x, y);

            // Draw the "continue" prompt next to the most recent bubble if interacting.
            if (entity.isInteracting && i == entity.chatBubbles.size() - 1) {
                int promptX = x + length + 5;
                int promptY = y - 12; // Aligned with the text
                g2.drawImage(entity.continuePrompt.getFrame(), promptX, promptY, 16, 16, null);
            }

            yOffset -= 20;
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawDebug(Graphics2D g2, Entity entity, int screenX, int screenY) {
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(1));
        // Draw the entity's collision area
        g2.drawRect(screenX + entity.solidArea.x, screenY + entity.solidArea.y, entity.solidArea.width, entity.solidArea.height);

        // If the entity is the player, also draw their interaction area
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Rectangle interactionArea = player.getInteractionArea();
            int screenInteractionX = interactionArea.x - (int) player.worldX + player.screenX;
            int screenInteractionY = interactionArea.y - (int) player.worldY + player.screenY;
            g2.drawRect(screenInteractionX, screenInteractionY, interactionArea.width, interactionArea.height);
        }
    }
}
