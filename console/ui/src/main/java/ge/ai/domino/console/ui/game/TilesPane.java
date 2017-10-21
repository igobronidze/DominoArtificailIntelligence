package ge.ai.domino.console.ui.game;

import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.tile.OpponentTile;
import ge.ai.domino.domain.tile.Tile;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

abstract class TilesPane extends FlowPane {

    static final int IMAGE_WIDTH = 65;

    static final int IMAGE_HEIGHT = 110;

    static Map<Integer, OpponentTile> opponentTiles;

    static Set<Tile> myTiles;

    static Round round;

    static ImageView upArrow;

    static ImageView rightArrow;

    static ImageView downArrow;

    static ImageView leftArrow;

    static private TilesPane tilePane;

    static private Tile tile;

    static private boolean arrowsVisible;

    Map<Integer, ImageView> imageViews = new HashMap<>();

    TilesPane(Round round) {
        opponentTiles = round.getOpponentTiles();
        myTiles = round.getMyTiles();
        TilesPane.round = round;
        initBasicUI();
    }

    private void initBasicUI() {
        this.setHgap(25);
        this.setVgap(10);
        upArrow = new ImageView(ImageFactory.getImage("arrows/up.png"));
        setImageStyle(upArrow);
        upArrow.setOnMouseClicked(e -> onUpArrowPressed());
        rightArrow = new ImageView(ImageFactory.getImage("arrows/right.png"));
        setImageStyle(rightArrow);
        rightArrow.setOnMouseClicked(e -> onRightArrowPressed());
        downArrow = new ImageView(ImageFactory.getImage("arrows/down.png"));
        setImageStyle(downArrow);
        downArrow.setOnMouseClicked(e -> onDownArrowPressed());
        leftArrow = new ImageView(ImageFactory.getImage("arrows/left.png"));
        leftArrow.setOnMouseClicked(e -> onLeftArrowPressed());
        setImageStyle(leftArrow);
        setImageVisibility(false);
    }

    ImageView getImageView(Tile tile, boolean clickable) {
        ImageView imageView = new ImageView(ImageFactory.getImage("tiles/" + tile.getLeft() + "-" + tile.getRight() + ".png"));
        imageView.setFitHeight(IMAGE_HEIGHT);
        imageView.setFitWidth(IMAGE_WIDTH);
        if (clickable) {
            imageView.setOnMouseEntered(event -> this.setCursor(Cursor.HAND));
            imageView.setOnMouseExited(event -> this.setCursor(Cursor.DEFAULT));
        }
        return imageView;
    }

    void setImageStyle(ImageView imageView) {
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setOnMouseEntered(event -> this.setCursor(Cursor.HAND));
        imageView.setOnMouseExited(event -> this.setCursor(Cursor.DEFAULT));
    }

    void showArrows(TilesPane tilesPane, Tile tile) {
        TilesPane.tilePane = tilesPane;
        TilesPane.tile = tile;
        setImageVisibility(true);
    }

    boolean isFirsMove() {
        TableInfo tableInfoD = round.getTableInfo();
        return tableInfoD.getTop() == null && tableInfoD.getRight() == null && tableInfoD.getBottom() == null && tableInfoD.getLeft() == null;
    }

    private void setImageVisibility(boolean visible) {
        upArrow.setVisible(visible);
        rightArrow.setVisible(visible);
        downArrow.setVisible(visible);
        leftArrow.setVisible(visible);
        TilesPane.arrowsVisible = visible;
    }

    static void onUpArrowPressed() {
        TableInfo tableInfo = round.getTableInfo();
        Integer top = tableInfo.getTop().getOpenSide();
        if ((top == tile.getLeft() || top == tile.getRight()) && !tableInfo.getLeft().isCenter() && !tableInfo.getRight().isCenter()) {
            tilePane.onTileEntered(tile, MoveDirection.TOP);
        } else {
            tilePane.onTileEntered(tile, null);
        }
    }

    static void onRightArrowPressed() {
        Integer right = round.getTableInfo().getRight().getOpenSide();
        if (right == tile.getLeft() || right == tile.getRight()) {
            tilePane.onTileEntered(tile, MoveDirection.RIGHT);
        } else {
            tilePane.onTileEntered(tile, null);
        }
    }

    static void onDownArrowPressed() {
        TableInfo tableInfo = round.getTableInfo();
        Integer bottom = tableInfo.getBottom().getOpenSide();
        if ((bottom == tile.getLeft()) || bottom == tile.getRight() && !tableInfo.getLeft().isCenter() && !tableInfo.getRight().isCenter()) {
            tilePane.onTileEntered(tile, MoveDirection.BOTTOM);
        } else {
            tilePane.onTileEntered(tile, null);
        }
    }

    static void onLeftArrowPressed() {
        Integer left = round.getTableInfo().getLeft().getOpenSide();
        if (left == tile.getLeft() || left == tile.getRight()) {
            tilePane.onTileEntered(tile, MoveDirection.LEFT);
        } else {
            tilePane.onTileEntered(tile, null);
        }
    }

    static boolean isArrowsVisible() {
        return arrowsVisible;
    }

    public abstract void onTilePressed(Tile tile);

    public abstract void onTileEntered(Tile tile, MoveDirection direction);

    public abstract boolean showTile(Tile tile);
}
