package entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import main.Gamepanel;

// Superclass for all entities in the game
public class Entity {

    Gamepanel gp;
    public int worldX, worldY;
    public int speed;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2; // Sprite images for the entity's movement
    public BufferedImage upStill, downStill, leftStill, rightStill; // Still images for when the player is not moving
    public boolean isMoving;
    public String direction;
    public int SpriteCounter = 0; // Counter to track the animation state of the entity's sprite
    public int SpriteNum = 1; // Variable to track which sprite image to use for animation
    public Rectangle solidArea; // Rectangle representing the solid area of the entity for collision detection
    public boolean collisionOn = false; // Flag to indicate whether a collision is currently occurring for the entity
    /**
     * Constructor for the Entity class, initializes the entity's position and direction.
     * @param gp The Gamepanel instance for accessing game properties and methods.
     */
    public Entity(Gamepanel gp) {
        this.gp = gp;
    }
}
