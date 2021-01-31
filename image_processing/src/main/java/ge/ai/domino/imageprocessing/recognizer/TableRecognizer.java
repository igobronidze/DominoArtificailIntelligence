package ge.ai.domino.imageprocessing.recognizer;

import ge.ai.domino.imageprocessing.javacv.clean.ImageCleaner;
import ge.ai.domino.imageprocessing.javacv.crop.CropImageParams;
import ge.ai.domino.imageprocessing.javacv.crop.CropImageParamsFactory;
import ge.ai.domino.imageprocessing.javacv.crop.ImageCropper;
import ge.ai.domino.imageprocessing.javacv.util.OpenCVUtil;
import ge.ai.domino.imageprocessing.service.Point;
import ge.ai.domino.imageprocessing.service.contour.Contour;
import ge.ai.domino.imageprocessing.service.contour.ContoursDetector;
import ge.ai.domino.imageprocessing.service.rectangle.Color;
import ge.ai.domino.imageprocessing.service.table.IPPossMovesAndCenter;
import ge.ai.domino.imageprocessing.service.table.IPRectangle;
import ge.ai.domino.imageprocessing.service.table.IPTile;
import ge.ai.domino.imageprocessing.util.BufferedImageUtil;
import org.bytedeco.javacpp.opencv_core;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableRecognizer {

    public static List<IPTile> recognizeMyTiles(BufferedImage srcImage, MyTileRecognizeParams myTileRecognizeParams) {
        opencv_core.Mat srcMat = OpenCVUtil.bufferedImageToMat(srcImage);

        CropImageParams myTilesCropParams = CropImageParamsFactory.getCropImageParams(myTileRecognizeParams.getTopLeft(), myTileRecognizeParams.getBottomRight());
        opencv_core.Mat myTilesMat = ImageCropper.cropImage(srcMat, myTilesCropParams);

        BufferedImage image = ImageCleaner.cleanImage(myTilesMat, myTileRecognizeParams.getBlurCoefficient());

        ContoursDetector contoursDetector = new ContoursDetector();
        List<Contour> contours = contoursDetector.detectContours(image, myTileRecognizeParams.getContourMinArea(), new Color().redFrom(255).redTo(256).greenFrom(255).greenTo(256).blueFrom(255).blueTo(256));

        return contours.stream()
                .map(contour -> getTile(contour, myTileRecognizeParams.isCombinedPoints(), myTileRecognizeParams.getTopLeft().getX(), myTileRecognizeParams.getTopLeft().getY()))
                .collect(Collectors.toList());
    }

    public static IPPossMovesAndCenter recognizePossMoveTiles(BufferedImage srcImage, PossMoveTileRecognizeParams possMoveTileRecognizeParams) {
        ContoursDetector contoursDetector = new ContoursDetector();

        int[][] croppedImage = BufferedImageUtil.bufferedImageToIntMatrix(srcImage, possMoveTileRecognizeParams.getTopLeft().getX(), possMoveTileRecognizeParams.getBottomRight().getX(),
                possMoveTileRecognizeParams.getTopLeft().getY(), possMoveTileRecognizeParams.getBottomRight().getY());

        List<Contour> possMoveContours = contoursDetector.detectContours(croppedImage, possMoveTileRecognizeParams.getContourMinArea(), new Color().redFrom(20).redTo(50).greenFrom(55).greenTo(150).blueFrom(10).blueTo(40));
        List<Contour> centerTileContours = contoursDetector.detectContours(croppedImage, possMoveTileRecognizeParams.getContourMinArea(), new Color().redFrom(241).redTo(245).greenFrom(87).greenTo(91).blueFrom(9).blueTo(13));

        IPPossMovesAndCenter ipPossMovesAndCenter = new IPPossMovesAndCenter();
        ipPossMovesAndCenter.setPossMoves(possMoveContours.stream()
                .map(contour -> getIPRectangle(contour, possMoveTileRecognizeParams.getTopLeft().getX(), possMoveTileRecognizeParams.getTopLeft().getY()))
                .collect(Collectors.toList()));
        ipPossMovesAndCenter.setCenter(centerTileContours.isEmpty() ? null : getIPRectangle(centerTileContours.get(0), possMoveTileRecognizeParams.getTopLeft().getX(), possMoveTileRecognizeParams.getTopLeft().getY()));
        return ipPossMovesAndCenter;
    }

    public static List<IPRectangle> recognizeMyBazaarTiles(MyBazaarTileRecognizeParams params) {
        int tilesContentWidth = params.getTilesCount() * params.getTileWidth() + (params.getTilesCount() - 1) * params.getTilesSpacing();
        int firstTileLeft = (int) Math.round((double) (params.getScreenWidth() - tilesContentWidth) / 2) + 1;

        List<IPRectangle> rectangles = new ArrayList<>();
        for (int i = 0; i < params.getTilesCount(); i++) {
            int left = firstTileLeft + i * (params.getTileWidth() + params.getTilesSpacing());
            Point leftTop = new Point(left, params.getTileTop());
            Point rightTop = new Point(left + params.getTileWidth(), params.getTileBottom());
            rectangles.add(new IPRectangle(leftTop, rightTop));
        }

        return rectangles;
    }

    private static IPRectangle getIPRectangle(Contour contour, int croppedX, int croppedY) {
        IPRectangle ipRectangle = new IPRectangle();
        ipRectangle.setTopLeft(new Point(contour.getLeft() + croppedX, contour.getTop() + croppedY));
        ipRectangle.setBottomRight(new Point(contour.getRight() + croppedX, contour.getBottom() + croppedY));
        return ipRectangle;
    }

    private static IPTile getTile(Contour contour, boolean combinedPoints, int croppedX, int croppedY) {
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

        IPTile tile = new IPTile();
        tile.setLeft(countPoints(topContours, combinedPoints));
        tile.setRight(countPoints(bottomContours, combinedPoints));
        tile.setTopLeft(new Point(contour.getLeft() + croppedX, contour.getTop() + croppedY));
        tile.setBottomRight(new Point(contour.getRight() + croppedX, contour.getBottom() + croppedY));

        return tile;
    }

    private static int countPoints(List<Contour> contours, boolean combinedPoints) {
        int count = 0;
        for (Contour contour : contours) {
            int max = Math.max(contour.getBottom() - contour.getTop(), contour.getRight() - contour.getLeft()) + 1;
            int min = Math.min(contour.getBottom() - contour.getTop(), contour.getRight() - contour.getLeft()) + 1;
            if (combinedPoints) {
                count += max / min;
            } else {
                if (Math.abs(max - min) <= 2) {
                    count++;
                }
            }
        }
        return count;
    }
}
