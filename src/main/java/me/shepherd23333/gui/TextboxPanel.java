package me.shepherd23333.gui;

import me.shepherd23333.Utils;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Iterator;

/**
 * 文本框组件
 */
public class TextboxPanel extends DraggablePanel {
    /**
     * 是否为新创建的文本框
     */
    boolean isNew;
    /**
     * 用于编辑、绘图的文本框对象
     */
    private JTextPane textPane;

    public TextboxPanel(XSLFTextBox tb) {
        super(tb);
        init(tb, false);
    }

    public TextboxPanel(XSLFTextBox tb, boolean in) {
        super(tb);
        init(tb, in);
    }

    private void init(XSLFTextBox tb, boolean in) {
        isNew = in;
        textPane = new JTextPane();
        Rectangle2D r = tb.getAnchor();
        textPane.setBounds(5, 5, (int) Math.max(r.getWidth(), 32), (int) Math.max(r.getHeight(), 32));
        setBounds((int) (r.getX() - 5), (int) (r.getY() - 5), textPane.getWidth() + 10, textPane.getHeight() + 10);
        setLayout(null);
        add(textPane);
        textPane.setEditable(true);
        //写入文本
        Iterator<XSLFTextParagraph> it = tb.getTextParagraphs().iterator();
        while (true) {
            XSLFTextParagraph tp = it.next();
            int a = Math.min(tp.getTextAlign().ordinal(), 3);
            for (XSLFTextRun tr : tp.getTextRuns()) {
                Color c = Color.BLACK;
                if (tr.getFontColor() instanceof PaintStyle.SolidPaint)
                    c = ((PaintStyle.SolidPaint) tr.getFontColor()).getSolidColor().getColor();
                insertText(tr.getRawText(), c, (int) (1.0 * tr.getFontSize()), tr.getFontFamily());
            }
            if (it.hasNext()) {
                Color c = Color.BLACK;
                if (tp.getBulletFontColor() instanceof PaintStyle.SolidPaint)
                    c = ((PaintStyle.SolidPaint) tp.getBulletFontColor()).getSolidColor().getColor();
                insertText("\n", c, (int) (1.0 * tp.getDefaultFontSize()), tp.getDefaultFontFamily(), a);
            } else
                break;
        }

        textPane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { //获得键盘输入焦点
                if (!isSelected) {
                    isSelected = true;
                    repaint();
                    Container c = getParent();
                    //获取当前鼠标位置
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    //将屏幕坐标变换的父容器的坐标系
                    SwingUtilities.convertPointFromScreen(p, c);
                    MouseEvent me = new MouseEvent(c, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, p.x, p.y, 1, false);
                    for (MouseListener l : c.getMouseListeners()) { //mon父容器的鼠标事件
                        l.mousePressed(me);
                        l.mouseReleased(me);
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {   //失去键盘输入焦点
                save();
                Rectangle r = getBounds();
                instance.setAnchor(new Rectangle(r.x + 5, r.y + 5, r.width - 10, r.height - 10));
                repaint();
            }
        });
    }

    public XSLFShape getShape() {
        return instance;
    }

    /**
     * 当前文本是否为空
     */
    public boolean isEmpty() {
        return !isNew && textPane.getText().isEmpty();
    }

    @Override
    public void select() {
        textPane.requestFocusInWindow();    //获取键盘焦点
        super.select();
    }

    @Override
    public void deselect() {
        //修改空文本框时阻断一次清除
        if (isNew && textPane.getText().isEmpty()) {
            isNew = false;
            return;
        }
        textPane.transferFocus();   //失去键盘焦点
        super.deselect();
    }

    /**
     * 设置文本字体
     * @param font 字体名称
     */
    public void setFont(String font) {
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setFontFamily(set, font);
        if (textPane.getSelectedText() == null)
            textPane.setParagraphAttributes(set, false);
        else
            textPane.setCharacterAttributes(set, false);
    }

    /**
     * 设置字体大小
     * @param size 单位为像素
     */
    public void setSize(int size) {
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setFontSize(set, size);
        if (textPane.getSelectedText() == null)
            textPane.setParagraphAttributes(set, false);
        else
            textPane.setCharacterAttributes(set, false);
    }

    /**
     * 设置文本颜色
     */
    public void setColor(Color color) {
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setForeground(set, color);
        if (textPane.getSelectedText() == null)
            textPane.setParagraphAttributes(set, false);
        else
            textPane.setCharacterAttributes(set, false);
    }

    /**
     * 设置段落对齐
     * @param align 0-3，左、中、右、两端
     */
    public void setAlign(int align) {
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setAlignment(set, align);
        textPane.setParagraphAttributes(set, false);
    }

    @Override
    protected boolean isOnMoveArea(Point p) {
        Rectangle r = getBounds(), in = new Rectangle(10, 10, r.width - 20, r.height - 20),
                out = new Rectangle(0, 0, r.width, r.height);
        return !in.contains(p) && out.contains(p);
    }

    protected boolean isOnResizeArea(MouseEvent m) {
        Rectangle r = textPane.getBounds(), ra = new Rectangle(r.width, r.height, 10, 10);
        return ra.contains(m.getPoint());
    }

    @Override
    protected void resizeObject(int width, int height) {
        textPane.setSize(width, height);
    }

    /**
     * 写入文本到文本框
     *
     * @param text  文本内容
     * @param color 文本颜色
     * @param size  字体大小
     * @param font  文本字体
     * @param align 段落对齐
     */
    private void insertText(String text, Color color, int size, String font, int align) {
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setForeground(set, color);
        StyleConstants.setFontSize(set, size);
        StyleConstants.setFontFamily(set, font);
        StyleConstants.setAlignment(set, align);
        doc.setParagraphAttributes(textPane.getText().length(), doc.getLength() - textPane.getText().length(),
                set, false);
        try {
            doc.insertString(doc.getLength(), text, set);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertText(String text, Color color, int size, String font) {
        insertText(text, color, size, font, 0);
    }

    /**
     * 保存到文本框形状
     */
    private void save() {
        ((XSLFTextBox) instance).clearText();

        //在String中，各段落用\r\n隔开
        Iterator<String> it = Arrays.stream(textPane.getText().split("\n")).iterator();
        int pos = 0;
        while (it.hasNext()) {
            String t = it.next();
            if (!it.hasNext())
                t = t.concat("\r");
            int l = t.length() - 1;
            StyledDocument doc = textPane.getStyledDocument();
            //新建文本段落对象
            XSLFTextParagraph p = ((XSLFTextBox) instance).addNewTextParagraph();
            //获取文本格式集
            AttributeSet s = doc.getCharacterElement(pos).getAttributes();
            //设置段落对齐
            p.setTextAlign(TextParagraph.TextAlign.values()[StyleConstants.getAlignment(s)]);
            //处理不同格式的文本串
            for (int i = 0; i < l; ) {
                s = doc.getCharacterElement(pos + i).getAttributes();
                int end = i + 1;
                for (; end < l && doc.getCharacterElement(end).getAttributes() == s; end++) ;
                String str = t.substring(i, end);

                XSLFTextRun tr = p.addNewTextRun();
                tr.setFontColor(StyleConstants.getForeground(s));
                tr.setFontSize((double) StyleConstants.getFontSize(s));
                tr.setFontFamily(StyleConstants.getFontFamily(s));
                tr.setText(str);

                i = end;
                if (t.charAt(i) == '\r')
                    break;
            }
            pos += l + 2;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isSelected) {   //绘制虚线选择框和拉伸点
            g2d.setColor(Color.BLACK);
            g2d.setStroke(Utils.dash);
            g2d.drawRect(5, 5, textPane.getWidth(), textPane.getHeight());
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawOval(getWidth() - 10, getHeight() - 10, 10, 10);
        }
    }
}
