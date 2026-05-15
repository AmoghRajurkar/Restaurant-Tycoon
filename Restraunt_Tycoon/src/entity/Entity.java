package entity;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

// Superclass for all entities in the game, such as players, customers, and servers. This class can be extended to create specific types of entities with their own unique behaviors and attributes.
public class Entity {
    public int worldX, worldY; // Position of the entity in the game world
    public int speed; // Speed at which any entity moves
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2 ; // Sprite images for the entity's movement in different directions (up, down, left, right) with two frames each for animation
    public BufferedImage upStill, downStill, leftStill, rightStill; // Still images for when the player is not moving
    public boolean isMoving; // Variable to track whether the entity is currently moving
    public String direction;
    public int SpriteCounter = 0; // Counter to track the animation state of the entity's sprite
    public int SpriteNum = 1; // Variable to track which sprite image to use for animation (e.g., 1 or 2 for a simple two-frame animation)
    public Rectangle solidArea; // Rectangle representing the solid area of the entity for collision detection
    public boolean collisionOn = false; // Flag to indicate whether a collision is currently occurring for the entity
}