package main;
import entity.Customer;
import entity.Player;
import java.awt.*;
import javax.swing.*;
import main.tile.TileManager;

public class Gamepanel extends JPanel implements Runnable {
    // Screen tile settings
    final int orignalTileSize = 16;
    public final int tileSize = orignalTileSize * 4;
    public final int stallTileSize = tileSize * 4; // Each stall block is 4 tiles wide = 256 pixels

    // 20 x 15 tiles set up
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 15;

    public final int screenWidth = tileSize * maxScreenCol; // 960 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 960 pixels

    int boostBarWidth = 220;
    int boostBarHeight = 28;
    int boostBarMargin = 20;

    int FPS = 60; // Frames per second for the game loop

    public TileManager tileM = new TileManager(this); // Create an instance of the TileManager class to manage tile images and properties
    KeyHandler keyH = new KeyHandler(); // Key handler for handling keyboard input
    Thread gameThread; // Thread to run the game loop
    public CollisionChecker cChecker = new CollisionChecker(this); // Create an instance of the CollisionChecker class to handle collision detection
    public Player player = new Player(this, keyH); // Create a player instance and pass the Gamepanel and KeyHandler references
    
    // Customer array
    public Customer[] customers;
    public int customersIndex = 0;
    private long lastCustomerSpawnTime = System.currentTimeMillis();
    private final long customerSpawnInterval = 15000; // 15 seconds in milliseconds
    private final int maxCustomers = 8; // Set a reasonable max


    // World settings
    public final int maxWorldCol = 60;
    public final int maxWorldRow = 45;
    public final int worldWidth = tileSize * maxWorldCol; // Total width of the game world in pixels
    public final int worldHeight = tileSize * maxWorldRow; // Total height of the game world in pixels

    public String gameState = "WORLD";
    public final String WORLD_STATE = "WORLD";
    public final String STALL_STATE = "STALL";

    // Constructor that allows us to set up the game panel
    public Gamepanel(){
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Set the preferred size of the panel
        this.setBackground(new Color(62, 194, 83)); // Set the background color of the panel to white
        this.setDoubleBuffered( true); // Improve rendering performance by enabling double buffering
        this.addKeyListener(keyH);
        this.setFocusable(true); // Make the panel focusable to receive keyboard input
        
        // Initialize customers
        customers = new Customer[maxCustomers]; // Initialize the customers array with a maximum size
        spawnCustomer();
    }
   
    private void spawnCustomer() {
        if(customersIndex < customers.length){
            int x = tileSize + tileSize * 20;
            int y =  tileSize + tileSize * 34;
            customers[customersIndex] = new Customer(this, x, y);
            customersIndex++; 
        }
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
        player.update(); // Update the player's state, including movement and boost logic
        
        // Update all customers
        for (int i = 0; i < customersIndex; i++) {
            customers[i].path();
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCustomerSpawnTime >= customerSpawnInterval) {
            spawnCustomer(); // Spawn a new customer if the spawn interval has passed
            lastCustomerSpawnTime = currentTime;
        }
        tileM.loadStallInsides();
    }

    private void drawBoostBar(Graphics2D g2) {
        int x = screenWidth - boostBarWidth - boostBarMargin;
        int y = boostBarMargin;
        float ratio = player.getBoostChargeRatio();
        int innerWidth = boostBarWidth - 4;
        int fillWidth = Math.max(0, Math.min(innerWidth, (int) (innerWidth * ratio)));

        // Background container
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(x, y, boostBarWidth, boostBarHeight, 14, 14);

        // Empty bar
        g2.setColor(new Color(30, 30, 30));
        g2.fillRoundRect(x + 2, y + 2, innerWidth, boostBarHeight - 4, 12, 12);

        // Filled boost bar
        g2.setColor(new Color(0, 120, 255));
        g2.fillRoundRect(x + 2, y + 2, fillWidth, boostBarHeight - 4, 12, 12);

        // Border
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, boostBarWidth, boostBarHeight, 14, 14);

        // Label
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String label = "SHIFT = SPRINT";
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (boostBarWidth - fm.stringWidth(label)) / 2;
        int textY = y + ((boostBarHeight - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(label, textX, textY);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the superclass method to ensure proper painting
        Graphics2D g2 = (Graphics2D) g; // Cast Graphics to Graphics2D for better control over rendering
       
        if(gameState.equals(WORLD_STATE)) {
            tileM.draw(g2);
            // Draw all customers
            for (int i = 0; i < customersIndex; i++) {
                customers[i].draw(g2);
            }
        } else if(gameState.equals(STALL_STATE)) {
            tileM.drawStallInterior(g2);
        }

        player.draw(g2); // Draw the player on the screen
        drawBoostBar(g2); // Draw the boost recharge bar in the top-right corner
        g2.dispose(); // Dispose of the graphics context to free up resources
    }
}