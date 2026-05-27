package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import main.Gamepanel;

public class Customer extends Entity {

    public int screenX; // X position of the customer on the screen, which can be used for rendering the customer
    public int screenY; // Y position of the customer on the screen, which can be used for rendering the customer
    public int stall1X = 760;// X position of the first stall, which can be used for determining the customer's path to the stall
    public int stall1Y = 750;// Y position of the first stall, which can be used for determining the customer's path to the stall
    public int stall2X = 2200;// X position of the second stall, which can be used for determining the customer's path to the stall
    public int stall2Y = 1010;// Y position of the second stall, which can be used for determining the customer's path to the stall
    public int animationThreshold; // Variable to control the speed of the walking animation, which can be adjusted based on boost status
    public Random rand = new Random();// Random number generator to determine the customer's path and behavior
    public int InPath = rand.nextInt(2) + 1; // Randomly choose a path to come in for the customer to take (1 or 2)
    public boolean isServed = true; // Flag to indicate whether the customer has been served or not
    public int outPath = InPath; // Out path is based on in path
    public boolean leftMap = false; // Flag to indicate whether the customer has left the map or not

    public Customer(Gamepanel gp, int x, int y) {
        super(gp);
        direction = "up";
        speed = 5;
        this.worldX = x;
        this.worldY = y;
        screenX = x;
        screenY = y;
        solidArea = new java.awt.Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = gp.tileSize - 16;
        solidArea.height = gp.tileSize - 16;
        getCustomerImage();
    }

    private void getCustomerImage() {
        // Load customer images for different directions and animations
        try {
            up1 = ImageIO.read(new File("res/customer/customer1up1.png"));
            up2 = ImageIO.read(new File("res/customer/customer1up2.png"));
            down1 = ImageIO.read(new File("res/customer/customer1down1.png"));
            down2 = ImageIO.read(new File("res/customer/customer1down2.png"));
            left1 = ImageIO.read(new File("res/customer/customer1left1.png"));
            left2 = ImageIO.read(new File("res/customer/customer1left_still.png"));
            right1 = ImageIO.read(new File("res/customer/customer1right1.png"));
            right2 = ImageIO.read(new File("res/customer/customer1right_still.png"));
            upStill = ImageIO.read(new File("res/customer/customer1up_still.png"));
            downStill = ImageIO.read(new File("res/customer/customer1down_still.png"));
            leftStill = ImageIO.read(new File("res/customer/customer1left_still.png"));
            rightStill = ImageIO.read(new File("res/customer/customer1right_still.png"));
        } catch (IOException e) {
            System.out.println("Error loading customer images");
        }
    }

    public void InPath() {
        if (InPath == 1) {
            if (worldY > stall1Y) {
                direction = "up";
                update(); // Update the customer's position and behavior based on the chosen InPath

            } else if (worldX > stall1X) {
                direction = "left";
                update(); // Update the customer's position and behavior based on the chosen InPath

            }

        }
        if (InPath == 2) {
            if (worldY > 1300) {
                direction = "up";
                update(); // Update the customer's position and behavior based on the chosen InPath
            } else if (worldX < 2000) {
                direction = "right";
                update(); // Update the customer's position and behavior based on the chosen InPath
            } else if (worldY > stall2Y) {
                direction = "up";
                update(); // Update the customer's position and behavior based on the chosen InPath
            } else if (worldX < stall2X) {
                direction = "right";
                update(); // Update the customer's position and behavior based on the chosen InPath
            }
        }
    }

    public void outPath() {
        if (InPath == 1) {
            if (worldY > 0) {
                direction = "up";
                update(); // Update the customer's position and behavior based on the chosen InPath
            } else if (worldX < 500) {
                direction = "right";
                update(); // Update the customer's position and behavior based on the chosen InPath
            }
            if (worldY < 20) {
                leftMap = true;
            }

        }
        if (InPath == 2) {
            if (worldY > 0) {
                direction = "up";
                update(); // Update the customer's position and behavior based on the chosen InPath
            } else if (worldX > 500) {
                direction = "left";
                update(); // Update the customer's position and behavior based on the chosen InPath
            }
            if (worldY < 20) {
                leftMap = true;
            }

        }

    }

    public void update() {
        // Logic to update the customer's position and behavior goes here
        isMoving = false;
        animationThreshold = 12; // Normal animation speed

        // Check for collisions with tiles
        collisionOn = false; // Reset collision flag before checking for collisions
        gp.cChecker.customerCheckTile(this); // Check for collisions with tiles
        gp.cChecker.checkEntityCollision(this, gp.customers); // Check for collisions with other customers

        // Check world boundary — stop the player when the edge of the map would come into view
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

        // If collision is false, customer can move
        if (collisionOn == false) {
            isMoving = true;
            switch (direction) {
                case "up" -> {
                    worldY -= speed; // Update worldX to reflect the customer's movement in the world
                    isMoving = true;
                    break;
                }
                case "down" -> {
                    worldY += speed; // Update worldY to reflect the customer's movement in the world
                    isMoving = true;
                    break;
                }
                case "left" -> {
                    worldX -= speed; // Update worldX to reflect the customer's movement in the world
                    isMoving = true;
                    break;
                }
                case "right" -> {
                    worldX += speed; // Update worldX to reflect the customer's movement in the world
                    isMoving = true;
                    break;
                }
            }

            SpriteCounter++; // Increment the sprite counter for animation timing
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
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null; // Variable to hold the current image to be drawn based on the player's direction and animation state

        if (!isMoving) {
            // Use still images when the player is not moving
            switch (direction) {
                case "up" ->
                    image = upStill;
                case "down" ->
                    image = downStill;
                case "left" ->
                    image = leftStill;
                case "right" ->
                    image = rightStill;
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

        int drawX = worldX - gp.player.worldX + gp.player.screenX;
        int drawY = worldY - gp.player.worldY + gp.player.screenY;
        g2.drawImage(image, drawX, drawY, gp.tileSize, gp.tileSize, null); // Draw the customer image at the current position with the specified tile size
    }
}
