package main;

public class Cook {

    public int cookTime;
    public int foodIndex; // Index of the item being cooked in the inventory
    public int ingredient1Index; // Index of the first ingredient in the inventory
    public int ingredient2Index = -1; // Index of the second ingredient in the inventory (for burgers and milkshakes)
    public static boolean cooking = false;

    public Cook(String item, Gamepanel gp) {
        switch (item) {
            case "Burger" -> {
                cookTime = 5;
                foodIndex = 6; // Burgers
                ingredient1Index = 0; // Raw Meat
                ingredient2Index = 1; // Buns

                break;
            }
            case "Fries" -> {
                cookTime = 3;
                foodIndex = 7; // Fries
                ingredient1Index = 3; // Potatoes
                break;
            }
            case "Milkshake" -> {
                cookTime = 5;
                foodIndex = 8; // Milk Shakes
                ingredient1Index = 4; // Milk
                ingredient2Index = 5; // Milk Shake Flavors
                break;
            }
            case "Ice Cream" -> {
                cookTime = 2;
                foodIndex = 9; // Ice Creams
                ingredient1Index = 2; // Ice
                break;
            }
        }

        cookTime = (int) (cookTime * 1000 / gp.player.cookLevel); // Convert cook time to milliseconds and multiply by cook level
    }

    @SuppressWarnings("BusyWait")
    public void startCooking() {
        if (!canCook()) {
            System.out.println("Not enough ingredients to cook this item.");
            return;
        }
        // Start a new thread to handle the cooking process
        new Thread(() -> {
            try {
                // Use delta to simulate cooking time without freezing the main game thread

                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < cookTime) {
                    cooking = true;
                    Thread.sleep(100); // Sleep for a short time to prevent busy waiting
                }
                Inventory.giveToPlayer(foodIndex, 1); // Add the cooked item to the player's inventory
                Inventory.takeFromPlayer(ingredient1Index, 1); // Remove the first ingredient from the player's inventory
                if (ingredient2Index != -1) {
                    Inventory.takeFromPlayer(ingredient2Index, 1); // Remove the second ingredient if it exists
                }
                cooking = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public boolean canCook() {
        // Check if the player has the necessary ingredients to cook the item
        boolean hasIngredient1 = Inventory.playerItems[ingredient1Index] > 0;
        boolean hasIngredient2 = ingredient2Index == -1 || Inventory.playerItems[ingredient2Index] > 0; // If there's a second ingredient, check for it

        return hasIngredient1 && hasIngredient2;
    }
}
