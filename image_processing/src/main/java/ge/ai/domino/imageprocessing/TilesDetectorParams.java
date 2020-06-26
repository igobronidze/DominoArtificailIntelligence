package ge.ai.domino.imageprocessing;

import lombok.Getter;

@Getter
public class TilesDetectorParams {

	private double heightPercentage;

	private double marginBottomPercentage;

	private double widthPercentage;

	private double marginLeftPercentage;

	private int contourMinArea;

	private int blurCoefficient;

	private boolean combinedPoints;

	public TilesDetectorParams heightPercentage(double heightPercentage) {
		this.heightPercentage = heightPercentage;
		return this;
	}

	public TilesDetectorParams marginBottomPercentage(double marginBottomPercentage) {
		this.marginBottomPercentage = marginBottomPercentage;
		return this;
	}

	public TilesDetectorParams widthPercentage(double widthPercentage) {
		this.widthPercentage = widthPercentage;
		return this;
	}

	public TilesDetectorParams marginLeftPercentage(double marginLeftPercentage) {
		this.marginLeftPercentage = marginLeftPercentage;
		return this;
	}

	public TilesDetectorParams contourMinArea(int contourMinArea) {
		this.contourMinArea = contourMinArea;
		return this;
	}

	public TilesDetectorParams blurCoefficient(int blurCoefficient) {
		this.blurCoefficient = blurCoefficient;
		return this;
	}

	public TilesDetectorParams combinedPoints(boolean combinedPoints) {
		this.combinedPoints = combinedPoints;
		return this;
	}
}
