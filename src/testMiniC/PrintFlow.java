package testMiniC;
import javax.swing.*;
import java.awt.*;

/**
 * Created by JongHun on 2016. 12. 18..
 */
public class PrintFlow {

    public void printRectangle(){
        Dimension dim = new Dimension(600,600);
        JFrame frame = new JFrame("Test");
        frame.setLocation(200,400);
        frame.setPreferredSize(dim);

        DrawPanel drawPanel = new DrawPanel();

        frame.add(drawPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
