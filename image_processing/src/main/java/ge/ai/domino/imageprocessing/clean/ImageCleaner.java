package ge.ai.domino.imageprocessing.clean;

import ge.ai.domino.imageprocessing.util.OpenCVUtil;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;

import java.awt.image.BufferedImage;

public class ImageCleaner {

	public static BufferedImage cleanImage(opencv_core.Mat srcMat) {
		opencv_core.Mat grayMat = new opencv_core.Mat();
		opencv_core.Mat resultMat = new opencv_core.Mat();
		opencv_imgproc.cvtColor(srcMat, grayMat, opencv_imgproc.CV_BGR2GRAY);
		opencv_imgproc.blur(grayMat, grayMat, new opencv_core.Size(3, 3));
		opencv_imgproc.Canny(grayMat, resultMat, 100, 200, 3, true);

		return OpenCVUtil.matToBufferedImage(resultMat);
	}
}
