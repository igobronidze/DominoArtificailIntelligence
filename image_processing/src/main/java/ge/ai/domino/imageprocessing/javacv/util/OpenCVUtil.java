package ge.ai.domino.imageprocessing.javacv.util;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.awt.image.BufferedImage;

public class OpenCVUtil {

    private static BufferedImage iplImageToBufferedImage(opencv_core.IplImage src) {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(src);
        return paintConverter.getBufferedImage(frame,1);
    }

    public static BufferedImage matToBufferedImage(opencv_core.Mat mat) {
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        return  iplImageToBufferedImage(converter.convertToIplImage(converter.convert(mat)));
    }

    public static opencv_core.Mat bufferedImageToMat(BufferedImage bufImage) {
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        return converter.convertToMat(converter.convert(bufferedImageToIplImage(bufImage)));
    }

    private static opencv_core.IplImage bufferedImageToIplImage(BufferedImage bufImage) {
        OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter java2dConverter = new Java2DFrameConverter();
        return iplConverter.convert(java2dConverter.convert(bufImage));
    }
}
