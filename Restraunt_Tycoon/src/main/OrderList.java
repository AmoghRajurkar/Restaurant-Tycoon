package main;
import java.util.Random;

public class OrderList {
    String[] Order = { "", ""};
    int[] OrderQuantity = {0,0};

    public OrderList (int level, String stall) {
        switch (level){
            case (1)-> {
                Random num = new Random();
                int looped = 0;
                for (int i = 0; i < Order.length; i++, looped++) {
                    if ((stall).equals("Red")){
                        int choice = num.nextInt(2);
                        if (choice == 0) {
                            Order[i] = "Burger";
                            QuantityOf(Order[i], looped);
                        }
                        if (choice == 1){
                            Order[i] = "Fries";
                            QuantityOf(Order[i], looped);
                        }
                    }
                    if ((stall).equals("Blue")){
                        int choice = num.nextInt(2);
                        if (choice == 0){
                            Order[i] = "MilkShake";
                            QuantityOf(Order[i], looped);
                        }
                        if (choice == 1){
                            Order[i] = "IceCream";
                            QuantityOf(Order[i], looped);
                        }
                    }
                }
                break;
            }
            case (2) -> {
                System.out.println("level 2 food posibilities");
            }
            case (3) -> {
                System.out.println("level 3 food posibilities");
            }
        }
    }

    private void QuantityOf(String food, int i) {
        Random num = new Random();
        switch (food) {
            case ("Burger")-> {
                int quantity = num.nextInt(1, 3);
                OrderQuantity[i] = quantity;
                System.out.println("Burger: " + OrderQuantity[i]);
                break;
            }
            case ("Fries")-> {
                int quantity = num.nextInt(2, 4);
                OrderQuantity[i] = quantity;
                System.out.println("Fries: " + OrderQuantity[i]);
                break;
            }
            case ("MilkShake")-> {
                int quantity = num.nextInt(1, 3);
                OrderQuantity[i] = quantity;
                System.out.println("MilkShake: " + OrderQuantity[i]);
                break;
            }
            case ("IceCream")-> {
                int quantity = num.nextInt(2, 5);
                OrderQuantity[i] = quantity;
                System.out.println("IceCream: " + OrderQuantity[i]);
                break;
            }
        }
    }
}