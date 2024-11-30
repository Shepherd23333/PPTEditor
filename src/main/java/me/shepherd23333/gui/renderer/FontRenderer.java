package me.shepherd23333.gui.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * 字体ComboBox的渲染器
 */
public class FontRenderer implements ListCellRenderer<String> {
    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel l = new JLabel(value);
        l.setOpaque(true);
        l.setFont(new Font(value, Font.PLAIN, 15));
        if (isSelected)
            l.setBackground(list.getSelectionBackground());
        return l;
    }
}
