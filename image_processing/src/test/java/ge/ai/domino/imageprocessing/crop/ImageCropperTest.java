package ge.ai.domino.imageprocessing.crop;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.junit.Assert;
import org.junit.Test;

public class ImageCropperTest {

	private final String SRC_IMAGE_PATH = "test_images/src/crop/for_crop.png";

	private final String RESULT_IMAGE_PATH = "test_images/result/crop/cropped.png";

	private final int X = 100;

	private final int Y = 200;

	private final int WIDTH = 250;

	private final int HEIGHT = 300;

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
