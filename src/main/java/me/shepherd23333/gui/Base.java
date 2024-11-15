package me.shepherd23333.gui;

import me.shepherd23333.file.PPTLoader;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class Base extends JFrame {
    PPTLoader ppt;
    Viewer v;

    public Base() {
        setTitle("PPT Editor");
        setSize(1024, 576);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);

        JMenu file = new JMenu("File");
        bar.add(file);

        JMenuItem create = new JMenuItem("New File");
        create.addActionListener(a -> {
            ppt = new PPTLoader();
            showArea();
        });
        file.add(create);

        JMenuItem open = new JMenuItem("Open File");
        open.addActionListener(a -> open());
        file.add(open);

        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(a -> {
            if (ppt.f == null)
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
            getContentPane().removeAll();
            revalidate();
            repaint();
        });
        file.add(close);

        JMenu edit = new JMenu("Edit");
        bar.add(edit);

        JMenu adds = new JMenu("Add..");
        edit.add(adds);

        JMenuItem blank = new JMenuItem("PPT");
        blank.addActionListener(a -> {
            ppt.ppt.createSlide();
            showArea();
        });
        adds.add(blank);

        JMenuItem text = new JMenuItem("Textbox");
        adds.add(text);

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
                ppt.f = f;
            else
                ppt.f = new File(p + ".pptx");
        }
    }

    private void showArea() {
        getContentPane().removeAll();
        v = new Viewer(ppt);
        add(v.toolBar(), BorderLayout.SOUTH);
        JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, v.thumbnailPanel(), v.mainDisplay());
        jsp.setDividerLocation(200);
        add(jsp, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
