package me.shepherd23333.gui;

import org.apache.poi.xslf.usermodel.XSLFAutoShape;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class RectanglePanel extends AutoShapePanel {
    public RectanglePanel(XSLFAutoShape r) {
        super(r);
        Rectangle2D re = r.getAnchor();
        double l = r.getLineWidth();
        auto = new Rectangle2D.Double(l / 2 + 5, l / 2 + 5, re.getWidth(), re.getHeight());
        setBounds((int) (re.getX() - l / 2 - 5), (int) (re.getY() - l / 2 - 5), (int) (re.getWidth() + l + 10), (int) (re.getHeight() + l + 10));
    }

    @Override
    protected void resizeShape(int width, int height) {
        Rectangle2D.Double oldR = (Rectangle2D.Double) auto;
        auto = new Rectangle2D.Double(oldR.x, oldR.y, width, height);
    }

    @Override
    public boolean contains(Point p) {
        if (p == null)
            return false;
        Rectangle2D.Double r = (Rectangle2D.Double) auto, in = new Rectangle2D.Double(r.x + 5, r.y + 5, r.width - 10, r.height - 10),
                out = new Rectangle2D.Double(r.x - 5, r.y - 5, r.width + 10, r.height + 10);
        return (isFilled || !in.contains(p)) && out.contains(p);
    }
}
