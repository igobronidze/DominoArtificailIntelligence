package ge.ai.domino.imageprocessing.crop;

import org.bytedeco.javacpp.opencv_core;

public class ImageCropper {

	public static opencv_core.Mat cropImage(opencv_core.Mat srcMat, CropImageParams params) {
		opencv_core.Rect rectCrop = new opencv_core.Rect(params.getX(), params.getY(), params.getWidth(), params.getHeight());
		return new opencv_core.Mat(srcMat, rectCrop);
	}
}
