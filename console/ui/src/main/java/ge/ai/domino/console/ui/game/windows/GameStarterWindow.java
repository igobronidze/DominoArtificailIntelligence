package ge.ai.domino.console.ui.game.windows;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.util.Messages;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class GameStarterWindow {

	public void showWindow() {
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.setTitle(Messages.get("gameStarter"));
		TCHLabel label = new TCHLabel(Messages.get("whoStartGame"));
		TCHButton meButton = new TCHButton(Messages.get("me"));
		meButton.setOnAction(event -> {
			onMe();
			stage.close();
		});
		TCHButton opponentButton = new TCHButton(Messages.get("he"));
		opponentButton.setOnAction(event -> {
			onHe();
			stage.close();
		});
		HBox hBox = new HBox(25);
		hBox.setAlignment(Pos.TOP_CENTER);
		hBox.getChildren().addAll(meButton, opponentButton);
		VBox vBox = new VBox(30);
		vBox.setPadding(new Insets(20));
		vBox.setAlignment(Pos.TOP_CENTER);
		vBox.getChildren().addAll(label, hBox);
		stage.setScene(new Scene(vBox));
		stage.setWidth(350);
		stage.setHeight(140);
		stage.showAndWait();
	}

	public abstract void onMe();

	public abstract void onHe();
}
