package ge.ai.domino.imageprocessing.crop;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.junit.Assert;
import org.junit.Test;

public class ImageCropperTest {

	private static final String SRC_IMAGE_PATH = "test_images/crop/src/for_crop.png";

	private static final String RESULT_IMAGE_PATH = "test_images/crop/result/cropped.png";

	private static final int X = 100;

	private static final int Y = 200;

	private static final int WIDTH = 250;

	private static final int HEIGHT = 300;

	@Test
	public void testCropImage() {
		CropImageParams cropImageParams = new CropImageParams()
				.x(X)
				.y(Y)
				.width(WIDTH)
				.height(HEIGHT);

		opencv_core.Mat resultMat;
		opencv_core.Mat srcMat = opencv_imgcodecs.imread(SRC_IMAGE_PATH);
		resultMat = ImageCropper.cropImage(srcMat, cropImageParams);
		Assert.assertEquals(WIDTH, resultMat.cols());
		Assert.assertEquals(HEIGHT, resultMat.rows());

		opencv_imgcodecs.imwrite(RESULT_IMAGE_PATH, resultMat);
	}
}
