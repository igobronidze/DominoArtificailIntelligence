package ge.ai.domino.console.ui.domino;

import ge.ai.domino.domain.domino.AIPrediction;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.util.tile.TileUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

abstract class MyTilesPane extends TilesPane {

    MyTilesPane(Hand hand, PlayType playType) {
        super(hand, playType);
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
            Tile tile = tiles.get(key);
            String uid = TileUtil.getTileUID(tile.getX(), tile.getY());
            if (showTile(uid)) {
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.TOP_CENTER);
                ImageView imageView = getImageView(uid, playType == PlayType.ME);
                imageViews.put(uid, imageView);
                AIPrediction aiPrediction = hand.getAiPrediction();
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
        if (playType == PlayType.ME) {
            if (!isFirsTurn()) {
                imageViews.get(uid).setFitHeight(IMAGE_HEIGHT + 10);
                imageViews.get(uid).setFitWidth(IMAGE_WIDTH + 10);
                showArrows(this, uid);
            } else {
                onTileEntered(tiles.get(uid), PlayDirection.LEFT);
            }
        }
    }

    @Override
    public boolean showTile(String uid) {
        Tile tile = tiles.get(uid);
        return !tile.isPlayed() && tile.getMe() == 1.0;
    }
}
