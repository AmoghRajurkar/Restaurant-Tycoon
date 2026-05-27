package main;

import java.util.ArrayList;
import java.util.Random;

public class OrderList {

    // Each order is represented as a String array of length 2
    // The first element is the item name and the second element is the quantity of that item remaining in the order
    public ArrayList<String[]> items = new ArrayList<>();

    // Generate a random order list for a customer based on the current level and stall type
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

    // Gives food, one item from the order list. Returns true if the order is completely fulfilled and can be removed from the queue
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
