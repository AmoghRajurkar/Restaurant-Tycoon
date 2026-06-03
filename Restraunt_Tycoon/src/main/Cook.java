package main;

public class Cook {

    public int cookTime;
    public int foodIndex; // Index of the item being cooked in the inventory
    public int ingredient1Index; // Index of the first ingredient in the inventory
    public int ingredient1Amount = 1; // Amount of ingredient1 required
    public int ingredient2Index = -1; // Index of the second ingredient in the inventory (for burgers and milkshakes)
    public int ingredient2Amount = 1; // Amount of ingredient2 required
    public static int price = 0;
    public static boolean cooking = false;
    public static long cookingStartTime = 0;
    public static int currentCookTime = 0;
    public static String currentCookItem = "";
    private Gamepanel gp;

    /**
     * Constructor for the Cook class, initializes the cook time based on the
     * item being cooked and the player's cook level, as well as the inventory
     * indices for the food and its ingredients
     *
     * @param item the name of the item to be cooked, used to determine cook
     * time and ingredients indexes
     * @param gp the Gamepanel instance, used to access the player's inventory
     * and display messages
     */
    public Cook(String item, Gamepanel gp) {
        this.gp = gp;
        switch (item) {
            case "Burger" -> {
                cookTime = 5;
                foodIndex = 6; // Burgers
                ingredient1Index = 0; // Raw Meat
                ingredient2Index = 1; // Buns
                price = 20;
                break;
            }
            case "Fries" -> {
                cookTime = 3;
                foodIndex = 7; // Fries
                ingredient1Index = 3; // Potatoes
                price = 15;
                break;
            }
            case "Milkshake" -> {
                cookTime = 5;
                foodIndex = 8; // Milk Shakes
                ingredient1Index = 4; // Milk
                ingredient2Index = 5; // Milk Shake Flavors
                price = 25;
                break;
            }
            case "Ice Cream" -> {
                cookTime = 2;
                foodIndex = 9; // Ice Creams
                ingredient1Index = 2; // Ice
                price = 15;
                break;
            }
            case "Popcorn" -> {
                cookTime = 3;
                foodIndex = 11; // Popcorn
                ingredient1Index = 10; // Kernels
                ingredient1Amount = 2;
                price = 10;
                break;
            }
            case "Soda" -> {
                cookTime = 2;
                foodIndex = 13; // Soda
                ingredient1Index = 12; // Cans
                price = 12;
                break;
            }
            case "Coffee" -> {
                cookTime = 4;
                foodIndex = 16; // Coffee
                ingredient1Index = 14; // Coffee Beans
                ingredient1Amount = 3;
                price = 18;
                break;
            }
            case "Omelet" -> {
                cookTime = 4;
                foodIndex = 17; // Omelet
                ingredient1Index = 15; // Eggs
                ingredient1Amount = 2;
                price = 20;
                break;
            }
        }

        cookTime = (int) (cookTime * 1000 / gp.player.cookLevel); // Convert cook time to milliseconds and scale by cook level
    }

    /**
     * Uses the canCook() method to check if the player has the ingredients
     * needed to cook the item, if so it starts a new thread to handle the
     * cooking process, simulates cooking using the thread sleep method and
     * updates player inventory, adds the food and removes the ingredients. If
     * the player doesn't have the necessary ingredients, it shows a message to
     * the player indicating that they cannot cook the item. The warnig
     * suppression is used to ignore the warning about busy waiting, since
     * Thread.sleep is used to simulate the cooking time without freezing the
     * main game thread.
     */
    @SuppressWarnings("BusyWait")
    public void startCooking() {
        if (!canCook()) {
            gp.messages.showMessageForDuration("Not enough ingredients to cook " + Inventory.INVENTORY[foodIndex]);
            return;
        }
        // Start a new thread to handle the cooking process
        new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                cookingStartTime = startTime;
                currentCookTime = cookTime;
                currentCookItem = Inventory.INVENTORY[foodIndex];
                cooking = true;

                while (System.currentTimeMillis() - startTime < cookTime) {
                    Thread.sleep(100); // Sleep for a short time to prevent busy waiting
                }
                Inventory.giveToPlayer(foodIndex, 1); // Add the cooked item to the player's inventory
                Inventory.takeFromPlayer(ingredient1Index, ingredient1Amount); // Remove the first ingredient from the player's inventory
                if (ingredient2Index != -1) {
                    Inventory.takeFromPlayer(ingredient2Index, ingredient2Amount); // Remove the second ingredient if it exists
                }
                cooking = false;
                currentCookItem = "";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Checks if the player has the necessary ingredients to cook the item
     *
     * @return true if the player has all required ingredients, false otherwise
     */
    public boolean canCook() {
        // Check if the player has the necessary ingredients to cook the item
        boolean hasIngredient1 = Inventory.playerItems[ingredient1Index] >= ingredient1Amount;
        boolean hasIngredient2 = ingredient2Index == -1 || Inventory.playerItems[ingredient2Index] >= ingredient2Amount; // If there's a second ingredient, check for it

        return hasIngredient1 && hasIngredient2;
    }


}
