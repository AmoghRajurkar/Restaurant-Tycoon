package main.tile;
// Graphics rendering for drawing on the game panel
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import main.Gamepanel;

public class TileManager {
    Gamepanel gp;
    Tile[] tile;
    int mapTileNum[][];

    public TileManager(Gamepanel gp) {
        this.gp = gp;
        tile = new Tile[17]; // 17 types of tiles
        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow]; // 20 x 15 tiles
        getTileImage(); // Load the tile images
        loadMap("/res/maps/map1.txt"); // Load the map layout from a text file
    }

    private BufferedImage loadImage(String fileName) {
        try (InputStream is = getClass().getResourceAsStream("/" + fileName)) {
            if (is != null) {
                return ImageIO.read(is);
            }
        } catch (IOException ignored) {
        }

        try {
            return ImageIO.read(new File("res/" + fileName));
        } catch (IOException e) {
            System.err.println("Failed to load tile image '" + fileName + "': " + e.getMessage());
            return null;
        }
    }

    private void getTileImage() {
        // Load tile images and set collision properties
        tile[0] = new Tile();
        tile[0].image = loadImage("Grass.png"); // res/Grass.png

        tile[1] = new Tile();
        tile[1].image = loadImage("Rock.png"); // res/Rock.png

        tile[2] = new Tile();
        tile[2].image = loadImage("Floor.png"); // res/Floor.png

        tile[3] = new Tile();
        tile[3].image = loadImage("Bush.png"); // res/Bush.png

        tile[4] = new Tile();
        tile[4].image = loadImage("Concrete_Path.png"); // res/Concrete_Path.png

        tile[5] = new Tile();
        tile[5].image = loadImage("Road.png"); // res/Road.png

        tile[6] = new Tile();
        tile[6].image = loadImage("Rough_Path.png"); // res/Rough_Path.png

        tile[7] = new Tile();
        tile[7].image = loadImage("Tree.png"); // res/Tree.png

        tile[8] = new Tile();
        tile[8].image = loadImage("stall/Top_Left_Stall.png"); // res/stall/Top_Left_Stall.png

        tile[9] = new Tile();
        tile[9].image = loadImage("stall/Top_Middle_Stall.png"); // res/stall/Top_Middle_Stall.png

        tile[10] = new Tile();
        tile[10].image = loadImage("stall/Top_Right_Stall.png"); // res/stall/Top_Right_Stall.png

        tile[11] = new Tile();
        tile[11].image = loadImage("stall/Middle_Left_Stall.png"); // res/stall/Middle_Left_Stall.png

        tile[12] = new Tile();
        tile[12].image = loadImage("stall/Middle_Middle_Stall.png"); // res/stall/Middle_Middle_Stall.png

        tile[13] = new Tile();
        tile[13].image = loadImage("stall/Middle_Right_Stall.png"); // res/stall/Middle_Right_Stall.png

        tile[14] = new Tile();
        tile[14].image = loadImage("stall/Bottom_Left_Stall.png"); // res/stall/Bottom_Left_Stall.png

        tile[15] = new Tile();
        tile[15].image = loadImage("stall/Bottom_Middle_Stall.png"); // res/stall/Bottom_Middle_Stall.png

        tile[16] = new Tile();
        tile[16].image = loadImage("stall/Bottom_Right_Stall.png"); // res/stall/Bottom_Right_Stall.png
    }

    private void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br;
            if (is != null) {
                br = new BufferedReader(new InputStreamReader(is));
            } else {
                // Fallback to file system reading
                br = new BufferedReader(new java.io.FileReader(filePath.substring(1))); // Remove leading "/"
            }
            int col = 0;
            int row = 0;
            while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
                String line = br.readLine();
                if (line == null) 
                    break; // End of file
                while (col < gp.maxScreenCol) {
                    String numbers[] = line.split(" ");
                    if (col < numbers.length) {
                        int num = Integer.parseInt(numbers[col]);
                        mapTileNum[col][row] = num;
                    }
                    col++;
                }
                if (col == gp.maxScreenCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error reading map file '" + filePath + "': " + e.getMessage());
        }
    }

    public void draw(Graphics2D g2) {
        // Draw the tiles on the screen

        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while  (col < gp.maxScreenCol && row < gp.maxScreenRow) {
            int tileNum = mapTileNum[col][row];
            g2.drawImage(tile[tileNum].image, x, y, gp.tileSize, gp.tileSize, null);
            col++;
            x += gp.tileSize;
            
            if (col == gp.maxScreenCol) {
                col = 0;
                x = 0;
                row++;
                y += gp.tileSize;
            }
        }
    }
}
