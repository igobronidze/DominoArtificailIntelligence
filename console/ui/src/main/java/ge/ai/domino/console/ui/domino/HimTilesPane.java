package ge.ai.domino.console.ui.domino;

import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.util.tile.TileUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.text.NumberFormat;

abstract class HimTilesPane extends TilesPane {

    HimTilesPane(Game game, PlayType playType) {
        super(game, playType);
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
                String uid = TileUtil.getTileUID(i, j);
                if (showTile(uid)) {
                    Tile tile = tiles.get(uid);
                    ImageView imageView = getImageView(uid, true);
                    Label himLabel = new Label("" + formatter.format(tile.getHim()));
                    himLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-font-weight: bold");
                    Label bazaarLabel = new Label("" + formatter.format(tile.getBazaar()));
                    bazaarLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-font-weight: bold");
                    VBox vBox = new VBox(5);
                    vBox.setAlignment(Pos.TOP_CENTER);
                    vBox.getChildren().addAll(himLabel, imageView, bazaarLabel);
                    hBox.getChildren().add(vBox);
                    imageViews.put(uid, imageView);
                    imageView.setOnMouseClicked(e -> onTilePressed(uid));
                } else {
                    VBox vBox = new VBox();
                    vBox.setPrefHeight(IMAGE_HEIGHT);
                    vBox.setPrefWidth(IMAGE_WIDTH);
                    hBox.getChildren().addAll(vBox);
                }
            }
            this.getChildren().add(hBox);
        }
        if (playType == PlayType.HIM) {
            ImageView addImageView = new ImageView(ImageFactory.getImage("add_black.png"));
            setImageStyle(addImageView);
            this.getChildren().add(addImageView);
            addImageView.setOnMouseClicked(e -> onAddTileEntered());
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void onTilePressed(String uid) {
        if (playType == PlayType.HIM) {
            if (!isFirsTurn()) {
                imageViews.get(uid).setFitHeight(IMAGE_HEIGHT + 10);
                imageViews.get(uid).setFitWidth(IMAGE_WIDTH + 10);
                showArrows(this, uid);
            } else {
                onTileEntered(tiles.get(uid), PlayDirection.LEFT);
            }
        } else {
            onTileEntered(tiles.get(uid), null);
        }
    }

    @Override
    public boolean showTile(String uid) {
        Tile tile = tiles.get(uid);
        return !tile.isPlayed() && tile.getMe() != 1.0;
    }

    public abstract void onAddTileEntered();
}
