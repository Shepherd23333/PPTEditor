package me.shepherd23333.gui;

import me.shepherd23333.Utils;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * 图片组件
 */
public class ImagePanel extends DraggablePanel {
    /**
     * 用于绘制的图片
     */
    private Image image;
    public ImagePanel(XSLFPictureShape pic) {
        super(pic);
        Rectangle2D r = pic.getAnchor();
        try {
            init(pic, r.getX(), r.getY());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImagePanel(XSLFPictureShape pic, double x, double y) {
        super(pic);
        try {
            init(pic, x, y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(XSLFPictureShape pic, double x, double y) throws IOException {
        image = Utils.getImageFromShape(pic);
        Rectangle2D r = pic.getAnchor();
        instance.setAnchor(new Rectangle2D.Double(x, y, r.getWidth(), r.getHeight()));
        //平滑缩放到指定大小
        image = image.getScaledInstance((int) r.getWidth(), (int) r.getHeight(), Image.SCALE_SMOOTH);
        setBounds((int) (r.getX() - 5), (int) (r.getY() - 5), (int) (r.getWidth() + 10), (int) (r.getHeight() + 10));
    }

    @Override
    protected boolean isOnMoveArea(Point p) {
        return true;
    }

    protected boolean isOnResizeArea(MouseEvent m) {
        int buttonX = getWidth() - 10, buttonY = getHeight() - 10;
        return buttonX <= m.getX() && m.getX() <= buttonX + 10 && buttonY <= m.getY() && m.getY() <= buttonY + 10;
    }

    @Override
    protected void resizeObject(int width, int height) {
        try {
            image = Utils.getImageFromShape((XSLFPictureShape) instance).getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 5, 5, this);
        if (isSelected) {
            g.setColor(Color.BLACK);
            g.drawOval(getWidth() - 10, getHeight() - 10, 10, 10);
        }
    }
}
