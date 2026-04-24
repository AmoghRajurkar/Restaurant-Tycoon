package entity;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.Gamepanel;
import main.KeyHandler;

public class Player extends Entity {
    Gamepanel gp; // Reference to the Gamepanel, which can be used to access game-related properties and methods
    KeyHandler keyH; // Reference to the KeyHandler, which can be used to check the state of key presses
    // Boost variables
    public boolean boostActive = false; // Indicates whether the boost is currently active
    public int boostTimer = 0; // Timer to track the duration of the boost
    public boolean boostReloading = false; // Indicates whether the boost is currently reloading
    public int reloadTimer = 0; // Timer to track the duration of the boost reload

    public Player(Gamepanel gp, KeyHandler keyH) {
        this.gp = gp; // Initialize the Gamepanel reference
        this.keyH = keyH; // Initialize the KeyHandler reference
        setDefaultValues(); // Set the default values for the player's position and speed
        getPlayerImage(); // Load player images
    }

    private void setDefaultValues() {
        x = 100; // Set the default X position of the player
        y = 100; // Set the default Y position of the player
        speed = 5; // Set the default speed of the player
        direction = "down"; // Set the default direction of the player
    }

    public void update() {
        // Update game state logic
        isMoving = false;

        if (keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true) {
            isMoving = true;

            if (keyH.upPressed == true) {
                y -= speed; // Move player up
                direction = "up";
            }
            if (keyH.downPressed == true) {
                y += speed; // Move player down
                direction = "down";
            }
            if (keyH.leftPressed == true) {
                x -= speed; // Move player left
                direction = "left";
            }
            if (keyH.rightPressed == true) {
                x += speed; // Move player right
                direction = "right";
            }

            SpriteCounter++; // Increment the sprite counter for animation timing
            if (SpriteCounter > 12) { // Change sprite every 12 frames (0.2 seconds at 60 FPS)
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
            speed = 9; // Increased speed when boost is active
        } else {
            speed = 5;
        }
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
                case "up" -> image = (SpriteNum == 1) ? up1 : up2;
                case "down" -> image = (SpriteNum == 1) ? down1 : down2;
                case "left" -> image = (SpriteNum == 1) ? left1 : left2;
                case "right" -> image = (SpriteNum == 1) ? right1 : right2;
            }
        }

        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null); // Draw the player image at the current position with the specified tile size
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