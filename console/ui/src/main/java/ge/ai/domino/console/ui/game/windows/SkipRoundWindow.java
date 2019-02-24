package ge.ai.domino.console.ui.game.windows;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHFieldLabel;
import ge.ai.domino.console.ui.tchcomponents.TCHNumberTextField;
import ge.ai.domino.console.ui.util.Messages;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class SkipRoundWindow {

	public void showWindow() {
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.setTitle(Messages.get("skipRound"));

		TCHNumberTextField myPointField = new TCHNumberTextField(TCHComponentSize.MEDIUM);
		TCHFieldLabel myPointFieldLabel = new TCHFieldLabel(Messages.get("myPoint"), myPointField);

		TCHNumberTextField opponentPointField = new TCHNumberTextField(TCHComponentSize.MEDIUM);
		TCHFieldLabel opponentPointFieldLabel = new TCHFieldLabel(Messages.get("opponentPoint"), opponentPointField);

		TCHNumberTextField leftTilesField = new TCHNumberTextField(TCHComponentSize.MEDIUM);
		TCHFieldLabel leftTilesFieldLabel = new TCHFieldLabel(Messages.get("leftTilesCount"), leftTilesField);

		CheckBox startMeCheckBox = new CheckBox();
		TCHFieldLabel startMeFieldLabel = new TCHFieldLabel(Messages.get("startMe"), startMeCheckBox);

		CheckBox finishGameCheckBox = new CheckBox();
		TCHFieldLabel finishGameFieldLabel = new TCHFieldLabel(Messages.get("finishGame"), finishGameCheckBox);

		TCHButton skipButton = new TCHButton(Messages.get("skip"));
		skipButton.setOnAction(event -> {
			onSkip(myPointField.getNumber().intValue(), opponentPointField.getNumber().intValue(), leftTilesField.getNumber().intValue(), startMeCheckBox.isSelected(), finishGameCheckBox.isSelected());
			stage.close();
		});
		TCHButton cancelButton = new TCHButton(Messages.get("cancel"));
		cancelButton.setOnAction(event -> stage.close());
		HBox hBox = new HBox(25);
		hBox.setAlignment(Pos.TOP_CENTER);
		hBox.getChildren().addAll(skipButton, cancelButton);

		VBox vBox = new VBox(15);
		vBox.setPadding(new Insets(20));
		vBox.setAlignment(Pos.TOP_CENTER);
		vBox.getChildren().addAll(myPointFieldLabel, opponentPointFieldLabel, leftTilesFieldLabel, startMeFieldLabel, finishGameFieldLabel, hBox);

		stage.setScene(new Scene(vBox));
		stage.setWidth(400);
		stage.setHeight(400);
		stage.showAndWait();
	}

	public abstract void onSkip(int myPoint, int opponentPoint, int leftTilesCount, boolean startMe, boolean finishGame);
}
