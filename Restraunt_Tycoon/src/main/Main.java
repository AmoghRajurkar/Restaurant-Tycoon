package main;

import javax.swing.*;

public class Main {

    /**
     * Main method start the game, it create a JFrame window, sets it size and
     * properties then it creates an instance of Gamepanel, adds it to the
     * window, and starts the game thread for the main game loop
     *
     * @param args command line arguments, not used in this program
     */
    public static void main(String[] args) {
        JFrame window = new JFrame(); // Create a new JFrame window
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application when the window is closed
        window.setResizable(false); // Prevent the window from being resized
        window.setSize(1280, 960); // Set the size of the window
        window.setLocationRelativeTo(null); // Center the window on the screens
        window.setTitle("Restaurant"); // Set the title of the window
        Gamepanel gamePanel = new Gamepanel(); // Create an instance of the GamePanel class
        window.add(gamePanel); // Add the GamePanel to the window
        window.pack(); // Adjust the window size to fit the preferred size of its components
        window.setVisible(true); // Make the window visible
        gamePanel.startGameThread(); // Start the game thread to run the game loops
        gamePanel.requestFocusInWindow(); // Request focus for the game panel to receive key inputs
    }
}
