package main;

public class Inventory {

    public static final String[] INVENTORY = {"Raw Meat", "Buns", "Ice", "Potatoes", "Milk", "Milk Shake Flavors", "Burgers", "Fries", "Milk Shakes", "Ice Creams", "Kernels", "Popcorn", "Cans", "Soda", "Coffee Beans", "Eggs", "Coffee", "Omelet"};

    // Max the player can carry per item
    public static final int PLAYER_CAP = 100;

    // Player starts with 0 of everything
    public static int[] playerItems = new int[INVENTORY.length];
    public static double playerMoney = 100000;

    // Green stall starts with 1000 of everything
    public static int[] stallItems = new int[INVENTORY.length];
    public static double moneyGiven;

    /**
     * Constructor method for inventory class, initializes the stall 1000 of
     * each item and player inventory to 0.
     */
    public Inventory() {
        for (int i = 0; i < INVENTORY.length; i++) {
            stallItems[i] = 1000;
        }
    }

    /**
     * Method to giving a certian amount of money to the player, used to giove
     * money when giving food to customer.
     *
     * @param amount the amount of money to give to the player, added to the
     * playerMoney variable
     */
    public static void giveMoneyToPlayer(double amount) {
        playerMoney += amount;
    }

    /**
     * Method that takes a certian amount of money from the player, used to take
     * money when in the restock cart.
     *
     * @param amount the amount of money being taken from the player, subtracted
     * from the playerMoney variable
     */
    public static void takeMoneyFromPlayer(double amount) {
        if (amount <= 0) {
            return;
        }
        playerMoney -= amount;
        if (playerMoney < 0) {
            playerMoney = 0;
        }
    }

    /**
     * Gives items to the players inventory based on index, like raw meat is 0,
     * buns are 1, etc. And the specific amount of items, 5 buns, 7 meat. Cannot
     * go more than inventory limits or less than restock stall limits
     *
     * @param itemIndex the index of the item in the INVENTORY list
     * @param amount the amount of items being added
     * @return the amount of items being taken from the stall
     */
    // Caps at player limit and stall stock. Returns how many were actually taken.
    public static int giveToPlayer(int itemIndex, int amount) {
        int inStall = stallItems[itemIndex];
        int playerHas = playerItems[itemIndex];
        int canCarry = PLAYER_CAP - playerHas;

        // Take as many as possible within both limits
        int taken = Math.min(amount, Math.min(inStall, canCarry));
        if (taken <= 0) {
            return 0;
        }
        stallItems[itemIndex] -= taken;
        playerItems[itemIndex] += taken;

        moneyGiven += 0.5 * taken; // Each item costs the player 50% of its base value when restocking

        return taken;
    }

    /**
     * Takes items from the players inventory based on index, like raw meat is
     * 0, buns are 1, etc. And the specific amount of items, 5 buns, 7 meat.
     * Cannot go less than inventory limits, takes the items out when it has the
     * item, if not, cannot take the item out
     *
     * @param itemIndex the index of the item in the INVENTORY list
     * @param amount the amount of items being removed
     * @return boolean, false if player doesn't have enough to give, true if the
     * player gives.
     */
    public static boolean takeFromPlayer(int itemIndex, int amount) {
        int playerHas = playerItems[itemIndex];
        if (playerHas < amount) {
            return false;
        }
        playerItems[itemIndex] -= amount;
        return true;
    }
}
