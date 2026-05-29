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



    /**
     * Constructor for the TileManager class, which initializes the reference to the Gamepanel, 
     * creates the tile array and map tile number arrays, calculates stall dimensions and positions, 
     * loads tile images, and loads the world map and stall maps from text files. It also preloads the 
     * interior maps for each stall to allow for instant loading when the player enters a stall. 
     * The constructor sets up all necessary data structures and resources for managing and rendering tiles in the game.
     * @param gp The Gamepanel instance that this TileManager belongs to, allowing it to access game-related properties and methods.
     */
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

    /**
     * Helper method to load an image from the given file name. It first tries to load the image as a resource from the classpath, and if that fails, it attempts to load it from the file system. If both attempts fail, it prints an error message and returns null. This method is used to load tile images for the game.
     * @param fileName The name of the image file to load, which can be a path relative to the classpath or an absolute file system path.
     * @return A BufferedImage object containing the loaded image, or null if the image could not be loaded from either the classpath or the file system.
     */
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


    /**
     * Method to initialize the tile array with Tile objects and load their corresponding images from the specified file paths.
     * Each tile is assigned an image and a collision property based on its type. The method uses the loadImage helper method to
     * read the image files and assigns them to the appropriate index in the tile array. If there is an error loading any of the images, 
     * it prints an error message indicating which image failed to load. This method sets up all the tile types that will be used in the game, 
     * including grass, rock, stall walls, bushes, roads, paths, trees, stalls, grills, fridges, tables, and food items.
     */
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

    /**
     * Method to load the world map from a text file. It reads the file line by line, splits each line into numbers, and fills the mapTileNum 2D array 
     * with the corresponding tile numbers. The method handles both loading from the classpath as a resource and from the file system, allowing for flexibility
     * in how the map files are stored. If there is an error reading the file, it catches the IOException and prints an error message indicating which file failed to load.
     * @param filePath The path to the map file to load. The file should contain rows of numbers representing tile types for the world map.
     */
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
            br.close();
        } catch (IOException e) {
            System.err.println("Error reading map file '" + filePath + "': " + e.getMessage());
        }
    }

    /**
     * Method to load the stall locations in the world from a text file. It reads the file line by line, splits each line into numbers, and fills the stallTileNum 2D array 
     * with the corresponding stall tile numbers. The method handles both loading from the classpath as a resource and from the file system, allowing for flexibility in how the 
     * stall map files are stored. If there is an error reading the file, it catches the IOException and prints an error message indicating which file failed to load.
     * @param filePath The path to the stall map file to load. The file should contain rows of numbers representing stall locations in the world.
     */
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

    /**
     * Helper method to load the interior map for a stall from a text file. It reads the file line by line, splits each line into numbers, and fills the targetMap 2D array 
     * with the corresponding tile numbers for the interior of the stall. The method handles both loading
     * @param fileName The name of the interior map file to load. The file should contain rows of numbers representing tile types for the stall interior.
     * @param targetMap The 2D array to fill with the tile numbers for the stall interior. This should be one of the pre-initialized arrays for the red, blue, or green stall interiors.
     */
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

    /**
     * Method to load the appropriate stall interior map based on the last stall the player entered. It checks the CollisionChecker's lastContactStall
     * variable to determine which stall was entered, and if it is different from the last handled stall, it updates the currentStallMap reference to point 
     * to the corresponding interior map for that stall. If the player has not entered any stall, it resets the lastHandledStall and currentStallMap to null.
     */
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

    /**
     * Draws the stall interior using the tile map, then draws the door on top
     * @param g2 The graphics context to draw on
     */
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

    /**
     * This method returns the current stall map
     * @return The current stall map
     */
    public int[][] getCurrentStallMap() {
        return currentStallMap;
    }
    /**
     * Draws the world map and stall tiles based on the players position
     * @param g2 The graphics context to draw on
     */
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
