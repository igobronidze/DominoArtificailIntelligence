package ge.ai.domino.imageprocessing;

import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.imageprocessing.clean.ImageCleaner;
import ge.ai.domino.imageprocessing.contour.Contour;
import ge.ai.domino.imageprocessing.contour.ContoursDetector;
import ge.ai.domino.imageprocessing.crop.CropImageParams;
import ge.ai.domino.imageprocessing.crop.CropImageParamsCreator;
import ge.ai.domino.imageprocessing.crop.ImageCropper;
import ge.ai.domino.imageprocessing.util.OpenCVUtil;
import org.bytedeco.javacpp.opencv_core;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TilesDetector {

	public List<TileContour> getTiles(BufferedImage initialImage, TilesDetectorParams tilesDetectorParams) {
		opencv_core.Mat srcMat = OpenCVUtil.bufferedImageToMat(initialImage);

		CropImageParams cropImageParams = CropImageParamsCreator.createCropImageParams(srcMat.cols(), srcMat.rows(), tilesDetectorParams.getHeightPercentage(),
				tilesDetectorParams.getMarginBottomPercentage(), tilesDetectorParams.getWidthPercentage(), tilesDetectorParams.getMarginLeftPercentage());

		opencv_core.Mat croppedMat = ImageCropper.cropImage(srcMat, cropImageParams);

		BufferedImage image = ImageCleaner.cleanImage(croppedMat, tilesDetectorParams.getBlurCoefficient());

		ContoursDetector contoursDetector = new ContoursDetector();
		List<Contour> contours = contoursDetector.detectContours(image, tilesDetectorParams.getContourMinArea());

		return contours.stream()
				.map(contour -> getTile(contour, tilesDetectorParams.isCombinedPoints(), cropImageParams.getX(), cropImageParams.getY()))
				.collect(Collectors.toList());
	}

	private TileContour getTile(Contour contour, boolean combinedPoints, int croppedX, int croppedY) {
		List<Contour> topContours = new ArrayList<>();
		List<Contour> bottomContours = new ArrayList<>();
		int middle = contour.getTop() + (contour.getBottom() - contour.getTop()) / 2;
		for (Contour child : contour.getChildren()) {
			if (child.getTop() <= middle - 3) {
				topContours.add(child);
			} else if (child.getTop() >= middle + 3) {
				bottomContours.add(child);
			}
		}
		Tile tile = new Tile(countPoints(topContours, combinedPoints), countPoints(bottomContours, combinedPoints));

		TileContour tileContour = new TileContour();
		tileContour.setTile(tile);
		tileContour.setTopLeftX(contour.getLeft() + croppedX);
		tileContour.setTopLeftY(contour.getTop() + croppedY);
		tileContour.setBottomRightX(contour.getRight() + croppedX);
		tileContour.setBottomRightY(contour.getBottom() + croppedY);
		return tileContour;
	}

	private int countPoints(List<Contour> contours, boolean combinedPoints) {
		int count = 0;
		for (Contour contour : contours) {
			int max = Math.max(contour.getBottom() - contour.getTop(), contour.getRight() - contour.getLeft()) + 1;
			int min = Math.min(contour.getBottom() - contour.getTop(), contour.getRight() - contour.getLeft()) + 1;
			if (combinedPoints) {
				count += Math.round((double) max / min);
			} else {
				if (Math.abs(max - min) <= 2) {
					count++;
				}
			}
		}
		return count;
	}
}
