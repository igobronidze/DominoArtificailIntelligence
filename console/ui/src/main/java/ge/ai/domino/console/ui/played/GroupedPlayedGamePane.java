package ge.ai.domino.console.ui.played;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHCheckBox;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHTextField;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.service.ServiceExecutor;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.service.played.PlayedGameService;
import ge.ai.domino.service.played.PlayedGameServiceImpl;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupedPlayedGamePane extends BorderPane {

    private static final int COLUMN_COUNT = 9;

    private final PlayedGameService playedGameService = new PlayedGameServiceImpl();

    private TableView<GroupedPlayedGameProperty> tableView;

    private final DoubleBinding doubleBinding;

    private TCHTextField versionField;

    private TCHCheckBox groupByVersionCheckBox;

    private TCHCheckBox groupByOpponentNameCheckBox;

    private TCHCheckBox groupByChannelCheckBox;

    private TCHCheckBox groupByPointForWinCheckBox;

    private TCHCheckBox groupByLevelCheckBox;

    public GroupedPlayedGamePane(DoubleBinding doubleBinding) {
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

        groupByVersionCheckBox = new TCHCheckBox(Messages.get("groupByVersion"));
        groupByVersionCheckBox.setSelected(true);
        groupByOpponentNameCheckBox = new TCHCheckBox(Messages.get("groupByOpponentName"));
        groupByChannelCheckBox = new TCHCheckBox(Messages.get("groupByChannel"));
        groupByPointForWinCheckBox = new TCHCheckBox(Messages.get("groupByPointForWin"));
        groupByLevelCheckBox = new TCHCheckBox(Messages.get("groupByLevel"));
        TCHButton searchButton = new TCHButton();
        searchButton.setGraphic(new ImageView(ImageFactory.getImage("search.png")));
        searchButton.setOnAction(e -> loadPlayedGames());
        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL);
        flowPane.setVgap(10);
        flowPane.setHgap(10);
        flowPane.setPadding(new Insets(15));
        flowPane.getChildren().addAll(versionField, groupByVersionCheckBox, groupByOpponentNameCheckBox, groupByChannelCheckBox, groupByPointForWinCheckBox, groupByLevelCheckBox, searchButton);
        this.setTop(flowPane);
    }

    private void initTable() {
        tableView = new TableView<>();
        tableView.setStyle("-fx-font-family: sylfaen; -fx-text-alignment: center; -fx-font-size: 16px;");

        TableColumn<GroupedPlayedGameProperty, Boolean> versionColumn = getVersionColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> pointForWinColumn = getPointForWinColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> opponentNameColumn = getOpponentNameColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> channelColumn = getChannelColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> levelColumn = getLevelColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> winPercentColumn = getWinPercentColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> losePercentColumn = getLosePercentColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> stoppedColumn = getStoppedColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> winPercentForFinishedColumn = getWinPercentForFinishedColumn();

        tableView.getColumns().addAll(Arrays.asList(versionColumn, pointForWinColumn, opponentNameColumn,
                channelColumn, levelColumn, winPercentColumn, losePercentColumn, stoppedColumn, winPercentForFinishedColumn));
        this.setCenter(tableView);
    }


    private TableColumn<GroupedPlayedGameProperty, Boolean> getWinPercentForFinishedColumn() {
        TableColumn<GroupedPlayedGameProperty, Boolean> winPercentForFinishedColumn = new TableColumn<>(Messages.get("winPercentForFinished"));
        winPercentForFinishedColumn.setCellValueFactory(new PropertyValueFactory<>("winPercentForFinished"));
        winPercentForFinishedColumn.prefWidthProperty().bind(doubleBinding.divide(8));
        return winPercentForFinishedColumn;
    }

    private TableColumn<GroupedPlayedGameProperty, Boolean> getStoppedColumn() {
        TableColumn<GroupedPlayedGameProperty, Boolean> stoppedColumn = new TableColumn<>(Messages.get("stoppedPercent"));
        stoppedColumn.setCellValueFactory(new PropertyValueFactory<>("stoppedPercent"));
        stoppedColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        return stoppedColumn;
    }

    private TableColumn<GroupedPlayedGameProperty, Boolean> getLosePercentColumn() {
        TableColumn<GroupedPlayedGameProperty, Boolean> losePercentColumn = new TableColumn<>(Messages.get("losePercent"));
        losePercentColumn.setCellValueFactory(new PropertyValueFactory<>("losePercent"));
        losePercentColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        return losePercentColumn;
    }

    private TableColumn<GroupedPlayedGameProperty, Boolean> getWinPercentColumn() {
        TableColumn<GroupedPlayedGameProperty, Boolean> winPercentColumn = new TableColumn<>(Messages.get("winPercent"));
        winPercentColumn.setCellValueFactory(new PropertyValueFactory<>("winPercent"));
        winPercentColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        return winPercentColumn;
    }

    private TableColumn<GroupedPlayedGameProperty, Boolean> getLevelColumn() {
        TableColumn<GroupedPlayedGameProperty, Boolean> levelColumn = new TableColumn<>(Messages.get("level"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        levelColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        return levelColumn;
    }

    private TableColumn<GroupedPlayedGameProperty, Boolean> getChannelColumn() {
        TableColumn<GroupedPlayedGameProperty, Boolean> channelColumn = new TableColumn<>(Messages.get("channel"));
        channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        channelColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        return channelColumn;
    }

    private TableColumn<GroupedPlayedGameProperty, Boolean> getOpponentNameColumn() {
        TableColumn<GroupedPlayedGameProperty, Boolean> opponentNameColumn = new TableColumn<>(Messages.get("opponentName"));
        opponentNameColumn.setCellValueFactory(new PropertyValueFactory<>("opponentName"));
        opponentNameColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        return opponentNameColumn;
    }

    private TableColumn<GroupedPlayedGameProperty, Boolean> getPointForWinColumn() {
        TableColumn<GroupedPlayedGameProperty, Boolean> pointForWinColumn = new TableColumn<>(Messages.get("pointForWin"));
        pointForWinColumn.setCellValueFactory(new PropertyValueFactory<>("pointForWin"));
        pointForWinColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        return pointForWinColumn;
    }

    private TableColumn<GroupedPlayedGameProperty, Boolean> getVersionColumn() {
        TableColumn<GroupedPlayedGameProperty, Boolean> versionColumn = new TableColumn<>(Messages.get("version"));
        versionColumn.setCellValueFactory(new PropertyValueFactory<>("version"));
        versionColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        return versionColumn;
    }

    private void loadPlayedGames() {
        List<GroupedPlayedGame> games = new ArrayList<>();
        new ServiceExecutor() {}.execute(() -> games.addAll(playedGameService.getGroupedPlayedGames(versionField.getText(), groupByVersionCheckBox.isSelected(), groupByOpponentNameCheckBox.isSelected(),
                groupByChannelCheckBox.isSelected(), groupByPointForWinCheckBox.isSelected(), groupByLevelCheckBox.isSelected())));

        List<GroupedPlayedGameProperty> groupedPlayedGameProperties = new ArrayList<>();
        for (GroupedPlayedGame game : games) {
            groupedPlayedGameProperties.add(new GroupedPlayedGameProperty(game));
        }
        ObservableList<GroupedPlayedGameProperty> data = FXCollections.observableArrayList(groupedPlayedGameProperties);
        tableView.setItems(data);
    }
}
