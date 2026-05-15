package main;
import entity.Entity;

public class CollisionChecker {
    Gamepanel gp;
    public static String contactStall = "";

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

        // Check stall layer
        switch (entity.direction) {
            case "up" -> {
                stallTopRow = (entityTopWorldY - entity.speed) / gp.stallTileSize;
                tileNum1 = gp.tileM.stallTileNum[stallLeftCol][stallTopRow];
                tileNum2 = gp.tileM.stallTileNum[stallRightCol][stallTopRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                }
            }
            case "down" -> {
                stallBottomRow = (entityBottomWorldY + entity.speed) / gp.stallTileSize;
                tileNum1 = gp.tileM.stallTileNum[stallLeftCol][stallBottomRow];
                tileNum2 = gp.tileM.stallTileNum[stallRightCol][stallBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                }
            }
            case "left" -> {
                stallLeftCol = (entityLeftWorldX - entity.speed) / gp.stallTileSize;
                tileNum1 = gp.tileM.stallTileNum[stallLeftCol][stallTopRow];
                tileNum2 = gp.tileM.stallTileNum[stallLeftCol][stallBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                }
            }
            case "right" -> {
                stallRightCol = (entityRightWorldX + entity.speed) / gp.stallTileSize;
                tileNum1 = gp.tileM.stallTileNum[stallRightCol][stallTopRow];
                tileNum2 = gp.tileM.stallTileNum[stallRightCol][stallBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
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

    private String getStallName(int tileNum) {
        return switch (tileNum) {
            case 8  -> "Red";
            case 9  -> "Blue";
            case 10 -> "Green";
            default -> "";
        };
    }
}