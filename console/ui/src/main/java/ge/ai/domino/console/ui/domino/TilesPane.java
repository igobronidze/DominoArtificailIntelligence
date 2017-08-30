package ge.ai.domino.console.ui.domino;

import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.util.HashMap;
import java.util.Map;

abstract class TilesPane extends FlowPane {

    static final int IMAGE_WIDTH = 65;

    static final int IMAGE_HEIGHT = 110;

    static Map<String, Tile> tiles;

    static Game game;

    static ImageView upArrow;

    static ImageView rightArrow;

    static ImageView downArrow;

    static ImageView leftArrow;

    static private TilesPane tilePane;

    static private String tileUID;

    static private boolean arrowsVisible;

    PlayType playType;

    Map<String, ImageView> imageViews = new HashMap<>();

    TilesPane(Game game, PlayType playType) {
        this.tiles = game.getCurrHand().getTiles();
        this.playType = playType;
        this.game = game;
        initBasicUI();
    }

    private void initBasicUI() {
        this.setHgap(25);
        this.setVgap(10);
        upArrow = new ImageView(ImageFactory.getImage("arrows/up.png"));
        setImageStyle(upArrow);
        upArrow.setOnMouseClicked(e -> {
            onUpArrowPressed();
        });
        rightArrow = new ImageView(ImageFactory.getImage("arrows/right.png"));
        setImageStyle(rightArrow);
        rightArrow.setOnMouseClicked(e -> {
            onRightArrowPressed();
        });
        downArrow = new ImageView(ImageFactory.getImage("arrows/down.png"));
        setImageStyle(downArrow);
        downArrow.setOnMouseClicked(e -> {
            onDownArrowPressed();
        });
        leftArrow = new ImageView(ImageFactory.getImage("arrows/left.png"));
        leftArrow.setOnMouseClicked(e -> {
            onLeftArrowPressed();
        });
        setImageStyle(leftArrow);
        setImageVisibility(false);
    }

    ImageView getImageView(String uid, boolean clickable) {
        ImageView imageView = new ImageView(ImageFactory.getImage("tiles/" + uid + ".png"));
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

    void showArrows(TilesPane tilesPane, String tileUID) {
        TilesPane.tilePane = tilesPane;
        TilesPane.tileUID = tileUID;
        setImageVisibility(true);
    }

    boolean isFirsTurn() {
        Hand hand = game.getCurrHand();
        TableInfo tableInfoD = hand.getTableInfo();
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
        TableInfo tableInfo = game.getCurrHand().getTableInfo();
        Integer top = tableInfo.getTop().getOpenSide();
        if ((top == tiles.get(tileUID).getX() || top == tiles.get(tileUID).getY()) && !tableInfo.getLeft().isCenter() && !tableInfo.getRight().isCenter()) {
            tilePane.onTileEntered(tiles.get(tileUID), PlayDirection.TOP);
        } else {
            tilePane.onTileEntered(tiles.get(tileUID), PlayDirection.INCORRECT);
        }
    }

    static void onRightArrowPressed() {
        Integer right = game.getCurrHand().getTableInfo().getRight().getOpenSide();
        if (right == tiles.get(tileUID).getX() || right == tiles.get(tileUID).getY()) {
            tilePane.onTileEntered(tiles.get(tileUID), PlayDirection.RIGHT);
        } else {
            tilePane.onTileEntered(tiles.get(tileUID), PlayDirection.INCORRECT);
        }
    }

    static void onDownArrowPressed() {
        TableInfo tableInfo = game.getCurrHand().getTableInfo();
        Integer bottom = tableInfo.getBottom().getOpenSide();
        if ((bottom == tiles.get(tileUID).getX() || bottom == tiles.get(tileUID).getY()) && !tableInfo.getLeft().isCenter() && !tableInfo.getRight().isCenter()) {
            tilePane.onTileEntered(tiles.get(tileUID), PlayDirection.BOTTOM);
        } else {
            tilePane.onTileEntered(tiles.get(tileUID), PlayDirection.INCORRECT);
        }
    }

    static void onLeftArrowPressed() {
        Integer left = game.getCurrHand().getTableInfo().getLeft().getOpenSide();
        if (left == tiles.get(tileUID).getX() || left == tiles.get(tileUID).getY()) {
            tilePane.onTileEntered(tiles.get(tileUID), PlayDirection.LEFT);
        } else {
            tilePane.onTileEntered(tiles.get(tileUID), PlayDirection.INCORRECT);
        }
    }

    static boolean isArrowsVisible() {
        return arrowsVisible;
    }

    public abstract void onTilePressed(String uid);

    public abstract void onTileEntered(Tile tile, PlayDirection direction);

    public abstract boolean showTile(String uid);
}
