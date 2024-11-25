package me.shepherd23333.gui;

import org.apache.poi.xslf.usermodel.XSLFConnectorShape;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class LinePanel extends JPanel {
    XSLFConnectorShape instance;
    Line2D.Double line;

    public LinePanel(XSLFConnectorShape l) {
        instance = l;
        Rectangle2D r = l.getAnchor();
        line = new Line2D.Double(0, 0, r.getWidth(), r.getHeight());
        setBounds(r.getBounds());
        setOpaque(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke((float) instance.getLineWidth()));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(instance.getLineColor());
        g2d.draw(line);
    }

    @Override
    public boolean contains(Point p) {
        boolean res = line.ptLineDist(p) <= 5;
        Point2D.Double m = new Point2D.Double((line.x1 + line.x2) * 0.5, (line.y1 + line.y2) * 0.5);
        return res && 2 * p.distance(m) <= Math.sqrt(100 + Math.pow(line.x1 - line.x2, 2) + Math.pow(line.y1 - line.y2, 2));
    }
}
