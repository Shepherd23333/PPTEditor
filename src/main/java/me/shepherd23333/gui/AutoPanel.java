package me.shepherd23333.gui;

import org.apache.poi.xslf.usermodel.XSLFAutoShape;

import javax.swing.*;
import java.awt.*;

public class AutoPanel extends JPanel {
    XSLFAutoShape instance;
    Shape auto;
    boolean isFilled;

    public AutoPanel(XSLFAutoShape r) {
        instance = r;
        isFilled = r.getFillColor() != null;
        setOpaque(false);
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
        g2d.setStroke(new BasicStroke((float) instance.getLineWidth()));
        g2d.setColor(instance.getLineColor());
        g2d.draw(auto);
    }
}
