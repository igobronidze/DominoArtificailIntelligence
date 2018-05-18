package ge.ai.domino.console.ui.opponentplays;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHCheckBox;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHFieldLabel;
import ge.ai.domino.console.ui.tchcomponents.TCHTextField;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.domain.game.opponentplay.GroupedOpponentPlay;
import ge.ai.domino.service.opponentplays.GroupedOpponentPlaysService;
import ge.ai.domino.service.opponentplays.GroupedOpponentPlaysServiceImpl;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupedOpponentPlaysPane extends BorderPane {

    private final GroupedOpponentPlaysService groupedOpponentPlaysService = new GroupedOpponentPlaysServiceImpl();

    private TableView tableView;

    private DoubleBinding doubleBinding;

    private TCHCheckBox groupByOpponentGameIdCheckBox;

    private TCHCheckBox groupByVersionCheckBox;

    private TCHTextField gameIdField;

    private TCHTextField versionField;

    private ObservableList<Map<String, String>> data;

    public GroupedOpponentPlaysPane(DoubleBinding doubleBinding) {
        this.doubleBinding = doubleBinding;
        initUI();
    }

    private void initUI() {
        initFilters();
        loadPlayedGames();
        initTable();
        tableView.setItems(data);
    }

    private void initFilters() {
        gameIdField = new TCHTextField(TCHComponentSize.SMALL);
        TCHFieldLabel gameIdFieldLabel = new TCHFieldLabel(Messages.get("gameId"), gameIdField);
        versionField = new TCHTextField(TCHComponentSize.SMALL);
        TCHFieldLabel versionFieldLabel = new TCHFieldLabel(Messages.get("version"), versionField);
        groupByOpponentGameIdCheckBox = new TCHCheckBox(Messages.get("groupByGameId"));
        groupByVersionCheckBox = new TCHCheckBox(Messages.get("groupByVersion"));
        TCHButton searchButton = new TCHButton();
        searchButton.setGraphic(new ImageView(ImageFactory.getImage("search.png")));
        searchButton.setOnAction(e -> {
            loadPlayedGames();
            tableView.setItems(data);
        });
        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL);
        flowPane.setVgap(10);
        flowPane.setHgap(10);
        flowPane.setPadding(new Insets(15));
        flowPane.getChildren().addAll(gameIdFieldLabel, versionFieldLabel, groupByOpponentGameIdCheckBox, groupByVersionCheckBox, searchButton);
        this.setTop(flowPane);
    }

    @SuppressWarnings("unchecked")
    private void initTable() {
        tableView = new TableView<>();
        tableView.setStyle("-fx-font-family: sylfaen; -fx-text-alignment: center; -fx-font-size: 16px;");

        if (data != null && !data.isEmpty()) {
            int columnsCount = data.get(0).size();

            TableColumn<Map<String, String>, Boolean> gameIdColumn = new TableColumn<>(Messages.get("gameId"));
            gameIdColumn.setCellValueFactory(new MapValueFactory(GroupedOpponentPlaysProperty.GAME_ID_KEY));
            gameIdColumn.prefWidthProperty().bind(doubleBinding.divide(columnsCount));
            tableView.getColumns().add(gameIdColumn);
            TableColumn<Map<String, String>, Boolean> versionColumn = new TableColumn<>(Messages.get("version"));
            versionColumn.setCellValueFactory(new MapValueFactory(GroupedOpponentPlaysProperty.VERSION_KEY));
            versionColumn.prefWidthProperty().bind(doubleBinding.divide(columnsCount));
            tableView.getColumns().add(versionColumn);

            List<TableColumn<Map<String, String>, String>> additionalColumns = new ArrayList<>();
            for (Map.Entry<String, String> entry : data.get(0).entrySet()) {
                if (!entry.getKey().equals(GroupedOpponentPlaysProperty.GAME_ID_KEY) && !entry.getKey().equals(GroupedOpponentPlaysProperty.VERSION_KEY)) {
                    TableColumn<Map<String, String>, Boolean> column = new TableColumn<>(Messages.get(entry.getKey()));
                    column.setCellValueFactory(new MapValueFactory(entry.getKey()));
                    column.prefWidthProperty().bind(doubleBinding.divide(columnsCount));
                }
            }
            tableView.getColumns().addAll(additionalColumns);
        }
        this.setCenter(tableView);
    }

    private void loadPlayedGames() {
        List<GroupedOpponentPlay> opponentPlays = groupedOpponentPlaysService.getGroupedOpponentPlays(getGameIdFieldValue(), versionField.getText(), groupByOpponentGameIdCheckBox.isSelected(), groupByVersionCheckBox.isSelected());
        data = GroupedOpponentPlaysProperty.generateDataInMap(opponentPlays);
    }

    private Integer getGameIdFieldValue() {
        String text = gameIdField.getText();
        if (text != null && !text.isEmpty()) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }
}
