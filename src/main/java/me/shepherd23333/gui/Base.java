package me.shepherd23333.gui;

import me.shepherd23333.PPTLoader;
import me.shepherd23333.gui.renderer.AlignRenderer;
import me.shepherd23333.gui.renderer.ColorRenderer;
import me.shepherd23333.gui.renderer.FontRenderer;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * GUI主框架
 */
public class Base extends JFrame {
    /**
     * 本地显示环境
     */
    private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private PPTLoader ppt;
    /**
     * 简单图形的名称
     */
    private final String[] shapes = new String[]{"直线", "文本框", "矩形", "椭圆"};
    /**当前幻灯片页数
     */
    private int currentSlide = 0;
    /**当前幻灯片对象
     */
    private XSLFSlide slide;
    /**状态栏组件
     */
    private JPanel statusBar;
    /**当前选中组件
     */
    private DraggablePanel selected;
    private DrawAction action = new DrawAction();
    /**
     * 编辑栏组件
     */
    private JPanel editBar;
    /**
     * 插入栏组件
     */
    private JPanel insertBar;
    /**
     * 绘图组件，即画板
     */
    private JPanel drawPanel;
    /**
     * 编辑按钮
     */
    private JButton edit;
    /**
     * 插入按钮
     */
    private JButton insert;
    /**幻灯片显示组件
     */
    private JLayeredPane slidePanel;
    /**
     * 幻灯片页数显示
     */
    private JLabel slideNumber;
    /**
     * 缩略图侧边栏
     */
    private JScrollPane thumbnailPanel;
    /**是否打开了PPT
     */
    private boolean isOpened = false;
    /**是否复制过幻灯片
     */
    private boolean isCopied = false;

    public Base() {
        setTitle("PPT Editor");
        setSize(1440, 810);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        //菜单栏
        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);
        //文件菜单
        JMenu file = getFileMenu();
        bar.add(file);

        editBar = new JPanel();
        //流式布局，水平间隔10，垂直间隔5
        editBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        edit = new JButton("编辑");
        //字体选择框
        JComboBox<String> textFont = new JComboBox<>(ge.getAvailableFontFamilyNames());
        textFont.setRenderer(new FontRenderer());
        textFont.addActionListener(a -> {
            TextboxPanel tb = (TextboxPanel) selected;
            tb.setFont((String) textFont.getSelectedItem());
        });
        editBar.add(textFont);
        //字号选择框
        JComboBox<Integer> textSize = new JComboBox<>(new Integer[]{10, 15, 20, 25});
        textSize.setEditable(true);
        textSize.addActionListener(a -> {
            TextboxPanel tb = (TextboxPanel) selected;
            if (textSize.getSelectedItem().equals(""))
                textSize.setSelectedIndex(0);
            tb.setSize((int) textSize.getSelectedItem());
        });
        editBar.add(textSize);
        //字体颜色选择框
        JComboBox<Color> textColor = new JComboBox<>(new Color[]{Color.BLACK, Color.RED, Color.GREEN, Color.BLUE});
        textColor.setPreferredSize(new Dimension(40, 25));
        textColor.setRenderer(new ColorRenderer());
        textColor.addActionListener(a -> {
            TextboxPanel tb = (TextboxPanel) selected;
            tb.setColor((Color) textColor.getSelectedItem());
        });
        editBar.add(textColor);
        //段落对齐选择框
        JComboBox<Integer> textAlign = new JComboBox<>(new Integer[]{0, 1, 2, 3});
        textAlign.setRenderer(new AlignRenderer());
        textAlign.addActionListener(a -> {
            TextboxPanel tb = (TextboxPanel) selected;
            tb.setAlign((int) textAlign.getSelectedItem());
        });
        editBar.add(textAlign);
        //边框色选择框
        JComboBox<Color> drawColor = new JComboBox<>(new Color[]{Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, null});
        drawColor.setPreferredSize(new Dimension(40, 25));
        drawColor.setRenderer(new ColorRenderer());
        drawColor.addActionListener(a -> selected.setDrawColor((Color) drawColor.getSelectedItem()));
        editBar.add(drawColor);
        //填充色选择框
        JComboBox<Color> fillColor = new JComboBox<>(new Color[]{Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, null});
        fillColor.setPreferredSize(new Dimension(40, 25));
        fillColor.setRenderer(new ColorRenderer());
        fillColor.addActionListener(a -> {
            AutoShapePanel p = (AutoShapePanel) selected;
            p.setFillColor((Color) fillColor.getSelectedItem());
        });
        editBar.add(fillColor);
        //边框线宽选择框
        JComboBox<Double> lineWidth = new JComboBox<>(new Double[]{1.0, 1.5, 2.0, 2.5});
        lineWidth.setEditable(true);
        lineWidth.addActionListener(a -> {
            if (lineWidth.getSelectedItem().equals(""))
                lineWidth.setSelectedIndex(0);
            selected.setLineWidth((double) lineWidth.getSelectedItem());
        });
        editBar.add(lineWidth);
        //更新组件状态
        edit.addActionListener(a -> {
            textFont.setEnabled(selected instanceof TextboxPanel);
            textSize.setEnabled(selected instanceof TextboxPanel);
            textColor.setEnabled(selected instanceof TextboxPanel);
            textAlign.setEnabled(selected instanceof TextboxPanel);
            drawColor.setEnabled(selected instanceof AutoShapePanel || selected instanceof LinePanel);
            fillColor.setEnabled(selected instanceof AutoShapePanel);
            lineWidth.setEnabled(selected instanceof AutoShapePanel || selected instanceof LinePanel);
            setToolBar(editBar);
        });
        bar.add(edit);

        insertBar = new JPanel();
        insertBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        insert = new JButton("插入");

        JButton blank = new JButton("幻灯片");
        blank.addActionListener(a -> {
            ppt.createSlide(currentSlide);
            insert.doClick(1);
        });
        insertBar.add(blank);

        JButton picture = new JButton("图片");
        picture.addActionListener(a -> insertPicture());
        insertBar.add(picture);
        //绘图组件初始化
        drawPanel = new JPanel();
        drawPanel.setOpaque(false);
        drawPanel.addMouseListener(action);
        drawPanel.addMouseMotionListener(action);
        //插入基本图形按钮
        for (String name : shapes) {
            JButton b = new JButton(name);
            b.addActionListener(action);
            insertBar.add(b);
        }
        //更新组件状态
        insert.addActionListener(a -> {
            for (Component c : insertBar.getComponents())
                c.setEnabled(isOpened);
            setToolBar(insertBar);
        });
        bar.add(insert);

        edit.doClick(1);
        setVisible(true);
    }

    /**
     * 创建文件菜单
     */
    private JMenu getFileMenu() {
        JMenu file = new JMenu("文件");

        JMenuItem create = new JMenuItem("新建");
        create.addActionListener(a -> {
            ppt = new PPTLoader();
            isOpened = true;
            isCopied = false;
            slide = ppt.getSlide(currentSlide = 0);
            showArea();
        });
        file.add(create);

        JMenuItem open = new JMenuItem("打开");
        open.addActionListener(a -> openPPT());
        file.add(open);

        JMenuItem save = new JMenuItem("保存");
        save.addActionListener(a -> {
            if (!(ppt.hasFile() || save()))
                return;
            try {
                ppt.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        file.add(save);

        JMenuItem saveAs = new JMenuItem("另存为..");
        saveAs.addActionListener(a -> {
            if (!save())
                return;
            try {
                ppt.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        file.add(saveAs);

        JMenuItem close = new JMenuItem("关闭");
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
            public void menuSelected(MenuEvent e) { //更新组件状态
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
        return file;
    }

    /**
     * 获取当前工具栏
     */
    private Component getToolBar() {
        BorderLayout layout = (BorderLayout) getContentPane().getLayout();
        return layout.getLayoutComponent(BorderLayout.NORTH);
    }

    /**
     * 设置工具栏
     */
    private void setToolBar(JPanel toolbar) {
        Component c = getToolBar();
        if (c != null)
            remove(c);
        add(toolbar, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    /**
     *打开PPT文件
     */
    private void openPPT() {
        //文件选择器
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);
        //设置扩展名过滤
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("演示文稿(*.pptx)", "pptx"));
        //打开文件选择窗口
        int res = jfc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {   //选择了文件
            File f = jfc.getSelectedFile();
            try {
                ppt = new PPTLoader(f);
                isOpened = true;
                isCopied = false;
                slide = ppt.getSlide(currentSlide = 0);
                showArea();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 另存为到其他文件
     *
     * @return 是否成功绑定其他文件
     */
    private boolean save() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("演示文稿(*.pptx)", "pptx"));
        jfc.setSelectedFile(new File("无标题-1"));
        int res = jfc.showDialog(this, "保存");
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            String p = f.getAbsolutePath();
            if (p.toLowerCase().endsWith(".pptx"))
                ppt.setFile(f);
            else    //补全扩展名
                ppt.setFile(new File(p + ".pptx"));
            return true;
        }
        return false;
    }

    /**
     *插入图片文件
     */
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
                //转换为字节流
                byte[] picture = IOUtils.toByteArray(new FileInputStream(f));
                //读取扩展名
                String fileName = f.getName();
                String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
                XSLFPictureData data = ppt.addPicture(picture, ext);
                //设置图片位置
                JPanel pic = new ImagePanel(slide.createPicture(data), 100, 100);
                drawSlide(currentSlide);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化状态栏
     */
    private void statusBar() {
        statusBar = new JPanel();
        statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        //上翻页按钮
        JButton prev = new JButton("Previous");
        prev.addActionListener(a -> toPrevious());
        //下翻页按钮
        JButton next = new JButton("Next");
        next.addActionListener(a -> toNext());
        //当前页数/总页数
        slideNumber = new JLabel("PPT " + (currentSlide + 1) + "/" + ppt.totalSlides);

        statusBar.add(prev);
        statusBar.add(next);
        statusBar.add(slideNumber);
    }

    /**
     * 初始化侧边栏
     */
    private void thumbnailPanel() {
        JPanel tp = new JPanel();
        //组件与各边缘间隔为10像素
        tp.setBorder(new EmptyBorder(10, 10, 10, 10));
        //网格布局，n行1列，垂直间隔为10
        tp.setLayout(new GridLayout(Math.max(ppt.totalSlides, 5), 1, 0, 10));
        //缩略图与侧边栏的弹出菜单
        JPopupMenu menuButton = getPopupMenu(), menuPanel = new JPopupMenu();

        JMenuItem cr = new JMenuItem("新建幻灯片");
        cr.addActionListener(a -> {
            ppt.createSlide(currentSlide);
            showArea();
        });
        menuPanel.add(cr);

        for (int i = 0; i < ppt.totalSlides; i++) {
            int si = i;
            Thumbnail tb = new Thumbnail(ppt.getSlide(si));
            tb.setComponentPopupMenu(menuButton);
            //点击切换到该幻灯片
            tb.addActionListener(a -> to(si));
            tb.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {  //如果为右键
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

    /**
     * 创建缩略图弹出菜单
     */
    private JPopupMenu getPopupMenu() {
        JPopupMenu m = new JPopupMenu();

        JMenuItem cr = new JMenuItem("新建幻灯片");
        cr.addActionListener(a -> {
            ppt.createSlide(currentSlide);
            showArea();
        });
        m.add(cr);

        JMenuItem co = new JMenuItem("复制");
        co.addActionListener(a -> {
            ppt.copySlide(currentSlide);
            isCopied = true;
        });
        m.add(co);

        JMenuItem pa = new JMenuItem("粘贴");
        pa.addActionListener(a -> {
            ppt.pasteSlide(currentSlide);
            showArea();
        });
        m.add(pa);

        m.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {  //更新菜单项状态
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

    /**
     * GUI重绘
     */
    private void showArea() {
        //移除所有组件
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
            public void mousePressed(MouseEvent e) {    //更新选中组件
                selected = null;
                label:  //从上到下遍历
                for (int i = slidePanel.getComponentCount(); i >= 0; i--)
                    for (Component c : slidePanel.getComponentsInLayer(i)) {
                        //将显示区坐标变换到子组件的坐标系
                        Point p = SwingUtilities.convertPoint(slidePanel, e.getPoint(), c);
                        if (c.contains(p)) {
                            selected = (DraggablePanel) c;
                            break label;
                        }
                    }
                //取消选中其他组件
                for (Component c : slidePanel.getComponents())
                    if (c != selected && c instanceof DraggablePanel) {
                        //若为空文本框，则删除
                        if (c instanceof TextboxPanel && ((TextboxPanel) c).isEmpty()) {
                            slide.removeShape(((TextboxPanel) c).getShape());
                            slidePanel.remove(c);
                        }else
                            ((DraggablePanel) c).deselect();
                    }
                slidePanel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selected != null)
                    selected.select();
                //更新工具栏
                (getToolBar() == editBar ? edit : insert).doClick(1);
            }
        });
        add(statusBar, BorderLayout.SOUTH);
        add(thumbnailPanel, BorderLayout.WEST);
        add(slidePanel, BorderLayout.CENTER);
        /*JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, thumbnailPanel, slidePanel);
        jsp.setDividerLocation(200);
        add(jsp, BorderLayout.CENTER);*/
        revalidate();
        drawPanel.setBounds(0, 0, slidePanel.getWidth(), slidePanel.getHeight());
        slidePanel.add(drawPanel, -1, 0);
        action.setGraphics(drawPanel.getGraphics());
        repaint();
    }

    /**
     * 幻灯片显示区绘制
     * @param index 第index张幻灯片
     */
    private void drawSlide(int index) {
        slidePanel.removeAll();

        slide = ppt.getSlide(currentSlide = index);
        slidePanel.setBackground(slide.getBackground().getFillColor());
        //依次处理所有形状
        List<XSLFShape> shapes = slide.getShapes();
        for (int i = 0; i < shapes.size(); i++) {
            XSLFShape shape = shapes.get(i);
            JPanel p = new JPanel();
            try {
                if (shape instanceof XSLFPictureShape)
                    p = new ImagePanel((XSLFPictureShape) shape);
                else if (shape instanceof XSLFConnectorShape)
                    p = new LinePanel((XSLFConnectorShape) shape);
                else if (shape instanceof XSLFTextBox)
                    p = new TextboxPanel((XSLFTextBox) shape);
                else if (shape instanceof XSLFAutoShape)
                    //增强switch，Java 14新特性
                    p = switch (((XSLFAutoShape) shape).getShapeType()) {
                        case RECT -> new RectanglePanel((XSLFAutoShape) shape);
                        case ELLIPSE -> new EllipsePanel((XSLFAutoShape) shape);
                        default -> new JPanel();
                    };
                slidePanel.add(p, i, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        slidePanel.revalidate();
        slidePanel.repaint();
    }

    /**
     * 切换幻灯片
     * @param index 第index张幻灯片
     */
    private void to(int index) {
        if (0 <= index && index < ppt.totalSlides) {
            drawSlide(index);
            slideNumber.setText("PPT " + (currentSlide + 1) + "/" + ppt.totalSlides);
        }
    }

    /**
     * 切换到上一张幻灯片
     */
    private void toPrevious() {
        if (0 < currentSlide)
            to(currentSlide - 1);
    }

    /**
     * 切换到下一张幻灯片
     */
    private void toNext() {
        if (currentSlide < ppt.totalSlides)
            to(currentSlide + 1);
    }

    /**
     * 绘画行为类 <p>
     * 实现插入形状时的画图功能
     */
    private class DrawAction implements MouseListener, MouseMotionListener, ActionListener {
        /**画笔
         */
        private Graphics g;
        /**
         * 待创建的形状类型
         */
        private String type;
        /**
         * 绘制时使用的坐标
         */
        private int startX, startY, curX, curY;
        /**是否正在绘画
         */
        private boolean isDrawing = false;

        public void setGraphics(Graphics g) {
            this.g = g;
        }

        @Override
        public void actionPerformed(ActionEvent a) {    //进入绘画状态
            type = a.getActionCommand();
            isDrawing = true;
            //将画板提升到最高层
            slidePanel.setLayer(drawPanel, 114514);
        }

        @Override
        public void mousePressed(MouseEvent m) {
            if (isDrawing) {    //记录坐标
                startX = m.getX();
                startY = m.getY();
                g.setColor(Color.BLACK);
            }
            //模拟父容器鼠标按下事件
            Point p = SwingUtilities.convertPoint(m.getComponent(), m.getPoint(), slidePanel);
            MouseEvent me = new MouseEvent(slidePanel, m.getID(), m.getWhen(), m.getModifiersEx(), p.x, p.y,
                    m.getClickCount(), m.isPopupTrigger());
            for (MouseListener l : slidePanel.getMouseListeners())
                l.mousePressed(me);
        }

        @Override
        public void mouseReleased(MouseEvent m) {
            if (isDrawing) {    //完成绘画
                slidePanel.setLayer(drawPanel, -1);
                isDrawing = false;
                int endX = m.getX(), endY = m.getY(), width = Math.max(Math.abs(endX - startX), 32),
                        height = Math.max(Math.abs(endY - startY), 32);
                //创建形状
                DraggablePanel p;
                if (type.equals(shapes[0])) {
                    XSLFConnectorShape l = slide.createConnector();
                    l.setAnchor(new Rectangle(startX, startY, width, height));
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
                //选中新建形状
                if (selected != null)
                    selected.deselect();
                selected = p;
                p.select();
            }
            //模拟父容器鼠标释放事件
            Point p = SwingUtilities.convertPoint(m.getComponent(), m.getPoint(), slidePanel);
            MouseEvent me = new MouseEvent(slidePanel, m.getID(), m.getWhen(), m.getModifiersEx(), p.x, p.y,
                    m.getClickCount(), m.isPopupTrigger());
            for (MouseListener l : slidePanel.getMouseListeners())
                l.mouseReleased(me);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (isDrawing) {  //动态更新形状
                g.setColor(Color.WHITE);
                if (type.equals(shapes[0])) {
                    //清理旧形状的图像
                    g.drawLine(startX, startY, curX, curY);
                    curX = e.getX();
                    curY = e.getY();
                    //绘制新形状图像
                    g.setColor(Color.BLACK);
                    g.drawLine(startX, startY, curX, curY);
                } else {
                    g.fillRect(Math.min(startX, curX) - 5, Math.min(startY, curY) - 5, Math.abs(curX - startX) + 10, Math.abs(curY - startY) + 10);
                    curX = e.getX();
                    curY = e.getY();
                    g.setColor(Color.BLACK);
                    g.drawRect(Math.min(startX, curX), Math.min(startY, curY), Math.abs(curX - startX), Math.abs(curY - startY));
                }
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
