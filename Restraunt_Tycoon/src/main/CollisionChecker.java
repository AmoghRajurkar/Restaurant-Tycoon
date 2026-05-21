package main;
import entity.Entity;
import java.awt.Rectangle;

public class CollisionChecker {
    Gamepanel gp;
    public static String contactStall = "";
    public static String lastContactStall = "";

    public CollisionChecker(Gamepanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int stallLeftCol = entityLeftWorldX / gp.stallTileSize;
        int stallRightCol = entityRightWorldX / gp.stallTileSize;
        int stallTopRow = entityTopWorldY / gp.stallTileSize;
        int stallBottomRow = entityBottomWorldY / gp.stallTileSize;
        int tileNum1, tileNum2;

        // Reset contactStall each frame — will be re-set if actively colliding
        contactStall = "";
        lastContactStall = "";

        // Check stall layer
        switch (entity.direction) {
            case "up" -> {
                stallTopRow = (entityTopWorldY - entity.speed) / gp.stallTileSize;
                tileNum1 = gp.tileM.stallTileNum[stallLeftCol][stallTopRow];
                tileNum2 = gp.tileM.stallTileNum[stallRightCol][stallTopRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                    lastContactStall = contactStall;
                }
            }
            case "down" -> {
                stallBottomRow = (entityBottomWorldY + entity.speed) / gp.stallTileSize;
                tileNum1 = gp.tileM.stallTileNum[stallLeftCol][stallBottomRow];
                tileNum2 = gp.tileM.stallTileNum[stallRightCol][stallBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                    lastContactStall = contactStall;
                }
            }
            case "left" -> {
                stallLeftCol = (entityLeftWorldX - entity.speed) / gp.stallTileSize;
                tileNum1 = gp.tileM.stallTileNum[stallLeftCol][stallTopRow];
                tileNum2 = gp.tileM.stallTileNum[stallLeftCol][stallBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                    lastContactStall = contactStall;
                }
            }
            case "right" -> {
                stallRightCol = (entityRightWorldX + entity.speed) / gp.stallTileSize;
                tileNum1 = gp.tileM.stallTileNum[stallRightCol][stallTopRow];
                tileNum2 = gp.tileM.stallTileNum[stallRightCol][stallBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                    lastContactStall = contactStall;
                }
            }
        }

        // Check tile layer
        switch (entity.direction) {
            case "up" -> {
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision)
                    entity.collisionOn = true;
            }
            case "down" -> {
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision)
                    entity.collisionOn = true;
            }
            case "left" -> {
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision)
                    entity.collisionOn = true;
            }
            case "right" -> {
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision)
                    entity.collisionOn = true;
            }
        }
    }

    public void checkEntityCollision(Entity entity, Entity[] targets) {
        // Logic to check for collisions between entities goes here
        if (targets==null) return;
        Rectangle entityRect = new Rectangle(
                entity.worldX + entity.solidArea.x,
                entity.worldY + entity.solidArea.y,
                entity.solidArea.width,
                entity.solidArea.height
        );
        for (Entity target : targets) {
            if (target == null || target == entity) {
                continue; // Skip empty slots and the entity itself
            }
            Rectangle targetRect = new Rectangle(
                    target.worldX + target.solidArea.x,
                    target.worldY + target.solidArea.y,
                    target.solidArea.width,
                    target.solidArea.height
            );
            if (entityRect.intersects(targetRect)) {
                entity.collisionOn = true;
                break; // Exit loop on first collision detected
            }
        }

    }



    public boolean checkStallTile(int roomX, int roomY, String direction, int speed) {
        int left   = roomX + gp.player.solidArea.x;
        int right  = roomX + gp.player.solidArea.x + gp.player.solidArea.width;
        int top    = roomY + gp.player.solidArea.y;
        int bottom = roomY + gp.player.solidArea.y + gp.player.solidArea.height;

        int leftCol, rightCol, topRow, bottomRow;

        switch (direction) {
            case "up" -> {
                topRow   = (top - speed) / gp.tileSize;
                leftCol  = left  / gp.tileSize;
                rightCol = right / gp.tileSize;
                if (topRow < 0) return true;
                int t1 = gp.tileM.getCurrentStallMap()[leftCol][topRow];
                int t2 = gp.tileM.getCurrentStallMap()[rightCol][topRow];
                if (gp.tileM.tile[t1].collision || gp.tileM.tile[t2].collision) return true;
            }
            case "down" -> {
                bottomRow = (bottom + speed) / gp.tileSize;
                leftCol   = left  / gp.tileSize;
                rightCol  = right / gp.tileSize;
                if (bottomRow >= 15) return true;
                int t1 = gp.tileM.getCurrentStallMap()[leftCol][bottomRow];
                int t2 = gp.tileM.getCurrentStallMap()[rightCol][bottomRow];
                if (gp.tileM.tile[t1].collision || gp.tileM.tile[t2].collision) return true;
            }
            case "left" -> {
                leftCol  = (left - speed) / gp.tileSize;
                topRow   = top    / gp.tileSize;
                bottomRow= bottom / gp.tileSize;
                if (leftCol < 0) return true;
                int t1 = gp.tileM.getCurrentStallMap()[leftCol][topRow];
                int t2 = gp.tileM.getCurrentStallMap()[leftCol][bottomRow];
                if (gp.tileM.tile[t1].collision || gp.tileM.tile[t2].collision) return true;
            }
            case "right" -> {
                rightCol = (right + speed) / gp.tileSize;
                topRow   = top    / gp.tileSize;
                bottomRow= bottom / gp.tileSize;
                if (rightCol >= 20) return true;
                int t1 = gp.tileM.getCurrentStallMap()[rightCol][topRow];
                int t2 = gp.tileM.getCurrentStallMap()[rightCol][bottomRow];
                if (gp.tileM.tile[t1].collision || gp.tileM.tile[t2].collision) return true;
            }
        }
        return false;
    }

    private String getStallName(int tileNum) {
        return switch (tileNum) {
            case 8  -> "Red";
            case 9  -> "Blue";
            case 10 -> "Green";
            default -> "";
        };
    }
}