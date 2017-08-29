package ge.ai.domino.console.ui.domino;

import ge.ai.domino.console.transfer.dto.domino.AIPredictionDTO;
import ge.ai.domino.console.transfer.dto.domino.GameDTO;
import ge.ai.domino.console.transfer.dto.domino.PlayDirectionDTO;
import ge.ai.domino.console.transfer.dto.domino.PlayTypeDTO;
import ge.ai.domino.console.transfer.dto.domino.TileDTO;
import ge.ai.domino.util.tile.TileUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

abstract class MyTilesPane extends TilesPane {

    MyTilesPane(GameDTO game, PlayTypeDTO playType) {
        super(game, playType);
        initUI();
        initComponents();
    }

    private void initUI() {
        this.setStyle("-fx-border-color: green; -fx-border-radius: 25px; -fx-border-size: 2px;");
        this.setPadding(new Insets(8));
    }

    @SuppressWarnings("Duplicates")
    private void initComponents() {
        for (String key : tiles.keySet()) {
            TileDTO tile = tiles.get(key);
            String uid = TileUtil.getTileUID(tile.getX(), tile.getY());
            if (showTile(uid)) {
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.TOP_CENTER);
                ImageView imageView = getImageView(uid, playType == PlayTypeDTO.ME);
                imageViews.put(uid, imageView);
                AIPredictionDTO aiPrediction = game.getCurrHand().getAiPrediction();
                if (aiPrediction != null) {
                    int bestX = aiPrediction.getX();
                    int bestY = aiPrediction.getY();
                    if (tile.getX() == bestX && tile.getY() == bestY) {
                        Label label = new Label(aiPrediction.getDirection().name());
                        vBox.getChildren().add(label);
                    }
                }
                imageView.setOnMouseClicked(e -> onTilePressed(uid));
                vBox.getChildren().add(imageView);
                this.getChildren().add(vBox);
            }
        }
        this.getChildren().addAll(leftArrow, upArrow, downArrow, rightArrow);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void onTilePressed(String uid) {
        if (playType == PlayTypeDTO.ME) {
            if (!isFirsTurn()) {
                imageViews.get(uid).setFitHeight(IMAGE_HEIGHT + 10);
                imageViews.get(uid).setFitWidth(IMAGE_WIDTH + 10);
                showArrows(this, uid);
            } else {
                onTileEntered(tiles.get(uid), PlayDirectionDTO.LEFT);
            }
        }
    }

    @Override
    public boolean showTile(String uid) {
        TileDTO tile = tiles.get(uid);
        return !tile.isPlayed() && tile.getMe() == 1.0;
    }
}
