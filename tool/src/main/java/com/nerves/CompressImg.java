package com.nerves;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author mouwenyao
 * @since 2019-09-30
 */
public class CompressImg {
    public static void main(String[] args) throws IOException {
        BufferedImage src = ImageIO.read(new File("/Users/mwy/Desktop/3.jpg"));
        BufferedImage out = new BufferedImage(10,10,1);
        out.getGraphics().drawImage(src,0,0,10,10,null);

        ImageIO.write(out,"png",new File("/Users/mwy/temp/nerves/compress.png"));
    }
}
