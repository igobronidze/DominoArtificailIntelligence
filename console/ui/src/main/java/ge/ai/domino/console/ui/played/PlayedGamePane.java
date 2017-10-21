package ge.ai.domino.console.ui.played;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHTextField;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.domain.played.GameResult;
import ge.ai.domino.service.played.PlayedGameService;
import ge.ai.domino.service.played.PlayedGameServiceImpl;
import ge.ai.domino.util.string.StringUtil;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class PlayedGamePane extends BorderPane {

    private final PlayedGameService playedGameService = new PlayedGameServiceImpl();

    private TableView<PlayedGameProperty> tableView;

    private TCHTextField versionField;

    private ComboBox<GameResult> resultComboBox;

    private TCHTextField opponentNameField;

    private TCHTextField websiteField;

    private DoubleBinding doubleBinding;

    public PlayedGamePane(DoubleBinding doubleBinding) {
        this.doubleBinding = doubleBinding;
        initUI();
    }

    private void initUI() {
        initFilters();
        initTable();
        loadPlayedGames();
    }

    private void initFilters() {
        versionField = new TCHTextField(TCHComponentSize.SMALL);
        versionField.setPromptText(Messages.get("version"));
        resultComboBox = new ComboBox<>();
        resultComboBox.getItems().add(null);
        resultComboBox.getItems().addAll(GameResult.values());
        resultComboBox.setConverter(new StringConverter<GameResult>() {
            @Override
            public String toString(GameResult object) {
                return Messages.get(object.name());
            }

            @Override
            public GameResult fromString(String string) {
                if (StringUtil.isEmpty(string)) {
                    return null;
                }
                return GameResult.valueOf(string);
            }
        });
        resultComboBox.setPrefWidth(180);
        resultComboBox.setMaxWidth(180);
        resultComboBox.setStyle("-fx-font-family: sylfaen; -fx-font-size: 14px;");
        opponentNameField = new TCHTextField(TCHComponentSize.SMALL);
        opponentNameField.setPromptText(Messages.get("opponentName"));
        websiteField = new TCHTextField(TCHComponentSize.SMALL);
        websiteField.setPromptText(Messages.get("website"));
        TCHButton searchButton = new TCHButton();
        searchButton.setGraphic(new ImageView(ImageFactory.getImage("search.png")));
        searchButton.setOnAction(e -> loadPlayedGames());
        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL);
        flowPane.setVgap(10);
        flowPane.setHgap(10);
        flowPane.setPadding(new Insets(15));
        flowPane.getChildren().addAll(versionField, resultComboBox, opponentNameField, websiteField, searchButton);
        this.setTop(flowPane);
    }

    @SuppressWarnings("unchecked")
    private void initTable() {
        tableView = new TableView<>();
        tableView.setStyle("-fx-font-family: sylfaen; -fx-text-alignment: center; -fx-font-size: 16px;");
        TableColumn<PlayedGameProperty, Boolean> idColumn = new TableColumn<>(Messages.get("id"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.prefWidthProperty().bind(doubleBinding.divide(9));
        TableColumn<PlayedGameProperty, Boolean> versionColumn = new TableColumn<>(Messages.get("version"));
        versionColumn.setCellValueFactory(new PropertyValueFactory<>("version"));
        versionColumn.prefWidthProperty().bind(doubleBinding.divide(9));
        TableColumn<PlayedGameProperty, Boolean> resultColumn = new TableColumn<>(Messages.get("result"));
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
        resultColumn.prefWidthProperty().bind(doubleBinding.divide(9));
        TableColumn<PlayedGameProperty, Boolean> dateColumn = new TableColumn<>(Messages.get("date"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.prefWidthProperty().bind(doubleBinding.divide(9));
        TableColumn<PlayedGameProperty, Boolean> myPointColumn = new TableColumn<>(Messages.get("myPoint"));
        myPointColumn.setCellValueFactory(new PropertyValueFactory<>("myPoint"));
        myPointColumn.prefWidthProperty().bind(doubleBinding.divide(9));
        TableColumn<PlayedGameProperty, Boolean> opponentPointColumn = new TableColumn<>(Messages.get("opponentPoint"));
        opponentPointColumn.setCellValueFactory(new PropertyValueFactory<>("opponentPoint"));
        opponentPointColumn.prefWidthProperty().bind(doubleBinding.divide(9));
        TableColumn<PlayedGameProperty, Boolean> pointForWinColumn = new TableColumn<>(Messages.get("pointForWin"));
        pointForWinColumn.setCellValueFactory(new PropertyValueFactory<>("pointForWin"));
        pointForWinColumn.prefWidthProperty().bind(doubleBinding.divide(9));
        TableColumn<PlayedGameProperty, Boolean> opponentNameColumn = new TableColumn<>(Messages.get("opponentName"));
        opponentNameColumn.setCellValueFactory(new PropertyValueFactory<>("opponentName"));
        opponentNameColumn.prefWidthProperty().bind(doubleBinding.divide(9));
        TableColumn<PlayedGameProperty, Boolean> websiteColumn = new TableColumn<>(Messages.get("website"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));
        websiteColumn.prefWidthProperty().bind(doubleBinding.divide(9));
        tableView.getColumns().addAll(idColumn, versionColumn, resultColumn, dateColumn, myPointColumn, opponentPointColumn,
                pointForWinColumn, opponentNameColumn, websiteColumn);
        this.setCenter(tableView);
    }

    private void loadPlayedGames() {
        List<PlayedGame> playedGames = playedGameService.getPlayedGames(versionField.getText(), resultComboBox.getValue() == null ? null : resultComboBox.getValue(),
                opponentNameField.getText(), websiteField.getText());
        List<PlayedGameProperty> playedGameProperties = new ArrayList<>();
        for (PlayedGame playedGame : playedGames) {
            playedGameProperties.add(new PlayedGameProperty(playedGame));
        }
        ObservableList<PlayedGameProperty> data = FXCollections.observableArrayList(playedGameProperties);
        tableView.setItems(data);
    }
}
