package me.shepherd23333.gui.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * 对齐方式ComboBox的渲染器
 */
public class AlignRenderer implements ListCellRenderer<Integer> {
    String[] align = new String[]{"左对齐", "居中", "右对齐", "两端对齐"};

    @Override
    public Component getListCellRendererComponent(JList<? extends Integer> list, Integer value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel l = new JLabel(align[value]);
        l.setOpaque(true);
        if (isSelected)
            l.setBackground(list.getSelectionBackground());
        return l;
    }
}
