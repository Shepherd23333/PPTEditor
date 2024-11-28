package me.shepherd23333.gui;

import me.shepherd23333.file.PPTLoader;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class Base extends JFrame {
    private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private PPTLoader ppt;
    private final String[] shapes = new String[]{"Line", "Textbox", "Rectangle", "Ellipse"};
    private int currentSlide = 0;
    private XSLFSlide slide;
    private JPanel statusBar;
    private DraggablePanel selected;
    private DrawAction action = new DrawAction();
    private JPanel editBar, insertBar, drawPanel;
    private JButton edit, insert;
    private JLayeredPane slidePanel;
    private JLabel slideNumber;
    private JScrollPane thumbnailPanel;
    private boolean isOpened = false;
    private boolean isCopied = false;

    public Base() {
        setTitle("PPT Editor");
        setSize(1440, 810);
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
            slide = ppt.getSlide(0);
            showArea();
        });
        file.add(create);

        JMenuItem open = new JMenuItem("Open File");
        open.addActionListener(a -> openPPT());
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
            add(editBar, BorderLayout.NORTH);
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

        editBar = new JPanel();
        edit = new JButton("Edit");

        JComboBox<String> textFont = new JComboBox<>(ge.getAvailableFontFamilyNames());
        textFont.addActionListener(a -> {
            TextboxPanel tb = (TextboxPanel) selected;
            tb.setFont((String) textFont.getSelectedItem());
        });
        editBar.add(textFont);

        JComboBox<Integer> textSize = new JComboBox<>(new Integer[]{5, 10, 15, 20});
        textSize.addActionListener(a -> {
            TextboxPanel tb = (TextboxPanel) selected;
            tb.setSize((int) textSize.getSelectedItem());
        });
        textSize.setEditable(true);
        editBar.add(textSize);

        JComboBox<Color> textColor = new JComboBox<>(new Color[]{Color.BLACK, Color.RED, Color.GREEN, Color.BLUE});
        textColor.addActionListener(a -> {
            TextboxPanel tb = (TextboxPanel) selected;
            tb.setColor((Color) textColor.getSelectedItem());
        });
        editBar.add(textColor);

        JComboBox<Integer> textAlign = new JComboBox<>(new Integer[]{0, 1, 2, 3});
        textAlign.addActionListener(a -> {
            TextboxPanel tb = (TextboxPanel) selected;
            tb.setAlign((int) textAlign.getSelectedItem());
        });
        editBar.add(textAlign);

        JComboBox<Color> drawColor = new JComboBox<>(new Color[]{Color.BLACK, Color.RED, Color.BLUE});
        drawColor.addActionListener(a -> {
            AutoShapePanel p = (AutoShapePanel) selected;
            p.setDrawColor((Color) drawColor.getSelectedItem());
        });
        editBar.add(drawColor);

        JComboBox<Color> fillColor = new JComboBox<>(new Color[]{Color.BLACK, Color.RED, Color.BLUE});
        fillColor.addActionListener(a -> {
            AutoShapePanel p = (AutoShapePanel) selected;
            p.setFillColor((Color) fillColor.getSelectedItem());
        });
        editBar.add(fillColor);

        edit.addActionListener(a -> {
            textFont.setEnabled(selected instanceof TextboxPanel);
            textSize.setEnabled(selected instanceof TextboxPanel);
            textColor.setEnabled(selected instanceof TextboxPanel);
            textAlign.setEnabled(selected instanceof TextboxPanel);
            drawColor.setEnabled(selected instanceof AutoShapePanel || selected instanceof LinePanel);
            fillColor.setEnabled(selected instanceof AutoShapePanel);
            setToolBar(editBar);
        });
        bar.add(edit);

        insertBar = new JPanel();
        insert = new JButton("Insert");

        JButton blank = new JButton("PPT");
        blank.addActionListener(a -> {
            ppt.createSlide(currentSlide);
            insert.doClick(1);
        });
        insertBar.add(blank);

        JButton picture = new JButton("Picture");
        picture.addActionListener(a -> insertPicture());
        insertBar.add(picture);

        drawPanel = new JPanel();
        drawPanel.setOpaque(false);
        drawPanel.addMouseListener(action);
        drawPanel.addMouseMotionListener(action);
        for (String name : shapes) {
            JButton b = new JButton(name);
            b.addActionListener(action);
            insertBar.add(b);
        }

        insert.addActionListener(a -> {
            for (Component c : insertBar.getComponents())
                c.setEnabled(isOpened);
            setToolBar(insertBar);
        });
        bar.add(insert);

        edit.doClick();
        setVisible(true);
    }

    private Component getToolBar() {
        BorderLayout layout = (BorderLayout) getContentPane().getLayout();
        return layout.getLayoutComponent(BorderLayout.NORTH);
    }

    private void setToolBar(JPanel toolbar) {
        Component c = getToolBar();
        if (c != null)
            remove(c);
        add(toolbar, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    private void openPPT() {
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
                slide = ppt.getSlide(0);
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

    private void insertPicture() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("JPEG(*.jpg)", "jpg"));
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("PNG(*.png)", "png"));
        int res = jfc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            try {
                byte[] picture = IOUtils.toByteArray(new FileInputStream(f));
                String fileName = f.getName();
                String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
                XSLFPictureData data = ppt.add(picture, ext);
                XSLFSlide s = ppt.getSlide(currentSlide);
                JPanel pic = new ImagePanel(s.createPicture(data), 100, 100);
                drawSlide(currentSlide);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void statusBar() {
        statusBar = new JPanel();
        statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton prev = new JButton("Previous");
        prev.addActionListener(a -> toPrevious());
        JButton next = new JButton("Next");
        next.addActionListener(a -> toNext());

        slideNumber = new JLabel("PPT " + (currentSlide + 1) + "/" + ppt.totalSlides);

        statusBar.add(prev);
        statusBar.add(next);
        statusBar.add(slideNumber);
    }

    private void thumbnailPanel() {
        JPanel tp = new JPanel();
        tp.setBorder(new EmptyBorder(10, 10, 10, 10));
        tp.setLayout(new GridLayout(Math.max(ppt.totalSlides, 5), 1, 0, 10));

        JPopupMenu menuButton = getPopupMenu(), menuPanel = new JPopupMenu();

        JMenuItem cr = new JMenuItem("Create");
        cr.addActionListener(a -> {
            ppt.createSlide(currentSlide);
            showArea();
        });
        menuPanel.add(cr);

        for (int i = 0; i < ppt.totalSlides; i++) {
            Thumbnail tb = new Thumbnail();
            tb.setComponentPopupMenu(menuButton);
            int si = i;
            XSLFSlide s = ppt.getSlide(si);
            Dimension d = ppt.getSize();

            BufferedImage img = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setPaint(Color.WHITE);
            g.fill(new Rectangle2D.Float(0, 0, d.width, d.height));
            s.draw(g);

            ImageIcon ti = new ImageIcon(img.getScaledInstance(150, 100, Image.SCALE_SMOOTH));
            tb.setIcon(ti);

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

    private void showArea() {
        getContentPane().removeAll();
        setToolBar(editBar);
        statusBar();
        thumbnailPanel();
        slidePanel = new JLayeredPane();
        slidePanel.setBackground(Color.WHITE);
        slidePanel.setOpaque(true);
        drawSlide(currentSlide);
        slidePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selected = null;
                label:
                for (int i = slidePanel.getComponentCount(); i >= 0; i--)
                    for (Component c : slidePanel.getComponentsInLayer(i)) {
                        Point p = SwingUtilities.convertPoint(slidePanel, e.getPoint(), c);
                        if (c.contains(p)) {
                            selected = (DraggablePanel) c;
                            break label;
                        }
                    }
                for (Component c : slidePanel.getComponents())
                    if (c != selected && c instanceof DraggablePanel) {
                        if (c instanceof TextboxPanel && ((TextboxPanel) c).isEmpty())
                            slidePanel.remove(c);
                        else
                            ((DraggablePanel) c).deselect();
                    }
                slidePanel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selected != null)
                    selected.select();
                (getToolBar() == editBar ? edit : insert).doClick();
            }
        });
        add(statusBar, BorderLayout.SOUTH);
        JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, thumbnailPanel, slidePanel);
        jsp.setDividerLocation(200);
        add(jsp, BorderLayout.CENTER);
        revalidate();
        drawPanel.setBounds(0, 0, slidePanel.getWidth(), slidePanel.getHeight());
        slidePanel.add(drawPanel, -1, 0);
        action.setGraphics(drawPanel.getGraphics());
        repaint();
    }

    private void drawSlide(int index) {
        slidePanel.removeAll();

        currentSlide = index;
        slide = ppt.getSlide(index);
        slidePanel.setBackground(slide.getBackground().getFillColor());
        List<XSLFShape> shapes = slide.getShapes();
        for (int i = 0; i < shapes.size(); i++) {
            XSLFShape shape = shapes.get(i);
            try {
                if (shape instanceof XSLFPictureShape) {
                    JPanel pic = new ImagePanel((XSLFPictureShape) shape);
                    slidePanel.add(pic, i, 0);
                } else if (shape instanceof XSLFConnectorShape) {
                    JPanel line = new LinePanel((XSLFConnectorShape) shape);
                    slidePanel.add(line, i, 0);
                } else if (shape instanceof XSLFTextBox) {
                    TextboxPanel text = new TextboxPanel((XSLFTextBox) shape);
                    slidePanel.add(text, i, 0);
                } else if (shape instanceof XSLFAutoShape) {
                    JPanel p = switch (((XSLFAutoShape) shape).getShapeType()) {
                        case RECT -> new RectanglePanel((XSLFAutoShape) shape);
                        case ELLIPSE -> new EllipsePanel((XSLFAutoShape) shape);
                        default -> new JPanel();
                    };
                    slidePanel.add(p, i, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        slidePanel.revalidate();
        slidePanel.repaint();
    }

    private void to(int index) {
        if (0 <= index && index < ppt.totalSlides) {
            drawSlide(index);
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

    private class DrawAction implements MouseListener, MouseMotionListener, ActionListener {
        private Graphics g;
        private String type;
        private int startX;
        private int startY;
        private int curX;
        private int curY;
        private boolean isDrawing = false;

        public void setGraphics(Graphics g) {
            this.g = g;
        }

        @Override
        public void actionPerformed(ActionEvent a) {
            type = a.getActionCommand();
            isDrawing = true;
            slidePanel.setLayer(drawPanel, 114514);
        }

        @Override
        public void mousePressed(MouseEvent m) {
            if (isDrawing) {
                startX = m.getX();
                startY = m.getY();
                g.setColor(Color.BLACK);
            }
            Point p = SwingUtilities.convertPoint(m.getComponent(), m.getPoint(), slidePanel);
            MouseEvent me = new MouseEvent(slidePanel, m.getID(), m.getWhen(), m.getModifiersEx(), p.x, p.y,
                    m.getClickCount(), m.isPopupTrigger());
            for (MouseListener l : slidePanel.getMouseListeners())
                l.mousePressed(me);
        }

        @Override
        public void mouseReleased(MouseEvent m) {
            if (isDrawing) {
                slidePanel.setLayer(drawPanel, -1);
                isDrawing = false;
                int endX = m.getX(), endY = m.getY();
                int width = Math.max(Math.abs(endX - startX), 32), height = Math.max(Math.abs(endY - startY), 32);
                DraggablePanel p;
                if (type.equals(shapes[0])) {
                    XSLFConnectorShape l = slide.createConnector();
                    l.setAnchor(new Rectangle(startX, startY, width, height));
                    l.setFlipHorizontal(endX < startX);
                    l.setFlipVertical(endY < startY);
                    p = new LinePanel(l);
                } else if (type.equals(shapes[1])) {
                    XSLFTextBox tb = slide.createTextBox();
                    tb.setAnchor(new Rectangle(Math.min(startX, endX), Math.min(startY, endY), width, height));
                    p = new TextboxPanel(tb, true);
                } else {
                    XSLFAutoShape a = slide.createAutoShape();
                    a.setAnchor(new Rectangle(Math.min(startX, endX), Math.min(startY, endY), width, height));
                    a.setLineColor(Color.BLACK);
                    a.setLineWidth(1.0);
                    if (type.equals(shapes[2])) {
                        a.setShapeType(ShapeType.RECT);
                        p = new RectanglePanel(a);
                    } else {
                        a.setShapeType(ShapeType.ELLIPSE);
                        p = new EllipsePanel(a);
                    }
                }
                slidePanel.add(p, slide.getShapes().size() - 1, 0);
                slidePanel.revalidate();
                slidePanel.repaint();
                if (selected != null)
                    selected.deselect();
                selected = p;
                p.select();
            }
            Point p = SwingUtilities.convertPoint(m.getComponent(), m.getPoint(), slidePanel);
            MouseEvent me = new MouseEvent(slidePanel, m.getID(), m.getWhen(), m.getModifiersEx(), p.x, p.y,
                    m.getClickCount(), m.isPopupTrigger());
            for (MouseListener l : slidePanel.getMouseListeners())
                l.mouseReleased(me);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int x = Math.min(startX, curX), y = Math.min(startY, curY), w = Math.abs(curX - startX), h = Math.abs(curY - startY);
            g.setColor(Color.WHITE);
            if (type.equals(shapes[0])) {
                g.drawLine(startX, startY, curX, curY);
                curX = e.getX();
                curY = e.getY();
                g.setColor(Color.BLACK);
                g.drawLine(startX, startY, curX, curY);
            } else {
                g.fillRect(x - 5, y - 5, w + 10, h + 10);
                curX = e.getX();
                curY = e.getY();
                x = Math.min(startX, curX);
                y = Math.min(startY, curY);
                w = Math.abs(curX - startX);
                h = Math.abs(curY - startY);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, w, h);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }
}
