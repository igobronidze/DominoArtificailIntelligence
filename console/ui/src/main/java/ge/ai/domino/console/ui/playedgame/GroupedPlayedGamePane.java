package ge.ai.domino.console.ui.playedgame;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHCheckBox;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.domain.domino.played.GroupedPlayedGame;
import ge.ai.domino.service.playedgame.PlayedGameService;
import ge.ai.domino.service.playedgame.PlayedGameServiceImpl;
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
import java.util.List;

public class GroupedPlayedGamePane extends BorderPane {

    private final PlayedGameService playedGameService = new PlayedGameServiceImpl();

    private TableView<GroupedPlayedGameProperty> tableView;

    private DoubleBinding doubleBinding;

    private TCHCheckBox groupByVersionCheckBox;

    private TCHCheckBox groupByOpponentNameCheckBox;

    private TCHCheckBox groupByWebsiteCheckBox;

    private TCHCheckBox groupByPointForWinCheckBox;

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
        groupByVersionCheckBox = new TCHCheckBox(Messages.get("groupByVersion"));
        groupByOpponentNameCheckBox = new TCHCheckBox(Messages.get("groupByOpponentName"));
        groupByWebsiteCheckBox = new TCHCheckBox(Messages.get("groupByWebsite"));
        groupByPointForWinCheckBox = new TCHCheckBox(Messages.get("groupByPointForWin"));
        TCHButton searchButton = new TCHButton();
        searchButton.setGraphic(new ImageView(ImageFactory.getImage("search.png")));
        searchButton.setOnAction(e -> loadPlayedGames());
        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL);
        flowPane.setVgap(10);
        flowPane.setHgap(10);
        flowPane.setPadding(new Insets(15));
        flowPane.getChildren().addAll(groupByVersionCheckBox, groupByOpponentNameCheckBox, groupByWebsiteCheckBox, groupByPointForWinCheckBox, searchButton);
        this.setTop(flowPane);
    }

    @SuppressWarnings("unchecked")
    private void initTable() {
        tableView = new TableView<>();
        tableView.setStyle("-fx-font-family: sylfaen; -fx-text-alignment: center; -fx-font-size: 16px;");
        TableColumn<GroupedPlayedGameProperty, Boolean> versionColumn = new TableColumn<>(Messages.get("version"));
        versionColumn.setCellValueFactory(new PropertyValueFactory<>("version"));
        versionColumn.prefWidthProperty().bind(doubleBinding.divide(8));
        TableColumn<GroupedPlayedGameProperty, Boolean> pointForWinColumn = new TableColumn<>(Messages.get("pointForWin"));
        pointForWinColumn.setCellValueFactory(new PropertyValueFactory<>("pointForWin"));
        pointForWinColumn.prefWidthProperty().bind(doubleBinding.divide(8));
        TableColumn<GroupedPlayedGameProperty, Boolean> opponentNameColumn = new TableColumn<>(Messages.get("opponentName"));
        opponentNameColumn.setCellValueFactory(new PropertyValueFactory<>("opponentName"));
        opponentNameColumn.prefWidthProperty().bind(doubleBinding.divide(8));
        TableColumn<GroupedPlayedGameProperty, Boolean> websiteColumn = new TableColumn<>(Messages.get("website"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));
        websiteColumn.prefWidthProperty().bind(doubleBinding.divide(8));
        TableColumn<GroupedPlayedGameProperty, Boolean> winPercentColumn = new TableColumn<>(Messages.get("winPercent"));
        winPercentColumn.setCellValueFactory(new PropertyValueFactory<>("winPercent"));
        winPercentColumn.prefWidthProperty().bind(doubleBinding.divide(8));
        TableColumn<GroupedPlayedGameProperty, Boolean> losePercentColumn = new TableColumn<>(Messages.get("losePercent"));
        losePercentColumn.setCellValueFactory(new PropertyValueFactory<>("losePercent"));
        losePercentColumn.prefWidthProperty().bind(doubleBinding.divide(8));
        TableColumn<GroupedPlayedGameProperty, Boolean> stoppedColumn = new TableColumn<>(Messages.get("stoppedPercent"));
        stoppedColumn.setCellValueFactory(new PropertyValueFactory<>("stoppedPercent"));
        stoppedColumn.prefWidthProperty().bind(doubleBinding.divide(8));
        TableColumn<GroupedPlayedGameProperty, Boolean> winPercentForFinishedColumn = new TableColumn<>(Messages.get("winPercentForFinished"));
        winPercentForFinishedColumn.setCellValueFactory(new PropertyValueFactory<>("winPercentForFinished"));
        winPercentForFinishedColumn.prefWidthProperty().bind(doubleBinding.divide(8));
        tableView.getColumns().addAll(versionColumn, pointForWinColumn, opponentNameColumn, websiteColumn, winPercentColumn, losePercentColumn, stoppedColumn, winPercentForFinishedColumn);
        this.setCenter(tableView);
    }

    private void loadPlayedGames() {
        List<GroupedPlayedGame> games = playedGameService.getGroupedPlayedGames(groupByVersionCheckBox.isSelected(), groupByOpponentNameCheckBox.isSelected(),
                groupByWebsiteCheckBox.isSelected(), groupByPointForWinCheckBox.isSelected());
        List<GroupedPlayedGameProperty> groupedPlayedGameProperties = new ArrayList<>();
        for (GroupedPlayedGame game : games) {
            groupedPlayedGameProperties.add(new GroupedPlayedGameProperty(game));
        }
        ObservableList<GroupedPlayedGameProperty> data = FXCollections.observableArrayList(groupedPlayedGameProperties);
        tableView.setItems(data);
    }
}
