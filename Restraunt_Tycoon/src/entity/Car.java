package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import main.Gamepanel;
import main.OrderList;

public class Car extends Entity {

    private BufferedImage image;
    public int carSize = 4; // Size is 4x tile size
    public Random rand = new Random();
    public boolean place_order = false;
    public boolean isServed = false;
    public boolean leftMap = false;
    public OrderList order;

    public Car(Gamepanel gp, int worldX, int worldY) {
        super(gp);
        this.worldX = worldX;
        this.worldY = worldY;
        direction = "left";
        speed = 5;
        solidArea = new java.awt.Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = gp.tileSize * carSize - 16;
        solidArea.height = gp.tileSize * carSize - 16;
        loadImage();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(new File("res/tiles/Red_truck.png"));
        } catch (IOException e) {
            System.out.println("Failed to load car image: " + e.getMessage());
            image = null;
        }
    }

    public void InPath() {

        if (worldX > gp.tileSize * 34) {
            direction = "left";
            update();
        }
    }

    public void outPath() {
        direction = "left";
        if (worldX < gp.tileSize * 5) {
            leftMap = true;
        }
        update();
    }

    public void update() {
        // Update the car's position
        isMoving = false;

        // Check for collisions with tiles
        collisionOn = false; // Reset collision flag before checking for collisions
        gp.cChecker.checkEntityCollision(this, gp.cars); // Check for collisions with other cars

        // Check world boundary and stop the player when the edge of the map would come into view
        if (direction.equals("up") && worldY <= 0) {
            collisionOn = true;
            SpriteCounter = 0;
        }
        if (direction.equals("down") && worldY + gp.tileSize >= gp.worldHeight) {
            collisionOn = true;
            SpriteCounter = 0;
        }
        if (direction.equals("left") && worldX <= 0) {
            collisionOn = true;
            SpriteCounter = 0;
        }
        if (direction.equals("right") && worldX + gp.tileSize >= gp.worldWidth) {
            collisionOn = true;
            SpriteCounter = 0;
        }

        // If collision is false, car can move
        if (collisionOn == false) {
            isMoving = true;
            switch (direction) {
                case "up" -> {
                    worldY -= speed;
                    isMoving = true;
                    break;
                }
                case "down" -> {
                    worldY += speed;
                    isMoving = true;
                    break;
                }
                case "left" -> {
                    worldX -= speed;
                    isMoving = true;
                    break;
                }
                case "right" -> {
                    worldX += speed;
                    isMoving = true;
                    break;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        if (image == null) {
            return;
        }
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        int size = gp.tileSize * carSize;
        g2.drawImage(image, screenX, screenY, size, size, null);
    }
}
