package ge.ai.domino.console.ui.domino;

import ge.ai.domino.console.transfer.dto.domino.GameDTO;
import ge.ai.domino.console.transfer.dto.domino.PlayDirectionDTO;
import ge.ai.domino.console.transfer.dto.domino.TileDTO;
import ge.ai.domino.console.transfer.dto.domino.PlayTypeDTO;
import ge.ai.domino.util.tile.TileUtil;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;

abstract class MyTilesPane extends TilesPane {

    MyTilesPane(GameDTO game, PlayTypeDTO playType) {
        super(game, playType);
        initUI();
        initComponents();
    }

    private void initUI() {
        this.setStyle("-fx-border-color: green; -fx-border-radius: 25px; -fx-border-size: 2px;");
        this.setPadding(new Insets(10));
    }

    @SuppressWarnings("Duplicates")
    private void initComponents() {
        for (String key : tiles.keySet()) {
            TileDTO tile = tiles.get(key);
            String uid = TileUtil.getTileUID(tile.getX(), tile.getY());
            if (isPossible(uid)) {
                ImageView imageView = getImageView(uid, playType == PlayTypeDTO.ME);
                this.getChildren().add(imageView);
                if (playType == PlayTypeDTO.ME) {
                    imageView.setOnMouseClicked(e -> {
                        if (!isFirsTurn()) {
                            imageView.setFitHeight(IMAGE_HEIGHT + 10);
                            imageView.setFitWidth(IMAGE_WIDTH + 10);
                            showArrows(this, uid);
                        } else {
                            onTileClick(tile, PlayDirectionDTO.LEFT);
                        }
                    });
                }
            }
        }
        this.getChildren().addAll(leftArrow, upArrow, downArrow, rightArrow);
    }

    private boolean isPossible(String uid) {
        TileDTO tile = tiles.get(uid);
        return !tile.isPlayed() && tile.getMe() == 1.0;
    }
}
