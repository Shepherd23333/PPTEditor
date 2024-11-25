package me.shepherd23333.gui;

import org.apache.poi.xslf.usermodel.XSLFAutoShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class AutoPanel extends JPanel {
    XSLFAutoShape instance;
    Shape auto;
    boolean isFilled, isSelected = false, isResizing = false;
    Point start, oldP, newP, c;

    public AutoPanel(XSLFAutoShape r) {
        instance = r;
        isFilled = r.getFillColor() != null;
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent m) {
                if (isSelected && isOnResizeArea(m)) {
                    isResizing = true;
                    start = m.getPoint();
                } else if (contains(m.getPoint())) {
                    start = getLocation();
                    c = m.getPoint();
                    oldP = m.getLocationOnScreen();
                }
            }

            @Override
            public void mouseReleased(MouseEvent m) {
                isResizing = false;
                isSelected = contains(c);
                Rectangle r = getBounds();
                instance.setAnchor(new Rectangle(r.x - 5, r.y - 5, r.width - 10, r.height - 10));
                repaint();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent m) {
                if (isResizing) {
                    int newWidth = Math.max(getWidth() - 10 + m.getX() - start.x, 32), newHeight = Math.max(getHeight() - 10 + m.getY() - start.y, 32);
                    resizeShape(newWidth, newHeight);
                    setSize(new Dimension(newWidth + 10, newHeight + 10));
                    start = m.getPoint();
                    repaint();
                } else if (contains(c)) {
                    newP = m.getLocationOnScreen();
                    setLocation(start.x + newP.x - oldP.x, start.y + newP.y - oldP.y);
                }
            }
        });
    }

    protected abstract void resizeShape(int width, int height);

    private boolean isOnResizeArea(MouseEvent m) {
        Rectangle r = auto.getBounds(), ra = new Rectangle(r.width, r.height, 10, 10);
        return ra.contains(m.getPoint());
    }

    public void deselect() {
        isSelected = false;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isFilled) {
            g2d.setColor(instance.getFillColor());
            g2d.fill(auto);
        }
        double l = instance.getLineWidth();
        g2d.setStroke(new BasicStroke((float) l));
        g2d.setColor(instance.getLineColor());
        g2d.draw(auto);
        if (isSelected) {
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.setColor(Color.BLACK);
            Rectangle r = auto.getBounds();
            g2d.draw(r);
            g2d.drawOval(r.width, r.height, 10, 10);
        }
    }

    @Override
    public boolean contains(Point p) {
        return auto.contains(p);
    }
}
