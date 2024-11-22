package me.shepherd23333.gui;

import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class Textbox extends JTextPane {
    private XSLFTextBox instance;

    public Textbox(XSLFTextBox tb) {
        instance = tb;
        for (XSLFTextParagraph tp : tb.getTextParagraphs()) {
            int a = Math.min(tp.getTextAlign().ordinal(), 3);
            for (XSLFTextRun tr : tp.getTextRuns()) {
                Color c = Color.BLACK;
                if (tr.getFontColor() instanceof PaintStyle.SolidPaint)
                    c = ((PaintStyle.SolidPaint) tr.getFontColor()).getSolidColor().getColor();
                insertText(tr.getRawText(), c, (int) (1.0 * tr.getFontSize()), tr.getFontFamily());
            }
            Color c = Color.BLACK;
            if (tp.getBulletFontColor() instanceof PaintStyle.SolidPaint)
                c = ((PaintStyle.SolidPaint) tp.getBulletFontColor()).getSolidColor().getColor();
            insertText("\n", c, (int) (1.0 * tp.getDefaultFontSize()), tp.getDefaultFontFamily(), a);
        }
        setOpaque(false);
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
}
