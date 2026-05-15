package out.entity;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.image

import main.GamePanel
import main.UtilityTool;

public class Customer extends Entity{

    public Customer(GamePanel gp){
        super(gp);

        direction = "down";
        speed = 5;


    }
    /*private void getPlayerImage() {
        // Load player images for different directions and animations
        try {
            up1 = ImageIO.read(new File("res/player/up1.png"));
            up2 = ImageIO.read(new File("res/player/up2.png"));
            down1 = ImageIO.read(new File("res/player/down1.png"));
            down2 = ImageIO.read(new File("res/player/down2.png"));
            left1 = ImageIO.read(new File("res/player/left1.png"));
            left2 = ImageIO.read(new File("res/player/left2.png"));
            right1 = ImageIO.read(new File("res/player/right1.png"));
            right2 = ImageIO.read(new File("res/player/right2.png"));
            upStill = ImageIO.read(new File("res/player/up_still.png"));
            downStill = ImageIO.read(new File("res/player/down_still.png"));
            leftStill = ImageIO.read(new File("res/player/left_still.png"));
            rightStill = ImageIO.read(new File("res/player/right_still.png"));
        }
        catch (IOException e) {
            System.out.println("Error loading player images");
        }
    }
*/

}
