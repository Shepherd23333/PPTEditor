package me.shepherd23333;

import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 常用对象及方法
 */
public class Utils {
    /**
     * 虚线线条
     */
    public static final BasicStroke dash = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{1.5f, 1.5f}, 0);

    /**
     * {@code XSLFPictureShape}转{@code Image}
     *
     * @param pic 图片形状
     * @return 对应的Java图像
     */
    public static Image getImageFromShape(XSLFPictureShape pic) throws IOException {
        byte[] data = pic.getPictureData().getData();
        return ImageIO.read(new ByteArrayInputStream(data));
    }

    /**
     * {@code XSLFSlide}转{@code Image}
     *
     * @param s 幻灯片
     * @return 对应的Java图像
     */
    public static Image getImageFromSlide(XSLFSlide s) {
        Dimension d = s.getSlideShow().getPageSize();
        BufferedImage img = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setPaint(Color.WHITE);
        g.fill(new Rectangle2D.Double(0, 0, d.width, d.height));
        s.draw(g);
        return img;
    }
}
