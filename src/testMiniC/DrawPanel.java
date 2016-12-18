package testMiniC;

import javax.swing.*;
import java.awt.*;

/**
 * Created by JongHun on 2016. 12. 18..
 */
public class DrawPanel extends JPanel{
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawRect(50,50,100,50);
        g.drawString("Main",60,80);
        g.drawLine(150,75,200,100);

    }
}
