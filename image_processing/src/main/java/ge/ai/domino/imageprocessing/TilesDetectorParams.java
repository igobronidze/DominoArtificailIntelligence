package ge.ai.domino.imageprocessing;

import ge.ai.domino.imageprocessing.crop.CropImageParams;

public class TilesDetectorParams {

	private CropImageParams cropImageParams;

	public TilesDetectorParams cropImageParams(CropImageParams cropImageParams) {
		this.cropImageParams = cropImageParams;
		return this;
	}

	public CropImageParams getCropImageParams() {
		return cropImageParams;
	}
}
