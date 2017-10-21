package ge.ai.domino.console.ui.game;

import ge.ai.domino.domain.ai.AIPrediction;
import ge.ai.domino.domain.ai.HeuristicInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.tile.Tile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.text.NumberFormat;

abstract class MyTilesPane extends TilesPane {

    MyTilesPane(Round round) {
        super(round);
        initUI();
        initComponents();
    }

    private void initUI() {
        this.setStyle("-fx-border-color: green; -fx-border-radius: 25px; -fx-border-size: 2px;");
        this.setPadding(new Insets(8));
    }

    @SuppressWarnings("Duplicates")
    private void initComponents() {
        for (Tile tile : round.getMyTiles()) {
            if (showTile(tile)) {
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.TOP_CENTER);
                ImageView imageView = getImageView(tile, round.getTableInfo().isMyMove());
                imageViews.put(tile.hashCode(), imageView);
                AIPrediction aiPrediction = round.getAiPrediction();
                if (aiPrediction != null) {
                    int bestX = aiPrediction.getLeft();
                    int bestY = aiPrediction.getRight();
                    if (tile.getLeft() == bestX && tile.getRight() == bestY) {
                        NumberFormat formatter = new DecimalFormat("#0.0000");
                        HeuristicInfo heuristicInfo = round.getHeuristicInfo();
                        Label label = new Label(aiPrediction.getDirection().name() + "(" + formatter.format(heuristicInfo.getValue()) + ")");
                        vBox.getChildren().add(label);
                    }
                }
                imageView.setOnMouseClicked(e -> onTilePressed(tile));
                vBox.getChildren().add(imageView);
                this.getChildren().add(vBox);
            }
        }
        this.getChildren().addAll(leftArrow, upArrow, downArrow, rightArrow);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void onTilePressed(Tile tile) {
        if (round.getTableInfo().isMyMove()) {
            if (!isFirsMove()) {
                imageViews.get(tile.hashCode()).setFitHeight(IMAGE_HEIGHT + 10);
                imageViews.get(tile.hashCode()).setFitWidth(IMAGE_WIDTH + 10);
                showArrows(this, tile);
            } else {
                onTileEntered(tile, MoveDirection.LEFT);
            }
        }
    }

    @Override
    public boolean showTile(Tile tile) {
        return myTiles.contains(tile);
    }
}
