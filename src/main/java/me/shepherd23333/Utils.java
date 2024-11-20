package me.shepherd23333;

import org.apache.poi.xslf.usermodel.XSLFPictureShape;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Utils {
    public static Image getImage(XSLFPictureShape pic) throws IOException {
        byte[] data = pic.getPictureData().getData();
        return ImageIO.read(new ByteArrayInputStream(data));
    }
}
