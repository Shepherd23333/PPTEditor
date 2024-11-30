package me.shepherd23333;

import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * PPT加载器
 */
public class PPTLoader {
    /**
     * 幻灯片总数
     */
    public int totalSlides;
    /**
     * 关联的文件
     */
    private File f;
    /**
     * PPT对象
     */
    private XMLSlideShow ppt;
    /**
     * 剪贴板存储的幻灯片
     */
    private XSLFSlide pasteSlide = new XMLSlideShow().createSlide();

    /**
     * 新建空白PPT
     */
    public PPTLoader() {
        ppt = new XMLSlideShow();
        ppt.createSlide();
        totalSlides = 1;
    }

    /**
     * 从文件加载PPT
     *
     * @param f 选择的文件(*.pptx)
     */
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

    /**
     * 保存PPT到文件
     */
    public void save() throws IOException {
        f.delete();
        FileOutputStream os = new FileOutputStream(f);
        ppt.write(os);
        os.close();
    }

    /**
     * 插入图片到PPT
     *
     * @param pic  图片字节流
     * @param type 图片类型(jpg/png)
     * @return {@code XSLFPictureData}对象，用于创建图片形状
     */
    public XSLFPictureData addPicture(byte[] pic, String type) {
        PictureData.PictureType t = type.equals("jpg") ? PictureData.PictureType.JPEG : PictureData.PictureType.PNG;
        return ppt.addPicture(pic, t);
    }

    /**
     * 在指定位置新建幻灯片
     *
     * @param index 第index张幻灯片后
     */
    public void createSlide(int index) {
        XSLFSlide s = ppt.createSlide();
        totalSlides++;
        ppt.setSlideOrder(s, index + 1);
    }

    /**
     * 获取幻灯片对象
     *
     * @param index 第index张幻灯片
     * @return {@code XSLFSlide}，对应的幻灯片
     */
    public XSLFSlide getSlide(int index) {
        return ppt.getSlides().get(index);
    }

    /**
     * 复制指定幻灯片
     *
     * @param index 第index张
     */
    public void copySlide(int index) {
        pasteSlide.importContent(ppt.getSlides().get(index));
    }

    /**
     * 在指定位置粘贴幻灯片
     *
     * @param index 第index张幻灯片后
     */
    public void pasteSlide(int index) {
        XSLFSlide s = ppt.createSlide();
        s.importContent(pasteSlide);
        totalSlides++;
        ppt.setSlideOrder(s, index + 1);
    }
}
