package me.shepherd23333.gui;

import me.shepherd23333.Utils;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class ImageLabel extends JLabel {
    private XSLFPictureShape instance;
    private Image image;
    private Point start, oldP, newP;
    private boolean isResizing = false, isSelected = false;

    public ImageLabel(XSLFPictureShape pic) throws IOException {
        instance = pic;
        image = Utils.getImage(pic);
        Rectangle2D r = pic.getAnchor();
        image = image.getScaledInstance((int) r.getWidth(), (int) r.getHeight(), Image.SCALE_SMOOTH);
        setBounds((int) (r.getX() - 5), (int) (r.getY() - 5), (int) (r.getWidth() + 10), (int) (r.getHeight() + 10));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent m) {
                start = getLocation();
                oldP = m.getLocationOnScreen();
                if (isSelected && isOnResizeArea(m)) {
                    isResizing = true;
                    start = m.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent m) {
                isResizing = false;
                isSelected = true;
                Rectangle r = getBounds();
                instance.setAnchor(new Rectangle(r.x + 5, r.y + 5, r.width - 10, r.height - 10));
                repaint();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent m) {
                if (isResizing) {
                    int newWidth = Math.max(getWidth() - 10 + m.getX() - start.x, 32), newHeight = Math.max(getHeight() - 10 + m.getY() - start.y, 32);
                    try {
                        image = Utils.getImage(instance).getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    setSize(new Dimension(newWidth + 10, newHeight + 10));
                    start = m.getPoint();
                    repaint();
                } else {
                    newP = m.getLocationOnScreen();
                    setLocation(start.x + newP.x - oldP.x, start.y + newP.y - oldP.y);
                }
            }
        });
    }

    public void deselect() {
        isSelected = false;
        repaint();
    }

    private boolean isOnResizeArea(MouseEvent m) {
        int buttonX = getWidth() - 10, buttonY = getHeight() - 10;
        return buttonX <= m.getX() && m.getX() <= buttonX + 10 && buttonY <= m.getY() && m.getY() <= buttonY + 10;
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
