package me.shepherd23333.gui;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Viewer {
    private List<XSLFSlide> ppts;
    private int currentSlide = 0;
    private int totalSlides;
    private JPanel slidePanel;
    private JLabel slideNumber;

    public Viewer(XMLSlideShow f) {
        ppts = f.getSlides();
        totalSlides = ppts.size();
    }

    public JPanel toolBar() {
        JPanel res = new JPanel();
        res.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton prev = new JButton("Previous");
        prev.addActionListener(e -> toPrevious());
        JButton next = new JButton("Next");
        next.addActionListener(e -> toNext());

        slideNumber = new JLabel("PPT " + (currentSlide + 1) + "/" + totalSlides);

        res.add(prev);
        res.add(next);
        res.add(slideNumber);
        return res;
    }

    public JScrollPane thumbnailPanel() {
        JPanel tp = new JPanel();
        tp.setLayout(new GridLayout(totalSlides, 1));

        Dimension size = new Dimension(150, 100);
        for (int i = 0; i < totalSlides; i++) {
            JButton tb = new JButton();
            //ImageIcon ti=new ImageIcon(ppts.get(i).getSlideMaster().get);
            //tb.setIcon(ti);
            tb.setPreferredSize(size);
            int si = i;
            tb.addActionListener(e -> to(si));
            tp.add(tb);
        }
        JScrollPane sp = new JScrollPane(tp);
        sp.setPreferredSize(new Dimension(200, 0));
        return sp;
    }

    public JPanel mainDisplay() {
        slidePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSlide(g, currentSlide);
            }
        };
        slidePanel.setBackground(Color.WHITE);
        return slidePanel;
    }

    private void drawSlide(Graphics g, int i) {
        String slideContent = "PPT " + (i + 1);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.drawString(slideContent, 100, 100);
    }

    private void to(int index) {
        if (0 <= index && index < totalSlides) {
            currentSlide = index;
            slidePanel.repaint();
            slideNumber.setText("PPT " + (currentSlide + 1) + "/" + totalSlides);
        }
    }

    private void toPrevious() {
        if (0 < currentSlide)
            to(currentSlide - 1);
    }

    private void toNext() {
        if (currentSlide < totalSlides)
            to(currentSlide + 1);
    }
}
