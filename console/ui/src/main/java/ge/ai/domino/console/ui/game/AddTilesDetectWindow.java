package ge.ai.domino.console.ui.game;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHCheckBox;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.util.Messages;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

abstract class AddTilesDetectWindow {

	void showWindow() {
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.setTitle(Messages.get("detectAddedTiles"));
		TCHLabel label = new TCHLabel(Messages.get("executeAddedTilesDetector"));
		TCHCheckBox withSecondParams = new TCHCheckBox(Messages.get("withSecondParams"));
		TCHButton yesButton = new TCHButton(Messages.get("yes"));
		yesButton.setOnAction(event -> {
			onYes(withSecondParams.isSelected());
			stage.close();
		});
		yesButton.requestFocus();
		TCHButton noButton = new TCHButton(Messages.get("no"));
		noButton.setOnAction(event -> {
			onNo();
			stage.close();
		});
		HBox hBox = new HBox(25);
		hBox.setAlignment(Pos.TOP_CENTER);
		hBox.getChildren().addAll(yesButton, noButton);
		VBox vBox = new VBox(30);
		vBox.setPadding(new Insets(20));
		vBox.setAlignment(Pos.TOP_CENTER);
		vBox.getChildren().addAll(label, hBox, withSecondParams);
		stage.setScene(new Scene(vBox));
		stage.setWidth(390);
		stage.setHeight(200);
		stage.showAndWait();
	}

	public abstract void onYes(boolean withSecondParams);

	public abstract void onNo();
}
