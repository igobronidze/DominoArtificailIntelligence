package ge.ai.domino.console.ui.played;

import ge.ai.domino.common.params.playedgames.GetGroupedPlayedGamesParams;
import ge.ai.domino.console.ui.tchcomponents.*;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.service.ServiceExecutor;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.service.channel.ChannelService;
import ge.ai.domino.service.channel.ChannelServiceImpl;
import ge.ai.domino.service.initial.InitialDataService;
import ge.ai.domino.service.initial.InitialDataServiceImpl;
import ge.ai.domino.service.played.PlayedGameService;
import ge.ai.domino.service.played.PlayedGameServiceImpl;
import ge.ai.domino.util.string.StringUtil;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.*;
import java.util.stream.Collectors;

public class GroupedPlayedGamePane extends BorderPane {

    private static final int COLUMN_COUNT = 11;

    private static final int FILTER_FIELD_WIDTH = 220;

    private static final PlayedGameService playedGameService = new PlayedGameServiceImpl();

    private static final ChannelService channelService = new ChannelServiceImpl();

    private static final InitialDataService initialDataService = new InitialDataServiceImpl();

    private final DoubleBinding doubleBinding;

    private TableView<GroupedPlayedGameProperty> tableView;

    private TCHTextField versionField;

    private TCHTextField channelsField;

    private TCHNumberTextField pointForWinField;

    private TCHNumberTextField levelField;

    private TCHDatePicker fromDatePicker;

    private HBox fromToDateComponent;

    private TCHDatePicker toDatePicker;

    private TCHCheckBox groupByVersionCheckBox;

    private TCHCheckBox groupByChannelCheckBox;

    private TCHCheckBox groupByPointForWinCheckBox;

    private TCHCheckBox groupByLevelCheckBox;

    private TCHCheckBox groupByDateCheckBox;

    private TCHButton searchButton;

    private Map<String, Channel> channelsMap;

    public GroupedPlayedGamePane(DoubleBinding doubleBinding) {
        this.doubleBinding = doubleBinding;
        initUI();
    }

    private void initUI() {
        iniTopPane();
        initTable();
        loadPlayedGames();
    }

    private void iniTopPane() {
        initFilterFields();
        initGroupByFields();
        initSearchButton();

        initToolBar();
    }

    private void initToolBar() {
        GridPane toolBar = new GridPane();
        toolBar.setHgap(10);
        toolBar.setVgap(10);
        toolBar.setPadding(new Insets(15));

        toolBar.add(versionField, 0, 0);
        toolBar.add(channelsField, 1, 0);
        toolBar.add(pointForWinField, 2, 0);
        toolBar.add(levelField, 3, 0);
        toolBar.add(fromToDateComponent, 4, 0);
        toolBar.add(searchButton, 5, 0);

        toolBar.add(groupByVersionCheckBox, 0, 1);
        toolBar.add(groupByChannelCheckBox, 1, 1);
        toolBar.add(groupByPointForWinCheckBox, 2, 1);
        toolBar.add(groupByLevelCheckBox, 3, 1);
        toolBar.add(groupByDateCheckBox, 4, 1);

        this.setTop(toolBar);
    }

    private void initSearchButton() {
        searchButton = new TCHButton();
        searchButton.setGraphic(new ImageView(ImageFactory.getImage("search.png")));
        searchButton.setOnAction(e -> loadPlayedGames());
    }

    private void initFilterFields() {
        initVersionField();
        initChannelField();
        initPointField();
        initLevelField();
        initDatePickers();
    }

    private void initLevelField() {
        levelField = new TCHNumberTextField(null, TCHComponentSize.MEDIUM);
        levelField.setPrefWidth(FILTER_FIELD_WIDTH);
        levelField.setPromptText(Messages.get("level"));
    }

    private void initPointField() {
        pointForWinField = new TCHNumberTextField(null, TCHComponentSize.MEDIUM);
        pointForWinField.setPrefWidth(FILTER_FIELD_WIDTH);
        pointForWinField.setPromptText(Messages.get("pointForWin"));
    }

    private void initChannelField() {
        List<Channel> channels = new ArrayList<>();
        new ServiceExecutor() {}.execute(() -> channels.addAll(channelService.getChannels()));
        channelsMap = channels.stream().collect(Collectors.toMap(Channel::getName, channel -> channel));

        channelsField = new TCHTextField(TCHComponentSize.MEDIUM);
        channelsField.setPrefWidth(FILTER_FIELD_WIDTH);
        channelsField.setPromptText(Messages.get("channel"));
    }

    private void initVersionField() {
        versionField = new TCHTextField(TCHComponentSize.SMALL);
        versionField.setPrefWidth(FILTER_FIELD_WIDTH);
        versionField.setPromptText(Messages.get("version"));

        new ServiceExecutor() {}.execute(() -> versionField.setText(initialDataService.getInitialData().getVersion()));
    }

    private void initDatePickers() {
        fromDatePicker = new TCHDatePicker();
        fromDatePicker.setPrefWidth((double) FILTER_FIELD_WIDTH / 2);
        toDatePicker = new TCHDatePicker();
        toDatePicker.setPrefWidth((double) FILTER_FIELD_WIDTH / 2);

        fromToDateComponent = new HBox(5);
        fromToDateComponent.getChildren().addAll(fromDatePicker, toDatePicker);
    }

    private void initGroupByFields() {
        groupByVersionCheckBox = new TCHCheckBox(Messages.get("groupByVersion"));
        groupByVersionCheckBox.setSelected(true);

        groupByChannelCheckBox = new TCHCheckBox(Messages.get("groupByChannel"));
        groupByChannelCheckBox.setSelected(true);

        groupByPointForWinCheckBox = new TCHCheckBox(Messages.get("groupByPointForWin"));
        groupByPointForWinCheckBox.setSelected(true);

        groupByLevelCheckBox = new TCHCheckBox(Messages.get("groupByLevel"));
        groupByLevelCheckBox.setSelected(true);

        groupByDateCheckBox = new TCHCheckBox(Messages.get("groupByDate"));
        groupByDateCheckBox.setSelected(true);
    }

    private void initTable() {
        tableView = new TableView<>();
        tableView.setStyle("-fx-font-family: sylfaen; -fx-text-alignment: center; -fx-font-size: 16px;");

        TableColumn<GroupedPlayedGameProperty, Boolean> versionColumn = getVersionColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> pointForWinColumn = getPointForWinColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> channelColumn = getChannelColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> levelColumn = getLevelColumn();
        TableColumn<GroupedPlayedGameProperty, Date> dateColumn = getDateColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> finishedColumn = getFinishedColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> winPercentColumn = getWinPercentColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> losePercentColumn = getLosePercentColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> stoppedColumn = getStoppedColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> winPercentForFinishedColumn = getWinPercentForFinishedColumn();
        TableColumn<GroupedPlayedGameProperty, Boolean> profitColumn = getProfitColumn();

        tableView.getColumns().addAll(Arrays.asList(versionColumn,
                pointForWinColumn,
                channelColumn,
                levelColumn,
                dateColumn,
                finishedColumn,
                winPercentColumn,
                losePercentColumn,
                stoppedColumn,
                winPercentForFinishedColumn,
                profitColumn));
        this.setCenter(tableView);
    }

    private TableColumn<GroupedPlayedGameProperty, Boolean> getProfitColumn() {
        TableColumn<GroupedPlayedGameProperty, Boolean> versionColumn = new TableColumn<>(Messages.get("profit"));
        versionColumn.setCellValueFactory(new PropertyValueFactory<>("profit"));
        versionColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        return versionColumn;
    }

    private TableColumn<GroupedPlayedGameProperty, Boolean> getWinPercentForFinishedColumn() {
        TableColumn<GroupedPlayedGameProperty, Boolean> winPercentForFinishedColumn = new TableColumn<>(Messages.get("winPercentForFinished"));
        winPercentForFinishedColumn.setCellValueFactory(new PropertyValueFactory<>("winPercentForFinished"));
        winPercentForFinishedColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
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

    private TableColumn<GroupedPlayedGameProperty, Boolean> getFinishedColumn() {
        TableColumn<GroupedPlayedGameProperty, Boolean> winPercentColumn = new TableColumn<>(Messages.get("finished"));
        winPercentColumn.setCellValueFactory(new PropertyValueFactory<>("finished"));
        winPercentColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        return winPercentColumn;
    }

    private TableColumn<GroupedPlayedGameProperty, Date> getDateColumn() {
        TableColumn<GroupedPlayedGameProperty, Date> dateColumn = new TableColumn<>(Messages.get("date"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        return dateColumn;
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
        new ServiceExecutor() {}.execute(() -> games.addAll(playedGameService.getGroupedPlayedGames(
                new GetGroupedPlayedGamesParams(
                        versionField.getText(),
                        getChannelIdFromField(),
                        pointForWinField.getNumber() == null ? null : pointForWinField.getNumber().intValue(),
                        levelField.getNumber() == null ? null : levelField.getNumber().doubleValue(),
                        fromDatePicker.getValue(),
                        toDatePicker.getValue(),
                        groupByVersionCheckBox.isSelected(),
                        groupByChannelCheckBox.isSelected(),
                        groupByPointForWinCheckBox.isSelected(),
                        groupByLevelCheckBox.isSelected(),
                        groupByDateCheckBox.isSelected()
                ))));

        List<GroupedPlayedGameProperty> groupedPlayedGameProperties = new ArrayList<>();
        for (GroupedPlayedGame game : games) {
            groupedPlayedGameProperties.add(new GroupedPlayedGameProperty(game));
        }
        ObservableList<GroupedPlayedGameProperty> data = FXCollections.observableArrayList(groupedPlayedGameProperties);
        tableView.setItems(data);
    }

    private Integer getChannelIdFromField() {
        if (StringUtil.isEmpty(channelsField.getText())) {
            return null;
        }
        Channel channel = channelsMap.get(channelsField.getText());
        if (channel == null) {
            return 0;
        }
        return channel.getId();
    }
}
