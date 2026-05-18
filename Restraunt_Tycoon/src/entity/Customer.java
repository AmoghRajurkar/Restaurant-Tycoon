package entity;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import main.Gamepanel;

public class Customer extends Entity {
    public  int screenX; // X position of the customer on the screen, which can be used for rendering the customer
    public  int screenY; // Y position of the customer on the screen, which can be used for rendering the customer
    public int stall1X = 870;
    public int stall1Y = 780;
    public int stall2X = 2140;
    public int stall2Y = 1030;
    public Random rand = new Random();
    public int path = rand.nextInt(2)+1; // Randomly choose a path for the customer to take (1 or 2)

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


    public void path(){
        
        if (path==1){
            if (worldY>stall1Y){
                direction = "up";
                update(); // Update the customer's position and behavior based on the chosen path
            }
            else if (worldX>stall1X){
                direction = "left";
                update(); // Update the customer's position and behavior based on the chosen path
    
            
            }
        direction = "up";
        }
        if (path==2){
            if (worldY>1300){
                direction = "up";
                update(); // Update the customer's position and behavior based on the chosen path
            }
            else if (worldX<stall2X){
                direction = "right";
                update(); // Update the customer's position and behavior based on the chosen path
            }
            else if (worldY>stall2Y){
                direction = "up";
                update(); // Update the customer's position and behavior based on the chosen path
            }
        }
    }


    public void update() {
        // Logic to update the customer's position and behavior goes here
        isMoving = false;
        

        // Check for collisions with tiles
        collisionOn = false; // Reset collision flag before checking for collisions
        gp.cChecker.checkTile(this); // Check for collisions with tiles
        gp.cChecker.checkEntityCollision(this, gp.customers); // Check for collisions with other customers

        // Check world boundary — stop the player when the edge of the map would come into view
        if (direction.equals("up") && worldY <= 0) {
            collisionOn = true;      
        }
        if (direction.equals("down") && worldY + gp.tileSize >= gp.worldHeight) {
            collisionOn = true;
        }
        if (direction.equals("left") && worldX  <= 0) {
            collisionOn = true;
        }
        if (direction.equals("right") && worldX  + gp.tileSize >= gp.worldWidth) {
            collisionOn = true;
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



