package ge.ai.domino.console.ui.domino;

import ge.ai.domino.console.transfer.dto.domino.GameDTO;
import ge.ai.domino.console.transfer.dto.domino.PlayDirectionDTO;
import ge.ai.domino.console.transfer.dto.domino.TileDTO;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.transfer.dto.domino.PlayTypeDTO;
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

    private ImageView addImageView;

    HimTilesPane(GameDTO game, PlayTypeDTO playType) {
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
            hBox.setPadding(new Insets(10));
            for (int j = i; j >= 0; j--) {
                String uid = TileUtil.getTileUID(i, j);
                if (isPossible(uid)) {
                    TileDTO tile = tiles.get(uid);
                    ImageView imageView = getImageView(uid, true);
                    Label label = new Label("" + formatter.format(tile.getHim()));
                    label.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-font-weight: bold");
                    VBox vBox = new VBox(5);
                    vBox.setAlignment(Pos.TOP_CENTER);
                    vBox.getChildren().addAll(label, imageView);
                    hBox.getChildren().add(vBox);
                    imageView.setOnMouseClicked(e -> {
                        if (playType == PlayTypeDTO.HIM) {
                            if (!isFirsTurn()) {
                                imageView.setFitHeight(IMAGE_HEIGHT + 10);
                                imageView.setFitWidth(IMAGE_WIDTH + 10);
                                showArrows(this, uid);
                            } else {
                                onTileClick(tile, PlayDirectionDTO.LEFT);
                            }
                        } else {
                            onTileClick(tile, null);
                        }
                    });
                } else {
                    VBox vBox = new VBox();
                    vBox.setPrefHeight(IMAGE_HEIGHT);
                    vBox.setPrefWidth(IMAGE_WIDTH);
                    hBox.getChildren().addAll(vBox);
                }
            }
            this.getChildren().add(hBox);
        }
        if (playType == PlayTypeDTO.HIM) {
            addImageView = new ImageView(ImageFactory.getImage("add_black.png"));
            setImageStyle(addImageView);
            this.getChildren().add(addImageView);
            addImageView.setOnMouseClicked(e -> {
                onAddTileClick();
            });
        }
    }

    private boolean isPossible(String uid) {
        TileDTO tile = tiles.get(uid);
        return !tile.isPlayed() && tile.getMe() != 1.0;
    }

    public abstract void onAddTileClick();
}
