package ge.ai.domino.imageprocessing.crop;

import lombok.Getter;

@Getter
public class CropImageParams {

	private int x;

	private int y;

	private int width;

	private int height;

	public CropImageParams x(int x) {
		this.x = x;
		return this;
	}

	public CropImageParams y(int y) {
		this.y = y;
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
