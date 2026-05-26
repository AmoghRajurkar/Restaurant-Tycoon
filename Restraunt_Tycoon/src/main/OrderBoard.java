package main;

import java.awt.*;
import java.util.ArrayList;

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

    public OrderBoard(Gamepanel gp) {
        this.gp = gp;
    }

    // clears old orders for the stall we just walked into, since the player can only see the current customers in line for that stall
    public void loadForStall(String stall) {
        customers.clear();
    }

    // Press 2 — give the next available item from any waiting customer, remove them if done
    public void fulfillFirst() {
        if (customers.isEmpty()) {
            return;
        }

        for (int customerIndex = 0; customerIndex < customers.size(); customerIndex++) {
            OrderList order = customers.get(customerIndex);
            if (order.items.isEmpty()) {
                customers.remove(customerIndex);
                customerIndex--;
                continue;
            }

            for (int itemIndex = 0; itemIndex < order.items.size(); itemIndex++) {
                String[] orderItem = order.items.get(itemIndex);
                String itemName = orderItem[0];
                int invIndex = itemNameToIndex(itemName);
                if (invIndex == -1 || gp.inventory.playerItems[invIndex] <= 0) {
                    continue;
                }

                // Remove one from player inventory and fulfill one unit of this order item
                gp.inventory.playerItems[invIndex] -= 1;
                int qty = Integer.parseInt(orderItem[1]) - 1;
                if (qty <= 0) {
                    order.items.remove(itemIndex);
                } else {
                    orderItem[1] = String.valueOf(qty);
                }

                if (order.items.isEmpty()) {
                    customers.remove(customerIndex);
                }
                return;
            }
        }

        System.out.println("No available items in inventory to serve any customer.");
    }

    // Map OrderList item names to Inventory indices
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
            default ->
                -1;
        };
    }

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
