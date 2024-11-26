package me.shepherd23333.file;

import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import java.awt.*;
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

    public void save() throws IOException {
        f.delete();
        FileOutputStream os = new FileOutputStream(f);
        ppt.write(os);
        os.close();
    }

    public XSLFPictureData add(byte[] pic, String type) {
        PictureData.PictureType t = type.equals("jpg") ? PictureData.PictureType.JPEG : PictureData.PictureType.PNG;
        return ppt.addPicture(pic, t);
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
