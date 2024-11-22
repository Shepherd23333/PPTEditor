package me.shepherd23333.gui;

import org.apache.poi.xslf.usermodel.XSLFAutoShape;

import java.awt.geom.Rectangle2D;

public class RectanglePanel extends AutoPanel {
    public RectanglePanel(XSLFAutoShape r) {
        super(r);
        Rectangle2D re = r.getAnchor();
        double l = r.getLineWidth();
        auto = new Rectangle2D.Double(l / 2 + 1, l / 2 + 1, re.getWidth(), re.getHeight());
        setBounds((int) (re.getX() - l / 2), (int) (re.getY() - l / 2), (int) (re.getWidth() + l + 2), (int) (re.getHeight() + l + 2));
    }
}
