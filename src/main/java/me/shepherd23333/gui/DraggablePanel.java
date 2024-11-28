package me.shepherd23333.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class DraggablePanel extends JPanel {
    public DraggablePanel() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent m) {
                Container c = getParent();
                Point p = SwingUtilities.convertPoint(m.getComponent(), m.getPoint(), c);
                MouseEvent me = new MouseEvent(c, m.getID(), m.getWhen(), m.getModifiersEx(), p.x, p.y,
                        m.getClickCount(), m.isPopupTrigger());
                for (MouseListener l : c.getMouseListeners())
                    l.mousePressed(me);
            }

            @Override
            public void mouseReleased(MouseEvent m) {
                Container c = getParent();
                Point p = SwingUtilities.convertPoint(m.getComponent(), m.getPoint(), c);
                MouseEvent me = new MouseEvent(c, m.getID(), m.getWhen(), m.getModifiersEx(), p.x, p.y,
                        m.getClickCount(), m.isPopupTrigger());
                for (MouseListener l : c.getMouseListeners())
                    l.mouseReleased(me);
            }
        });
    }

    public abstract void select();
    public abstract void deselect();
}
