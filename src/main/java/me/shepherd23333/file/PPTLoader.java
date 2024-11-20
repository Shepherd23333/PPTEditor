package me.shepherd23333.file;

import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PPTLoader {
    public int totalSlides;
    private File f;
    private XMLSlideShow ppt;
    private XSLFSlide pasteSlide = new XMLSlideShow().createSlide();

    public PPTLoader() {
        ppt = new XMLSlideShow();
        ppt.createSlide();
        totalSlides = 1;
    }

    public PPTLoader(File f) throws IOException {
        FileInputStream fi = new FileInputStream(f);
        ppt = new XMLSlideShow(fi);
        totalSlides = ppt.getSlides().size();
        fi.close();
        this.f = f;
    }

    public boolean hasFile() {
        return f != null;
    }

    public void setFile(File f) {
        this.f = f;
    }

    public static void test() throws IOException {
        XMLSlideShow ppt = new XMLSlideShow();

        XSLFSlide slide = ppt.createSlide();//新建幻灯片
        XSLFTextBox tb = slide.createTextBox();//新建文本框对象
        XSLFTextParagraph tp = tb.addNewTextParagraph();//添加文本段落
        XSLFTextRun tr = tp.addNewTextRun();
        tr.setText("Test");//添加文本
        tr.setFontSize(48.0);
        tr.setFontColor(Color.BLACK);//设置文本格式

        tp.setTextAlign(TextParagraph.TextAlign.CENTER);//设置段落格式

        //设置图形的位置与大小
        Rectangle2D.Double rect = new Rectangle2D.Double(100.0, 100.0, 400.0, 100.0);
        tb.setAnchor(rect);//将图形参数赋予文本框

        File out = new File("test.pptx");
        out.delete();

        FileOutputStream os = new FileOutputStream(out);
        ppt.write(os);
        os.close();//导出结果
        System.out.println("Successful.");
    }

    public void save() throws IOException {
        f.delete();
        FileOutputStream os = new FileOutputStream(f);
        ppt.write(os);
        os.close();
    }

    public Dimension getSize() {
        return ppt.getPageSize();
    }

    /// Create a slide behind the index
    public void createSlide(int index) {
        XSLFSlide s = ppt.createSlide();
        totalSlides++;
        ppt.setSlideOrder(s, index + 1);
    }

    public XSLFSlide getSlide(int index) {
        return ppt.getSlides().get(index);
    }

    public void copySlide(int index) {
        pasteSlide.importContent(ppt.getSlides().get(index));
    }

    /// Paste the slide behind the index
    public void paste(int index) {
        XSLFSlide s = ppt.createSlide();
        s.importContent(pasteSlide);
        totalSlides++;
        ppt.setSlideOrder(s, index + 1);
    }
}
