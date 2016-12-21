package testMiniC;

import javax.swing.*;
import java.awt.*;

/**
 * Created by JongHun on 2016. 12. 18..
 */
public class DrawPanel extends JPanel{

    private class Block{
        private int x, y;
        private String name;
        private Block(String name, int x, int y){
            this.x = x;
            this.y = y;
            this.name = name;
        }
    }

    private int x, y;

    public DrawPanel(){
        x = 50;
        y = 50;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Block main = new Block("Main", x, y);
        shiftRight();
        Block test = new Block("Test", x, y);
        shiftDown();
        Block test2 = new Block("Test2", x, y);

        drawBlock(g, main);
        drawBlock(g, test);
        drawBlock(g, test2);
        drawArrow(g, main, test);
        drawArrow(g, main, test2);

    }

    private void drawBlock(Graphics g, Block block){
        g.drawRect(block.x, block.y, 100, 50);
        g.drawString(block.name, block.x + 5, block.y + 30);
    }

    private void shiftDown(){
        y += 75;
    }
    private void shiftRight(){
        x += 200;
    }

    private void drawArrow(Graphics g, Block src, Block dst){
        int x1 = src.x+100;
        int x2 = dst.x;
        int y1 = src.y+25;
        int y2 = dst.y+25;

        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx*dx + dy*dy);
        double xm = D - 10, xn = xm, ym = 10, yn = -10, x;
        double sin = dy/D, cos = dx/D;

        x = xm*cos - ym*sin + x1;
        ym = xm*sin + ym*cos + y1;
        xm = x;

        x = xn*cos - yn*sin + x1;
        yn = xn*sin + yn*cos + y1;
        xn = x;

        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};

        g.drawLine(x1, y1, x2, y2);
        g.fillPolygon(xpoints, ypoints, 3);

    }



}
