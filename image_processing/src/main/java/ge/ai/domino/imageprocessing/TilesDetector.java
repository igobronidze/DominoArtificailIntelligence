package ge.ai.domino.imageprocessing;

import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.imageprocessing.contour.ContoursDetector;
import ge.ai.domino.imageprocessing.crop.ImageCropper;
import ge.ai.domino.imageprocessing.util.TilesDetectorParamsCreator;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgcodecs;

import java.util.List;

public class TilesDetector {

	public List<Tile> getTiles(String imagePath) {
		opencv_core.Mat srcMat = opencv_imgcodecs.imread(imagePath);

		TilesDetectorParams tilesDetectorParams = TilesDetectorParamsCreator.createTilesDetectorParams(srcMat.cols(), srcMat.rows());

		opencv_core.Mat croppedMat = ImageCropper.cropImage(srcMat, tilesDetectorParams.getCropImageParams());

		opencv_core.Mat rame = ContoursDetector.findContours(croppedMat);

//		opencv_imgcodecs.imwrite("test_images/result/rame.png", croppedMat);

		return null;
	}
}
