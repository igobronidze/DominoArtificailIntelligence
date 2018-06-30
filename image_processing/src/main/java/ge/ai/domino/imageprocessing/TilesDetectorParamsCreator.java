package ge.ai.domino.imageprocessing;

import ge.ai.domino.imageprocessing.crop.CropImageParams;

public class TilesDetectorParamsCreator {

	private static final double TILES_HEIGHT_PERCENTAGE = 20;

	private static final double TILES_MARGIN_BOTTOM_PERCENTAGE = 5;

	private static final double TILES_WIDTH_PERCENTAGE = 60;

	private static final double TILES_MARGIN_LEFT_PERCENTAGE =  20;

	public static TilesDetectorParams createTilesDetectorParams(int imageWidth, int imageHeight) {
		int x = (int) (imageWidth * TILES_MARGIN_LEFT_PERCENTAGE / 100);
		int width = (int) (imageWidth * TILES_WIDTH_PERCENTAGE / 100);
		int y = imageHeight - (int) (imageHeight * (TILES_HEIGHT_PERCENTAGE + TILES_MARGIN_BOTTOM_PERCENTAGE) / 100);
		int height = (int) (imageHeight * TILES_HEIGHT_PERCENTAGE / 100);

		CropImageParams cropImageParams = new CropImageParams()
				.x(x)
				.y(y)
				.width(width)
				.height(height);

		return new TilesDetectorParams()
				.cropImageParams(cropImageParams);
	}
}
