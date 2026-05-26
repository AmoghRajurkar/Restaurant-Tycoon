package main;

import java.awt.*;

public class inventoryPanel {

    private final Inventory inventory;

    private static final int PANEL_W = 500;
    private static final int PADDING = 30;
    private static final int ROW_HEIGHT = 50;
    private static final int HEADER_H = 60;
    private static final int CORNER_ARC = 24;
    private static final int MAX_VISIBLE_ROWS = 6;

    // Screen dimensions
    private static final int SCREEN_W = 1280;
    private static final int SCREEN_H = 960;

    public boolean visible = false;
    private int selectedIndex = 0; // Currently highlighted item in the list
    private int scrollOffset = 0; // Top item index visible in the list

    public inventoryPanel(Inventory inventory) {
        this.inventory = inventory;
    }

    // Move selection up/down through the inventory items
    public void moveSelection(int dir) {
        int totalItems = Inventory.INVENTORY.length;
        selectedIndex = (selectedIndex + dir + totalItems) % totalItems;

        // Auto-scroll to keep selected item visible
        if (selectedIndex < scrollOffset) {
            scrollOffset = selectedIndex;
        } else if (selectedIndex >= scrollOffset + MAX_VISIBLE_ROWS) {
            scrollOffset = selectedIndex - MAX_VISIBLE_ROWS + 1;
        }
    }

    // Toggle visibility
    public void toggle() {
        visible = !visible;
        if (visible) {
            selectedIndex = 0;
            scrollOffset = 0;
        }
    }

    @SuppressWarnings("static-access")
    public void draw(Graphics2D g2) {
        if (!visible) {
            return;
        }

        int totalItems = Inventory.INVENTORY.length;
        int visibleRows = Math.min(MAX_VISIBLE_ROWS, totalItems);
        int panelH = HEADER_H + visibleRows * ROW_HEIGHT + PADDING * 2;

        // Center the panel on screen
        int panelX = (SCREEN_W - PANEL_W) / 2;
        int panelY = (SCREEN_H - panelH) / 2;

        // Background with border
        g2.setColor(new Color(0, 0, 0, 225));
        g2.fillRoundRect(panelX, panelY, PANEL_W, panelH, CORNER_ARC, CORNER_ARC);
        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(panelX, panelY, PANEL_W, panelH, CORNER_ARC, CORNER_ARC);

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Title
        g2.setFont(new Font("Arial", Font.BOLD, 27));
        g2.setColor(Color.WHITE);
        g2.drawString("Inventory", panelX + PADDING, panelY + PADDING + 10);

        // Item rows
        for (int i = 0; i < visibleRows; i++) {
            int itemIndex = scrollOffset + i;
            int rowY = panelY + PADDING + HEADER_H + i * ROW_HEIGHT;
            boolean selectedRow = (itemIndex == selectedIndex);

            // Highlight selected row
            if (selectedRow) {
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillRoundRect(panelX + PADDING - 4, rowY - 33, PANEL_W - PADDING * 2 + 4, ROW_HEIGHT, 8, 8);
            }

            int qty = inventory.playerItems[itemIndex];

            g2.setColor(Color.WHITE);
            // Bold and > for the selected row, else normal font
            g2.setFont(new Font("Arial", selectedRow ? Font.BOLD : Font.PLAIN, 20));
            String arrow = selectedRow ? "> " : "  ";
            String line = arrow + Inventory.INVENTORY[itemIndex] + "  x" + qty;
            g2.drawString(line, panelX + PADDING, rowY);
        }
    }
}