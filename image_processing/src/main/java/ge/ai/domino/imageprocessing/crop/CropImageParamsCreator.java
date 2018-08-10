package ge.ai.domino.imageprocessing.crop;

public class CropImageParamsCreator {

	public static CropImageParams createCropImageParams(int imageWidth, int imageHeight, double heightPercentage,
															double marginBottomPercentage, double widthPercentage, double marginLeftPercentage) {
		int x = (int) (imageWidth * marginLeftPercentage / 100);
		int width = (int) (imageWidth * widthPercentage / 100);
		int y = imageHeight - (int) (imageHeight * (heightPercentage + marginBottomPercentage) / 100);
		int height = (int) (imageHeight * heightPercentage / 100);

		return new CropImageParams()
				.x(x)
				.y(y)
				.width(width)
				.height(height);
	}
}
