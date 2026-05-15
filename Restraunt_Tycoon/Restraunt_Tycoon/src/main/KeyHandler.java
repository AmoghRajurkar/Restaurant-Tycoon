package main;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, boostPressed; // Variables to track the state of key presses

    @Override
    public void keyTyped(KeyEvent e) {
        // This method is called when a key is typed (pressed and released)
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // This method is called when a key is pressed
            int code = e.getKeyCode(); // Get the code of the key that was pressed
            if (code == KeyEvent.VK_W) {
                // Move up
                upPressed = true;
            }
            if (code == KeyEvent.VK_S) {
                // Move down
                downPressed = true;
            }
            if (code == KeyEvent.VK_A) {
                // Move left
                leftPressed = true;
            }
            if (code == KeyEvent.VK_D) {
                // Move right
                rightPressed = true;
            }
            if (code == KeyEvent.VK_SHIFT) {
                // Activate boost
                boostPressed = true;
            }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // This method is called when a key is released
        int code = e.getKeyCode(); 
        if (code == KeyEvent.VK_W) {
            // Stop moving up
            upPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            // Stop moving down
            downPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            // Stop moving left
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            // Stop moving right
            rightPressed = false;
        }
        if (code == KeyEvent.VK_SHIFT) {
            // Stop boosting
            boostPressed = false;
        }
    }
}