package ge.ai.domino.console.ui.replaygame;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.tchcomponents.TCHNumberTextField;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.service.ServiceExecutor;
import ge.ai.domino.domain.played.PlayedMove;
import ge.ai.domino.domain.played.ReplayMoveInfo;
import ge.ai.domino.service.game.GameService;
import ge.ai.domino.service.game.GameServiceImpl;
import ge.ai.domino.service.replaygame.ReplayGameService;
import ge.ai.domino.service.replaygame.ReplayGameServiceImpl;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class ReplayGamePane extends BorderPane {

    private final ReplayGameService replayGameService = new ReplayGameServiceImpl();

    private final GameService gameService = new GameServiceImpl();

    private ReplayMoveInfo replayMoveInfo;

    public ReplayGamePane() {
        initUI();
    }

    private void initUI() {
        this.setPadding(new Insets(5));
        initTopPane();
        initMainPane();
    }

    private void initTopPane() {
        TCHNumberTextField gameIdField = new TCHNumberTextField(TCHComponentSize.MEDIUM);
        gameIdField.setNumber(new BigDecimal(0));

        TCHButton startGameButton = new TCHButton(Messages.get("start"));
        startGameButton.setOnAction(e -> ServiceExecutor.execute(() -> {
            replayMoveInfo = replayGameService.startReplayGame(gameIdField.getNumber().intValue());
            initMainPane();
        }));

        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL);
        flowPane.setVgap(10);
        flowPane.setHgap(20);
        flowPane.setPadding(new Insets(5));
        flowPane.getChildren().addAll(gameIdField, startGameButton);
        this.setTop(flowPane);
    }

    private void initMainPane() {
        VBox mainVBox = new VBox(10);
        mainVBox.setPadding(new Insets(15));

        TCHLabel previousMoveLabel = new TCHLabel(getPreviousMoveInfo());
        TCHLabel nextMoveLabel = new TCHLabel(getNextMoveInfo());
        mainVBox.getChildren().addAll(previousMoveLabel, nextMoveLabel);

        ImageView replayImage = new ImageView(ImageFactory.getImage("skip.png"));
        replayImage.setCursor(Cursor.HAND);
        replayImage.setOnMouseClicked(event -> ServiceExecutor.execute(() -> {
            replayMoveInfo = replayGameService.replayMove(replayMoveInfo.getGameId(), replayMoveInfo.getMoveIndex());
            initMainPane();
        }));
        ImageView undoImage = new ImageView(ImageFactory.getImage("undo.png"));
        undoImage.setCursor(Cursor.HAND);
        undoImage.setOnMouseClicked(event -> ServiceExecutor.execute(() -> {
            replayMoveInfo = replayGameService.undoReplayedMove(replayMoveInfo.getGameId(), replayMoveInfo.getMoveIndex());
            initMainPane();
        }));

        HBox hBox = new HBox(20);
        if (replayMoveInfo != null && replayMoveInfo.getNextMove() != null) {
            hBox.getChildren().add(replayImage);
        }
        if (replayMoveInfo != null && replayMoveInfo.getMoveIndex() != 0) {
            hBox.getChildren().add(undoImage);
        }
        mainVBox.getChildren().add(hBox);

        ScrollPane scrollPane = new ScrollPane();
        TCHLabel gameInfoLabel = new TCHLabel(replayMoveInfo == null ? "" : gameService.getCurrentRoundInfoInString(replayMoveInfo.getGameId()));
        scrollPane.setContent(gameInfoLabel);
        scrollPane.setPrefHeight(800);
        scrollPane.setMaxHeight(540);
        mainVBox.getChildren().add(scrollPane);

        this.setCenter(mainVBox);
    }

    private String getPreviousMoveInfo() {
        String info = Messages.get("previousMove") + ": ";
        if (replayMoveInfo != null) {
            PlayedMove playedMove = replayMoveInfo.getPreviousMove();
            if (playedMove != null) {
                info += playedMove.toString();
            }
        }
        return info;
    }

    private String getNextMoveInfo() {
        String info = Messages.get("nextMove") + ": ";
        if (replayMoveInfo != null) {
            PlayedMove playedMove = replayMoveInfo.getNextMove();
            if (playedMove != null) {
                info += playedMove.toString();
            }
        }
        return info;
    }
}
