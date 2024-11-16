package me.shepherd23333.gui;

import me.shepherd23333.file.PPTLoader;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class Base extends JFrame {
    private PPTLoader ppt;
    private int currentSlide = 0;
    private JPanel slidePanel, toolbar;
    private JLabel slideNumber;
    private JScrollPane thumbnailPanel;
    private boolean isOpened = false;
    private boolean isCopied = false;

    public Base() {
        setTitle("PPT Editor");
        setSize(1024, 576);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);

        JMenu file = new JMenu("File");

        JMenuItem create = new JMenuItem("New File");
        create.addActionListener(a -> {
            ppt = new PPTLoader();
            isOpened = true;
            isCopied = false;
            currentSlide = 0;
            showArea();
        });
        file.add(create);

        JMenuItem open = new JMenuItem("Open File");
        open.addActionListener(a -> open());
        file.add(open);

        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(a -> {
            if (!ppt.hasFile())
                save();
            try {
                ppt.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        file.add(save);

        JMenuItem saveAs = new JMenuItem("Save as..");
        saveAs.addActionListener(a -> {
            save();
            try {
                ppt.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        file.add(saveAs);

        JMenuItem close = new JMenuItem("Close");
        close.addActionListener(a -> {
            ppt = null;
            isOpened = false;
            getContentPane().removeAll();
            revalidate();
            repaint();
        });
        file.add(close);

        file.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                save.setEnabled(isOpened);
                saveAs.setEnabled(isOpened);
                close.setEnabled(isOpened);
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });
        bar.add(file);

        JMenu edit = new JMenu("Edit");

        JMenu adds = new JMenu("Add..");

        JMenuItem blank = new JMenuItem("PPT");
        blank.addActionListener(a -> {
            ppt.createSlide(currentSlide);
            showArea();
        });
        adds.add(blank);

        JMenuItem text = new JMenuItem("Textbox");
        adds.add(text);

        adds.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                blank.setEnabled(isOpened);
                text.setEnabled(isOpened);
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });
        edit.add(adds);

        bar.add(edit);

        setVisible(true);
    }

    private void open() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("PPT new(*.pptx)", "pptx"));
        int res = jfc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            try {
                ppt = new PPTLoader(f);
                currentSlide = 0;
                isOpened = true;
                isCopied = false;
                showArea();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void save() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("PPT new(*.pptx)", "pptx"));
        jfc.setSelectedFile(new File("Untitled-1"));
        int res = jfc.showDialog(this, "Save");
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            String p = f.getAbsolutePath();
            if (p.toLowerCase().endsWith(".pptx"))
                ppt.setFile(f);
            else
                ppt.setFile(new File(p + ".pptx"));
        }
    }

    private void toolBar() {
        toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton prev = new JButton("Previous");
        prev.addActionListener(a -> toPrevious());
        JButton next = new JButton("Next");
        next.addActionListener(a -> toNext());

        slideNumber = new JLabel("PPT " + (currentSlide + 1) + "/" + ppt.totalSlides);

        toolbar.add(prev);
        toolbar.add(next);
        toolbar.add(slideNumber);
    }

    private void thumbnailPanel() {
        JPanel tp = new JPanel();
        tp.setLayout(new GridLayout(Math.max(ppt.totalSlides + 1, 5), 1, 0, 10));

        JPopupMenu menuButton = getPopupMenu(), menuPanel = new JPopupMenu();

        JMenuItem cr = new JMenuItem("Create");
        cr.addActionListener(a -> {
            ppt.createSlide(currentSlide);
            showArea();
        });
        menuPanel.add(cr);

        Dimension pSize = new Dimension(150, 100);
        for (int i = 0; i < ppt.totalSlides; i++) {
            JButton tb = new JButton();
            //ImageIcon ti=new ImageIcon(ppts.get(i).getSlideMaster().get);
            //tb.setIcon(ti);
            tb.setPreferredSize(pSize);
            tb.setComponentPopupMenu(menuButton);
            int si = i;
            tb.addActionListener(a -> to(si));
            tb.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        tb.doClick();
                        menuButton.show(tb, e.getX(), e.getY());
                    }
                }
            });
            tp.add(tb);
        }
        thumbnailPanel = new JScrollPane(tp);
        thumbnailPanel.setPreferredSize(new Dimension(200, 0));
        thumbnailPanel.setComponentPopupMenu(menuPanel);
    }

    private JPopupMenu getPopupMenu() {
        JPopupMenu m = new JPopupMenu();

        JMenuItem cr = new JMenuItem("Create");
        cr.addActionListener(a -> {
            ppt.createSlide(currentSlide);
            showArea();
        });
        m.add(cr);

        JMenuItem co = new JMenuItem("Copy");
        co.addActionListener(a -> {
            ppt.copySlide(currentSlide);
            isCopied = true;
        });
        m.add(co);

        JMenuItem pa = new JMenuItem("Paste");
        pa.addActionListener(a -> {
            ppt.paste(currentSlide);
            showArea();
        });
        m.add(pa);

        m.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                pa.setEnabled(isCopied);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
        return m;
    }

    private void mainDisplay() {
        slidePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSlide(g, currentSlide);
            }
        };
        slidePanel.setBackground(Color.WHITE);
    }
    private void showArea() {
        getContentPane().removeAll();
        toolBar();
        thumbnailPanel();
        mainDisplay();
        add(toolbar, BorderLayout.SOUTH);
        JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, thumbnailPanel, slidePanel);
        jsp.setDividerLocation(200);
        add(jsp, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void drawSlide(Graphics g, int i) {
        String slideContent = "PPT " + (i + 1);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.drawString(slideContent, 100, 100);
    }

    private void to(int index) {
        if (0 <= index && index < ppt.totalSlides) {
            currentSlide = index;
            slidePanel.repaint();
            slideNumber.setText("PPT " + (currentSlide + 1) + "/" + ppt.totalSlides);
        }
    }

    private void toPrevious() {
        if (0 < currentSlide)
            to(currentSlide - 1);
    }

    private void toNext() {
        if (currentSlide < ppt.totalSlides)
            to(currentSlide + 1);
    }
}
