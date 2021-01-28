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
import ge.ai.domino.imageprocessing.service.table.IPPossMoveTile;
import ge.ai.domino.imageprocessing.service.table.IPTile;
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
        return getMyTiles(myTilesMat, myTileRecognizeParams);
    }

    public static List<IPPossMoveTile> recognizePossMoveTiles(BufferedImage srcImage, PossMoveTileRecognizeParams possMoveTileRecognizeParams) {
        opencv_core.Mat srcMat = OpenCVUtil.bufferedImageToMat(srcImage);

        CropImageParams tableCropParams = CropImageParamsFactory.getCropImageParams(possMoveTileRecognizeParams.getTopLeft(), possMoveTileRecognizeParams.getBottomRight());
        opencv_core.Mat playedContentMat = ImageCropper.cropImage(srcMat, tableCropParams);
        return getPossMoveTiles(playedContentMat, possMoveTileRecognizeParams);
    }

    private static List<IPPossMoveTile> getPossMoveTiles(opencv_core.Mat srcMat, PossMoveTileRecognizeParams possMoveTileRecognizeParams) {
        BufferedImage srcImage = OpenCVUtil.matToBufferedImage(srcMat);

        ContoursDetector contoursDetector = new ContoursDetector();
        List<Contour> contours = contoursDetector.detectContours(srcImage, possMoveTileRecognizeParams.getContourMinArea(), new Color().redFrom(20).redTo(50).greenFrom(55).greenTo(150).blueFrom(10).blueTo(40));
        return contours.stream()
                .map(contour -> getPossMoveTile(contour, possMoveTileRecognizeParams.getTopLeft().getX(), possMoveTileRecognizeParams.getTopLeft().getY()))
                .collect(Collectors.toList());
    }

    private static List<IPTile> getMyTiles(opencv_core.Mat srcMat, MyTileRecognizeParams myTileRecognizeParams) {
        BufferedImage image = ImageCleaner.cleanImage(srcMat, myTileRecognizeParams.getBlurCoefficient());

        ContoursDetector contoursDetector = new ContoursDetector();
        List<Contour> contours = contoursDetector.detectContours(image, myTileRecognizeParams.getContourMinArea(), new Color().redFrom(255).redTo(256).greenFrom(255).greenTo(256).blueFrom(255).blueTo(256));

        return contours.stream()
                .map(contour -> getTile(contour, myTileRecognizeParams.isCombinedPoints(), myTileRecognizeParams.getTopLeft().getX(), myTileRecognizeParams.getTopLeft().getY()))
                .collect(Collectors.toList());
    }

    private static IPPossMoveTile getPossMoveTile(Contour contour, int croppedX, int croppedY) {
        IPPossMoveTile possMoveTile = new IPPossMoveTile();
        possMoveTile.setTopLeft(new Point(contour.getLeft() + croppedX, contour.getTop() + croppedY));
        possMoveTile.setBottomRight(new Point(contour.getRight() + croppedX, contour.getBottom() + croppedY));
        return possMoveTile;
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
