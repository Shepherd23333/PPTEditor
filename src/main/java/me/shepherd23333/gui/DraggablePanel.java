package me.shepherd23333.gui;

import org.apache.poi.xslf.usermodel.XSLFSimpleShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 可移动、拉伸组件 <p>
 * 外观上表现为，在原形状周围增加5像素宽的区域，用于选择组件
 */
public abstract class DraggablePanel extends JPanel {
    /**
     * 关联的形状
     */
    XSLFSimpleShape instance;
    /**
     * 是否被选中
     */
    boolean isSelected = false;
    /**
     * 是否正在拉伸
     */
    boolean isResizing = false;
    /**
     * 形状变换时用到的点
     */
    Point start, oldP, newP, c;

    public DraggablePanel(XSLFSimpleShape s) {
        instance = s;
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent m) {
                if (isSelected && isOnResizeArea(m)) {  //开始拉伸
                    isResizing = true;
                    start = m.getPoint();
                } else if (isOnMoveArea(c = m.getPoint())) {    //开始移动
                    start = getLocation();
                    oldP = m.getLocationOnScreen();
                }
                Container c = getParent();
                //将坐标变换到父容器的坐标系
                Point p = SwingUtilities.convertPoint(m.getComponent(), m.getPoint(), c);
                MouseEvent me = new MouseEvent(c, m.getID(), m.getWhen(), m.getModifiersEx(), p.x, p.y,
                        m.getClickCount(), m.isPopupTrigger());
                for (MouseListener l : c.getMouseListeners())
                    l.mousePressed(me); //模拟父容器的鼠标按下事件
            }

            @Override
            public void mouseReleased(MouseEvent m) {
                //状态更新
                isSelected = isResizing || isOnMoveArea(c);
                isResizing = false;
                Rectangle r = getBounds();
                instance.setAnchor(new Rectangle(r.x + 5, r.y + 5, r.width - 10, r.height - 10));
                repaint();
                Container c = getParent();
                Point p = SwingUtilities.convertPoint(m.getComponent(), m.getPoint(), c);
                MouseEvent me = new MouseEvent(c, m.getID(), m.getWhen(), m.getModifiersEx(), p.x, p.y,
                        m.getClickCount(), m.isPopupTrigger());
                for (MouseListener l : c.getMouseListeners())
                    l.mouseReleased(me);    //模拟父容器的鼠标释放事件
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent m) {
                //动态更新形状
                if (isResizing) {
                    //计算新的宽度和高度
                    int newWidth = Math.max(getWidth() - 10 + m.getX() - start.x, 32),
                            newHeight = Math.max(getHeight() - 10 + m.getY() - start.y, 32);
                    resizeObject(newWidth, newHeight);
                    setSize(newWidth + 10, newHeight + 10);
                    start = m.getPoint();
                    repaint();
                } else if (isOnMoveArea(c)) {
                    newP = m.getLocationOnScreen();
                    setLocation(start.x + newP.x - oldP.x, start.y + newP.y - oldP.y);
                }
            }
        });
    }

    /**
     * 选中组件
     */
    public void select() {
        isSelected = true;
        repaint();
    }

    /**
     * 取消选中组件
     */
    public void deselect() {
        isSelected = false;
        repaint();
    }

    /**
     * 坐标是否在可移动形状的区域
     */
    protected boolean isOnMoveArea(Point p) {
        return contains(p);
    }

    /**
     * 鼠标事件是否发生在可拉伸形状的区域
     */
    protected abstract boolean isOnResizeArea(MouseEvent m);

    /**
     * 设置形状大小
     */
    protected abstract void resizeObject(int width, int height);

    /**
     * 设置边框色
     */
    public void setDrawColor(Color c) {
    }

    /**
     * 设置边框线宽
     */
    public void setLineWidth(double w) {
    }
}
