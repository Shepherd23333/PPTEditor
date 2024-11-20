package me.shepherd23333.gui;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class Textbox extends JTextPane {
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
