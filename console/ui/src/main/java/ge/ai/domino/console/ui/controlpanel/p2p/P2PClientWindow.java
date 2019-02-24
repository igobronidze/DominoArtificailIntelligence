package ge.ai.domino.console.ui.controlpanel.p2p;

import ge.ai.domino.console.ui.played.GroupedPlayedGameProperty;
import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.tchcomponents.TCHNumberTextField;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.domain.game.GameInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class P2PClientWindow {

    private TableView tableView;

    public void showWindow(Stage primaryStage) {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setTitle(Messages.get("p2pClient"));

        TCHLabel label = new TCHLabel(Messages.get("specifyGameAmount"));
        TCHNumberTextField countField = new TCHNumberTextField(TCHComponentSize.MEDIUM);
        countField.setNumber(new BigDecimal(1));

        TCHButton startButton = new TCHButton(Messages.get("start"));
        TCHButton closeButton = new TCHButton(Messages.get("close"));
        startButton.setOnAction(event -> {
            onStart(countField.getNumber().intValue());
            startButton.setDisable(true);
        });
        closeButton.setOnAction(event -> {
            onClose();
            stage.close();
        });

        HBox hBox = new HBox(25);
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.getChildren().addAll(startButton, closeButton);
        VBox vBox = new VBox(30);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.TOP_CENTER);

        initTable();
        vBox.getChildren().addAll(label, countField, hBox, tableView);

        stage.setScene(new Scene(vBox));
        stage.setWidth(600);
        stage.setHeight(670);
        stage.showAndWait();
    }

    private void initTable() {
        tableView = new TableView<>();
        tableView.setStyle("-fx-font-family: sylfaen; -fx-text-alignment: center; -fx-font-size: 16px;");
        TableColumn<GroupedPlayedGameProperty, Boolean> gameIdColumn = new TableColumn<>(Messages.get("gameId"));
        gameIdColumn.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        gameIdColumn.setPrefWidth(100);
        TableColumn<GroupedPlayedGameProperty, Boolean> myPointColumn = new TableColumn<>(Messages.get("myPoint"));
        myPointColumn.setCellValueFactory(new PropertyValueFactory<>("myPoint"));
        myPointColumn.setPrefWidth(150);
        TableColumn<GroupedPlayedGameProperty, Boolean> opponentPointColumn = new TableColumn<>(Messages.get("opponentPoint"));
        opponentPointColumn.setCellValueFactory(new PropertyValueFactory<>("opponentPoint"));
        opponentPointColumn.setPrefWidth(150);
        TableColumn<GroupedPlayedGameProperty, Boolean> winnerColumn = new TableColumn<>(Messages.get("winner"));
        winnerColumn.setCellValueFactory(new PropertyValueFactory<>("winner"));
        winnerColumn.setPrefWidth(150);
        tableView.getColumns().addAll(gameIdColumn, myPointColumn, opponentPointColumn, winnerColumn);
    }

    public void setGameInfos(List<GameInfo> gameInfos) {
        List<GameInfoProperty> gameInfoProperties = new ArrayList<>();
        for (GameInfo gameInfo : gameInfos) {
            gameInfoProperties.add(new GameInfoProperty(gameInfo));
        }
        ObservableList<GameInfoProperty> data = FXCollections.observableArrayList(gameInfoProperties);
        tableView.setItems(data);
    }

    public abstract void onStart(int count);

    public abstract void onClose();
}
