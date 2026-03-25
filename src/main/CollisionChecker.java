package main;

import entity.Entity;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        // 1. CALCULATE HITBOX EDGES (World Coordinates in Pixels)
        // Calculate the world coordinates of the entity's hitbox (solidArea)
        int entityLeftWorldX = (int) entity.worldX + entity.solidArea.x; // The X-coordinate of the hitbox's left edge
        int entityRightWorldX = (int) entity.worldX + entity.solidArea.x + entity.solidArea.width; // The X-coordinate of the right edge
        int entityTopWorldY = (int) entity.worldY + entity.solidArea.y; // The Y-coordinate of the top edge (Y decreases as you go up)
        int entityBottomWorldY = (int) entity.worldY + entity.solidArea.y + entity.solidArea.height; // The Y-coordinate of the bottom edge

        // 2. TRANSLATE PIXELS TO GRID INDICES
        // We divide by tileSize to find which column and row these edges are currently inside
        int entityLeftCol = entityLeftWorldX / gp.tileSize; // Column index for left edge
        int entityRightCol = entityRightWorldX / gp.tileSize; // Column index for right edge
        int entityTopRow = entityTopWorldY / gp.tileSize; // Row index for top edge
        int entityBottomRow = entityBottomWorldY / gp.tileSize; // Row index for bottom edge

        int tileNum1, tileNum2; // Variables to store the indices of the two tiles the entity is checking

        // 3. PREDICTIVE COLLISION CHECK
        switch(entity.direction) {
            case "up":
                // Subtract speed from the top edge to see which row the entity will be in next
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                // Get the tile numbers at the top-left and top-right corners of the predicted position
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityTopRow];
                // If either tile has collision enabled, stop movement
                if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "down":
                // Add speed to the bottom edge to predict the next row
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                // Check the two tiles at the bottom-left and bottom-right corners
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                // Predict the column the entity will enter if it moves left
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                // Check the two tiles at the top-left and bottom-left corners
                tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                // Predict the column the entity will enter if it moves right
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                // Check the two tiles at the top-right and bottom-right corners
                tileNum1 = gp.tileManager.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

    public int checkObject(Entity entity, boolean player) {
        int index = 999;

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] != null) {
                // Get entity's solid area position
                entity.solidArea.x = (int) entity.worldX + entity.solidArea.x;
                entity.solidArea.y = (int) entity.worldY + entity.solidArea.y;
                // Get the object's solid area position
                gp.obj[i].solidArea.x = (int) gp.obj[i].worldX + gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = (int) gp.obj[i].worldY + gp.obj[i].solidArea.y;

                switch(entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        break;
                }

                if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                    if (gp.obj[i].collision) {
                        entity.collisionOn = true;
                    }
                    if (player) {
                        index = i;
                    }
                }

                // 4. RESET HITBOXES
                // We must reset these to their default relative values (e.g., 8, 16)
                // so they are ready for the next object check or the next frame.
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }
        return index;
    }
    public int checkEntity(Entity entity, Entity[] targets) {
        int index = 999;

        for (int i = 0; i < targets.length; i++) {
            if (targets[i] != null) {
                // Get entity's solid area position
                entity.solidArea.x = (int) entity.worldX + entity.solidArea.x;
                entity.solidArea.y = (int) entity.worldY + entity.solidArea.y;
                // Get the object's solid area position
                targets[i].solidArea.x = (int) targets[i].worldX + targets[i].solidArea.x;
                targets[i].solidArea.y = (int) targets[i].worldY + targets[i].solidArea.y;

                switch(entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        break;
                }

                if (entity.solidArea.intersects(targets[i].solidArea)) {
                    if (targets[i] != entity) {
                        entity.collisionOn = true;
                        index = i;
                    }
                }

                // 4. RESET HITBOXES
                // We must reset these to their default relative values (e.g., 8, 16)
                // so they are ready for the next object check or the next frame.
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                targets[i].solidArea.x = targets[i].solidAreaDefaultX;
                targets[i].solidArea.y = targets[i].solidAreaDefaultY;
            }
        }
        return index;
    }
    public void checkPlayer(Entity entity) {
        if (gp.player != null) {
            // Get entity's solid area position
            entity.solidArea.x = (int) entity.worldX + entity.solidArea.x;
            entity.solidArea.y = (int) entity.worldY + entity.solidArea.y;
            // Get the object's solid area position
            gp.player.solidArea.x = (int) gp.player.worldX + gp.player.solidArea.x;
            gp.player.solidArea.y = (int) gp.player.worldY + gp.player.solidArea.y;

            switch(entity.direction) {
                case "up":
                    entity.solidArea.y -= entity.speed;
                    break;
                case "down":
                    entity.solidArea.y += entity.speed;
                    break;
                case "left":
                    entity.solidArea.x -= entity.speed;
                    break;
                case "right":
                    entity.solidArea.x += entity.speed;
                    break;
            }

            if (entity.solidArea.intersects(gp.player.solidArea)) {
                entity.collisionOn = true;
            }

            // 4. RESET HITBOXES
            // We must reset these to their default relative values (e.g., 8, 16)
            // so they are ready for the next object check or the next frame.
            entity.solidArea.x = entity.solidAreaDefaultX;
            entity.solidArea.y = entity.solidAreaDefaultY;
            gp.player.solidArea.x = gp.player.solidAreaDefaultX;
            gp.player.solidArea.y = gp.player.solidAreaDefaultY;
        }
    }
}
