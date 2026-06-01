package entity;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import main.CollisionChecker;
import main.Gamepanel;
import main.KeyHandler;
import main.OrderList;

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
    public int roomX;
    public int roomY;
    public int cookLevel = 1;

    /**
     * Constructer for the Player class, which initializes the player's
     * position, speed, and loads the images for the player sprite. It also sets
     * up the rect for collision detection and initalizes the reference to
     * KeyHandler to check for key presses in the update method
     *
     * @param gp It is used to access states and methods in the GamePanel class,
     * like for checking collsion and chainging game states
     * @param keyH It is used to check for key presses in the update method, for
     * movement and interactions
     */
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

    /**
     * Sets the default values for the player's position, speed, and diretion.
     * It's called once in the construtor to initialize the player's state when
     * the game starts.
     */
    private void setDefaultValues() {
        worldX = gp.tileSize * 30; // Set the default X position of the player
        worldY = gp.tileSize * 22; // Set the default Y position of the player
        roomX = 400;
        roomY = 500;
        speed = 5; // Set the default speed of the player
        direction = "down"; // Set the default direction of the player
    }

    /**
     * Update the player's state based on key presses and collision detection.
     * It is called every frame in the main thread, and handles movement,
     * animation, interactions with stalls, and boost mechanics Warrning
     * surpresses static access because it needs to check the static variables
     * from GamePanel
     */
    @SuppressWarnings("static-access")
    public void update() {
        // Update game state logic
        isMoving = false;

        // Ensure we always update world collision/contact each frame so the
        // per-frame contactStall and contactTruck variables are correct even
        // when the player is standing still
        if (gp.gameState.equals(gp.WORLD_STATE)) {
            gp.cChecker.checkTile(this);
        }

        // Check for interaction key press to enter stall or truck, only if we're in the world and standing next to one
        if (keyH.interactPressed && gp.gameState.equals(gp.WORLD_STATE)) {
            // Use the instantaneous contact variables (reset each frame) to
            // avoid opening a room based on stale `lastContact*` values.
            if (gp.Current_level == 1 && !CollisionChecker.contactStall.equals("")) {
                enterRoom("stall");
                keyH.interactPressed = false;
            } else if (gp.Current_level >= 2 && !CollisionChecker.contactTruck.equals("")) {
                // Treat level 2+ as truck levels (includes level 3 fallback)
                enterRoom("truck");
                keyH.interactPressed = false;
            }
        }

        // Upgrade cook level
        if (gp.keyH.UpgradeCookPressed && !gp.UpgradeCookUsed) {
            if (!canUpgradeCook()) {
                gp.messages.showMessageForDuration("Not enough money to upgrade cook level!");
                return;
            }
            UpgradeCookLevel();
            gp.messages.showMessageForDuration("Cook level upgraded to " + cookLevel + ", -$" + (100 * cookLevel));
            gp.inventory.takeMoneyFromPlayer(100 * cookLevel); // Pay for the upgrade
            gp.UpgradeCookUsed = true;
        }
        if (!gp.keyH.UpgradeCookPressed) {
            gp.UpgradeCookUsed = false;
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
            if (gp.gameState.equals(gp.WORLD_STATE)) {
                gp.cChecker.checkTile(this);
            }

            // Check world boundary and stop the player when the edge of the map would come into view
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

            // If collision is false, player can move
            if (collisionOn == false) {
                if (gp.gameState.equals(gp.WORLD_STATE)) {
                    switch (direction) {
                        case "up" ->
                            worldY -= speed;
                        case "down" ->
                            worldY += speed;
                        case "left" ->
                            worldX -= speed;
                        case "right" ->
                            worldX += speed;
                    }
                } else if (gp.gameState.equals(gp.STALL_STATE)) {
                    if (!gp.cChecker.checkStallTile(roomX, roomY, direction, speed)) {
                        switch (direction) {
                            case "up" ->
                                roomY -= speed;
                            case "down" ->
                                roomY += speed;
                            case "left" ->
                                roomX -= speed;
                            case "right" ->
                                roomX += speed;
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

        if (gp.gameState.equals(gp.STALL_STATE)) {
            // Exit zone spans the full height of the door (7 tiles tall)
            Rectangle exitZone = new Rectangle(
                    gp.tileM.doorX,
                    gp.tileM.doorY,
                    gp.tileSize,
                    gp.tileSize * 7
            );
            Rectangle playerBox = new Rectangle(
                    roomX,
                    roomY,
                    gp.tileSize,
                    gp.tileSize
            );

            if (playerBox.intersects(exitZone)) {
                exitStall();
            }
        }
    }

    /**
     * Returns the ratio of boost charge for the boost meter display. If the
     * boost is active or reloading is not active, it gets between a 1.0 and
     * 0.0, used to draw the boost meter.
     *
     * @return a float between 0.0 and 1.0 representing the boost ratio
     */
    public float getBoostChargeRatio() {
        if (boostActive || !boostReloading) {
            return 1.0f;
        }
        return 1.0f - (float) reloadTimer / maxBoostReload;
    }

    /**
     * Checks if boost is active, if its active, we can't use it again till it
     * reloads, and it can be used if its not active and not actively reloading.
     *
     * @return true if boost can be used, false otherwise
     */
    public boolean isBoostReady() {
        return !boostActive && !boostReloading;
    }

    /**
     * Handles the logic for entering a room (stall or truck), including
     * changing the game state, loading the interior, and stall-specific setup
     * like orders.
     */
    private void enterRoom(String roomType) {
        gp.gameState = gp.STALL_STATE;
        gp.tileM.loadInterior();

        if ("stall".equals(roomType)) {
            gp.currentStallType = CollisionChecker.lastContactStall;
            gp.orderBoard.loadForStall(CollisionChecker.lastContactStall);
            int waitingCustomers = gp.countCustomersOutsideStall(CollisionChecker.lastContactStall);
            if (waitingCustomers > 0) {
                for (int i = 0; i < waitingCustomers; i++) {
                    gp.orderBoard.customers.add(new OrderList(1, CollisionChecker.lastContactStall));
                }
            }
        } else if ("truck".equals(roomType)) {
            gp.currentStallType = CollisionChecker.lastContactTruck;
            gp.orderBoard.loadForStall(CollisionChecker.lastContactTruck);
        }

        roomX = gp.screenWidth / 2 - gp.tileSize / 2;
        roomY = gp.screenHeight - 350;
        gp.repaint();
    }

    /**
     * Handles the logic for entering a stall, including changing the game
     * state, loading the stall interior, loading the orders, etc.
     */
    public void enterStall() {
        enterRoom("stall");
    }

    /**
     * Handles the logic for exiting a room (stall or truck), including changing
     * the game state and resetting contact tracking.
     */
    public void exitRoom() {
        gp.gameState = gp.WORLD_STATE;
        gp.restockPanel.typingMode = false;
        gp.keyH.typingMode = false;
        // Clear contact tracking so the interact key cannot re-open a room
        CollisionChecker.lastContactStall = "";
        CollisionChecker.lastContactTruck = "";
        CollisionChecker.contactStall = "";
        CollisionChecker.contactTruck = "";
        // Clear the currently loaded interior map to avoid stale state
        gp.tileM.clearCurrentInterior();
        gp.currentStallType = "";
        gp.repaint();
    }

    /**
     * Handles the logic for exiting a stall, including changing the game state,
     * resetting the world again, etc
     */
    public void exitStall() {
        exitRoom();
    }

    /**
     * Draws the player on the screen based on the current direction and
     * animation state. It also uses the world state to see if it should draw
     * the world or another stall.
     *
     * @param g2 the Graphics2D object is used to draw everything on the screen.
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

        // Move the player or move the world depending on the game state
        if (gp.gameState.equals(gp.WORLD_STATE)) {
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        } else {
            g2.drawImage(image, roomX, roomY, gp.tileSize, gp.tileSize, null);
        }
    }

    /**
     * Upgrade the player's cook level, which increases the speed of cooking.
     */
    public void UpgradeCookLevel() {
        cookLevel++;
    }

    /**
     * Checks if the player has enough money to upgrade cooking. Surpress the
     * static access warning to check the static playerMoney variable in
     * Inventory
     */
    @SuppressWarnings("static-access")
    private boolean canUpgradeCook() {
        // Check if player has enough money to upgrade cook level
        return gp.inventory.playerMoney >= 100 * (cookLevel + 1);
    }

    /**
     * Loads the player movement animation images from res folder.
     */
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
        } catch (IOException e) {
            System.out.println("Error loading player images");
        }
    }
}
