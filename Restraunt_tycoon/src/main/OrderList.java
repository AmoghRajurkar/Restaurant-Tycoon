package main;

import java.util.ArrayList;
import java.util.Random;

public class OrderList {

    // Each order is represented as a String array of length 2
    // The first element is the item name and the second element is the quantity of that item remaining in the order
    public ArrayList<String[]> items = new ArrayList<>();

    /**
     * Constructor for the OrderList class, which generates a random order based on the given level and stall type. 
     * It creates a list of items and their quantities that a customer has ordered, which can then be fulfilled by the player.
     * @param level The level of the game for which to generate the order
     * @param stall The type of stall for which to generate the order
     */
    public OrderList(int level, String stall) {
        Random num = new Random();
        switch (level) {
            case 1 -> {
                int orderCount = num.nextInt(2, 5); // 2 or 4 items
                for (int i = 0; i < orderCount; i++) {
                    if (stall.equals("Red")) {
                        int choice = num.nextInt(2);
                        if (choice == 0) {
                            int qty = num.nextInt(1, 3);
                            items.add(new String[]{"Burger", String.valueOf(qty)});
                        } else {
                            int qty = num.nextInt(2, 4);
                            items.add(new String[]{"Fries", String.valueOf(qty)});
                        }
                    }
                    if (stall.equals("Blue")) {
                        int choice = num.nextInt(2);
                        if (choice == 0) {
                            int qty = num.nextInt(1, 3);
                            items.add(new String[]{"IceCream", String.valueOf(qty)});
                        } else {
                            int qty = num.nextInt(1, 3);
                            items.add(new String[]{"MilkShake", String.valueOf(qty)});
                        }
                    }
                }
            }
            case 2 ->
                System.out.println("level 2 food possibilities");
            case 3 ->
                System.out.println("level 3 food possibilities");
        }
    }

    /**
     * Gives food, one item from the order list. Returns true if the order is completely fulfilled and can be removed from the list
     * @return true if the order is completely fulfilled, false otherwise
     */ 
    public boolean giveFood() {
        if (items.isEmpty()) {
            return true;
        }
        String[] first = items.get(0);
        int qty = Integer.parseInt(first[1]) - 1;
        if (qty <= 0) {
            items.remove(0);
        } else {
            first[1] = String.valueOf(qty);
        }
        return items.isEmpty();
    }
}
