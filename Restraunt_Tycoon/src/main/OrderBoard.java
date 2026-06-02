package main;

import entity.Car;
import entity.Customer;
import java.awt.*;
import java.util.*;

public class OrderBoard {

    // ArrayList where each element is a customer's full OrderList (their items + quantities).
    // It is final, but customers can still be added/removed from it
    public final ArrayList<OrderList> customers = new ArrayList<>();

    // Layout constants
    private static final int PANEL_X = 10;
    private static final int PANEL_Y = 10;
    private static final int ROW_WIDTH = 200;
    private static final int ROW_HEIGHT = 22;
    private static final int HEADER_HEIGHT = 28;
    private static final int PADDING = 10;
    private static final int CORNER_ARC = 12;

    public boolean visible = true;
    private final Gamepanel gp;

    /**
     * Constructor for the OrderBoard class, which initializes the game panel.
     *
     * @param gp The game panel instance to be used for displaying orders and
     * interacting with the game.
     */
    public OrderBoard(Gamepanel gp) {
        this.gp = gp;
    }

    /**
     * Loads the order board with the current customers waiting at a specific
     * stall. It clears any existing orders and populates the board with the
     * orders of customers waiting for the specified stall type.
     *
     * @param stall The type of stall for which to display orders.
     */
    public void loadForStall(String stall) {
        customers.clear();
    }

    // Press 2 — give the next available item from any waiting customer, remove them if done
    /**
     * Fulfills the first available item from the waiting customers. It iterates
     * through the customers and their orders, checking if the player has the
     * required item in their inventory. If an item is fulfilled, it updates the
     * customer's order and gives the player money for selling the item. If a
     * customer's order is completely fulfilled, they are removed from the
     * waiting list.
     */
    @SuppressWarnings("static-access")
    public void fulfillFirst() {
        if (customers.isEmpty()) {
            gp.messages.showMessageForDuration("No customers waiting.");
            return;
        }

        for (int customerIndex = 0; customerIndex < customers.size(); customerIndex++) {
            OrderList order = customers.get(customerIndex);
            if (order.items.isEmpty()) {
                customers.remove(customerIndex);
                customerIndex--;
                Customer servedCustomer = gp.getFirstWaitingCustomerStall(gp.currentStallType);
                if (servedCustomer != null) {
                    servedCustomer.isServed = true;
                }
                servedCustomer = gp.getFirstWaitingCustomerTruck(gp.currentStallType);
                if (servedCustomer != null) {
                    servedCustomer.isServed = true;
                }
                continue;
            }

            for (int itemIndex = 0; itemIndex < order.items.size(); itemIndex++) {
                String[] orderItem = order.items.get(itemIndex);
                String itemName = orderItem[0];
                int invIndex = itemNameToIndex(itemName);
                if (invIndex == -1 || gp.inventory.playerItems[invIndex] <= 0) {
                    continue;
                }

                if (gp.Current_level == 3) {
                    // Check if any waiting cars can be served this item
                    // For cars
                    for (Car car : gp.cars) {
                        if (car.isServed || !car.place_order || !car.isOnDropZone()) {
                            continue;
                        }
                        if (gp.inventory.playerItems[invIndex] <= 0) {
                            break; // No more of this item to give
                        }
                        // Remove one from player inventory and fulfill one unit of this order item
                        gp.inventory.playerItems[invIndex] -= 1;
                        int qty = Integer.parseInt(orderItem[1]) - 1;
                        if (qty <= 0) {
                            order.items.remove(itemIndex);
                            gp.inventory.giveMoneyToPlayer(Cook.price); // Give player money for selling the item
                        } else {
                            orderItem[1] = String.valueOf(qty);
                            gp.inventory.giveMoneyToPlayer(Cook.price); // Give player money for selling the item
                        }

                        car.isServed = true; // Mark the car as served
                    }
                }

                // Remove one from player inventory and fulfill one unit of this order item
                gp.inventory.playerItems[invIndex] -= 1;
                    int qty = Integer.parseInt(orderItem[1]) - 1;
                    if (qty <= 0) {
                        order.items.remove(itemIndex);
                        gp.inventory.giveMoneyToPlayer(Cook.price); // Give player money for selling the item
                    } else {
                        orderItem[1] = String.valueOf(qty);
                        gp.inventory.giveMoneyToPlayer(Cook.price); // Give player money for selling the item
                    }

                    if (order.items.isEmpty()) {
                        customers.remove(customerIndex);
                        customerIndex--;
                        Customer servedCustomer = gp.getFirstWaitingCustomerStall(gp.currentStallType);
                        if (servedCustomer != null) {
                            servedCustomer.isServed = true;
                        }
                        servedCustomer = gp.getFirstWaitingCustomerTruck(gp.currentStallType);
                        if (servedCustomer != null) {
                            servedCustomer.isServed = true;
                        }
                    }
                    return;
                }
            }

            gp.messages.showMessageForDuration("No available items in inventory to serve any customer.");
        }
        /**
         * Helper method to convert an item name from the order into the
         * corresponding index in the player's inventory. It uses a switch
         * statement to map item names to their respective inventory indices.
         *
         * @param name The name of the item to convert
         * @return The index of the item in the player's inventory, or -1 if not
         * found
         */
    private int itemNameToIndex(String name) {
        return switch (name) {
            case "Burger" ->
                6; // "Burgers"
            case "Fries" ->
                7;  // "Fries"
            case "MilkShake" ->
                8; // "Milk Shakes"
            case "IceCream" ->
                9; // "Ice Creams"
            case "Popcorn" ->
                11; // "Popcorn"
            case "Soda" ->
                13; // "Soda"
            case "Coffee" ->
                16; // "Coffee"
            case "Omelet" ->
                17; // "Omelet"
            default ->
                -1;
        };
    }

    /**
     * Draws the order board on the screen if it is visible. It renders a
     * semi-transparent background with a border, and displays the orders of
     * each waiting customer in separate columns.
     *
     * @param g2 The Graphics2D context to draw on
     */
    public void draw(Graphics2D g2) {
        if (!visible || customers.isEmpty()) {
            return;
        }

        int rows = customers.size();
        int panelWidth = rows * ROW_WIDTH + PADDING * 2;

        // Find max rows needed so background height fits the tallest column
        int maxItems = 0;
        for (OrderList o : customers) {
            maxItems = Math.max(maxItems, o.items.size());
        }
        int panelHeight = HEADER_HEIGHT + maxItems * ROW_HEIGHT + PADDING * 2;

        // Semi-transparent background
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(PANEL_X, PANEL_Y, panelWidth, panelHeight, CORNER_ARC, CORNER_ARC);
        // Border
        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(PANEL_X, PANEL_Y, panelWidth, panelHeight, CORNER_ARC, CORNER_ARC);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw each customer's order in a column
        for (int i = 0; i < rows; i++) {
            OrderList order = customers.get(i);
            int rowX = PANEL_X + PADDING + i * ROW_WIDTH;

            // Divider line between columns
            if (i > 0) {
                g2.setColor(new Color(255, 255, 255, 40));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(rowX - PADDING / 2, PANEL_Y + 6, rowX - PADDING / 2, PANEL_Y + panelHeight - 6);
            }

            // Header
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.setColor(new Color(255, 220, 80));
            g2.drawString("Customer " + (i + 1) + ":", rowX, PANEL_Y + PADDING + 14);

            // Items and quantities on the planet
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.setColor(Color.WHITE);
            for (int j = 0; j < order.items.size(); j++) {
                String[] item = order.items.get(j);
                String line = item[0] + " x" + item[1];
                int textY = PANEL_Y + PADDING + HEADER_HEIGHT + j * ROW_HEIGHT;
                g2.drawString(line, rowX, textY);
            }
        }
    }
}
