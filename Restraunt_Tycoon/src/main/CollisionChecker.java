package main;

import entity.Entity;
import java.awt.*;

public class CollisionChecker {

    Gamepanel gp;
    public static String contactStall = "";
    public static String lastContactStall = "";
    public static String lastStation = ""; // Used to track the last station to cook

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

        // Reset contactStall each frame to make sure it only has a value while we're actually touching the stall
        // lastContactStall is used to track the last stall we touched so we don't enter repeatedly while standing still
        contactStall = "";
        lastContactStall = "";

        // Checks collision with stall tiles in the world map, and sets contactStall if we touched one
        switch (entity.direction) {
            case "up" -> {
                stallTopRow = (entityTopWorldY - entity.speed) / gp.stallTileSize;
                tileNum1 = gp.tileM.stallTileNum[stallLeftCol][stallTopRow];
                tileNum2 = gp.tileM.stallTileNum[stallRightCol][stallTopRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                    lastContactStall = contactStall;
                }
            }
            case "down" -> {
                stallBottomRow = (entityBottomWorldY + entity.speed) / gp.stallTileSize;
                tileNum1 = gp.tileM.stallTileNum[stallLeftCol][stallBottomRow];
                tileNum2 = gp.tileM.stallTileNum[stallRightCol][stallBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                    lastContactStall = contactStall;
                }
            }
            case "left" -> {
                stallLeftCol = (entityLeftWorldX - entity.speed) / gp.stallTileSize;
                tileNum1 = gp.tileM.stallTileNum[stallLeftCol][stallTopRow];
                tileNum2 = gp.tileM.stallTileNum[stallLeftCol][stallBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                    lastContactStall = contactStall;
                }
            }
            case "right" -> {
                stallRightCol = (entityRightWorldX + entity.speed) / gp.stallTileSize;
                tileNum1 = gp.tileM.stallTileNum[stallRightCol][stallTopRow];
                tileNum2 = gp.tileM.stallTileNum[stallRightCol][stallBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                    lastContactStall = contactStall;
                }
            }
        }

        // Checks collision with world tiles
        switch (entity.direction) {
            case "up" -> {
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
            }
            case "down" -> {
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
            }
            case "left" -> {
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
            }
            case "right" -> {
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                }
            }
        }
    }

    public boolean checkStallTile(int roomX, int roomY, String direction, int speed) {
        int left = roomX + gp.player.solidArea.x;
        int right = roomX + gp.player.solidArea.x + gp.player.solidArea.width;
        int top = roomY + gp.player.solidArea.y;
        int bottom = roomY + gp.player.solidArea.y + gp.player.solidArea.height;

        int leftCol, rightCol, topRow, bottomRow;

        switch (direction) {
            case "up" -> {
                topRow = (top - speed) / gp.tileSize;
                leftCol = left / gp.tileSize;
                rightCol = right / gp.tileSize;
                if (topRow < 0) {
                    return true;
                }
                int tile1 = gp.tileM.getCurrentStallMap()[leftCol][topRow];
                int tile2 = gp.tileM.getCurrentStallMap()[rightCol][topRow];
                if (gp.tileM.tile[tile1].collision || gp.tileM.tile[tile2].collision) {
                    checkStationContact(tile1 != 0 ? tile1 : tile2);
                    return true;
                }
            }
            case "down" -> {
                bottomRow = (bottom + speed) / gp.tileSize;
                leftCol = left / gp.tileSize;
                rightCol = right / gp.tileSize;
                if (bottomRow >= 15) {
                    return true;
                }
                int tile1 = gp.tileM.getCurrentStallMap()[leftCol][bottomRow];
                int tile2 = gp.tileM.getCurrentStallMap()[rightCol][bottomRow];
                if (gp.tileM.tile[tile1].collision || gp.tileM.tile[tile2].collision) {
                    checkStationContact(tile1 != 0 ? tile1 : tile2);
                    return true;
                }
            }
            case "left" -> {
                leftCol = (left - speed) / gp.tileSize;
                topRow = top / gp.tileSize;
                bottomRow = bottom / gp.tileSize;
                if (leftCol < 0) {
                    return true;
                }
                int tile1 = gp.tileM.getCurrentStallMap()[leftCol][topRow];
                int tile2 = gp.tileM.getCurrentStallMap()[leftCol][bottomRow];
                if (gp.tileM.tile[tile1].collision || gp.tileM.tile[tile2].collision) {
                    checkStationContact(tile1 != 0 ? tile1 : tile2);
                    return true;
                }
            }
            case "right" -> {
                rightCol = (right + speed) / gp.tileSize;
                topRow = top / gp.tileSize;
                bottomRow = bottom / gp.tileSize;
                if (rightCol >= 20) {
                    return true;
                }
                int tile1 = gp.tileM.getCurrentStallMap()[rightCol][topRow];
                int tile2 = gp.tileM.getCurrentStallMap()[rightCol][bottomRow];
                if (gp.tileM.tile[tile1].collision || gp.tileM.tile[tile2].collision) {
                    checkStationContact(tile1 != 0 ? tile1 : tile2);
                    return true;
                }
            }
        }

        // No collision — clear the last printed name so walking away and back prints again
        lastStation = "";
        return false;
    }

    private void checkStationContact(int tileNum) {
        String name = getStationName(tileNum);
        if (name.isEmpty()) {
            return;
        }
        if (name.equals(lastStation)) {
            return;
        }
        lastStation = name;
        System.out.println(name);
    }

    private String getStationName(int tileNum) {
        return switch (tileNum) {
            case 11, 12, 13, 14, 15, 16 ->
                "grill";
            case 17 ->
                "ice cream fridge";
            case 18 ->
                "milkshake table";
            case 19 ->
                "fryer";
            default ->
                "";
        };
    }

    private String getStallName(int tileNum) {
        return switch (tileNum) {
            case 8 ->
                "Red";
            case 9 ->
                "Blue";
            case 10 ->
                "Green";
            default ->
                "";
        };
    }

    public String getCustomerContactStall(Entity entity) {
        // Same code from checkTile but returns the stall name instead of setting a variable and doesn't check world tile collisions since it's only used for customers who don't interact with those
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int expandedLeftCol = Math.max(0, (entityLeftWorldX - entity.speed) / gp.stallTileSize);
        int expandedRightCol = Math.min(gp.tileM.stallTileNum.length - 1, (entityRightWorldX + entity.speed) / gp.stallTileSize);
        int expandedTopRow = Math.max(0, (entityTopWorldY - entity.speed) / gp.stallTileSize);
        int expandedBottomRow = Math.min(gp.tileM.stallTileNum[0].length - 1, (entityBottomWorldY + entity.speed) / gp.stallTileSize);

        for (int col = expandedLeftCol; col <= expandedRightCol; col++) {
            for (int row = expandedTopRow; row <= expandedBottomRow; row++) {
                int tileNum = gp.tileM.stallTileNum[col][row];
                if (tileNum != 0 && gp.tileM.tile[tileNum].collision) {
                    return getStallName(tileNum);
                }
            }
        }

        return "";
    }

    public void customerCheckTile(Entity entity) {
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // Reset contactStall each frame and will be re-set if actively colliding
        contactStall = "";

        // Check stall layer around the customer, including a small buffer so stationary customers still count as touching the stall.
        // Scan the tiles overlapping the customer's solid area plus the movement buffer, bounded by the stall map.
        // Max is used to not check postive world coordinates outside the map, min is used to not check negative world coordinates.
        int expandedLeftCol = Math.max(0, (entityLeftWorldX - entity.speed) / gp.stallTileSize);
        int expandedRightCol = Math.min(gp.tileM.stallTileNum.length - 1, (entityRightWorldX + entity.speed) / gp.stallTileSize);
        int expandedTopRow = Math.max(0, (entityTopWorldY - entity.speed) / gp.stallTileSize);
        int expandedBottomRow = Math.min(gp.tileM.stallTileNum[0].length - 1, (entityBottomWorldY + entity.speed) / gp.stallTileSize);

        // Check the expanded area for stall tiles and set contactStall if there is a collision
        for (int col = expandedLeftCol; col <= expandedRightCol; col++) {
            for (int row = expandedTopRow; row <= expandedBottomRow; row++) {
                int tileNum = gp.tileM.stallTileNum[col][row];
                if (tileNum != 0 && gp.tileM.tile[tileNum].collision) {
                    contactStall = getStallName(tileNum);
                    System.out.println("Collision with stall detected! " + contactStall);
                    return;
                }
            }
        }
    }

    public void checkEntityCollision(Entity entity, Entity[] targets) {
        // Check for collisions between entities
        if (targets == null) {
            return;
        }
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
}
