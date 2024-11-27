package me.shepherd23333.gui;

import org.apache.poi.xslf.usermodel.XSLFAutoShape;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class EllipsePanel extends AutoShapePanel {

    public EllipsePanel(XSLFAutoShape e) {
        super(e);
        Rectangle2D r = e.getAnchor();
        double l = e.getLineWidth();
        auto = new Ellipse2D.Double(l / 2 + 5, l / 2 + 5, r.getWidth(), r.getHeight());
        setBounds((int) (r.getX() - l / 2 - 5), (int) (r.getY() - l / 2 - 5),
                (int) (r.getWidth() + l + 10), (int) (r.getHeight() + l + 10));
    }

    @Override
    protected void resizeShape(int width, int height) {
        Ellipse2D.Double oldE = (Ellipse2D.Double) auto;
        auto = new Ellipse2D.Double(oldE.x, oldE.y, width, height);
    }

    @Override
    public boolean contains(Point p) {
        if (p == null)
            return false;
        Ellipse2D.Double e = (Ellipse2D.Double) auto,
                in = new Ellipse2D.Double(e.x + 5, e.y + 5, e.width - 10, e.height - 10),
                out = new Ellipse2D.Double(e.x - 5, e.y - 5, e.width + 10, e.height + 10);
        return (isFilled || !in.contains(p)) && out.contains(p);
    }
}
