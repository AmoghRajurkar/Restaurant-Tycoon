package main.tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import main.CollisionChecker;
import main.Gamepanel;

public class TileManager {

    Gamepanel gp;
    public Tile[] tile;
    public int mapTileNum[][];
    public int stallTileNum[][];
    public int invisibleWallTileNum[][];
    int stallTileSize;
    int stallMapCol;
    int stallMapRow;
    private String lastHandledStall = "";

    // Interior map dimensions — 20 cols x 15 rows, fits the screen perfectly
    private final int interiorCols = 20;
    private final int interiorRows = 15;

    // One 2D array per stall, loaded once at startup
    private final int[][] redStallMap = new int[interiorCols][interiorRows];
    private final int[][] blueStallMap = new int[interiorCols][interiorRows];
    private final int[][] greenStallMap = new int[interiorCols][interiorRows];

    // Points to whichever stall the player just entered
    private int[][] currentStallMap = null;

    // Door position — bottom-right corner, one tileSize away from each wall
    public int doorX;
    public int doorY;

    public TileManager(Gamepanel gp) {
        this.gp = gp;
        tile = new Tile[26];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        stallTileSize = gp.tileSize * 4;
        stallMapCol = gp.maxWorldCol / 4;
        stallMapRow = gp.maxWorldRow / 4;
        stallTileNum = new int[stallMapCol][stallMapRow];

        // Door is one tileSize away from the right and bottom walls
        doorX = gp.screenWidth - gp.tileSize;
        doorY = gp.screenHeight - gp.tileSize * 11;

        getTileImage();
        loadWorldMap("/res/maps/worldmap1.txt");
        loadStallsInWorld("/res/maps/stalls.txt");

        // Load all three stall interiors so entering is instant
        LoadStallInteriorMap("red_stall.txt", redStallMap);
        LoadStallInteriorMap("blue_stall.txt", blueStallMap);
        LoadStallInteriorMap("green_stall.txt", greenStallMap);
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
        tile[5].image = loadImage("res/tiles/Road.png");

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
        tile[18].image = loadImage("res/tiles/milkshake_table.png");
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
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                while (col < gp.maxWorldCol) {
                    String[] numbers = line.split(" ");
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
            System.out.println(mapTileNum[0][0]);
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
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                while (col < stallMapCol) {
                    String[] numbers = line.split(" ");
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

    private void LoadStallInteriorMap(String fileName, int[][] targetMap) {
        // Load the inside of the stalls
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
                String line = br.readLine();
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

    // Checks if the player has just entered a stall, and if so loads the corresponding interior map
    public void LoadStallInterior() {
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

    // Draws the stall interior using the tile map, then draws the door on top
    public void drawStallInterior(Graphics2D g2) {
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

        // Draw the exit door in the stall
        g2.setColor(new Color(32, 32, 32));
        g2.fillRect(doorX, doorY, gp.tileSize, gp.tileSize * 7);
    }

    // For CollisionChecker to access the currently loaded stall map and check for collisions with stations
    public int[][] getCurrentStallMap() {
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
    }
}
