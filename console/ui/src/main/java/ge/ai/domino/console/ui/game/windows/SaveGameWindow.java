package ge.ai.domino.console.ui.game.windows;

import ge.ai.domino.console.ui.controlpanel.AppController;
import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHCheckBox;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.service.played.PlayedGameService;
import ge.ai.domino.service.played.PlayedGameServiceImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class SaveGameWindow extends Stage {

    private final PlayedGameService playedGameService = new PlayedGameServiceImpl();

    public void showWindow(boolean specifyWinner) {
        if (AppController.round != null) {
            this.setResizable(false);
            this.setTitle(Messages.get("saveGame"));
            VBox optionsVBox = new VBox(10);
            TCHCheckBox saveGameCheckBox = new TCHCheckBox(Messages.get("saveGame2"));
            saveGameCheckBox.setSelected(true);
            TCHCheckBox saveOpponentPlaysCheckBox = new TCHCheckBox(Messages.get("saveOpponentPlays"));
            saveOpponentPlaysCheckBox.setSelected(true);
            optionsVBox.getChildren().addAll(saveGameCheckBox, saveOpponentPlaysCheckBox);
            TCHButton saveButton = new TCHButton(Messages.get("save"));
            saveButton.setOnAction(event -> {
                playedGameService.finishGame(AppController.round.getGameInfo().getGameId(), saveGameCheckBox.isSelected(), saveOpponentPlaysCheckBox.isSelected(), specifyWinner);
                onSave();
                this.close();
            });
            TCHButton cancelButton = new TCHButton(Messages.get("cancel"));
            cancelButton.setOnAction(event -> {
                onCancel();
                this.close();
            });
            this.setOnCloseRequest(we -> onCancel());
            HBox hBox = new HBox(25);
            hBox.setAlignment(Pos.TOP_CENTER);
            hBox.getChildren().addAll(saveButton, cancelButton);
            VBox vBox = new VBox(30);
            vBox.setPadding(new Insets(20));
            vBox.setAlignment(Pos.TOP_CENTER);
            vBox.getChildren().addAll(optionsVBox, hBox);
            this.setScene(new Scene(vBox));
            this.setWidth(400);
            this.setHeight(165);
            this.showAndWait();

            this.setOnCloseRequest(we -> {
                if (AppController.round != null) {
                    onCancel();
                }
            });
        }
    }

    public abstract void onSave();

    public abstract void onCancel();
}
