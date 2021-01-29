package ge.ai.domino.imageprocessing.util;

import java.awt.image.BufferedImage;

public class BufferedImageUtil {

    public static int[][] bufferedImageToIntMatrix(BufferedImage image, int fromX, int toX, int fromY, int toY) {
        int[][] matrix = new int[toY - fromY][toX -fromX];
        for (int i = fromY; i < toY; i++) {
            for (int j = fromX; j < toX; j++) {
                matrix[i - fromY][j - fromX] = image.getRGB(j, i);
            }
        }
        return matrix;
    }
}
