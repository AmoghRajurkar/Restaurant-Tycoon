package entity;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.Gamepanel;
import main.KeyHandler;

public class Player extends Entity {
    KeyHandler keyH; // Reference to the KeyHandler, which can be used to check the state of key presses
    public boolean boostActive = false; // Indicates whether the boost is currently active
    public int boostTimer = 0; // Timer to track the duration of the boost
    public boolean boostReloading = false; // Indicates whether the boost is currently reloading
    public int reloadTimer = 0; // Timer to track the duration of the boost reload
    public final int maxBoostTimer = 30; // Frames boost lasts at 60 FPS
    public final int maxBoostReload = 60; // Frames to recharge boost at 60 FPS
    public int animationThreshold; // Variable to control the speed of the walking animation, which can be adjusted based on boost status
    public final int screenX; // X position of the player on the screen, which can be used for rendering the player
    public final int screenY; // Y position of the player on the screen, which can be used for rendering the player

    public Player(Gamepanel gp, KeyHandler keyH) {

        super(gp);
        this.keyH = keyH; // Initialize the KeyHandler reference

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2); // Set the player's X position to the center of the screen
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2); // Set the player's Y position to the center of the screen

        solidArea = new Rectangle(); // Define the solid area for collision detection (adjust as needed)
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = gp.tileSize - 16;
        solidArea.height = gp.tileSize - 16;
        setDefaultValues(); // Set the default values for the player's position and speed
        getPlayerImage(); // Load player images
    }

    private void setDefaultValues() {
        worldX = gp.tileSize * 30; // Set the default X position of the player
        worldY = gp.tileSize * 22; // Set the default Y position of the player
        speed = 5; // Set the default speed of the player
        direction = "down"; // Set the default direction of the player
    }

    public void update() {
        // Update game state logic
        isMoving = false;

        if (keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true) {
            isMoving = true;

            if (keyH.upPressed == true) {
                direction = "up";
            }
            if (keyH.downPressed == true) {
                direction = "down";
            }
            if (keyH.leftPressed == true) {
                direction = "left";
            }
            if (keyH.rightPressed == true) {
                direction = "right";
            }

            // Check for collisions with tiles
            collisionOn = false; // Reset collision flag before checking for collisions
            gp.cChecker.checkTile(this); // Check for collisions with tiles

            // Check world boundary — stop the player when the edge of the map would come into view
            if (direction.equals("up") && worldY - screenY <= 0) {
                collisionOn = true;
            }
            if (direction.equals("down") && worldY + screenY + gp.tileSize >= gp.worldHeight) {
                collisionOn = true;
            }
            if (direction.equals("left") && worldX - screenX <= 0) {
                collisionOn = true;
            }
            if (direction.equals("right") && worldX + screenX + gp.tileSize >= gp.worldWidth) {
                collisionOn = true;
            }

            // If collision is fasle, player can move
            if (collisionOn == false) {
                switch (direction) {
                    case "up" -> {
                    worldY -= speed; // Move player up
                    break;
                    }
                    case "down" -> {
                        worldY += speed; // Move player down
                        break;
                    }
                    case "left" -> {
                        worldX -= speed; // Move player left
                        break;
                    }
                    case "right" -> {
                        worldX += speed; // Move player right
                        break;
                    }
                }
            }

            SpriteCounter++; // Increment the sprite counter for animation timing
            if (boostActive) {
                animationThreshold = 6; // Faster animation when boosting
            } else {
                animationThreshold = 12; // Normal animation speed
            }

            if (SpriteCounter > animationThreshold) {
                if (SpriteNum == 1) {
                    SpriteNum = 2; // Switch to the second sprite image
                } else if (SpriteNum == 2) {
                    SpriteNum = 1; // Switch back to the first sprite image
                }
                SpriteCounter = 0; // Reset the sprite counter
            }
        } else {
            SpriteCounter = 0; // Stop animation timing when idle
            SpriteNum = 1; // Reset to the first frame so still image is stable
        }

        // if boost key is pressed and boost is not active or reloading, activate boost
        if (keyH.boostPressed == true && !boostActive && !boostReloading) {
            boostActive = true;
            boostTimer = 30; // .5 second at 60 FPS
        }
        
        // Handle boost timer and reloading logic
        if (boostActive) {
            boostTimer--;
            if (boostTimer <= 0) {
                boostActive = false;
                boostReloading = true;
                reloadTimer = 60; // 1 second reload time at 60 FPS
            }
        }
        
        // Handle boost reloading timer
        if (boostReloading) {
            reloadTimer--;
            if (reloadTimer <= 0) {
                boostReloading = false;
            }
        }
        
        // Set player speed based on boost status
        if (boostActive) {
            speed = 8; // Increased speed when boost is active
        } else {
            speed = 5;
        }
    }

    public float getBoostChargeRatio() {
        if (boostActive || !boostReloading) {
            return 1.0f;
        }
        return 1.0f - (float) reloadTimer / maxBoostReload;
    }

    public boolean isBoostReady() {
        return !boostActive && !boostReloading;
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null; // Variable to hold the current image to be drawn based on the player's direction and animation state

        if (!isMoving) {
            // Use still images when the player is not moving
            switch (direction) {
                case "up" -> image = upStill;
                case "down" -> image = downStill;
                case "left" -> image = leftStill;
                case "right" -> image = rightStill;
            }
        } else {
            // Use walking animation when the player is moving
            switch (direction) {
                case "up" -> {
                    if (SpriteNum == 1) {
                        image = up1;
                    } else {
                        image = up2;
                    }
                }
                case "down" -> {
                if (SpriteNum == 1) {
                        image = down1;
                    } else {
                        image = down2;
                    }
                }
                case "left" -> {
                    if (SpriteNum == 1) {
                        image = left1;
                    } else {
                        image = left2;
                    }
                }
                case "right" -> {
                    if (SpriteNum == 1) {
                        image = right1;
                    } else {
                        image = right2;
                    }
                }
            }
        }

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null); // Draw the player image at the current position with the specified tile size
    }

    private void getPlayerImage() {
        // Load player images for different directions and animations
        try {
            up1 = ImageIO.read(new File("res/player/up1.png"));
            up2 = ImageIO.read(new File("res/player/up2.png"));
            down1 = ImageIO.read(new File("res/player/down1.png"));
            down2 = ImageIO.read(new File("res/player/down2.png"));
            left1 = ImageIO.read(new File("res/player/left1.png"));
            left2 = ImageIO.read(new File("res/player/left2.png"));
            right1 = ImageIO.read(new File("res/player/right1.png"));
            right2 = ImageIO.read(new File("res/player/right2.png"));
            upStill = ImageIO.read(new File("res/player/up_still.png"));
            downStill = ImageIO.read(new File("res/player/down_still.png"));
            leftStill = ImageIO.read(new File("res/player/left_still.png"));
            rightStill = ImageIO.read(new File("res/player/right_still.png"));
        }
        catch (IOException e) {
            System.out.println("Error loading player images");
        }
    }
}