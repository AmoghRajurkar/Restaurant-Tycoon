package main;

import entity.Entity;
import java.awt.*;

public class CollisionChecker {

    Gamepanel gp;
    public static String contactStall = "";
    public static String lastContactStall = "";
    public static String lastStation = ""; // Used to track the last station to cook

    /**
     * Constructor for CollisionChecker, used in Gameplanel to check for
     * collisions between every entity and the world tiles, stall tiles and
     * other entities.
     *
     * @param gp Gamepanel instance, used to access the tile manager for
     * collision data and player inventory for station contact checks
     */
    public CollisionChecker(Gamepanel gp) {
        this.gp = gp;
    }

    /**
     * Checks for collision with player and world tiles, like trees, rock,
     * walls, etc. If collsion is detected, it sets the player's collisionOn
     * variable to true, which will stop movement in the Gameplanel update
     * method. It also checks for collision with stall tiles and sets the
     * contactStall variable to the name of the stall we're colliding with,
     * which is used in. It does not check stop the players movement when
     * colliding with stall tiles since we want the player to be able to walk
     * around while touching the stall, only checks what stall it is for the
     * order board and restock panel.
     */
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
        // Preserve lastContactStall so we remember the last stall touched (used when entering a stall)
        contactStall = "";

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

    /**
     * Checks for collision with stall tiles in the world map, its read from a
     * different txt file, placed on top of the world map and uses a different
     * tile size, so it needs sperate collion checking. It sets up the station
     * contact for the Cook class to use, contactStall is used in the order
     * board and restock panel to check if we're touching a stall to open those
     * interfaces.
     *
     * @param roomX the x-coordinate of the room
     * @param roomY the y-coordinate of the room
     * @param direction the direction of movement
     * @param speed the speed of movement
     * @return true if there is a collision with a stall tile, false otherwise
     */
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

    /**
     * Checks for collision with the station tiles, and sets the lastStation
     * variable to the stations name, which is used to start cooking. It uses
     * the same code as stall tile collision checking, but instead of returning
     * a boolean, it sets the station contact for the cook class to use and
     * doesn't check world tile collisions.
     *
     * @param tileNum the tile number of the station tile we collided with, used
     * to get the station name
     */
    private void checkStationContact(int tileNum) {
        String name = getStationName(tileNum);
        if (name.isEmpty()) {
            return;
        }
        if (name.equals(lastStation)) {
            return;
        }
        lastStation = name;

        switch (name) {
            case "grill" -> {
                Cook cook = new Cook("Burger", gp);
                cook.startCooking();
            }
            case "fryer" -> {
                Cook cook = new Cook("Fries", gp);
                cook.startCooking();
            }
            case "milkshake table" -> {
                Cook cook = new Cook("Milkshake", gp);
                cook.startCooking();
            }
            case "ice cream fridge" -> {
                Cook cook = new Cook("Ice Cream", gp);
                cook.startCooking();
            }
        }
    }

    /**
     * Helper method to get the name of the station based on the tile number,
     * used in checkStationContact to set the station contact for the cook
     * class.
     *
     * @param tileNum the tile number of the station tile we collided with, used
     * to get the station name
     * @return the name of the station, or an empty string if the tile number
     * doesn't correspond to a station
     */
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

    /**
     * Helper method to get the name of the stall based on the tile number, used
     * in checkTile to set the stall contact for the order board and restock
     * panel.
     *
     * @param tileNum the tile number of the stall tile we collided with, used
     * to get the stall name
     * @return the name of the stall (red, blue or green), or an empty string if
     * the tile number doesn't correspond to a stall
     */
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

    /**
     * Checks for collision between entities, used for player and customers so
     * they don't walk through each other. It takes an array of entities to
     * check against, which can be either the customers or the player depending
     * on who is calling the method, and sets the collisionOn variable to true
     * if there is a collision, which will stop movement in the Gameplanel
     * update method. Used for customers to check if they're touching a stall to
     * decide whether to start moving towards the order board or not
     *
     * @param entity the entity to check for collisions, either the player or a
     * customer
     * @param targets the array of entities to check against, either the
     * customers or the player
     * @return String name of the stall we're colliding with, or an empty string
     * if we're not colliding with a stall.
     */
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

    /**
     * Checks for collision between a customer and the stall tiles, used to
     * check if the customer is touching the stall to decide whether to start
     * moving towards the order board or not. It uses the same code as checkTile
     * but returns the stall name instead of setting a variable and doesn't
     * check world tile collisions since it's only used for customers who don't
     * interact with those. It also includes a small buffer so stationary
     * customers still count as touching the stall, by expanding the area we
     * check for stall collisions by the customer's speed in all directions.
     *
     * @param entity the customer to check for collisions
     */
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
                    return;
                }
            }
        }
    }

    /**
     * Checks for collision between entities, used for entities so they don't
     * walk through each other. It takes an array of entities to check against,
     * which can be either the customers or the player depending on who is
     * calling the method, and sets the collisionOn variable to true if there is
     * a collision, which will stop movement in the Gameplanel update method.
     * Used for customers to check if they're touching a stall to decide whether
     * to start moving towards the order board or not
     *
     * @param entity
     * @param targets
     */
    public void checkEntityCollision(Entity entity, Entity[] targets) {
        // Check for collisions between entities based on the entity's next move,
        // so already-touching customers can still move away instead of freezing.
        if (targets == null) {
            return;
        }

        int nextX = entity.worldX;
        int nextY = entity.worldY;
        switch (entity.direction) {
            case "up" -> nextY -= entity.speed;
            case "down" -> nextY += entity.speed;
            case "left" -> nextX -= entity.speed;
            case "right" -> nextX += entity.speed;
        }

        Rectangle nextRect = new Rectangle(
                nextX + entity.solidArea.x,
                nextY + entity.solidArea.y,
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
            if (nextRect.intersects(targetRect)) {
                entity.collisionOn = true;
                break; // Exit loop on first collision detected
            }
        }
    }
}
