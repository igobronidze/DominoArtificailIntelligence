package ge.ai.domino.console.ui.domino;

import ge.ai.domino.console.transfer.dto.domino.GameDTO;
import ge.ai.domino.console.transfer.dto.domino.HandDTO;
import ge.ai.domino.console.transfer.dto.domino.PlayDirectionDTO;
import ge.ai.domino.console.transfer.dto.domino.TileDTO;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.transfer.dto.domino.PlayTypeDTO;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.util.Map;

abstract class TilesPane extends FlowPane {

    static final int IMAGE_WIDTH = 65;

    static final int IMAGE_HEIGHT = 115;

    Map<String, TileDTO> tiles;

    GameDTO game;

    PlayTypeDTO playType;

    ImageView upArrow;

    ImageView rightArrow;

    ImageView downArrow;

    ImageView leftArrow;

    private TilesPane tilePane;

    private String tileUID;

    TilesPane(GameDTO game, PlayTypeDTO playType) {
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
        final HandDTO hand = game.getCurrHand();
        final TileDTO clickedTile = tiles.get(tileUID);
        upArrow.setOnMouseClicked(e -> {
            Integer top = hand.getTop();
            if (top != null && (top == clickedTile.getX() || top == clickedTile.getY())) {
                tilePane.onTileClick(clickedTile, PlayDirectionDTO.TOP);
            } else {
                tilePane.onTileClick(clickedTile, PlayDirectionDTO.INCORRECT);
            }
        });
        rightArrow = new ImageView(ImageFactory.getImage("arrows/right.png"));
        setImageStyle(rightArrow);
        rightArrow.setOnMouseClicked(e -> {
            Integer right = hand.getRight();
            if (right != null && (right == clickedTile.getX() || right == clickedTile.getY())) {
                tilePane.onTileClick(clickedTile, PlayDirectionDTO.RIGHT);
            } else {
                tilePane.onTileClick(clickedTile, PlayDirectionDTO.INCORRECT);
            }
        });
        downArrow = new ImageView(ImageFactory.getImage("arrows/down.png"));
        setImageStyle(downArrow);
        downArrow.setOnMouseClicked(e -> {
            Integer bottom = hand.getBottom();
            if (bottom != null && (bottom == clickedTile.getX() || bottom == clickedTile.getY())) {
                tilePane.onTileClick(clickedTile, PlayDirectionDTO.BOTTOM);
            } else {
                tilePane.onTileClick(clickedTile, PlayDirectionDTO.INCORRECT);
            }
        });
        leftArrow = new ImageView(ImageFactory.getImage("arrows/left.png"));
        leftArrow.setOnMouseClicked(e -> {
            Integer left = hand.getLeft();
            if (left != null && (left == clickedTile.getX() || left == clickedTile.getY())) {
                tilePane.onTileClick(clickedTile, PlayDirectionDTO.LEFT);
            } else {
                tilePane.onTileClick(clickedTile, PlayDirectionDTO.INCORRECT);
            }
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
        this.tilePane = tilesPane;
        this.tileUID = tileUID;
        setImageVisibility(true);
    }

    boolean isFirsTurn() {
        HandDTO hand = game.getCurrHand();
        return hand.getTop() == null && hand.getRight() == null && hand.getBottom() == null && hand.getLeft() == null;
    }

    private void setImageVisibility(boolean visible) {
        upArrow.setVisible(visible);
        rightArrow.setVisible(visible);
        downArrow.setVisible(visible);
        leftArrow.setVisible(visible);
    }

    public abstract void onTileClick(TileDTO tile, PlayDirectionDTO direction);
}
