package me.shepherd23333.gui;

import org.apache.poi.xslf.usermodel.XSLFConnectorShape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * 直线组件
 */
public class LinePanel extends DraggablePanel {
    /**
     * 用于绘图的直线
     */
    Line2D.Double line;

    public LinePanel(XSLFConnectorShape l) {
        super(l);
        Rectangle2D r = l.getAnchor();
        line = new Line2D.Double(5, 5, r.getWidth() + 5, r.getHeight() + 5);
        setBounds(new Rectangle((int) (r.getX() - 5), (int) (r.getY() - 5), (int) (r.getWidth() + 10), (int) (r.getHeight() + 10)));
    }

    protected boolean isOnResizeArea(MouseEvent m) {
        Rectangle2D.Double ra = new Rectangle2D.Double(line.x2 - 5, line.y2 - 5, 10, 10);
        return ra.contains(m.getPoint());
    }

    @Override
    public void setDrawColor(Color c) {
        instance.setLineColor(c);
        repaint();
    }

    @Override
    public void setLineWidth(double w) {
        instance.setLineWidth(w);
        repaint();
    }

    @Override
    protected void resizeObject(int width, int height) {
        line.setLine(5, 5, width, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke((float) instance.getLineWidth()));
        g2d.setColor(instance.getLineColor());
        g2d.draw(line);
        if (isSelected) {   //绘制拉伸点
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.setColor(Color.BLACK);
            g2d.drawOval((int) (line.x2 - 5), (int) (line.y2 - 5), 10, 10);
        }
    }

    /**
     * 在垂直于直线方向上左右各保留5像素的区域
     * @param p     the point
     */
    @Override
    public boolean contains(Point p) {
        boolean res = line.ptLineDist(p) <= 5;
        Point2D.Double m = new Point2D.Double((line.x1 + line.x2) * 0.5, (line.y1 + line.y2) * 0.5);
        return res && 2 * p.distance(m) <= Math.sqrt(100 + Math.pow(line.x1 - line.x2, 2) + Math.pow(line.y1 - line.y2, 2));
    }
}
