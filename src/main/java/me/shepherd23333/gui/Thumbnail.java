package me.shepherd23333.gui;

import me.shepherd23333.Utils;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * 缩略图按钮
 * <p>
 * 用于更换幻灯片
 */
public class Thumbnail extends JButton {
    /**
     * 关联的幻灯片对象
     */
    private final XSLFSlide instance;
    /**
     * 纵横比
     */
    private double aspectRatio = 1.5;

    public Thumbnail(XSLFSlide s) {
        instance=s;
        setPreferredSize(new Dimension(150, 100));
        setOpaque(true);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                lock();
                revalidate();
                repaint();
            }
        });
    }

    /**
     * 锁定纵横比
     */
    private void lock() {
        int width = getWidth(), height = getHeight();

        if (width / (double) height > aspectRatio)
            width = (int) (height * aspectRatio);
        else
            height = (int) (width / aspectRatio);

        setPreferredSize(new Dimension(width, height));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        lock();
        Image i = Utils.getImageFromSlide(instance).getScaledInstance(150, 100, Image.SCALE_SMOOTH);
        g.drawImage(i, 0,0,this);
    }
}
