package com.nerves;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author mouwenyao
 * @since 2019-09-30
 */
public class PrintImg {
    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("/Users/mwy/temp/nerves/out3.png"));
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                System.out.printf("%d,",(image.getRGB(j,i) & 0xFF) > 00? 1: 0);
            }
            System.out.println("");
        }

    }
}
