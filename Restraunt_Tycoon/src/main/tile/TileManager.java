package main.tile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import main.CollisionChecker;
import main.Gamepanel;

public class TileManager {

    Gamepanel gp;
    public Tile[] tile;
    public int mapTileNum[][];
    public int stallTileNum[][];
    int stallTileSize;
    int stallMapCol;
    int stallMapRow;
    private String lastHandledStall = "";
    public int truckTileNum[][];
    int truckTileSize;
    int truckMapCol;
    int truckMapRow;
    private String lastHandledTruck = "";

    // Interior map dimensions — 20 cols x 15 rows, fits the screen perfectly
    private final int interiorCols = 20;
    private final int interiorRows = 15;

    // One 2D array per stall, loaded once at startup
    private final int[][] redStallMap = new int[interiorCols][interiorRows];
    private final int[][] blueStallMap = new int[interiorCols][interiorRows];
    private final int[][] greenStallMap = new int[interiorCols][interiorRows];
    private final int[][] redtruckMap = new int[interiorCols][interiorRows];
    private final int[][] greentruckMap = new int[interiorCols][interiorRows];

    // Points to whichever stall the player just entered
    private int[][] currentStallMap = null;

    // Door position — bottom-right corner, one tileSize away from each wall
    public int doorX;
    public int doorY;

    public TileManager(Gamepanel gp) {
        this.gp = gp;
        tile = new Tile[55];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        stallTileSize = gp.tileSize * 4;
        stallMapCol = gp.maxWorldCol / 4;
        stallMapRow = gp.maxWorldRow / 4;
        stallTileNum = new int[stallMapCol][stallMapRow];

        truckTileSize = gp.tileSize * 4;
        truckMapCol = gp.maxWorldCol / 4;
        truckMapRow = gp.maxWorldRow / 4;
        truckTileNum = new int[truckMapCol][truckMapRow];

        // Door is one tileSize away from the right and bottom walls
        doorX = gp.screenWidth - gp.tileSize;
        doorY = gp.screenHeight - gp.tileSize * 11;

        getTileImage();
        switch (gp.Current_level) {
            case 1 -> {
                loadWorldMap("/res/maps/worldmap1.txt");
                loadStallsInWorld("/res/maps/stalls.txt");

                // Preload all three stall interiors so entering is instant
                loadInteriorMap("red_stall.txt", redStallMap);
                loadInteriorMap("blue_stall.txt", blueStallMap);
                loadInteriorMap("green_stall.txt", greenStallMap);
                break;
            }
            case 2 -> {
                loadWorldMap("/res/maps/worldmap2.txt");
                loadTrucksInWorld("/res/maps/trucks.txt");

                // Preload truck interiors so entering is instant
                loadInteriorMap("red_truck", redtruckMap);
                loadInteriorMap("green_truck", greentruckMap);
            }

            case 3 -> {
                loadWorldMap("/res/maps/worldmap3.txt");
            }
        }
    }

    // Reload the world map when level changes
    public void reloadLevelMap() {
        switch (gp.Current_level) {
            case 1 -> {
                loadWorldMap("/res/maps/worldmap1.txt");
                loadStallsInWorld("/res/maps/stalls.txt");
                loadInteriorMap("red_stall.txt", redStallMap);
                loadInteriorMap("blue_stall.txt", blueStallMap);
                loadInteriorMap("green_stall.txt", greenStallMap);
            }
            case 2 -> {
                loadWorldMap("/res/maps/worldmap2.txt");
                loadTrucksInWorld("/res/maps/trucks.txt");
                loadInteriorMap("red_truck", redtruckMap);
                loadInteriorMap("green_truck", greentruckMap);
            }
            case 3 -> {
                loadWorldMap("/res/maps/worldmap3.txt");
            }
        }
    }

    private BufferedImage loadImage(String fileName) {
        try (InputStream is = getClass().getResourceAsStream(fileName)) {
            if (is != null) {
                return ImageIO.read(is);
            }
        } catch (IOException ignored) {
        }

        try {
            return ImageIO.read(new File(fileName));
        } catch (IOException e) {
            System.err.println("Failed to load tile image '" + fileName + "': " + e.getMessage());
            return null;
        }
    }

    private void getTileImage() {
        tile[0] = new Tile();
        tile[0].image = loadImage("res/tiles/Grass.png");

        tile[1] = new Tile();
        tile[1].image = loadImage("res/tiles/Rock.png");
        tile[1].collision = true;

        tile[2] = new Tile();
        tile[2].image = loadImage("res/tiles/stall_wall.png");
        tile[2].collision = true;

        tile[3] = new Tile();
        tile[3].image = loadImage("res/tiles/Bush.png");
        tile[3].collision = true;

        tile[4] = new Tile();
        tile[4].image = loadImage("res/tiles/Stall_floor.png");

        tile[5] = new Tile();
        tile[5].image = loadImage("res/tiles/Road_middle_vertical.png");

        tile[6] = new Tile();
        tile[6].image = loadImage("res/tiles/Rough_Path.png");

        tile[7] = new Tile();
        tile[7].image = loadImage("res/tiles/Tree.png");
        tile[7].collision = true;

        tile[8] = new Tile();
        tile[8].image = loadImage("res/tiles/RedStall.png");
        tile[8].collision = true;

        tile[9] = new Tile();
        tile[9].image = loadImage("res/tiles/BlueStall.png");
        tile[9].collision = true;

        tile[10] = new Tile();
        tile[10].image = loadImage("res/tiles/GreenStall.png");
        tile[10].collision = true;

        tile[11] = new Tile();
        tile[11].image = loadImage("res/tiles/grill_horizontal_L.png");
        tile[11].collision = true;

        tile[12] = new Tile();
        tile[12].image = loadImage("res/tiles/grill_horizontal_M.png");
        tile[12].collision = true;

        tile[13] = new Tile();
        tile[13].image = loadImage("res/tiles/grill_horizontal_R.png");
        tile[13].collision = true;

        tile[14] = new Tile();
        tile[14].image = loadImage("res/tiles/grill_vertical_B.png");
        tile[14].collision = true;

        tile[15] = new Tile();
        tile[15].image = loadImage("res/tiles/grill_vertical_M.png");
        tile[15].collision = true;

        tile[16] = new Tile();
        tile[16].image = loadImage("res/tiles/grill_vertical_T.png");
        tile[16].collision = true;

        tile[17] = new Tile();
        tile[17].image = loadImage("res/tiles/ice_cream_fridge.png");
        tile[17].collision = true;

        tile[18] = new Tile();
        tile[18].image = loadImage("res/tiles/blender_table.png");
        tile[18].collision = true;

        tile[19] = new Tile();
        tile[19].image = loadImage("res/tiles/fryer.png");
        tile[19].collision = true;

        tile[20] = new Tile();
        tile[20].image = loadImage("res/food/Burger_on_table.png");
        tile[20].collision = true;

        tile[21] = new Tile();
        tile[21].image = loadImage("res/food/Fries_on_table.png");
        tile[21].collision = true;

        tile[22] = new Tile();
        tile[22].image = loadImage("res/food/Ice_cream_on_table.png");
        tile[22].collision = true;

        tile[23] = new Tile();
        tile[23].image = loadImage("res/food/Soda_on_table.png");
        tile[23].collision = true;

        tile[24] = new Tile();
        tile[24].image = loadImage("res/tiles/stall_table.png");
        tile[24].collision = true;

        tile[25] = new Tile();
        tile[25].image = loadImage("res/food/Milkshake_on_table.png");
        tile[25].collision = true;

        tile[26] = new Tile();
        tile[26].image = loadImage("res/tiles/Road_left.png");

        tile[27] = new Tile();
        tile[27].image = loadImage("res/tiles/Road_right.png");

        tile[28] = new Tile();
        tile[28].image = loadImage("res/tiles/Road_blank.png");

        tile[29] = new Tile();
        tile[29].image = loadImage("res/tiles/Sidewalk.png");

        tile[30] = new Tile();
        tile[30].image = loadImage("res/tiles/Road_middle_horizontal.png");

        tile[31] = new Tile();
        tile[31].image = loadImage("res/tiles/Road_top.png");

        tile[32] = new Tile();
        tile[32].image = loadImage("res/tiles/Road_bottom.png");

        tile[33] = new Tile();
        tile[33].image = loadImage("res/tiles/Red_truck.png");
        tile[33].collision = true;

        tile[34] = new Tile();
        tile[34].image = loadImage("res/tiles/Green_truck.png");
        tile[34].collision = true;

        tile[35] = new Tile();
        tile[35].image = loadImage("res/tiles/Popcorn_machine.png");
        tile[35].collision = true;

        tile[36] = new Tile();
        tile[36].image = loadImage("res/tiles/Restraunt_floor.png");

        tile[37] = new Tile();
        tile[37].image = loadImage("res/tiles/Soda_Fridge.png");
        tile[37].collision = true;

        tile[38] = new Tile();
        tile[38].image = loadImage("res/tiles/Truck_floor.png");

        tile[39] = new Tile();
        tile[39].image = loadImage("res/tiles/Truck_Restock_floor.png");

        tile[40] = new Tile();
        tile[40].image = loadImage("res/tiles/grill_horizontal_R2.png");
        tile[40].collision = true;

        tile[41] = new Tile();
        tile[41].image = loadImage("res/tiles/grill_horizontal_M2.png");
        tile[41].collision = true;

        tile[42] = new Tile();
        tile[42].image = loadImage("res/tiles/grill_horizontal_L2.png");
        tile[42].collision = true;

        tile[43] = new Tile();
        tile[43].image = loadImage("res/tiles/grill_vertical_T2.png");
        tile[43].collision = true;

        tile[44] = new Tile();
        tile[44].image = loadImage("res/tiles/grill_vertical_M2.png");
        tile[44].collision = true;

        tile[45] = new Tile();
        tile[45].image = loadImage("res/tiles/grill_vertical_B2.png");
        tile[45].collision = true;

        tile[46] = new Tile();
        tile[46].image = loadImage("res/tiles/ice_cream_fridge2.png");
        tile[46].collision = true;

        tile[47] = new Tile();
        tile[47].image = loadImage("res/tiles/blender_table2.png");
        tile[47].collision = true;

        tile[48] = new Tile();
        tile[48].image = loadImage("res/tiles/fryer2.png");
        tile[48].collision = true;

        tile[49] = new Tile();
        tile[49].image = loadImage("res/tiles/Popcorn_machine2.png");
        tile[49].collision = true;

        tile[50] = new Tile();
        tile[50].image = loadImage("res/tiles/Soda_fridge2.png");
        tile[50].collision = true;

        tile[51] = new Tile();
        tile[51].image = loadImage("res/tiles/stall_wall2.png");
        tile[51].collision = true;

        tile[52] = new Tile();
        tile[52].image = loadImage("res/tiles/stall_table2.png");
        tile[52].collision = true;

        tile[53] = new Tile();
        tile[53].image = loadImage("res/tiles/coffee_machine.png");
        tile[53].collision = true;

        tile[54] = new Tile();
        tile[54].image = loadImage("res/tiles/egg_pan_fryer.png");
        tile[54].collision = true;
    }

    private String readNextNonEmptyLine(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                return line;
            }
        }
        return null;
    }

    private void loadWorldMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br;
            if (is != null) {
                br = new BufferedReader(new InputStreamReader(is));
            } else {
                br = new BufferedReader(new java.io.FileReader(filePath.substring(1)));
            }
            int col = 0;
            int row = 0;
            while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
                String line = readNextNonEmptyLine(br);
                if (line == null) {
                    break;
                }
                while (col < gp.maxWorldCol) {
                    String[] numbers = line.split("\\s+");
                    if (col < numbers.length) {
                        int num = Integer.parseInt(numbers[col]);
                        mapTileNum[col][row] = num;
                    }
                    col++;
                }
                if (col == gp.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error reading map file '" + filePath + "': " + e.getMessage());
        }
    }

    private void loadStallsInWorld(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br;
            if (is != null) {
                br = new BufferedReader(new InputStreamReader(is));
            } else {
                br = new BufferedReader(new java.io.FileReader(filePath.substring(1)));
            }
            int col = 0;
            int row = 0;
            while (col < stallMapCol && row < stallMapRow) {
                String line = readNextNonEmptyLine(br);
                if (line == null) {
                    break;
                }
                while (col < stallMapCol) {
                    String[] numbers = line.split("\\s+");
                    if (col < numbers.length) {
                        int num = Integer.parseInt(numbers[col]);
                        stallTileNum[col][row] = num;
                    }
                    col++;
                }
                if (col == stallMapCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error reading stalls file '" + filePath + "': " + e.getMessage());
        }
    }

    private void loadTrucksInWorld(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br;
            if (is != null) {
                br = new BufferedReader(new InputStreamReader(is));
            } else {
                br = new BufferedReader(new java.io.FileReader(filePath.substring(1)));
            }
            int col = 0;
            int row = 0;
            while (col < truckMapCol && row < truckMapRow) {
                String line = readNextNonEmptyLine(br);
                if (line == null) {
                    break;
                }
                while (col < truckMapCol) {
                    String[] numbers = line.split("\\s+");
                    if (col < numbers.length) {
                        int num = Integer.parseInt(numbers[col]);
                        truckTileNum[col][row] = num;
                    }
                    col++;
                }
                if (col == truckMapCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error reading trucks file '" + filePath + "': " + e.getMessage());
        }
    }

    private void loadInteriorMap(String fileName, int[][] targetMap) {
        // Load the inside of the stalls or trucks
        try {
            InputStream is = getClass().getResourceAsStream("/res/maps/" + fileName);
            BufferedReader br;
            if (is != null) {
                br = new BufferedReader(new InputStreamReader(is));
            } else {
                br = new BufferedReader(new java.io.FileReader("res/maps/" + fileName));
            }

            int col = 0;
            int row = 0;
            while (col < interiorCols && row < interiorRows) {
                String line = readNextNonEmptyLine(br);
                if (line == null) {
                    break;
                }
                while (col < interiorCols) {
                    String[] numbers = line.split("\\s+");
                    if (col < numbers.length) {
                        targetMap[col][row] = Integer.parseInt(numbers[col]);
                    }
                    col++;
                }
                if (col == interiorCols) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Failed to load interior map: " + fileName);
        }
    }

    // Checks if the player has just entered a room (stall or truck) and loads the corresponding interior map
    public void loadInterior() {
        if (gp.Current_level == 1) {
            loadStallInterior();
        } else if (gp.Current_level >= 2) {
            // Level 2 and 3 both use truck interiors
            loadTruckInterior();
        }
    }

    /**
     * Clears any currently loaded interior and resets the last-handled markers.
     * Call this when exiting an interior so stale state doesn't persist.
     */
    public void clearCurrentInterior() {
        currentStallMap = null;
        lastHandledStall = "";
        lastHandledTruck = "";
    }

    // Checks if the player has just entered a stall, and if so loads the corresponding interior map
    private void loadStallInterior() {
        String current = CollisionChecker.lastContactStall;

        if (current.isEmpty()) {
            lastHandledStall = "";
            return;
        }

        // Do nothing if player just entered the same stall, avoids reloading the same map every frame while inside
        if (current.equals(lastHandledStall)) {
            return;
        }

        lastHandledStall = current;
        switch (current) {
            case "Red" ->
                currentStallMap = redStallMap;
            case "Blue" ->
                currentStallMap = blueStallMap;
            case "Green" ->
                currentStallMap = greenStallMap;
        }
    }

    // Checks if the player has just entered a truck, and if so loads the corresponding interior map
    private void loadTruckInterior() {
        String current = CollisionChecker.lastContactTruck;

        if (current.isEmpty()) {
            lastHandledTruck = "";
            return;
        }

        // Do nothing if player just entered the same truck, avoids reloading the same map every frame while inside
        if (current.equals(lastHandledTruck)) {
            return;
        }

        lastHandledTruck = current;
        switch (current) {
            case "Red" ->
                currentStallMap = redtruckMap;
            case "Green" ->
                currentStallMap = greentruckMap;
        }
    }

    // Draws the interior (stall or truck) using the tile map, then draws the door on top
    public void drawInterior(Graphics2D g2) {
        if (currentStallMap != null) {
            int col = 0;
            int row = 0;
            while (col < interiorCols && row < interiorRows) {
                int tileNum = currentStallMap[col][row];
                int screenX = col * gp.tileSize;
                int screenY = row * gp.tileSize;
                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                col++;
                if (col == interiorCols) {
                    col = 0;
                    row++;
                }
            }
        }

        // Draw the exit door
        g2.setColor(new Color(32, 32, 32));
        g2.fillRect(doorX, doorY, gp.tileSize, gp.tileSize * 7);
    }

    // For CollisionChecker to access the currently loaded interior map and check for collisions with stations
    public int[][] getCurrentInteriorMap() {
        return currentStallMap;
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[worldCol][worldRow];
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX
                    && worldX - gp.tileSize < gp.player.worldX + gp.player.screenX
                    && worldY + gp.tileSize > gp.player.worldY - gp.player.screenY
                    && worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
            worldCol++;
            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }

        int stallCol = 0;
        int stallRow = 0;

        switch (gp.Current_level) {
            case 1 -> {
                while (stallCol < stallMapCol && stallRow < stallMapRow) {
                    int stallNum = stallTileNum[stallCol][stallRow];

                    if (stallNum != 0) {
                        int stallWorldX = stallCol * stallTileSize;
                        int stallWorldY = stallRow * stallTileSize;
                        int stallScreenX = stallWorldX - gp.player.worldX + gp.player.screenX;
                        int stallScreenY = stallWorldY - gp.player.worldY + gp.player.screenY;
                        if (stallScreenX + stallTileSize > 0 && stallScreenX < gp.screenWidth
                                && stallScreenY + stallTileSize > 0 && stallScreenY < gp.screenHeight) {
                            for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 4; j++) {
                                    int bgTileX = stallCol * 4 + i;
                                    int bgTileY = stallRow * 4 + j;
                                    if (bgTileX < gp.maxWorldCol && bgTileY < gp.maxWorldRow) {
                                        int bgTileNum = mapTileNum[bgTileX][bgTileY];
                                        int bgScreenX = (stallCol * stallTileSize + i * gp.tileSize) - gp.player.worldX + gp.player.screenX;
                                        int bgScreenY = (stallRow * stallTileSize + j * gp.tileSize) - gp.player.worldY + gp.player.screenY;
                                        g2.drawImage(tile[bgTileNum].image, bgScreenX, bgScreenY, gp.tileSize, gp.tileSize, null);
                                    }
                                }
                            }
                            g2.drawImage(tile[stallNum].image, stallScreenX, stallScreenY, stallTileSize, stallTileSize, null);
                        }
                    }
                    stallCol++;
                    if (stallCol == stallMapCol) {
                        stallCol = 0;
                        stallRow++;
                    }
                }
                break;
            }
            case 2 -> {
                while (stallCol < truckMapCol && stallRow < truckMapRow) {
                    int truckNum = truckTileNum[stallCol][stallRow];

                    if (truckNum != 0) {
                        int truckWorldX = stallCol * truckTileSize;
                        int truckWorldY = stallRow * truckTileSize;
                        int truckScreenX = truckWorldX - gp.player.worldX + gp.player.screenX;
                        int truckScreenY = truckWorldY - gp.player.worldY + gp.player.screenY;
                        if (truckScreenX + truckTileSize > 0 && truckScreenX < gp.screenWidth
                                && truckScreenY + truckTileSize > 0 && truckScreenY < gp.screenHeight) {
                            for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 4; j++) {
                                    int bgTileX = stallCol * 4 + i;
                                    int bgTileY = stallRow * 4 + j;
                                    if (bgTileX < gp.maxWorldCol && bgTileY < gp.maxWorldRow) {
                                        int bgTileNum = mapTileNum[bgTileX][bgTileY];
                                        int bgScreenX = (stallCol * truckTileSize + i * gp.tileSize) - gp.player.worldX + gp.player.screenX;
                                        int bgScreenY = (stallRow * truckTileSize + j * gp.tileSize) - gp.player.worldY + gp.player.screenY;
                                        g2.drawImage(tile[bgTileNum].image, bgScreenX, bgScreenY, gp.tileSize, gp.tileSize, null);
                                    }
                                }
                            }
                            g2.drawImage(tile[truckNum].image, truckScreenX, truckScreenY, truckTileSize, truckTileSize, null);
                        }
                    }
                    stallCol++;
                    if (stallCol == truckMapCol) {
                        stallCol = 0;
                        stallRow++;
                    }
                }
                break;
            }
            case 3 -> {
                // Level 3 has no trucks - do nothing
            }
        }
    }
}
