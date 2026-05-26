package main;

import java.awt.*;

public class RestockPanel {

    private final Inventory inventory;

    private static final int PANEL_W = 720;
    private static final int PADDING = 30;
    private static final int ROW_HEIGHT = 57;
    private static final int HEADER_H = 60;
    private static final int CORNER_ARC = 24;
    private static final int INPUT_H = 54;

    // Screen dimensions, must match Gamepanel
    private static final int SCREEN_W = 1280;
    private static final int SCREEN_H = 960;

    // Indices into Inventory.INVENTORY that the player is allowed to purchase.
    // Ignores burgers, fries, milk shakes, icecreams and money
    private static final int[] Buyable_items = {0, 1, 2, 3, 4, 5};

    public boolean visible = true;
    private int selectedIndex = 0; // index into Buyable_items, not INVENTORY
    private String typedQty = ""; // digits the player has typed so far
    public boolean typingMode = false; // true while the quantity box is active

    public RestockPanel(Inventory inventory) {
        this.inventory = inventory;
    }

    // Move selection up/down through the buyable item list
    public void moveSelection(int dir) {
        if (typingMode) {
            return;
        }
        selectedIndex = (selectedIndex + dir + Buyable_items.length) % Buyable_items.length;
    }

    // Append a digit to the typed quantity, up to 4 digits long
    public void appendDigit(char digit) {
        if (typedQty.length() < 4) {
            typedQty += digit;
        }
    }

    // Remove the last digit from the typed quantity
    public void deleteDigit() {
        if (!typedQty.isEmpty()) {
            typedQty = typedQty.substring(0, typedQty.length() - 1);
        }
    }

    // Gives the typed quantity of items from the stall to the player
    @SuppressWarnings("static-access")
    public void confirmTransfer() {
        if (typedQty.isEmpty()) {
            return;
        }
        int amount = Integer.parseInt(typedQty);
        int itemIndex = Buyable_items[selectedIndex];
        int taken = inventory.giveToPlayer(itemIndex, amount);
        System.out.println("Got " + taken + "x " + Inventory.INVENTORY[itemIndex]);

        // Reset typing mode and clear typed quantity after confirming
        typedQty = "";
        typingMode = false;
    }

    // Activates typing mode for the currently selected item
    public void selectCurrentItem() {
        typingMode = true;
        typedQty = "";
    }

    @SuppressWarnings("static-access")
    public void draw(Graphics2D g2) {
        if (!visible) {
            return;
        }

        int rows = Buyable_items.length;
        int panelH = HEADER_H + rows * ROW_HEIGHT + PADDING + INPUT_H + PADDING * 2;

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
        g2.setColor(new Color(100, 220, 100));
        g2.drawString("Restock Cart:", panelX + PADDING, panelY + PADDING + 10);

        // Item rows — only show buyable items
        for (int i = 0; i < rows; i++) {
            int itemIndex = Buyable_items[i];
            int rowY = panelY + PADDING + HEADER_H + i * ROW_HEIGHT;
            boolean selectedRow = (i == selectedIndex);

            // Highlight selected row
            if (selectedRow) {
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillRoundRect(panelX + PADDING - 4, rowY - 33, PANEL_W - PADDING * 2 + 4, ROW_HEIGHT, 8, 8);
            }

            int qty = inventory.stallItems[itemIndex];
            // Red when out of stock, white normally
            if (qty <= 0) {
                g2.setColor(new Color(220, 60, 60));
            } else {
                g2.setColor(Color.WHITE);
            }

            // Bolds and > for the selected row, else normal font and no arrow
            g2.setFont(new Font("Arial", selectedRow ? Font.BOLD : Font.PLAIN, 24));
            String arrow = selectedRow ? "> " : "  ";
            g2.drawString(arrow + Inventory.INVENTORY[itemIndex] + "  x" + qty, panelX + PADDING, rowY);
        }

        // Quantity input box
        int boxY = panelY + PADDING + HEADER_H + rows * ROW_HEIGHT + PADDING / 2;

        g2.setColor(new Color(30, 30, 30));
        g2.fillRoundRect(panelX + PADDING, boxY, PANEL_W - PADDING * 2, INPUT_H, 10, 10);
        // Brighter border when active, darker when not
        g2.setColor(typingMode ? new Color(255, 220, 80) : new Color(100, 100, 100));
        g2.setStroke(new BasicStroke(2f)); // Thicker border when active
        g2.drawRoundRect(panelX + PADDING, boxY, PANEL_W - PADDING * 2, INPUT_H, 10, 10);

        g2.setFont(new Font("Arial", Font.PLAIN, 22));
        String label = "Quantity: " + typedQty + (typingMode ? "|" : "");
        g2.setColor(typingMode ? Color.WHITE : new Color(150, 150, 150));
        g2.drawString(label, panelX + PADDING + 10, boxY + 33);

        // Hint on right side of the input box
        g2.setFont(new Font("Arial", Font.BOLD, 19));
        g2.setColor(new Color(100, 220, 100));
        String hint = typingMode ? "ENTER = get" : "ENTER = select";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(hint, panelX + PANEL_W - PADDING - fm.stringWidth(hint) - 6, boxY + 33);
    }
}
