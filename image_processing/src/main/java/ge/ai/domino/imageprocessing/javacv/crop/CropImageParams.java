package ge.ai.domino.imageprocessing.javacv.crop;

import lombok.Getter;

@Getter
public class CropImageParams {

	private int positionX;

	private int positionY;

	private int width;

	private int height;

	public CropImageParams positionX(int positionX) {
		this.positionX = positionX;
		return this;
	}

	public CropImageParams positionY(int positionY) {
		this.positionY = positionY;
		return this;
	}

	public CropImageParams width(int width) {
		this.width = width;
		return this;
	}

	public CropImageParams height(int height) {
		this.height = height;
		return this;
	}
}
