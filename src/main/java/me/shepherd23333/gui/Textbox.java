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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Iterator;

public class Textbox extends JTextPane {
    private XSLFTextBox instance;
    boolean isSelected = false;

    public Textbox(XSLFTextBox tb) {
        Rectangle2D r = tb.getAnchor();
        init(tb, (int) r.getX(), (int) r.getY());
    }

    public Textbox(XSLFTextBox tb, int x, int y) {
        init(tb, x, y);
    }

    private void init(XSLFTextBox tb, int x, int y) {
        instance = tb;
        Rectangle2D r = tb.getAnchor();
        setBounds(x, y, (int) Math.max(r.getWidth(), 32), (int) Math.max(r.getHeight(), 32));
        setEditable(true);
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

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isSelected = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                save();
                instance.setAnchor(getBounds());
                isSelected = false;
                repaint();
            }
        });
    }
    public void insertText(String text, Color color, int size, String font, int align) {
        StyledDocument doc = getStyledDocument();
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setForeground(set, color);
        StyleConstants.setFontSize(set, size);
        StyleConstants.setFontFamily(set, font);
        StyleConstants.setAlignment(set, align);
        doc.setParagraphAttributes(getText().length(), doc.getLength() - getText().length(), set, false);
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

        Iterator<String> it = Arrays.stream(getText().split("\n")).iterator();
        int pos = 0;
        while (it.hasNext()) {
            String t = it.next();
            if (!it.hasNext())
                t = t.concat("\r");
            int l = t.length() - 1;
            StyledDocument doc = getStyledDocument();

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
        if (isSelected) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(Utils.dash);
            g2d.drawRect(0, 0, getWidth(), getHeight());
        }
    }
}
