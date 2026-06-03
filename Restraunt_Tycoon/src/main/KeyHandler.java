package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed, boostPressed;
    public boolean upArrow, downArrow; // arrow keys only
    public boolean interactPressed; // E — enter/exit stalls
    public boolean toggleOrdersPressed; // Tab — show/hide order/restock board
    public boolean toggleInventoryPressed; // Q - show/hide inventory
    public boolean toggleInfoPressed; // 1 - show/hide information panel
    public boolean enterPressed; // Enter — select item or confirm quantity
    public boolean backspacePressed; // Backspace — delete last digit
    public boolean fulfillPressed; // 2 — send cooked food
    public boolean UpgradeCookPressed; // U - upgrade cooking level
    public boolean SpacePressed; // Space - next level

    // Blocks WASD while typing a quantity in the restock panel
    public boolean typingMode = false;

    // Last number key typed this frame (0-9), -1 if none
    public int lastDigit = -1;
    /**
     * Handles key typed events.
     * @param e The KeyEvent object representing the key typed event.
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }
    /**
     * Handles key pressed events, updating the state of various boolean flags based on which keys are pressed.
     * @param e The KeyEvent object representing the key pressed event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // WASD moves the player is blocked while typing and cooking
        if (!typingMode && !Cook.cooking) {
            if (code == KeyEvent.VK_W) {
                upPressed = true;
            }
            if (code == KeyEvent.VK_S) {
                downPressed = true;
            }
            if (code == KeyEvent.VK_A) {
                leftPressed = true;
            }
            if (code == KeyEvent.VK_D) {
                rightPressed = true;
            }
        }

        // Arrow keys navigate the restock list, never move the player
        if (code == KeyEvent.VK_UP) {
            upArrow = true;
        }
        if (code == KeyEvent.VK_DOWN) {
            downArrow = true;
        }

        if (code == KeyEvent.VK_SHIFT) {
            boostPressed = true;
        }
        if (code == KeyEvent.VK_E) {
            interactPressed = true;
        }
        if (code == KeyEvent.VK_TAB) {
            toggleOrdersPressed = true;
        }
        if (code == KeyEvent.VK_2) {
            fulfillPressed = true;
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }
        if (code == KeyEvent.VK_BACK_SPACE) {
            backspacePressed = true;
        }
        if (code == KeyEvent.VK_U) {
            UpgradeCookPressed = true;
        }
        if (code == KeyEvent.VK_I) {
            toggleInventoryPressed = true;
        }
        if (code == KeyEvent.VK_CAPS_LOCK) {
            toggleInfoPressed = true;
        }
        if (code == KeyEvent.VK_SPACE) {
            SpacePressed = true;
        }

        // Capture digit keys 0-9
        if (code >= KeyEvent.VK_0 && code <= KeyEvent.VK_9) {
            lastDigit = code - KeyEvent.VK_0;
        }
    }
    /**
     * Handles key released events, updating the state of various boolean flags based on which keys are released.
     * @param e The KeyEvent object representing the key released event.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }

        if (code == KeyEvent.VK_UP) {
            upArrow = false;
        }
        if (code == KeyEvent.VK_DOWN) {
            downArrow = false;
        }

        if (code == KeyEvent.VK_SHIFT) {
            boostPressed = false;
        }
        if (code == KeyEvent.VK_E) {
            interactPressed = false;
        }
        if (code == KeyEvent.VK_TAB) {
            toggleOrdersPressed = false;
        }
        if (code == KeyEvent.VK_2) {
            fulfillPressed = false;
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false;
        }
        if (code == KeyEvent.VK_BACK_SPACE) {
            backspacePressed = false;
        }
        if (code == KeyEvent.VK_U) {
            UpgradeCookPressed = false;
        }
        if (code == KeyEvent.VK_I) {
            toggleInventoryPressed = false;
        }
        if (code == KeyEvent.VK_CAPS_LOCK) {
            toggleInfoPressed = false;
        }
        if (code == KeyEvent.VK_SPACE) {
            SpacePressed = false;
        }

        // Clear digit on release so each keypress registers once
        if (code >= KeyEvent.VK_0 && code <= KeyEvent.VK_9) {
            lastDigit = -1;
        }
    }
}
