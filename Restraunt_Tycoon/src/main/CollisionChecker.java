package main;

import entity.Entity;
import java.awt.*;

public class CollisionChecker {

    Gamepanel gp;
    public static String contactStall = "";
    public static String lastContactStall = "";
    public static String contactTruck = "";
    public static String lastContactTruck = "";
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
     * walls, etc. If collision is detected, it sets the player's collisionOn
     * variable to true, which will stop movement in the Gamepanel update
     * method. It also checks for collision with stall or truck tiles and sets
     * the corresponding contact variable. It does not stop the player's
     * movement when colliding with these tiles since we want the player to be
     * able to walk around while touching them, only checks what stall or truck
     * it is for entering.
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

        int buildingLeftCol = entityLeftWorldX / gp.stallTileSize;
        int buildingRightCol = entityRightWorldX / gp.stallTileSize;
        int buildingTopRow = entityTopWorldY / gp.stallTileSize;
        int buildingBottomRow = entityBottomWorldY / gp.stallTileSize;
        int tileNum1, tileNum2;

        // Reset contact variables each frame
        contactStall = "";
        contactTruck = "";

        // Check collisions based on current level
        if (gp.Current_level == 1) {
            // Check collision with stall tiles in level 1
            switch (entity.direction) {
                case "up" -> {
                    buildingTopRow = (entityTopWorldY - entity.speed) / gp.stallTileSize;
                    tileNum1 = gp.tileM.stallTileNum[buildingLeftCol][buildingTopRow];
                    tileNum2 = gp.tileM.stallTileNum[buildingRightCol][buildingTopRow];
                    if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                        contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                        lastContactStall = contactStall;
                    }
                }
                case "down" -> {
                    buildingBottomRow = (entityBottomWorldY + entity.speed) / gp.stallTileSize;
                    tileNum1 = gp.tileM.stallTileNum[buildingLeftCol][buildingBottomRow];
                    tileNum2 = gp.tileM.stallTileNum[buildingRightCol][buildingBottomRow];
                    if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                        contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                        lastContactStall = contactStall;
                    }
                }
                case "left" -> {
                    buildingLeftCol = (entityLeftWorldX - entity.speed) / gp.stallTileSize;
                    tileNum1 = gp.tileM.stallTileNum[buildingLeftCol][buildingTopRow];
                    tileNum2 = gp.tileM.stallTileNum[buildingLeftCol][buildingBottomRow];
                    if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                        contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                        lastContactStall = contactStall;
                    }
                }
                case "right" -> {
                    buildingRightCol = (entityRightWorldX + entity.speed) / gp.stallTileSize;
                    tileNum1 = gp.tileM.stallTileNum[buildingRightCol][buildingTopRow];
                    tileNum2 = gp.tileM.stallTileNum[buildingRightCol][buildingBottomRow];
                    if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                        contactStall = getStallName(tileNum1 != 0 ? tileNum1 : tileNum2);
                        lastContactStall = contactStall;
                    }
                }
            }
        } else if (gp.Current_level >= 2) {
            // Check collision with truck tiles in level 2 and 3
            switch (entity.direction) {
                case "up" -> {
                    buildingTopRow = (entityTopWorldY - entity.speed) / gp.stallTileSize;
                    tileNum1 = gp.tileM.truckTileNum[buildingLeftCol][buildingTopRow];
                    tileNum2 = gp.tileM.truckTileNum[buildingRightCol][buildingTopRow];
                    if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                        contactTruck = getTruckName(tileNum1 != 0 ? tileNum1 : tileNum2);
                        lastContactTruck = contactTruck;
                    }
                }
                case "down" -> {
                    buildingBottomRow = (entityBottomWorldY + entity.speed) / gp.stallTileSize;
                    tileNum1 = gp.tileM.truckTileNum[buildingLeftCol][buildingBottomRow];
                    tileNum2 = gp.tileM.truckTileNum[buildingRightCol][buildingBottomRow];
                    if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                        contactTruck = getTruckName(tileNum1 != 0 ? tileNum1 : tileNum2);
                        lastContactTruck = contactTruck;
                    }
                }
                case "left" -> {
                    buildingLeftCol = (entityLeftWorldX - entity.speed) / gp.stallTileSize;
                    tileNum1 = gp.tileM.truckTileNum[buildingLeftCol][buildingTopRow];
                    tileNum2 = gp.tileM.truckTileNum[buildingLeftCol][buildingBottomRow];
                    if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                        contactTruck = getTruckName(tileNum1 != 0 ? tileNum1 : tileNum2);
                        lastContactTruck = contactTruck;
                    }
                }
                case "right" -> {
                    buildingRightCol = (entityRightWorldX + entity.speed) / gp.stallTileSize;
                    tileNum1 = gp.tileM.truckTileNum[buildingRightCol][buildingTopRow];
                    tileNum2 = gp.tileM.truckTileNum[buildingRightCol][buildingBottomRow];
                    if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                        contactTruck = getTruckName(tileNum1 != 0 ? tileNum1 : tileNum2);
                        lastContactTruck = contactTruck;
                    }
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

        // If playing level 3, some station tiles exist in the world layer (worldmap3).
        // Detect contact with those station tiles and trigger cooking just like interior stations.
        if (gp.Current_level == 3) {
            boolean stationFound = false;
            int wtile1, wtile2;
            switch (entity.direction) {
                case "up" -> {
                    int topRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                    if (topRow >= 0) {
                        wtile1 = gp.tileM.mapTileNum[entityLeftCol][topRow];
                        wtile2 = gp.tileM.mapTileNum[entityRightCol][topRow];
                        String s1 = getStationName(wtile1);
                        String s2 = getStationName(wtile2);
                        if (!s1.isEmpty() || !s2.isEmpty()) {
                            stationFound = true;
                            checkStationContact(wtile1 != 0 ? wtile1 : wtile2);
                        }
                    }
                }
                case "down" -> {
                    int bottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                    if (bottomRow < gp.maxWorldRow) {
                        wtile1 = gp.tileM.mapTileNum[entityLeftCol][bottomRow];
                        wtile2 = gp.tileM.mapTileNum[entityRightCol][bottomRow];
                        String s1 = getStationName(wtile1);
                        String s2 = getStationName(wtile2);
                        if (!s1.isEmpty() || !s2.isEmpty()) {
                            stationFound = true;
                            checkStationContact(wtile1 != 0 ? wtile1 : wtile2);
                        }
                    }
                }
                case "left" -> {
                    int leftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                    if (leftCol >= 0) {
                        wtile1 = gp.tileM.mapTileNum[leftCol][entityTopRow];
                        wtile2 = gp.tileM.mapTileNum[leftCol][entityBottomRow];
                        String s1 = getStationName(wtile1);
                        String s2 = getStationName(wtile2);
                        if (!s1.isEmpty() || !s2.isEmpty()) {
                            stationFound = true;
                            checkStationContact(wtile1 != 0 ? wtile1 : wtile2);
                        }
                    }
                }
                case "right" -> {
                    int rightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                    if (rightCol < gp.maxWorldCol) {
                        wtile1 = gp.tileM.mapTileNum[rightCol][entityTopRow];
                        wtile2 = gp.tileM.mapTileNum[rightCol][entityBottomRow];
                        String s1 = getStationName(wtile1);
                        String s2 = getStationName(wtile2);
                        if (!s1.isEmpty() || !s2.isEmpty()) {
                            stationFound = true;
                            checkStationContact(wtile1 != 0 ? wtile1 : wtile2);
                        }
                    }
                }
            }
            if (!stationFound) {
                lastStation = "";
            }
        }
    }

    /**
     * Checks for collision with interior tiles (stall or truck), reads from the
     * currently loaded interior map. Sets up the station contact for the Cook
     * class to use.
     *
     * @param roomX the x-coordinate of the room
     * @param roomY the y-coordinate of the room
     * @param direction the direction of movement
     * @param speed the speed of movement
     * @return true if there is a collision with an interior tile, false
     * otherwise
     */
    public boolean checkInteriorTile(int roomX, int roomY, String direction, int speed) {
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
                int tile1 = gp.tileM.getCurrentInteriorMap()[leftCol][topRow];
                int tile2 = gp.tileM.getCurrentInteriorMap()[rightCol][topRow];
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
                int tile1 = gp.tileM.getCurrentInteriorMap()[leftCol][bottomRow];
                int tile2 = gp.tileM.getCurrentInteriorMap()[rightCol][bottomRow];
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
                int tile1 = gp.tileM.getCurrentInteriorMap()[leftCol][topRow];
                int tile2 = gp.tileM.getCurrentInteriorMap()[leftCol][bottomRow];
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
                int tile1 = gp.tileM.getCurrentInteriorMap()[rightCol][topRow];
                int tile2 = gp.tileM.getCurrentInteriorMap()[rightCol][bottomRow];
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
     * Wrapper method for backward compatibility. Calls checkInteriorTile.
     */
    public boolean checkStallTile(int roomX, int roomY, String direction, int speed) {
        return checkInteriorTile(roomX, roomY, direction, speed);
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
            case "popcorn machine" -> {
                Cook cook = new Cook("Popcorn", gp);
                cook.startCooking();
            }
            case "soda fridge" -> {
                Cook cook = new Cook("Soda", gp);
                cook.startCooking();
            }
            case "coffee machine" -> {
                Cook cook = new Cook("Coffee", gp);
                cook.startCooking();
            }
            case "egg pan fryer" -> {
                Cook cook = new Cook("Omelet", gp);
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
            case 11, 12, 13, 14, 15, 16, 40, 41, 42, 43, 44, 45 ->
                "grill";
            case 17, 46 ->
                "ice cream fridge";
            case 18, 47 ->
                "milkshake table";
            case 19, 48 ->
                "fryer";
            case 35, 49 ->
                "popcorn machine";
            case 37, 50 ->
                "soda fridge";
            case 53 ->
                "coffee machine";
            case 54 ->
                "egg pan fryer";
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
     * Helper method to get the name of the truck based on the tile number, used
     * in checkTile to set the truck contact for entering.
     *
     * @param tileNum the tile number of the truck tile we collided with, used
     * to get the truck name
     * @return the name of the truck (red or green), or an empty string if the
     * tile number doesn't correspond to a truck
     */
    private String getTruckName(int tileNum) {
        return switch (tileNum) {
            case 33 ->
                "Red";
            case 34 ->
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

    public String getCustomerContactTruck(Entity entity) {
        // Same code from checkTile but returns the truck name instead of setting a variable and doesn't check world tile collisions since it's only used for customers who don't interact with those
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int expandedLeftCol = Math.max(0, (entityLeftWorldX - entity.speed) / gp.stallTileSize);
        int expandedRightCol = Math.min(gp.tileM.truckTileNum.length - 1, (entityRightWorldX + entity.speed) / gp.stallTileSize);
        int expandedTopRow = Math.max(0, (entityTopWorldY - entity.speed) / gp.stallTileSize);
        int expandedBottomRow = Math.min(gp.tileM.truckTileNum[0].length - 1, (entityBottomWorldY + entity.speed) / gp.stallTileSize);

        for (int col = expandedLeftCol; col <= expandedRightCol; col++) {
            for (int row = expandedTopRow; row <= expandedBottomRow; row++) {
                int tileNum = gp.tileM.truckTileNum[col][row];
                if (tileNum != 0 && gp.tileM.tile[tileNum].collision) {
                    return getTruckName(tileNum);
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

        // check for truck tiles in level 2 only, so customers can enter trucks by touching them like the player
        if (gp.Current_level == 2) {
            for (int col = expandedLeftCol; col <= expandedRightCol; col++) {
                for (int row = expandedTopRow; row <= expandedBottomRow; row++) {
                    int tileNum = gp.tileM.truckTileNum[col][row];
                    if (tileNum != 0 && gp.tileM.tile[tileNum].collision) {
                        contactStall = getTruckName(tileNum);
                        return;
                    }
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
            case "up" ->
                nextY -= entity.speed;
            case "down" ->
                nextY += entity.speed;
            case "left" ->
                nextX -= entity.speed;
            case "right" ->
                nextX += entity.speed;
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
