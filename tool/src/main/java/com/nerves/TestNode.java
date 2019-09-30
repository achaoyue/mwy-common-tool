package com.nerves;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author mouwenyao
 * @since 2019-09-30
 */
public class TestNode {
    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 9; i++) {
            draw(i);
        }
        return;
    }

    private static void draw(int  num) throws IOException {
        BufferedImage image = new BufferedImage(100,100,1);
        Graphics graphics = image.getGraphics();

        graphics.setColor(new Color(255, 64, 97));
        graphics.setFont(new Font("微软雅黑", Font.PLAIN, 100));
        graphics.drawString(""+num,20,80);

        BufferedImage out = new BufferedImage(9,9,1);
        out.getGraphics().drawImage(image,0,0,10,10,null);

        ImageIO.write(out,"png",new File("/Users/mwy/temp/nerves/out"+num+".png"));
    }
}
