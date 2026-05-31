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
    public final int stallTileSize = tileSize * 4;

    // 20 x 15 tile screen
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 15;

    public final int screenWidth = tileSize * maxScreenCol; // 1280 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 960 pixels

    int boostBarWidth = 220;
    int boostBarHeight = 28;
    int boostBarMargin = 20;

    int FPS = 60;

    // Customer array
    public Customer[] customers;
    public int customersIndex = 0;
    private long lastCustomerSpawnTime = System.currentTimeMillis();
    private final long customerSpawnInterval = 15000; // 15 seconds in milliseconds
    private final int maxCustomers = 8; // Set a reasonable max

    // World settings
    public final int maxWorldCol = 60;
    public final int maxWorldRow = 45;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    public String gameState = "WORLD";
    public final String WORLD_STATE = "WORLD";
    public final String STALL_STATE = "STALL";
    public final String RESTOCK_STATE = "RESTOCK";
    public int Current_level = 3;

    // Tracks which stall the player is currently inside
    public String currentStallType = "";

    // One-shot Used flags so held keys don't repeat actions
    private boolean toggleUsed = false;
    private boolean InventoryUsed = false;
    private boolean infoUsed = false;
    private boolean fulfillUsed = false;
    private boolean enterUsed = false;
    private boolean backspaceUsed = false;
    private boolean upUsed = false;
    private boolean downUsed = false;
    public boolean UpgradeCookUsed = false;
    private int lastDigitUsed = -1;

    Thread gameThread; // Thread to run the game loop
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public Player player = new Player(this, keyH);
    public OrderBoard orderBoard = new OrderBoard(this);
    public Inventory inventory = new Inventory();
    public RestockPanel restockPanel = new RestockPanel(inventory);
    public InventoryPanel inventoryPanel = new InventoryPanel(inventory);
    public InformationPanel informationPanel = new InformationPanel();
    public Messages messages;
    public boolean level3RestockZone = false;

    public Gamepanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(new Color(62, 194, 83));
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        this.messages = new Messages("");

        if (Current_level == 1) {
            // Initialize customers
            customers = new Customer[maxCustomers]; // Initialize the customers array with a maximum size
            spawnCustomer();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        player.update();
        messages.update();

        if (Current_level == 3) {
            Rectangle zone = new Rectangle(33 * tileSize, 17 * tileSize, 4 * tileSize, 4 * tileSize);
            Rectangle playerRect = new Rectangle(player.worldX, player.worldY, tileSize, tileSize);
            level3RestockZone = zone.intersects(playerRect);
            restockPanel.visible = level3RestockZone;
        }

        updateInventoryPanel();
        updateInformationPanel();
        if (gameState.equals(STALL_STATE)) {
            if (currentStallType.equals("Green")) {
                updateRestockPanel();
            } else {
                updateOrderBoard();
            }
        }

        if (Current_level == 3 && level3RestockZone) {
            updateRestockPanel();
        } else if (Current_level == 1 && gameState.equals(WORLD_STATE)) {
            // Update all customers
            for (int i = 0; i < customers.length; i++) {
                Customer customer = customers[i];
                if (customer == null) {
                    continue;
                }
                if (!customer.isServed) {
                    customer.InPath();
                }
                if (customer.isServed) {
                    customer.outPath();
                    if (customer.leftMap) {
                        customers[i] = null; // Remove customer from array once they have left the map
                        customersIndex--;
                        continue;
                    }
                }
                // Always check stall contact each frame even if the customer isn't moving
                cChecker.customerCheckTile(customer);

            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCustomerSpawnTime >= customerSpawnInterval) {
                spawnCustomer(); // Spawn a new customer if the spawn interval has passed
                lastCustomerSpawnTime = currentTime;
            }
        }
    }

    private void spawnCustomer() {
        if (customersIndex >= customers.length) {
            return;
        }

        int x = tileSize + tileSize * 20;
        int y = tileSize + tileSize * 43;
        for (int i = 0; i < customers.length; i++) {
            if (customers[i] == null) {
                customers[i] = new Customer(this, x, y);
                customersIndex++;
                break;
            }
        }
    }

    public int countCustomersOutsideStall(String stallType) {
        int count = 0;
        for (int i = 0; i < customersIndex; i++) {
            Customer customer = customers[i];
            if (customer == null) {
                continue;
            }
            String contact = cChecker.getCustomerContactStall(customer);
            if (stallType.equals(contact)) {
                count++;
            }
        }
        return count;
    }

    public Customer getFirstWaitingCustomer(String stallType) {
        for (Customer customer : customers) {
            if (customer == null) {
                continue;
            }
            if (!customer.isServed && cChecker.getCustomerContactStall(customer).equals(stallType)) {
                return customer;
            }
        }
        return null;
    }

    private void updateOrderBoard() {
        // toggle order board visibility
        if (keyH.toggleOrdersPressed && !toggleUsed) {
            orderBoard.visible = !orderBoard.visible;
            toggleUsed = true;
        }
        if (!keyH.toggleOrdersPressed) {
            toggleUsed = false;
        }

        // fulfill next item on customer 1
        if (keyH.fulfillPressed && !fulfillUsed) {
            orderBoard.fulfillFirst();
            fulfillUsed = true;
        }
        if (!keyH.fulfillPressed) {
            fulfillUsed = false;
        }
    }

    @SuppressWarnings("static-access")
    private void updateRestockPanel() {
        // toggle restock panel visibility
        if (keyH.toggleOrdersPressed && !toggleUsed) {
            restockPanel.visible = !restockPanel.visible;
            toggleUsed = true;
        }
        if (!keyH.toggleOrdersPressed) {
            toggleUsed = false;
        }

        if (!restockPanel.visible) {
            return;
        }

        // Keep KeyHandler typingMode in sync with the panel
        keyH.typingMode = restockPanel.typingMode;

        // Arrow keys navigate the list, WASD moves the player
        if (!restockPanel.typingMode) {
            if (keyH.upArrow && !upUsed) {
                restockPanel.moveSelection(-1);
                upUsed = true;
            }
            if (!keyH.upArrow) {
                upUsed = false;
            }

            if (keyH.downArrow && !downUsed) {
                restockPanel.moveSelection(1);
                downUsed = true;
            }
            if (!keyH.downArrow) {
                downUsed = false;
            }
        }

        // Enter — either enter typing mode or confirm transfer
        if (keyH.enterPressed && !enterUsed) {
            if (!restockPanel.typingMode) {
                restockPanel.selectCurrentItem();
                keyH.typingMode = true;
            } else {
                restockPanel.confirmTransfer();
                inventory.takeMoneyFromPlayer(.5 * inventory.moneyGiven);
                keyH.typingMode = false;
            }
            enterUsed = true;
        }
        if (!keyH.enterPressed) {
            enterUsed = false;
        }

        // Number keys — append digit to typed quantity
        if (restockPanel.typingMode && keyH.lastDigit != -1 && keyH.lastDigit != lastDigitUsed) {
            restockPanel.appendDigit((char) ('0' + keyH.lastDigit));
            lastDigitUsed = keyH.lastDigit;
        }
        if (keyH.lastDigit == -1) {
            lastDigitUsed = -1;
        }

        // Backspace — delete last typed digit
        if (keyH.backspacePressed && !backspaceUsed) {
            restockPanel.deleteDigit();
            backspaceUsed = true;
        }
        if (!keyH.backspacePressed) {
            backspaceUsed = false;
        }
    }

    private void updateInventoryPanel() {
        // toggle inventory panel visibility
        if (keyH.toggleInventoryPressed && !InventoryUsed) {
            inventoryPanel.toggle();
            InventoryUsed = true;
        }
        if (!keyH.toggleInventoryPressed) {
            InventoryUsed = false;
        }

        // Handle arrow key navigation when inventory is visible
        if (inventoryPanel.visible) {
            if (keyH.upArrow && !upUsed) {
                inventoryPanel.moveSelection(-1);
                upUsed = true;
            }
            if (!keyH.upArrow) {
                upUsed = false;
            }

            if (keyH.downArrow && !downUsed) {
                inventoryPanel.moveSelection(1);
                downUsed = true;
            }
            if (!keyH.downArrow) {
                downUsed = false;
            }
        }
    }

    private void updateInformationPanel() {
        if (keyH.toggleInfoPressed && !infoUsed) {
            informationPanel.toggle();
            infoUsed = true;
        }
        if (!keyH.toggleInfoPressed) {
            infoUsed = false;
        }
    }

    private void drawBoostBar(Graphics2D g2) {
        int x = screenWidth - boostBarWidth - boostBarMargin;
        int y = boostBarMargin;
        float ratio = player.getBoostChargeRatio();
        int innerWidth = boostBarWidth - 4;
        int fillWidth = Math.max(0, Math.min(innerWidth, (int) (innerWidth * ratio)));

        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(x, y, boostBarWidth, boostBarHeight, 14, 14);

        g2.setColor(new Color(30, 30, 30));
        g2.fillRoundRect(x + 2, y + 2, innerWidth, boostBarHeight - 4, 12, 12);

        g2.setColor(new Color(0, 120, 255));
        g2.fillRoundRect(x + 2, y + 2, fillWidth, boostBarHeight - 4, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, boostBarWidth, boostBarHeight, 14, 14);

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String label = "SHIFT = SPRINT";
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (boostBarWidth - fm.stringWidth(label)) / 2;
        int textY = y + ((boostBarHeight - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(label, textX, textY);
    }

    private void drawCookBar(Graphics2D g2) {
        int x = screenWidth - boostBarWidth - boostBarMargin;
        int y = boostBarMargin + boostBarHeight + 8;
        float ratio = 0f;
        if (Cook.cooking && Cook.currentCookTime > 0) {
            ratio = Math.min(1f, (System.currentTimeMillis() - Cook.cookingStartTime) / (float) Cook.currentCookTime);
        }
        int innerWidth = boostBarWidth - 4;
        int fillWidth = Math.max(0, Math.min(innerWidth, (int) (innerWidth * ratio)));

        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(x, y, boostBarWidth, boostBarHeight, 14, 14);

        g2.setColor(new Color(30, 30, 30));
        g2.fillRoundRect(x + 2, y + 2, innerWidth, boostBarHeight - 4, 12, 12);

        g2.setColor(new Color(255, 200, 0));
        g2.fillRoundRect(x + 2, y + 2, fillWidth, boostBarHeight - 4, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, boostBarWidth, boostBarHeight, 14, 14);

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String label = "COOKING";
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (boostBarWidth - fm.stringWidth(label)) / 2;
        int textY = y + ((boostBarHeight - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(label, textX, textY);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState.equals(WORLD_STATE)) {
            tileM.draw(g2);
            // Draw all customers
            for (int i = 0; i < customersIndex; i++) {
                if (customers[i] != null) {
                    customers[i].draw(g2);
                }
            }
        } else if (gameState.equals(STALL_STATE)) {
            tileM.drawInterior(g2);
        }

        if (Current_level == 3 && gameState.equals(WORLD_STATE)) {
            g2.setColor(new Color(139, 69, 19));
            int sx = 33 * tileSize - player.worldX + player.screenX;
            int sy = 17 * tileSize - player.worldY + player.screenY;
            g2.fillRect(sx, sy, 4 * tileSize, 4 * tileSize);
        }

        player.draw(g2);
        drawBoostBar(g2);
        drawCookBar(g2);

        messages.showMessage(g2);

        if (gameState.equals(STALL_STATE)) {
            if (currentStallType.equals("Green")) {
                restockPanel.draw(g2);
            } else {
                orderBoard.draw(g2);
            }
        }

        if (Current_level == 3 && level3RestockZone) {
            restockPanel.draw(g2);
        }

        // Draw inventory panel
        inventoryPanel.draw(g2);
        informationPanel.draw(g2);

        g2.dispose();
    }
}
