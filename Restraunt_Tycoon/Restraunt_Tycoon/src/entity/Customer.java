package entity;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.Gamepanel;


public class Customer extends Entity {
    public  int screenX; // X position of the customer on the screen, which can be used for rendering the customer
    public  int screenY; // Y position of the customer on the screen, which can be used for rendering the customer
    public Customer(Gamepanel gp, int x, int y) {
        super(gp);
        direction = "up";
        speed = 5;
        this.worldX = x;
        this.worldY = y;
        screenX = x;
        screenY = y;


        getCustomerImage();
    }


    private void getCustomerImage() {
        try {
            up1 = ImageIO.read(new File("res/customer/customer1up1.png"));
            down1 = ImageIO.read(new File("res/customer/customer1down1.png"));
            left1 = ImageIO.read(new File("res/customer/customer1left1.png"));
            right1 = ImageIO.read(new File("res/customer/customer1right1.png"));
            upStill = ImageIO.read(new File("res/customer/customer1up_still.png"));
            downStill = ImageIO.read(new File("res/customer/customer1down_still.png"));
            leftStill = ImageIO.read(new File("res/customer/customer1left_still.png"));
            rightStill = ImageIO.read(new File("res/customer/customer1right_still.png"));
   
           


        } catch (IOException e) {
            System.out.println("Error loading customer images");
        }
    }


    public void update() {
        // Logic to update the customer's position and behavior goes here
        isMoving = false;


        // Check for collisions with tiles
        collisionOn = false; // Reset collision flag before checking for collisions
        gp.cChecker.checkTile(this); // Check for collisions with tiles


        // Check world boundary — stop the player when the edge of the map would come into view
        if (direction.equals("up") && worldY <= 0) {
            collisionOn = true;      
            System.out.println("Collision with upper boundary");
      }
        if (direction.equals("down") && worldY + gp.tileSize >= gp.worldHeight) {
            collisionOn = true;
            System.out.println("Collision with lower boundary");
        }
        if (direction.equals("left") && worldX  <= 0) {
            collisionOn = true;
            System.out.println("Collision with left boundary");
        }
        if (direction.equals("right") && worldX  + gp.tileSize >= gp.worldWidth) {
            collisionOn = true;
            System.out.println("Collision with right boundary");
        }


        // If collision is fasle, player can move
        if (collisionOn == false) {
            isMoving = true;
            switch (direction) {
                case "up" -> {
                    worldY -= speed; // Update worldX to reflect the player's movement in the world
                    isMoving = true;
                    break;
                }
                case "down" -> {
                    worldY += speed; // Update worldY to reflect the player's movement in the world
                    isMoving = true;
                    break;
                        
                 }
                case "left" -> {
                    worldX -= speed; // Update worldX to reflect the player's movement in the world
                    isMoving = true;    
                    break;
                }
                    case "right" -> {
                    worldX += speed; // Update worldX to reflect the player's movement in the world
                    isMoving = true;
                    break;
                }
            }
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
                    case "up" -> {
                        image = up1;
                    }
                    case "down" -> {
                        image = down1;
                    }
                    case "left" -> {
                        image = left1;
                    }
                    case "right" -> {
                        image = right1;
                    }
                }
            }
        int drawX = worldX - gp.player.worldX + gp.player.screenX;
        int drawY = worldY - gp.player.worldY + gp.player.screenY;
        g2.drawImage(image, drawX, drawY, gp.tileSize, gp.tileSize, null); // Draw the customer image at the current position with the specified tile size


    }
}



