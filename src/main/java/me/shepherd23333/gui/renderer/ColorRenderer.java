package me.shepherd23333.gui.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * 颜色ComboBox的渲染器
 */
public class ColorRenderer implements ListCellRenderer<Color> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Color> list, Color value, int index, boolean isSelected, boolean cellHasFocus) {
        Color bg = list.getSelectionBackground();
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                //抗锯齿
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                if (isSelected) {
                    g2d.setColor(bg);
                    g2d.fillRect(0, 0, w, h);
                }
                if (value != null) {
                    g2d.setColor(value);
                    g2d.fillRect(2, 2, w - 4, h - 4);
                } else {
                    g2d.setColor(Color.black);
                    g2d.drawLine(2, 2, w - 2, h - 2);
                }
            }
        };
    }
}
