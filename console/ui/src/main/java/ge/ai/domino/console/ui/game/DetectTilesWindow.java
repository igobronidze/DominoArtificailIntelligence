package ge.ai.domino.console.ui.game;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.util.Messages;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

abstract class DetectTilesWindow {

	void showWindow(boolean firstRound) {
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.setTitle(Messages.get("detectTiles"));
		TCHLabel label = new TCHLabel(Messages.get("executeTilesDetector"));
		TCHButton yesButton = new TCHButton(Messages.get("yes"));
		yesButton.setOnAction(event -> {
			onYes();
			stage.close();
		});
		TCHButton startMeButton = new TCHButton(Messages.get("yesAndStartMe"));
		startMeButton.setOnAction(event -> {
			onStartMe();
			stage.close();
		});
		TCHButton startHeButton = new TCHButton(Messages.get("yesAndStartHe"));
		startHeButton.setOnAction(event -> {
			onStartHe();
			stage.close();
		});
		TCHButton noButton = new TCHButton(Messages.get("no"));
		noButton.setOnAction(event -> {
			onNo();
			stage.close();
		});
		HBox hBox = new HBox(25);
		hBox.setAlignment(Pos.TOP_CENTER);
		if (firstRound) {
			hBox.getChildren().addAll(startMeButton, startHeButton, noButton);
		} else {
			hBox.getChildren().addAll(yesButton, noButton);
		}
		VBox vBox = new VBox(30);
		vBox.setPadding(new Insets(20));
		vBox.setAlignment(Pos.TOP_CENTER);
		vBox.getChildren().addAll(label, hBox);
		stage.setScene(new Scene(vBox));
		stage.setWidth(350);
		stage.setHeight(140);
		stage.showAndWait();
	}

	public abstract void onYes();

	public abstract void onStartMe();

	public abstract void onStartHe();

	public abstract void onNo();
}
