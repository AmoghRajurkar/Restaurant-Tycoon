package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InformationPanel {

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
    public List<String> lines = new ArrayList<>();

    public InformationPanel() {
        // Default placeholder text until the game fills actual information
        lines.add("WASD to move");
        lines.add("Arrow keys to select items");
        lines.add("E to enter stall");
        lines.add("I to toggle inventory");
        lines.add("2 to give food");
        lines.add("Green stall is restock");
        lines.add("Red and Blue stall are food");
    }

    // Toggle visibility
    public void toggle() {
        visible = !visible;
    }

    public void setLines(List<String> newLines) {
        lines.clear();
        lines.addAll(newLines);
    }

    public void setLines(String... newLines) {
        lines.clear();
        Collections.addAll(lines, newLines);
    }

    public void draw(Graphics2D g2) {
        if (!visible) {
            return;
        }

        int totalItems = lines.size();
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
        g2.drawString("Information", panelX + PADDING, panelY + PADDING + 10);

        // Draw the lines of text
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(Color.WHITE);
        for (int i = 0; i < visibleRows; i++) {
            int rowY = panelY + PADDING + HEADER_H + i * ROW_HEIGHT;
            g2.drawString(lines.get(i), panelX + PADDING, rowY);
        }
    }
}
