package ge.ai.domino.util.tile;

public class TileUtil {

    private static final String DELIMITER = "-";

    public static String getTileUID(int x, int y) {
        return x + DELIMITER + y;
    }
}
