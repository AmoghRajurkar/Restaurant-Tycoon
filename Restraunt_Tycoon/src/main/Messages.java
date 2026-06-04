package main;

import java.awt.*;

public class Messages {

    private static final int PANEL_W = 700;
    private static final int PANEL_H = 100;
    private static final int CORNER_ARC = 24;

    // Screen dimensions
    private static final int SCREEN_W = 740;
    private static final int SCREEN_H = 100;

    private static final long DURATION = 2000; // 2 seconds
    public boolean visible = false;
    public static String message;
    private long messageStartTime = 0;
    /**
     * Initializes the messages panel with the given message.
     * @param Message The message to display.
     */
    @SuppressWarnings("static-access")
    public Messages(String Message) {
        this.message = Message;
    }
    /**
     * Shows the specified message for a limited duration.
     * @param msg The message to display.
     */
    @SuppressWarnings("static-access")
    public void showMessageForDuration(String msg) {
        this.message = msg;
        this.visible = true;
        this.messageStartTime = System.currentTimeMillis();
    }
    /**
     * Updates the visibility of the message based on its duration.
     */
    public void update() {
        if (visible && System.currentTimeMillis() - messageStartTime >= DURATION) {
            visible = false;
        }
    }
    /* 
     * Toggles the visibility of the message panel.
     */
    public void toggleVisible() {
        visible = !visible;
    }
    /**
     * Draws the message panel on the screen if it is visible.
     * @param g2 The Graphics2D object for drawing.
     */
    public void showMessage(Graphics2D g2) {
        if (!visible) {
            return;
        }

        // Panel at top left of screen
        int panelX = (SCREEN_W - PANEL_W) + 250;
        int panelY = (SCREEN_H - PANEL_H) + 100;

        // Background with border top right of screen
        g2.setColor(new Color(0, 0, 0, 225));
        g2.fillRoundRect(panelX, panelY, PANEL_W, PANEL_H, CORNER_ARC, CORNER_ARC);
        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(panelX, panelY, PANEL_W, PANEL_H, CORNER_ARC, CORNER_ARC);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Message at the center of box
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int messageWidth = fm.stringWidth(message);
        int messageX = panelX + (PANEL_W - messageWidth) / 2;
        int messageY = panelY + (PANEL_H + fm.getAscent()) / 2 - 10;
        g2.drawString(message, messageX, messageY);
    }
}
