package entity;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.CollisionChecker;
import main.Gamepanel;
import main.KeyHandler;
import main.OrderList;

public class Player extends Entity {
    Gamepanel gp; // Reference to the Gamepanel, which can be used to access game-related properties and methods
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
    public int roomX;
    public int roomY;

    public Player(Gamepanel gp, KeyHandler keyH) {
        this.gp = gp; // Initialize the Gamepanel reference
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
        roomX = 400;
        roomY = 500;
        speed = 5; // Set the default speed of the player
        direction = "down"; // Set the default direction of the player
    }

    public void update() {
        // Update game state logic
        isMoving = false;

        if(keyH.interactPressed &&
        gp.gameState.equals(gp.WORLD_STATE) &&
        !CollisionChecker.lastContactStall.equals("")) {
            enterStall();
            keyH.interactPressed = false;
        }

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

            collisionOn = false;
            if(gp.gameState.equals(gp.WORLD_STATE)) {
                gp.cChecker.checkTile(this);
            }

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
                if(gp.gameState.equals(gp.WORLD_STATE)) {
                    switch (direction) {
                        case "up" -> worldY -= speed;
                        case "down" -> worldY += speed;
                        case "left" -> worldX -= speed;
                        case "right" -> worldX += speed;
                    }
                } else if(gp.gameState.equals(gp.STALL_STATE)) {
                    if (!gp.cChecker.checkStallTile(roomX, roomY, direction, speed)) {
                        switch (direction) {
                            case "up" -> roomY -= speed;
                            case "down" -> roomY += speed;
                            case "left" -> roomX -= speed;
                            case "right" -> roomX += speed;
                        }
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

        if(gp.gameState.equals(gp.STALL_STATE)) {
            // Exit zone matches the door drawn in TileManager — bottom-right, one tile from each wall
            Rectangle exitZone = new Rectangle(
                    gp.tileM.doorX,
                    gp.tileM.doorY,
                    gp.tileSize,
                    gp.tileSize
            );
            Rectangle playerBox = new Rectangle(
                    roomX,
                    roomY,
                    gp.tileSize,
                    gp.tileSize
            );

            if(playerBox.intersects(exitZone)) {
                exitStall();
            }
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

    public void enterStall() {
        gp.gameState = gp.STALL_STATE;
        gp.tileM.loadStallInsides();
        roomX = gp.screenWidth / 2 - gp.tileSize / 2;
        roomY = gp.screenHeight - 350;
        gp.repaint();

        OrderList order = new OrderList(1, CollisionChecker.contactStall);
        System.out.println(order);
    }

    public void exitStall() {
        gp.gameState = gp.WORLD_STATE;
        CollisionChecker.lastContactStall = "";
        gp.repaint();
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

        if(gp.gameState.equals(gp.WORLD_STATE)) {
            g2.drawImage(image, screenX, screenY,
                    gp.tileSize, gp.tileSize, null);
        } else {
            g2.drawImage(image, roomX, roomY,
                    gp.tileSize, gp.tileSize, null);
        }
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