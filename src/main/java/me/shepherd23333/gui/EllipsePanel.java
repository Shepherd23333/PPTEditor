package me.shepherd23333.gui;

import org.apache.poi.xslf.usermodel.XSLFAutoShape;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class EllipsePanel extends AutoPanel {

    public EllipsePanel(XSLFAutoShape e) {
        super(e);
        Rectangle2D r = e.getAnchor();
        double l = e.getLineWidth();
        auto = new Ellipse2D.Double(l / 2 + 1, l / 2 + 1, r.getWidth(), r.getHeight());
        setBounds((int) (r.getX() - l / 2), (int) (r.getY() - l / 2), (int) (r.getWidth() + l + 2), (int) (r.getHeight() + l + 2));
    }
}
