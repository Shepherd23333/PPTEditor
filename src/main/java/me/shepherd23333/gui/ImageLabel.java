package me.shepherd23333.gui;

import me.shepherd23333.Utils;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class ImageLabel extends JLabel {
    private XSLFPictureShape instance;
    private int startX, startY;
    private boolean isDragging = false;

    public ImageLabel(XSLFPictureShape pic) throws IOException {
        instance = pic;
        Image b = Utils.getImage(pic);
        setIcon(new ImageIcon(b));
        setSize(b.getWidth(null) + 5, b.getHeight(null) + 5);
        setOpaque(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isOnResizeArea(e)) {
                    isDragging = true;
                    startX = e.getX();
                    startY = e.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
                instance.setAnchor(getBounds());
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent m) {
                if (isDragging) {
                    int newWidth = Math.max(getWidth() + m.getX() - startX, 20), newHeight = Math.max(getHeight() + m.getY() - startY, 20);
                    try {
                        Image newImage = Utils.getImage(instance).getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(newImage));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    setSize(new Dimension(newWidth, newHeight));
                    startX = m.getX();
                    startY = m.getY();
                    repaint();
                }
            }
        });
    }

    private boolean isOnResizeArea(MouseEvent e) {
        int buttonX = getWidth() - 10, buttonY = getHeight() - 10;
        return buttonX <= e.getX() && e.getX() <= buttonX + 10 && buttonY <= e.getY() && e.getY() <= buttonY + 10;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.drawOval(getWidth() - 5, getHeight() - 5, 10, 10);
    }
}
