package ge.ai.domino.console.ui.gameproperties;

import ge.ai.domino.console.ui.controlpanel.AppController;
import ge.ai.domino.console.ui.controlpanel.ControlPanel;
import ge.ai.domino.console.ui.game.GamePane;
import ge.ai.domino.console.ui.game.GamePaneInitialData;
import ge.ai.domino.console.ui.tchcomponents.*;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.console.ui.util.service.ServiceExecutor;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.service.channel.ChannelService;
import ge.ai.domino.service.channel.ChannelServiceImpl;
import ge.ai.domino.service.game.GameService;
import ge.ai.domino.service.game.GameServiceImpl;
import ge.ai.domino.service.sysparam.SystemParameterService;
import ge.ai.domino.service.sysparam.SystemParameterServiceImpl;
import ge.ai.domino.util.string.StringUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GamePropertiesPane extends VBox {

    private static final SystemParameterService systemParameterService = new SystemParameterServiceImpl();

    private static final GameService gameService = new GameServiceImpl();

    private static final ChannelService channelService = new ChannelServiceImpl();

    private static final SysParam possiblePoints = new SysParam("possiblePoints", "75,155,175,255,355");

    private static final SysParam levelDefaultValue = new SysParam("levelDefaultValue", "5");

    private static final SysParam defaultWinPoint = new SysParam("defaultWinPoint", "255");

    private static final SysParam defaultChannelName = new SysParam("defaultChannelName", "Real");

    private static final SysParam bestMoveAutoPlay = new SysParam("bestMoveAutoPlay", "true");

    private static final SysParam detectAddedTiles = new SysParam("detectAddedTiles", "true");

    private final ControlPanel controlPanel;

    private GamePane gamePane;

    public GamePropertiesPane(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
        initComponents();
        initUI();
    }

    private void initUI() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(15);
        this.setPadding(new Insets(50, 0, 0, 0));
    }

    private void initComponents() {
        List<Channel> channels = new ArrayList<>();

        new ServiceExecutor() {}.execute(() -> channels.addAll(channelService.getChannels()));

        Map<String, Channel> channelsMap = channels.stream().collect(Collectors.toMap(Channel::getName, channel -> channel));
        TCHComboBox<String> channelsCombo = new TCHComboBox<>(new ArrayList<>(channelsMap.keySet()));

        new ServiceExecutor() {}.execute(() -> channelsCombo.setValue(systemParameterService.getStringParameterValue(defaultChannelName)));

        TCHFieldLabel channelFieldLabel = new TCHFieldLabel(Messages.get("channel"), channelsCombo);

        TCHTextField nameField = new TCHTextField("tmp", TCHComponentSize.MEDIUM);
        TCHFieldLabel nameFieldLabel = new TCHFieldLabel(Messages.get("name"), nameField);

        List<Integer> points = new ArrayList<>();
        new ServiceExecutor() {}.execute(() -> points.addAll(systemParameterService.getIntegerListParameterValue(possiblePoints)));
        TCHComboBox<Integer> pointComboBox = new TCHComboBox<>(points);

        new ServiceExecutor() {}.execute(() -> pointComboBox.setValue(systemParameterService.getIntegerParameterValue(defaultWinPoint)));

        TCHFieldLabel pointFieldLabel = new TCHFieldLabel(Messages.get("point"), pointComboBox);

        TCHButton startButton = new TCHButton(Messages.get("start"));
        TCHNumberTextField levelField = new TCHNumberTextField(TCHComponentSize.MEDIUM);

        new ServiceExecutor() {}.execute(() -> levelField.setNumber(new BigDecimal(systemParameterService.getIntegerParameterValue(levelDefaultValue))));

        TCHFieldLabel levelFieldLabel = new TCHFieldLabel(Messages.get("level"), levelField);
        startButton.setOnAction(e -> {
            if (StringUtil.isEmpty(channelsCombo.getValue()) || StringUtil.isEmpty(nameField.getText())) {
                WarnDialog.showWarnDialog(Messages.get("pleaseFillAllField"));
            } else {
                GameProperties gameProperties = new GameProperties();
                gameProperties.setPointsForWin(Integer.parseInt(pointComboBox.getValue().toString()));
                gameProperties.setChannel(channelsMap.get(channelsCombo.getValue()));
                gameProperties.setOpponentName(nameField.getText());
                gameProperties.setLevel(levelField.getNumber().doubleValue());

                new ServiceExecutor() {}.execute(() -> AppController.round =  gameService.startGame(gameProperties));

                GamePaneInitialData gamePaneInitialData = new GamePaneInitialData();
                new ServiceExecutor() {}.execute(() -> gamePaneInitialData.setBestMoveAutoPlay(systemParameterService.getBooleanParameterValue(bestMoveAutoPlay)));
                new ServiceExecutor() {}.execute(() -> gamePaneInitialData.setDetectAddedTiles(systemParameterService.getBooleanParameterValue(detectAddedTiles)));
                gamePane = new GamePane(controlPanel, gameProperties, gamePaneInitialData) {
                    @Override
                    public void onNewGame() {
                        gamePane = null;
                    }
                };
                controlPanel.getRoot().setCenter(gamePane);
            }
        });
        this.getChildren().addAll(nameFieldLabel, channelFieldLabel, pointFieldLabel, levelFieldLabel, startButton);
    }
}
