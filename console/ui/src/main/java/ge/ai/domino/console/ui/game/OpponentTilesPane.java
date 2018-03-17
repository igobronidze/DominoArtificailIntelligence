package ge.ai.domino.console.ui.game;

import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.MoveDirection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.text.NumberFormat;

abstract class OpponentTilesPane extends TilesPane {

    OpponentTilesPane(Round round) {
        super(round);
        initComponents();
    }

    @SuppressWarnings("Duplicates")
    private void initComponents() {
        NumberFormat formatter = new DecimalFormat("#0.0000");
        for (int i = 6; i >= 0; i--) {
            HBox hBox = new HBox();
            hBox.setStyle("-fx-border-color: green; -fx-border-radius: 25px; -fx-border-size: 1px;");
            hBox.setSpacing(10);
            hBox.setPadding(new Insets(8));
            for (int j = i; j >= 0; j--) {
                Tile tile = new Tile(i, j);
                float prob = opponentTiles.get(tile);
                if (showTile(tile)) {
                    ImageView imageView = getImageView(tile, true);
                    Label opponentLabel = new Label("" + formatter.format(prob));
                    opponentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-font-weight: bold");
                    VBox vBox = new VBox(5);
                    vBox.setAlignment(Pos.TOP_CENTER);
                    vBox.getChildren().addAll(opponentLabel, imageView);
                    hBox.getChildren().add(vBox);
                    imageViews.put(tile, imageView);
                    imageView.setOnMouseClicked(e -> onTilePressed(tile));
                } else {
                    VBox vBox = new VBox();
                    vBox.setPrefHeight(IMAGE_HEIGHT);
                    vBox.setPrefWidth(IMAGE_WIDTH);
                    hBox.getChildren().addAll(vBox);
                }
            }
            this.getChildren().add(hBox);
        }
        if (!round.getTableInfo().isMyMove()) {
            ImageView addImageView = new ImageView(ImageFactory.getImage("add_black.png"));
            addImageView.setOnMouseEntered(event -> this.setCursor(Cursor.HAND));
            addImageView.setOnMouseExited(event -> this.setCursor(Cursor.DEFAULT));
            setImageStyle(addImageView);
            this.getChildren().add(addImageView);
            addImageView.setOnMouseClicked(e -> onAddTileEntered());
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void onTilePressed(Tile tile) {
        if (!round.getTableInfo().isMyMove()) {
            if (!isFirsMove()) {
                imageViews.get(tile.hashCode()).setFitHeight(IMAGE_HEIGHT + 10);
                imageViews.get(tile.hashCode()).setFitWidth(IMAGE_WIDTH + 10);
                showArrows(this, tile);
            } else {
                onTileEntered(tile, MoveDirection.LEFT);
            }
        } else {
            onTileEntered(tile, MoveDirection.LEFT);
        }
    }

    @Override
    public boolean showTile(Tile tile) {
        return tile != null && opponentTiles.containsKey(tile.hashCode());
    }

    public abstract void onAddTileEntered();
}
