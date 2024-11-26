package me.shepherd23333.gui;

import org.apache.poi.xslf.usermodel.XSLFConnectorShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class LinePanel extends JPanel {
    XSLFConnectorShape instance;
    Line2D.Double line;
    boolean isSelected = false, isResizing = false;
    Point start, oldP, newP, c;

    public LinePanel(XSLFConnectorShape l) {
        instance = l;
        Rectangle2D r = l.getAnchor();
        line = new Line2D.Double(5, 5, r.getWidth() + 5, r.getHeight() + 5);
        setBounds(new Rectangle((int) (r.getX() - 5), (int) (r.getY() - 5), (int) (r.getWidth() + 10), (int) (r.getHeight() + 10)));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent m) {
                if (isSelected && isOnResizeArea(m)) {
                    isResizing = true;
                    start = m.getPoint();
                } else if (contains(c = m.getPoint())) {
                    start = getLocation();
                    oldP = m.getLocationOnScreen();
                }
            }

            @Override
            public void mouseReleased(MouseEvent m) {
                isSelected = isResizing || contains(c);
                isResizing = false;
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
                    line.setLine(5, 5, newWidth, newHeight);
                    setSize(newWidth + 10, newHeight + 10);
                    start = m.getPoint();
                    repaint();
                } else if (contains(c)) {
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
        Rectangle2D.Double ra = new Rectangle2D.Double(line.x2 - 5, line.y2 - 5, 10, 10);
        return ra.contains(m.getPoint());
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke((float) instance.getLineWidth()));
        g2d.setColor(instance.getLineColor());
        g2d.draw(line);
        if (isSelected) {
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.setColor(Color.BLACK);
            g2d.drawOval((int) (line.x2 - 5), (int) (line.y2 - 5), 10, 10);
        }
    }

    @Override
    public boolean contains(Point p) {
        boolean res = line.ptLineDist(p) <= 5;
        Point2D.Double m = new Point2D.Double((line.x1 + line.x2) * 0.5, (line.y1 + line.y2) * 0.5);
        return res && 2 * p.distance(m) <= Math.sqrt(100 + Math.pow(line.x1 - line.x2, 2) + Math.pow(line.y1 - line.y2, 2));
    }
}
