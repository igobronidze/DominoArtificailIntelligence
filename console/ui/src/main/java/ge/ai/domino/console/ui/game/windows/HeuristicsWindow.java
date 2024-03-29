package ge.ai.domino.console.ui.game.windows;

import ge.ai.domino.console.ui.controlpanel.AppController;
import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.service.ServiceExecutor;
import ge.ai.domino.service.heuristic.HeuristicService;
import ge.ai.domino.service.heuristic.HeuristicServiceImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class HeuristicsWindow {

	private final HeuristicService heuristicService = new HeuristicServiceImpl();

	public void showWindow() {
		Map<String, Double> heuristics = new HashMap<>();
		new ServiceExecutor() {}.execute(() -> heuristics.putAll(heuristicService.getHeuristics(AppController.round.getGameInfo().getGameId())));

		Stage stage = new Stage();
		stage.setResizable(false);
		stage.setTitle(Messages.get("heuristic"));

		NumberFormat formatter = new DecimalFormat("#0.000");

		VBox heuristicsBox = new VBox(10);
		for (Map.Entry<String, Double> entry : heuristics.entrySet()) {
			HBox hBox = new HBox();
			TCHLabel typeLabel = new TCHLabel(entry.getKey());
			typeLabel.setPrefWidth(430);
			typeLabel.setStyle("-fx-font-family: sylfaen; -fx-font-size: 18px;");
			TCHLabel valueLabel = new TCHLabel(formatter.format(entry.getValue()));
			valueLabel.setStyle("-fx-font-family: sylfaen; -fx-font-size: 18px;");
			hBox.getChildren().addAll(typeLabel, valueLabel);
			heuristicsBox.getChildren().add(hBox);
		}
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(heuristicsBox);
		scrollPane.setPrefHeight(300);

		TCHButton cancelButton = new TCHButton(Messages.get("cancel"));
		cancelButton.setOnAction(event -> {
			stage.close();
		});
		HBox hBox = new HBox(25);
		hBox.setAlignment(Pos.TOP_CENTER);
		hBox.getChildren().addAll(cancelButton);
		VBox vBox = new VBox(30);
		vBox.setPadding(new Insets(20));
		vBox.setAlignment(Pos.TOP_CENTER);
		vBox.getChildren().addAll(scrollPane, hBox);

		stage.setScene(new Scene(vBox));
		stage.setWidth(550);
		stage.setHeight(330);
		stage.showAndWait();
	}
}
