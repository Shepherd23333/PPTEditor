package me.shepherd23333.gui;

import me.shepherd23333.Utils;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Iterator;

public class TextboxPanel extends DraggablePanel {
    private XSLFTextBox instance;
    boolean isSelected = false, isResizing = false, isNew;
    Point start, oldP, newP, c;
    private JTextPane textPane;
    private int insertPos;
    private String selectedText;

    public TextboxPanel(XSLFTextBox tb) {
        init(tb, false);
    }

    public TextboxPanel(XSLFTextBox tb, boolean in) {
        init(tb, in);
    }

    private void init(XSLFTextBox tb, boolean in) {
        instance = tb;
        isNew = in;
        textPane = new JTextPane();
        Rectangle2D r = tb.getAnchor();
        instance.setAnchor(new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), r.getHeight()));
        textPane.setBounds(5, 5, (int) Math.max(r.getWidth(), 32), (int) Math.max(r.getHeight(), 32));
        setBounds((int) (r.getX() - 5), (int) (r.getY() - 5), textPane.getWidth() + 10, textPane.getHeight() + 10);
        setLayout(null);
        add(textPane);
        textPane.setEditable(true);
        setOpaque(false);

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
            public void focusGained(FocusEvent e) {
                isSelected = true;
                repaint();
                Container c = getParent();
                Point p = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(p, c);
                MouseEvent me = new MouseEvent(c, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, p.x, p.y,
                        1, false);
                for (MouseListener l : c.getMouseListeners()) {
                    l.mousePressed(me);
                    l.mouseReleased(me);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                insertPos = textPane.getSelectionStart();
                selectedText = textPane.getSelectedText();
                save();
                Rectangle r = getBounds();
                instance.setAnchor(new Rectangle(r.x + 5, r.y + 5, r.width - 10, r.height - 10));
                repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent m) {
                if (isSelected && isOnResizeArea(m)) {
                    isResizing = true;
                    start = m.getPoint();
                } else if (isOnMoveArea(c = m.getPoint())) {
                    start = getLocation();
                    oldP = m.getLocationOnScreen();
                }
            }

            @Override
            public void mouseReleased(MouseEvent m) {
                isResizing = false;
                isSelected = true;
                Rectangle r = getBounds();
                instance.setAnchor(new Rectangle(r.x + 5, r.y + 5, r.width - 10, r.height - 10));
                repaint();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent m) {
                if (isResizing) {
                    int newWidth = Math.max(getWidth() - 10 + m.getX() - start.x, 32),
                            newHeight = Math.max(getHeight() - 10 + m.getY() - start.y, 32);
                    textPane.setSize(newWidth, newHeight);
                    setSize(newWidth + 10, newHeight + 10);
                    start = m.getPoint();
                    repaint();
                } else if (isOnMoveArea(c = m.getPoint())) {
                    newP = m.getLocationOnScreen();
                    setLocation(start.x + newP.x - oldP.x, start.y + newP.y - oldP.y);
                }
            }
        });
    }

    public boolean isEmpty() {
        return !isNew && textPane.getText().isEmpty();
    }

    public void select() {
        textPane.requestFocusInWindow();
        isSelected = true;
        repaint();
    }

    public void deselect() {
        if (isNew) {
            isNew = false;
            return;
        }
        textPane.transferFocus();
        isSelected = false;
        repaint();
    }

    public void setFont(String font) {
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setFontFamily(set, font);
        if (textPane.getSelectedText() == null)
            textPane.setParagraphAttributes(set, false);
        else
            textPane.setCharacterAttributes(set, false);
    }

    public void setSize(int size) {
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setFontSize(set, size);
        if (textPane.getSelectedText() == null)
            textPane.setParagraphAttributes(set, false);
        else
            textPane.setCharacterAttributes(set, false);
    }

    public void setColor(Color color) {
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setForeground(set, color);
        if (textPane.getSelectedText() == null)
            textPane.setParagraphAttributes(set, false);
        else
            textPane.setCharacterAttributes(set, false);
    }

    public void setAlign(int align) {
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setAlignment(set, align);
        textPane.setParagraphAttributes(set, false);
    }

    private boolean isOnMoveArea(Point p) {
        Rectangle r = getBounds(), in = new Rectangle(10, 10, r.width - 20, r.height - 20),
                out = new Rectangle(0, 0, r.width, r.height);
        return !in.contains(p) && out.contains(p);
    }

    private boolean isOnResizeArea(MouseEvent m) {
        Rectangle r = textPane.getBounds(), ra = new Rectangle(r.width, r.height, 10, 10);
        return ra.contains(m.getPoint());
    }
    public void insertText(String text, Color color, int size, String font, int align) {
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

    public void insertText(String text, Color color, int size, String font) {
        insertText(text, color, size, font, 0);
    }

    private void save() {
        instance.clearText();

        Iterator<String> it = Arrays.stream(textPane.getText().split("\n")).iterator();
        int pos = 0;
        while (it.hasNext()) {
            String t = it.next();
            if (!it.hasNext())
                t = t.concat("\r");
            int l = t.length() - 1;
            StyledDocument doc = textPane.getStyledDocument();

            XSLFTextParagraph p = instance.addNewTextParagraph();
            AttributeSet s = doc.getCharacterElement(pos).getAttributes();
            p.setTextAlign(TextParagraph.TextAlign.values()[StyleConstants.getAlignment(s)]);
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
        if (isSelected) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(Utils.dash);
            g2d.drawRect(5, 5, textPane.getWidth(), textPane.getHeight());
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawOval(getWidth() - 10, getHeight() - 10, 10, 10);
        }
    }
}
