package main;

import entity.Car;
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
    private long customerSpawnInterval = 15000; // 15 seconds in milliseconds
    private int maxCustomers; // Changes up based on level

    // Car array
    public Car[] cars;
    public int carsIndex = 0;
    private long lastCarSpawnTime = System.currentTimeMillis();
    private final long carSpawnInterval = 30000; // Spawn a car every 30 seconds in level 3
    private final int maxCars = 4; // Max cars in level 4

    // World settings
    public final int maxWorldCol = 60;
    public final int maxWorldRow = 45;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    public String gameState = "WORLD";
    public final String WORLD_STATE = "WORLD";
    public final String STALL_STATE = "STALL";
    public int Current_level = 1;

    // Tracks which stall the player is currently inside
    public String currentStallType = "";
    // No need for turck, there is only 1 type for cooking

    // One-shot used flags so held keys don't repeat actions
    private boolean toggleUsed = false;
    private boolean InventoryUsed = false;
    private boolean infoUsed = false;
    private boolean fulfillUsed = false;
    private boolean enterUsed = false;
    private boolean backspaceUsed = false;
    private boolean upUsed = false;
    private boolean downUsed = false;
    public boolean UpgradeCookUsed = false;
    public boolean SpaceUsed = false;
    private int lastDigitUsed = -1;

    Thread gameThread; // Thread to run the game loop
    // Instances of other classes
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
    /**
     * Constructor for the Gamepanel class, initializes the game panel and its components.
     */
    public Gamepanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(new Color(62, 194, 83));
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        this.messages = new Messages("");

        // Changes some part based on level
        LevelChanges();
        if (Current_level == 1) {
            customers = new Customer[maxCustomers];
            spawnCustomer();
        }
    }
    /**
     * Changes the game settings based on the current level such as the maximum number of customers and the customer spawn interval. 
     * This method is called when the player advances to a new level to adjust the game's difficulty and pacing accordingly.
     */
    private void LevelChanges() {
        switch (Current_level) {
            case 1 -> {
                maxCustomers = 8;
                customerSpawnInterval = 15000;
            }
            case 2 -> {
                maxCustomers = 12;
                customerSpawnInterval = 12500;
            }
            case 3 -> {
                maxCustomers = 15;
                customerSpawnInterval = 10000;
            }
            default -> {
                maxCustomers = 8;
                customerSpawnInterval = 15000;
            }
        }
    }
    /**
     * Starts the game thread.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    /**
     * The main game loop that updates the game state and repaints the screen.
     */
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
    
    /**
     * Updates the game state including the player, customers, inventory panel, information panel, level progression, and order board. 
     * It also handles customer spawning and car spawning for level 3, as well as checking for player interactions with the restock zone and updating the restock panel accordingly.
     */
    public void update() {
        player.update();
        messages.update();
        updateInventoryPanel();
        updateInformationPanel();
        updateLevel();
        updateOrderBoard();

        if (Current_level == 3) {
            Rectangle zone = new Rectangle(33 * tileSize, 17 * tileSize, 4 * tileSize, 4 * tileSize);
            Rectangle playerRect = new Rectangle(player.worldX, player.worldY, tileSize, tileSize);
            level3RestockZone = zone.intersects(playerRect);
            restockPanel.visible = level3RestockZone;
        }

        if (gameState.equals(STALL_STATE)) {
            if (currentStallType.equals("Green")) {
                updateRestockPanel();
            }
        }

        if (Current_level == 3 && level3RestockZone) {
            updateRestockPanel();
        }

        if (gameState.equals(WORLD_STATE)) {
            updateCustomers();

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCustomerSpawnTime >= customerSpawnInterval) {
                spawnCustomer(); // Spawn a new customer if the spawn interval has passed
                lastCustomerSpawnTime = currentTime;
            }

            // Update cars in level 3
            if (Current_level == 3) {
                updateCars();
                if (currentTime - lastCarSpawnTime >= carSpawnInterval) {
                    spawnCar();
                    lastCarSpawnTime = currentTime;
                }
            }
        }
    }
    /**
     * Updates the customer entities in the game by iterating through the customers array and checking their states.
     */
    private void updateCustomers() {
        if (customers == null) {
            return;
        }

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
                    customers[i] = null;
                    customersIndex--;
                    continue;
                }
            }
            // Check stall contact each frame even if the customer isn't moving
            cChecker.customerCheckTile(customer);

            // ONly level 3, detect customers standing on the special 3x3 rect and place a level-3 order for them once.
            if (Current_level == 3) {
                int rectX = 40 * tileSize;
                int rectY = 25 * tileSize;
                int rectW = 3 * tileSize;
                int rectH = 3 * tileSize;

                Rectangle rect = new Rectangle(rectX, rectY, rectW, rectH);
                Rectangle customerArea = new Rectangle(
                        customer.worldX + customer.solidArea.x,
                        customer.worldY + customer.solidArea.y,
                        customer.solidArea.width,
                        customer.solidArea.height
                );

                if (rect.intersects(customerArea) && !customer.place_order && !customer.isServed) {
                    OrderList order = new OrderList(3, "Red");
                    customer.order = order;
                    orderBoard.customers.add(order);
                    orderBoard.visible = true;
                    customer.place_order = true;
                }
            }
        }
    }
    /**
     * Retrieves the customer associated with a specific order.
     * @param order The order for which to find the customer.
     * @return The customer associated with the order, or null if not found.
     */
    public 
    public Customer getCustomerForOrder(OrderList order) {
        for (Customer customer : customers) {
            if (customer != null && customer.order == order) {
                return customer;
            }
        }
        return null;
    }
    /**
     * Retrieves the car associated with a specific order.
     * @param order The order for which to find the car.
     * @return The car associated with the order, or null if not found.
     */
    public Car getCarForOrder(OrderList order) {
        if (cars == null) {
            return null;
        }
        for (Car car : cars) {
            if (car != null && car.order == order) {
                return car;
            }
        }

        return null;
    }
    /**
     * Spawns a new customer in the game. The spawn location is determined based on the current level, and the method ensures that the number of
     * customers does not exceed the maximum allowed for the level.
     */
    private void spawnCustomer() {
        if (customersIndex >= maxCustomers) {
            return;
        }
        ensureCustomersArray();

        int[] spawn = switch (Current_level) {
            case 2 ->
                spawnCustomerLevel2();
            case 3 ->
                spawnCustomerLevel3();
            default ->
                spawnCustomerLevel1();
        };

        int x = spawn[0];
        int y = spawn[1];
        for (int i = 0; i < customers.length; i++) {
            if (customers[i] == null) {
                customers[i] = new Customer(this, x, y);
                customersIndex++;
                break;
            }
        }
    }
    /**
     * Ensures that the customers array is initialized.
     */
    private void ensureCustomersArray() {
        if (customers == null) {
            customers = new Customer[maxCustomers];
            customersIndex = 0;
        }
    }
    /**
     * Spawns a new car in the game. The spawn location is determined based on the level 3 design, and the method ensures 
     * that the number of cars does not exceed the maximum allowed for the level.
     */
    private void spawnCar() {
        if (carsIndex >= maxCars || cars == null) {
            return;
        }
        ensureCarsArray();

        // Cars spawn off-screen to the bottom-left and drive to the rect
        for (int i = 0; i < cars.length; i++) {
            if (cars[i] == null) {
                cars[i] = new Car(this, tileSize * 55, tileSize * 36);
                carsIndex++;
                break;
            }
        }
    }
    /**
     * Updates the car entities in the game. The method iterates through the cars array and checks their 
     * states to determine if they are following the in path or out path behavior.
     */
    private void updateCars() {
        if (cars == null) {
            return;
        }

        for (int i = 0; i < cars.length; i++) {
            Car car = cars[i];
            if (car == null) {
                continue;
            }
            if (!car.isServed) {
                car.InPath();
            }
            if (car.isServed) {
                car.outPath();
                if (car.leftMap) {
                    cars[i] = null;
                    carsIndex--;
                    continue;
                }
            }

            // Place the order once the car arrives on the 4x4 zone.
            int rectX = tileSize * 35;
            int rectY = tileSize * 37;
            int rectH = tileSize * 4;
            int rectW = tileSize * 4;
            Rectangle rect = new Rectangle(rectX, rectY, rectW, rectH);
            Rectangle carArea = new Rectangle(
                    car.worldX + car.solidArea.x,
                    car.worldY + car.solidArea.y,
                    car.solidArea.width,
                    car.solidArea.height
            );
            if (rect.intersects(carArea) && !car.place_order && !car.isServed) {
                OrderList order = new OrderList(3, "Red");
                car.order = order;
                orderBoard.cars.add(order);
                orderBoard.visible = true;
                car.place_order = true;
            }
        }
    }
    /**
     * Ensures that the cars array is initialized.
     */
    private void ensureCarsArray() {
        if (cars == null) {
            cars = new Car[maxCars];
            carsIndex = 0;
        }
    }
    /**
     * Returns the spawn location for customers in level 1.
     * @return An array containing the x and y coordinates for the spawn location.
     */
    private int[] spawnCustomerLevel1() {
        return new int[]{tileSize + tileSize * 20, tileSize + tileSize * 43};
    }
    /**
     * Returns the spawn location for customers in level 2.
     * @return An array containing the x and y coordinates for the spawn location.
     */
    private int[] spawnCustomerLevel2() {
        return new int[]{tileSize + tileSize * 6, tileSize + tileSize * 36};
    }
    /**
     * Returns the spawn location for customers in level 3.
     * @return An array containing the x and y coordinates for the spawn location.
     */
    private int[] spawnCustomerLevel3() {
        return new int[]{tileSize + tileSize * 1, tileSize + tileSize * 40};
    }
    /**
     * Counts the number of customers outside a specific stall.
     * @param stallType The type of stall to check.
     * @return The number of customers outside the specified stall.
     */
    public int countCustomersOutsideStall(String stallType) {
        // Checks if customer is in contact with stall and counts them
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
    /**
     * Counts the number of customers outside a specific truck.
     * @param truckType The type of truck to check.
     * @return The number of customers outside the specified truck.
     */
    public int countCustomerOutsideTruck(String truckType) {
        // Checks if customer is in contact with stall and counts them
        int count = 0;
        for (int i = 0; i < customersIndex; i++) {
            Customer customer = customers[i];
            if (customer == null) {
                continue;
            }
            if (truckType.equals(cChecker.getCustomerContactTruck(customer))) {
                count++;
            }
        }
        return count;
    }
    /**
     * Returns the first waiting customer at a specific stall.
     * @param stallType The type of stall to check.
     * @return The first waiting customer at the specified stall, or null if none found.
     */
    public Customer getFirstWaitingCustomerStall(String stallType) {
        if (stallType == null) {
            return null;
        }
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
    /**
     * Returns the first waiting customer at a specific truck.
     * @param truckType The type of truck to check.
     * @return The first waiting customer at the specified truck, or null if none found.
     */
    public Customer getFirstWaitingCustomerTruck(String truckType) {
        if (truckType == null) {
            return null;
        }
        for (Customer customer : customers) {
            if (customer == null) {
                continue;
            }
            if (!customer.isServed && cChecker.getCustomerContactTruck(customer).equals(truckType)) {
                return customer;
            }
        }
        return null;
    }
    /**
     * Returns the first waiting car at a specific truck.
     * @param truckType The type of truck to check.
     * @return The first waiting car at the specified truck, or null if none found.
     */
    public Car getFirstWaitingCar() {
        if (cars == null) {
            return null;
        }

        for (Car car : cars) {
            if (car == null) {
                continue;
            }
            if (!car.isServed) {
                return car;
            }
        }
        return null;
    }
    /**
     * Updates the level-specific settings and entities. This method is called when the player advances to a new level to 
     * adjust the game's difficulty and pacing accordingly. It also resets the player's position and state, and clears or initializes
     * entities such as customers and cars based on the new level's requirements.
     */
    private void updateLevel() {
        if (keyH.SpacePressed && !SpaceUsed) {
            if (Current_level == 1 && gameState.equals(WORLD_STATE) && Inventory.playerMoney >= 1000) {
                Current_level = 2;
                LevelChanges();
                tileM.reloadLevelMap();
                // Reset player position and state for level 2
                player.worldX = tileSize * 15;
                player.worldY = tileSize * 22;
                gameState = WORLD_STATE;
                // Reset customers for level 2
                customers = new Customer[maxCustomers];
                customersIndex = 0;
                lastCustomerSpawnTime = System.currentTimeMillis();
                spawnCustomer();
                // Clear cars when leaving level 3
                cars = null;
                carsIndex = 0;
            } else if (Current_level == 2 && Inventory.playerMoney >= 2500) {
                Current_level = 3;
                LevelChanges();
                tileM.reloadLevelMap();
                // Reset player position and state for level 3
                player.worldX = tileSize * 15;
                player.worldY = tileSize * 22;
                gameState = WORLD_STATE;
                // Reset customers for level 3
                customers = new Customer[maxCustomers];
                customersIndex = 0;
                lastCustomerSpawnTime = System.currentTimeMillis();
                spawnCustomer();
                // Initialize cars for level 3
                cars = new Car[maxCars];
                carsIndex = 0;
                lastCarSpawnTime = System.currentTimeMillis();
            }
            SpaceUsed = true;
        }
        if (!keyH.SpacePressed) {
            SpaceUsed = false;
        }
    }
    /**
     * Updates the order board.
     */
    private void updateOrderBoard() {
        // toggle order board visibility
        if (keyH.toggleOrdersPressed && !toggleUsed) {
            orderBoard.visible = !orderBoard.visible;
            toggleUsed = true;
        }
        if (!keyH.toggleOrdersPressed) {
            toggleUsed = false;
        }

        // fulfill next item
        if (keyH.fulfillPressed && !fulfillUsed) {
            orderBoard.fulfillFirst();
            fulfillUsed = true;
        }
        if (!keyH.fulfillPressed) {
            fulfillUsed = false;
        }
    }
    /**
     * Updates the restock panel.
     */
    @SuppressWarnings("static-access")
    private void updateRestockPanel() {
        // Toggle restock panel visibility
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
    /**
     * Updates the inventory panel.
     */
    private void updateInventoryPanel() {
        // toggle inventory panel visibility
        if (keyH.toggleInventoryPressed && !InventoryUsed) {
            inventoryPanel.visible();
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
    /**
     * Updates the information panel.
     */
    private void updateInformationPanel() {
        if (keyH.toggleInfoPressed && !infoUsed) {
            informationPanel.toggle();
            infoUsed = true;
        }
        if (!keyH.toggleInfoPressed) {
            infoUsed = false;
        }
    }
    /**
     * Draws the boost bar on the screen. The boost bar visually represents the player's current boost charge level, 
     * allowing the player to see how much boost they have available for sprinting. 
     */
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
    /**
     * Draws the cook bar on the screen. The cook bar visually represents the current cooking progress for the player
     */
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
    /**
     * Paints the game components on the screen. The method is responsible for drawing the game world, including the tiles, player, customers, cars, order board, 
     * boost bar, cook bar, inventory panel, information panel, and restock panel based on the current game state and level.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState.equals(WORLD_STATE)) {
            tileM.draw(g2);
            if (Current_level == 3) {
                g2.setColor(new Color(139, 69, 19, 0));
                int sx = 40 * tileSize - player.worldX + player.screenX;
                int sy = 25 * tileSize - player.worldY + player.screenY;
                g2.fillRect(sx, sy, 3 * tileSize, 3 * tileSize);

                g2.setColor(new Color(100, 100, 100, 0));
                sx = 35 * tileSize - player.worldX + player.screenX - tileSize;
                sy = 37 * tileSize - player.worldY + player.screenY - tileSize;
                g2.fillRect(sx, sy, tileSize * 4, tileSize * 4);
            }
            // Draw all customers
            for (int i = 0; i < customersIndex; i++) {
                if (customers[i] != null) {
                    customers[i].draw(g2);
                }
            }
            // Draw all cars in the world for level 3
            if (Current_level == 3 && cars != null) {
                for (Car car : cars) {
                    if (car != null) {
                        car.draw(g2);
                    }
                }
            }
        } else if (gameState.equals(STALL_STATE)) {
            tileM.drawInterior(g2);
        }

        if (Current_level == 3 && gameState.equals(WORLD_STATE)) {
            g2.setColor(new Color(139, 69, 19, 150));
            int sx = 33 * tileSize - player.worldX + player.screenX;
            int sy = 17 * tileSize - player.worldY + player.screenY;
            g2.fillRect(sx, sy, 4 * tileSize, 4 * tileSize);
        }
        player.draw(g2);
        messages.showMessage(g2);

        // Draw order board in-world if it's visible (used for level 3 world orders)
        if (orderBoard.visible) {
            orderBoard.drawCustomerOrder(g2);
            orderBoard.drawCarOrder(g2);
        }

        drawBoostBar(g2);
        drawCookBar(g2);

        if (gameState.equals(STALL_STATE)) {
            if (currentStallType.equals("Green")) {
                restockPanel.draw(g2);
            } else {
                orderBoard.drawCustomerOrder(g2);
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
