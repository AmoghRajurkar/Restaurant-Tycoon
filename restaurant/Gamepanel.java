package restaurant; 
import java.awt.*;
import javax.swing.*;

public class Gamepanel extends JPanel implements Runnable {
    // Screen tile settings
    final int orignalTileSize = 16;
    public final int tileSize = orignalTileSize * 4;

    // 18 x 14 tiles set up
    public final int maxScreenCol = 18;
    public final int maxScreenRow = 14;

    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    int FPS = 60; // Frames per second for the game loop

    KeyHandler keyH = new KeyHandler(); // Key handler for handling keyboard input
    Thread gameThread; // Thread to run the game loop

    //Set player position
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;

    // Constructor that allows us to set up the game panel
    public Gamepanel(){
        this.setPreferredSize(new Dimension(1200, 800)); // Set the preferred size of the panel
        this.setBackground(Color.white); // Set the background color of the panel to white
        this.setDoubleBuffered( true); // Improve rendering performance by enabling double buffering
        this.addKeyListener(keyH);
        this.setFocusable(true); // Make the panel focusable to receive keyboard input
    }

    public void startGameThread() {
        gameThread = new Thread(this); // Create a new thread for the game loop
        gameThread.start(); // Start the game thread
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS; // 0.1666666 seconds per frame
        double delta = 0; // Variable to track the time difference for frame updates
        long lastTime = System.nanoTime();
        long currentTime;

        // Game loop logic
        while (gameThread != null) {
            // Calculate the time difference and update the game state at the specified FPS
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            // If enough time has passed (delta >= 1), update the game state and repaint the screen
            if (delta >= 1) {
                update(); // Update game state
                repaint(); // Request a repaint to update the screen
                delta--;
            }
        }
    }

    public void update() {
        // Update game state logic
        // Player postion 
        if (keyH.upPressed == true) {
            playerY -= playerSpeed; // Move player up
        }
        if (keyH.downPressed == true) {
            playerY += playerSpeed; // Move player down
        }
        if (keyH.leftPressed == true) {
            playerX -= playerSpeed; // Move player left
        }
        if (keyH.rightPressed == true) {
            playerX += playerSpeed; // Move player right
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the superclass method to ensure proper painting
        Graphics2D g2 = (Graphics2D) g; // Cast Graphics to Graphics2D for better control over rendering
        // Draw game elements here using g2
        g2.setColor(Color.black); // Set the drawing color to black
        g2.fillRect(playerX, playerY, tileSize, tileSize); // Fill the background with black

        g2.dispose(); // Dispose of the graphics context to free up resources
    }
}