package ge.ai.domino.console.ui.played;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComboBox;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHTextField;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.service.ServiceExecutor;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.played.GameResult;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.service.channel.ChannelService;
import ge.ai.domino.service.channel.ChannelServiceImpl;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayedGamePane extends BorderPane {

    private static final int COLUMN_COUNT = 10;

    private final PlayedGameService playedGameService = new PlayedGameServiceImpl();

    private final ChannelService channelService = new ChannelServiceImpl();

    private final DoubleBinding doubleBinding;

    private TableView<PlayedGameProperty> tableView;

    private TCHTextField versionField;

    private ComboBox<GameResult> resultComboBox;

    private TCHTextField opponentNameField;

    private TCHComboBox<String> channelCombo;

    private TCHTextField levelField;

    private Map<String, Integer> channelsMap;

    public PlayedGamePane(DoubleBinding doubleBinding) {
        this.doubleBinding = doubleBinding;
        initUI();
    }

    private void initUI() {
        initChannels();
        initFilters();
        initTable();
        loadPlayedGames();
    }

    private void initChannels() {
        List<Channel> channels = new ArrayList<>();

        new ServiceExecutor() {}.execute(() -> {
            channels.addAll(channelService.getChannels());
        });


        channelsMap = channels.stream().collect(Collectors.toMap(Channel::getName, Channel::getId));
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
        channelCombo = new TCHComboBox<>(new ArrayList<>(channelsMap.keySet()));
        levelField = new TCHTextField(TCHComponentSize.SMALL);
        levelField.setPromptText(Messages.get("level"));
        TCHButton searchButton = new TCHButton();
        searchButton.setGraphic(new ImageView(ImageFactory.getImage("search.png")));
        searchButton.setOnAction(e -> loadPlayedGames());
        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL);
        flowPane.setVgap(10);
        flowPane.setHgap(10);
        flowPane.setPadding(new Insets(15));
        flowPane.getChildren().addAll(versionField, resultComboBox, opponentNameField, channelCombo, levelField, searchButton);
        this.setTop(flowPane);
    }

    private void initTable() {
        tableView = new TableView<>();
        tableView.setStyle("-fx-font-family: sylfaen; -fx-text-alignment: center; -fx-font-size: 16px;");
        TableColumn<PlayedGameProperty, Boolean> idColumn = new TableColumn<>(Messages.get("id"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        TableColumn<PlayedGameProperty, Boolean> versionColumn = new TableColumn<>(Messages.get("version"));
        versionColumn.setCellValueFactory(new PropertyValueFactory<>("version"));
        versionColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        TableColumn<PlayedGameProperty, Boolean> resultColumn = new TableColumn<>(Messages.get("result"));
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
        resultColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        TableColumn<PlayedGameProperty, Boolean> dateColumn = new TableColumn<>(Messages.get("date"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        TableColumn<PlayedGameProperty, Boolean> myPointColumn = new TableColumn<>(Messages.get("myPoint"));
        myPointColumn.setCellValueFactory(new PropertyValueFactory<>("myPoint"));
        myPointColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        TableColumn<PlayedGameProperty, Boolean> opponentPointColumn = new TableColumn<>(Messages.get("opponentPoint"));
        opponentPointColumn.setCellValueFactory(new PropertyValueFactory<>("opponentPoint"));
        opponentPointColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        TableColumn<PlayedGameProperty, Boolean> pointForWinColumn = new TableColumn<>(Messages.get("pointForWin"));
        pointForWinColumn.setCellValueFactory(new PropertyValueFactory<>("pointForWin"));
        pointForWinColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        TableColumn<PlayedGameProperty, Boolean> opponentNameColumn = new TableColumn<>(Messages.get("opponentName"));
        opponentNameColumn.setCellValueFactory(new PropertyValueFactory<>("opponentName"));
        opponentNameColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        TableColumn<PlayedGameProperty, Boolean> channelColumn = new TableColumn<>(Messages.get("channel"));
        channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        channelColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        TableColumn<PlayedGameProperty, Boolean> levelColumn = new TableColumn<>(Messages.get("level"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        levelColumn.prefWidthProperty().bind(doubleBinding.divide(COLUMN_COUNT));
        tableView.getColumns().addAll(Arrays.asList(idColumn, versionColumn, resultColumn, dateColumn, myPointColumn, opponentPointColumn,
                pointForWinColumn, opponentNameColumn, channelColumn, levelColumn));
        this.setCenter(tableView);
    }

    private void loadPlayedGames() {
        List<PlayedGame> playedGames = new ArrayList<>();
        new ServiceExecutor() {}.execute(() -> playedGames.addAll(playedGameService.getPlayedGames(versionField.getText(), resultComboBox.getValue(),
                opponentNameField.getText(), channelsMap.get(channelCombo.getValue()), levelField.getText())));

        List<PlayedGameProperty> playedGameProperties = new ArrayList<>();
        for (PlayedGame playedGame : playedGames) {
            playedGameProperties.add(new PlayedGameProperty(playedGame));
        }
        ObservableList<PlayedGameProperty> data = FXCollections.observableArrayList(playedGameProperties);
        tableView.setItems(data);
    }
}
