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
    public int InPath = rand.nextInt(3) + 1; // Randomly choose a path to come in for the customer to take (1 through 3)
    public boolean isServed = false; // Flag to indicate whether the customer has been served or not
    public boolean place_order = false; // True once we've placed a level 3 order for this customer
    public boolean leftMap = false; // Flag to indicate whether the customer has left the map or not

    /**
     * Constructor for the Customer class, which initializes the customer's
     * position, speed, direction, and collision area. It also loads the
     * customer's images for different directions and animations.
     *
     * @param gp The Gamepanel object, which provides access to the game
     * environment and resources
     * @param x The initial X position of the customer in the world, which can
     * be used for determining the customer's starting point and movement
     * @param y The initial Y position of the customer in the world, which can
     * be used for determining the customer's starting point and movement
     */
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

    /**
     * Method to load the customer's images for different directions and
     * animations, which can be used for rendering the customer in the game. The
     * method uses ImageIO to read the image files from the specified paths and
     * assigns them to the corresponding variables for each direction and
     * animation state. If there is an error loading the images, it catches the
     * IOException and prints an error message.
     */
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

    /**
     * Method to determine the customer's path to the stall based on the
     * randomly chosen InPath variable. The method checks the customer's current
     * position in the world and updates the direction accordingly to guide the
     * customer towards the stall. It also calls the update() method to update
     * the customer's position and behavior based on the chosen InPath. The
     * method handles two different paths for the customer to take, depending on
     * whether InPath is 1 or 2, and adjusts the direction and movement
     * accordingly to ensure that the customer reaches the stall successfully.
     */
    public void InPath() {
        switch (gp.Current_level) {
            case 2 ->
                inPathLevel2();
            case 3 ->
                inPathLevel3();
            default ->
                inPathLevel1();
        }
    }

    private void inPathLevel1() {
        if (InPath == 3) {
            InPath = rand.nextInt(2) + 1; // If InPath is 3, randomly choose between path 1 and 2 to ensure customers only come in from the two main paths

        }
        if (InPath == 1) {
            if (worldY > stall1Y) {
                direction = "up";
                update();
            } else if (worldX > stall1X) {
                direction = "left";
                update();
            }
        }
        if (InPath == 2) {
            if (worldY > 1300) {
                direction = "up";
                update();
            } else if (worldX < 2000) {
                direction = "right";
                update();
            } else if (worldY > stall2Y) {
                direction = "up";
                update();
            } else if (worldX < stall2X) {
                direction = "right";
                update();
            }
        }
    }

    private void inPathLevel2() {
        if (InPath == 1) {
            if (worldY > gp.tileSize * 28 - 40) {
                direction = "up";
                update();
            } else if (worldX < gp.tileSize * 15) {
                direction = "right";
                update();
            }
        }
        if (InPath == 2) {
            if (worldY > gp.tileSize * 29 - 40) {
                direction = "up";
                update();
            } else if (worldX < gp.tileSize * 27) {
                direction = "right";
                update();
            } else if (worldY > gp.tileSize * 28 - 40) {
                direction = "up";
                update();
            } else if (worldX < gp.tileSize * 31) {
                direction = "right";
                update();
            }
        }
        if (InPath == 3) {
            if (worldY > gp.tileSize * 30 - 40) {
                direction = "up";
                update();
            } else if (worldX < gp.tileSize * 43) {
                direction = "right";
                update();
            } else if (worldY > gp.tileSize * 28 - 40) {
                direction = "up";
                update();
            } else if (worldX < gp.tileSize * 47) {
                direction = "right";
                update();
            }
        }
    }

    private void inPathLevel3() {
        if (InPath == 1) {
            if (worldY > gp.tileSize * 34) {
                direction = "up";
                update();
            } else if (worldX < gp.tileSize * 40) {
                direction = "right";
                update();
            } else if (worldY > gp.tileSize * 25) {
                direction = "up";
                update();
            }
        }
        if (InPath == 2) {
            if (worldY > gp.tileSize * 34) {
                direction = "up";
                update();
            } else if (worldX < gp.tileSize * 41) {
                direction = "right";
                update();
            } else if (worldY > gp.tileSize * 25) {
                direction = "up";
                update();
            }
        }
        if (InPath == 3) {
            if (worldY > gp.tileSize * 34) {
                direction = "up";
                update();
            } else if (worldX < gp.tileSize * 42) {
                direction = "right";
                update();
            } else if (worldY > gp.tileSize * 25) {
                direction = "up";
                update();
            }
        }
    }

    public void outPath() {
        switch (gp.Current_level) {
            case 2 ->
                outPathLevel2();
            case 3 ->
                outPathLevel3();
            default ->
                outPathLevel1();
        }
    }

    private void outPathLevel1() {
        if (InPath == 1) {
            if (worldX > 700) {
                direction = "left";
            } else {
                direction = "up";
            }
            if (worldY < 20) {
                leftMap = true;
            }
            update();
        }
        if (InPath == 2) {
            if (worldX < 2300) {
                direction = "right";
            } else {
                direction = "up";
            }
            if (worldY < 20) {
                leftMap = true;
            }
            update();
        }
    }

    private void outPathLevel2() {
        if (InPath == 1 || InPath == 2 || InPath == 3) {
            direction = "down";
            if (worldY > gp.tileSize * 44) {
                leftMap = true;
            }
            update();
        }
    }

    private void outPathLevel3() {
        if (InPath == 1 || InPath == 2 || InPath == 3) {
            direction = "up";
            if (worldY < gp.tileSize) {
                leftMap = true;
            }
            update();
        }
    }

    /**
     * Method to update the customer's position and behavior based on the
     * current direction, speed, and collision status. The method checks for
     * collisions with tiles and other customers, as well as world boundaries,
     * to determine whether the customer can move in the desired direction. If
     * there are no collisions, it updates the customer's position accordingly
     * and manages the walking animation by switching between different sprite
     * images based on the SpriteCounter and animationThreshold variables. If
     * there is a collision or if the customer is idle, it resets the animation
     * to a still image to ensure a stable appearance when not moving.
     */
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

    /**
     * Method to draw the customer on the screen based on the current direction,
     * animation state, and position in the world. The method selects the
     * appropriate image to draw based on whether the customer is moving or
     * idle, and which direction they are facing. It calculates the drawing
     * position on the screen by adjusting the customer's world coordinates
     * relative to the player's position and screen coordinates. Finally, it
     * uses the Graphics2D object to draw the selected image at the calculated
     * position with the specified tile size.
     *
     * @param g2 The Graphics2D object used for drawing the customer on the
     * screen, which provides methods for rendering images and shapes in the
     * game
     */
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
