package me.shepherd23333.gui;

import org.apache.poi.xslf.usermodel.XMLSlideShow;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;

public class Base extends JFrame {
    Viewer v;

    public Base() {
        setTitle("PPT Editor");
        setSize(1024, 576);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JMenuBar jmb = new JMenuBar();
        setJMenuBar(jmb);
        JMenu jm = new JMenu("File");
        jmb.add(jm);
        JMenuItem jmi = new JMenuItem("Open");
        jmi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.setAcceptAllFileFilterUsed(false);
                //jfc.addChoosableFileFilter(new FileNameExtensionFilter("PPT old(*.ppt)","ppt"));
                jfc.addChoosableFileFilter(new FileNameExtensionFilter("PPT new(*.pptx)", "pptx"));
                int res = jfc.showOpenDialog(null);
                if (res == JFileChooser.APPROVE_OPTION) {
                    File f = jfc.getSelectedFile();
                    try {
                        XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(f));
                        v = new Viewer(ppt);
                        add(v.toolBar(), BorderLayout.SOUTH);
                        JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, v.thumbnailPanel(), v.mainDisplay());
                        jsp.setDividerLocation(200);
                        add(jsp, BorderLayout.CENTER);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        jm.add(jmi);
        setVisible(true);
    }

}
