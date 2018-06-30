package ge.ai.domino.imageprocessing;

import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.imageprocessing.clean.ImageCleaner;
import ge.ai.domino.imageprocessing.contour.Contour;
import ge.ai.domino.imageprocessing.contour.ContoursDetector;
import ge.ai.domino.imageprocessing.crop.ImageCropper;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgcodecs;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TilesDetector {

	public List<Tile> getTiles(String imagePath) {
		opencv_core.Mat srcMat = opencv_imgcodecs.imread(imagePath);

		TilesDetectorParams tilesDetectorParams = TilesDetectorParamsCreator.createTilesDetectorParams(srcMat.cols(), srcMat.rows());

		opencv_core.Mat croppedMat = ImageCropper.cropImage(srcMat, tilesDetectorParams.getCropImageParams());

		BufferedImage image = ImageCleaner.cleanImage(croppedMat);

		ContoursDetector contoursDetector = new ContoursDetector();
		List<Contour> contours = contoursDetector.detectContours(image);

		return contours.stream().map(this::getTile).collect(Collectors.toList());
	}

	private Tile getTile(Contour contour) {
		List<Contour> topContours = new ArrayList<>();
		List<Contour> bottomContours = new ArrayList<>();
		int middle = contour.getTop() + (contour.getBottom() - contour.getTop()) / 2;
		for (Contour child : contour.getChildren()) {
			if (child.getTop() < middle) {
				topContours.add(child);
			} else {
				bottomContours.add(child);
			}
		}
		return new Tile(countPoints(topContours), countPoints(bottomContours));
	}

	private int countPoints(List<Contour> contours) {
		int count = 0;
		for (Contour contour : contours) {
			int max = Math.max(contour.getBottom() - contour.getTop(), contour.getRight() - contour.getLeft());
			int min = Math.min(contour.getBottom() - contour.getTop(), contour.getRight() - contour.getLeft());
			count += Math.round((double) max / min);
		}
		return count;
	}
}
