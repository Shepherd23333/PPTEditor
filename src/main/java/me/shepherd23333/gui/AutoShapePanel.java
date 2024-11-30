package me.shepherd23333.gui;

import org.apache.poi.xslf.usermodel.XSLFAutoShape;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * 基本图形组件
 */
public abstract class AutoShapePanel extends DraggablePanel {
    /**
     * 用于绘图的形状对象
     */
    Shape auto;
    /**
     * 是否填充
     */
    boolean isFilled;

    public AutoShapePanel(XSLFAutoShape r) {
        super(r);
        isFilled = r.getFillColor() != null;
    }

    protected boolean isOnResizeArea(MouseEvent m) {
        Rectangle r = auto.getBounds(), ra = new Rectangle(r.width, r.height, 10, 10);
        return ra.contains(m.getPoint());
    }

    @Override
    public void setDrawColor(Color c) {
        instance.setLineColor(c);
        repaint();
    }

    /**
     * 设置填充色
     */
    public void setFillColor(Color c) {
        instance.setFillColor(c);
        isFilled =c!=null;
        repaint();
    }

    @Override
    public void setLineWidth(double w) {
        instance.setLineWidth(w);
        repaint();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        //抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isFilled) { //填充图形
            g2d.setColor(instance.getFillColor());
            g2d.fill(auto);
        }
        double l = instance.getLineWidth();
        g2d.setStroke(new BasicStroke((float) l));  //设置线宽
        g2d.setColor(instance.getLineColor());
        g2d.draw(auto); //绘制图形
        if (isSelected) {   //绘制选择框与拉伸点
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.setColor(Color.BLACK);
            Rectangle r = auto.getBounds();
            g2d.draw(r);
            g2d.drawOval(r.width, r.height, 10, 10);
        }
    }
}
