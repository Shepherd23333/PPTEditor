package me.shepherd23333.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Thumbnail extends JButton {
    private double aspectRatio = 1.5;

    public Thumbnail() {
        setPreferredSize(new Dimension(150, 100));
        setOpaque(true);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                lock();
            }
        });
    }

    private void lock() {
        int width = getWidth(), height = getHeight();

        if (width / (double) height > aspectRatio)
            width = (int) (height * aspectRatio);
        else
            height = (int) (width / aspectRatio);

        setSize(new Dimension(width, height));
        revalidate();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        lock();
        //getIcon().paintIcon(this,g,0,0);
    }
}
