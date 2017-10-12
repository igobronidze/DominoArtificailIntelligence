package ge.ai.domino.console.ui.domino;

import ge.ai.domino.console.ui.controlpanel.AppController;
import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.domain.domino.game.GameInfo;
import ge.ai.domino.domain.domino.played.PlayedGame;
import ge.ai.domino.domain.domino.played.PlayedGameResult;
import ge.ai.domino.service.playedgame.PlayedGameService;
import ge.ai.domino.service.playedgame.PlayedGameServiceImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class SaveGameWindow extends Stage {

    private final PlayedGameService playedGameService = new PlayedGameServiceImpl();

    public void showWindow() {
        if (AppController.startedGame) {
            this.setResizable(false);
            this.setTitle(Messages.get("saveGame"));
            TCHLabel label = new TCHLabel(Messages.get("doYouWantToSaveGame"));
            TCHButton yesButton = new TCHButton(Messages.get("yes"));
            yesButton.setOnAction(event -> {
                GameInfo gameInfo = AppController.hand.getGameInfo();
                PlayedGame playedGame = new PlayedGame();
                if (gameInfo.isFinished()) {
                    if (gameInfo.getMyPoints() > gameInfo.getHimPoints()) {
                        playedGame.setResult(PlayedGameResult.WIN_ME);
                    } else {
                        playedGame.setResult(PlayedGameResult.WIN_HIM);
                    }
                } else {
                    playedGame.setResult(PlayedGameResult.STOPPED);
                }
                playedGame.setMyPoint(gameInfo.getMyPoints());
                playedGame.setHimPoint(gameInfo.getHimPoints());
                playedGame.setId(gameInfo.getGameId());
                playedGameService.updatePlayedGame(playedGame);
                this.close();
                onYes();
            });
            TCHButton noButton = new TCHButton(Messages.get("no"));
            noButton.setOnAction(event ->  {
                onNo();
                this.close();
            });
            TCHButton cancelButton = new TCHButton(Messages.get("cancel"));
            cancelButton.setOnAction(event -> {
                onCancel();
                this.close();
            });
            HBox hBox = new HBox(25);
            hBox.setAlignment(Pos.TOP_CENTER);
            hBox.getChildren().addAll(yesButton, noButton, cancelButton);
            VBox vBox = new VBox(30);
            vBox.setPadding(new Insets(20));
            vBox.setAlignment(Pos.TOP_CENTER);
            vBox.getChildren().addAll(label, hBox);
            this.setScene(new Scene(vBox));
            this.setWidth(400);
            this.setHeight(140);
            this.showAndWait();
        }
    }

    public abstract void onYes();

    public abstract void onNo();

    public abstract void onCancel();
}
