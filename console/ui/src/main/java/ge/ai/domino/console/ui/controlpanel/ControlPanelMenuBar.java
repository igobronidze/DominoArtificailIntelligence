package ge.ai.domino.console.ui.controlpanel;

import ge.ai.domino.console.ui.controlpanel.p2p.P2PClientWindow;
import ge.ai.domino.console.ui.controlpanel.p2p.P2PServerWindow;
import ge.ai.domino.console.ui.game.SaveGameWindow;
import ge.ai.domino.console.ui.opponentplays.GroupedOpponentPlaysPane;
import ge.ai.domino.console.ui.played.GroupedPlayedGamePane;
import ge.ai.domino.console.ui.played.PlayedGamePane;
import ge.ai.domino.console.ui.shortcut.ShortcutPane;
import ge.ai.domino.console.ui.sysparam.SystemParametersPane;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.service.ServiceExecutor;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.service.p2p.P2PClientService;
import ge.ai.domino.service.p2p.P2PClientServiceImpl;
import ge.ai.domino.service.p2p.P2PServerService;
import ge.ai.domino.service.p2p.P2PServerServiceImpl;
import ge.ai.domino.service.played.PlayedGameService;
import ge.ai.domino.service.played.PlayedGameServiceImpl;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class ControlPanelMenuBar extends MenuBar {

    private final ControlPanel controlPanel;

    private static final String P2P_GAME_WEBSITE = "p2pGame";

    private static final String P2P_GAME_OPPONENT_NAME = "p2pOpponent";

    private static final int SLEEP_BETWEEN_P2P_GAME = 30_000;

    private static final int P2P_GAME_RELOAD_INTERVAL = 10_000;

    private final P2PServerService p2PServerService = new P2PServerServiceImpl();

    private final P2PClientService p2PClientService = new P2PClientServiceImpl();

    private final PlayedGameService playedGameService = new PlayedGameServiceImpl();

    private P2PClientWindow p2PClientWindow = null;

    private int lastPlayedGameId;

    ControlPanelMenuBar(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
        initUI();
        initMenu();
    }

    private void initUI() {
        this.setStyle("-fx-font-family: sylfaen");
    }

    private void initMenu() {
        Menu fileMenu = getFileMenu();
        Menu controlPanelMenu = getControlPanelMenu();
        Menu p2pMenu = getP2PMenu();
        Menu langMenu = getLangMenu();
        Menu helpMenu = getHelpMenu();
        this.getMenus().addAll(fileMenu, controlPanelMenu, p2pMenu, langMenu, helpMenu);
    }

    private Menu getHelpMenu() {
        Menu helpMenu = new Menu(Messages.get("help"));
        MenuItem shortcutItem = new MenuItem(Messages.get("shortcut"));
        shortcutItem.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setHeight(650);
            stage.setWidth(900);
            stage.setScene(new Scene(new ShortcutPane(stage.widthProperty().subtract(10))));
            stage.setTitle(Messages.get("shortcut"));
            stage.show();
        });
        helpMenu.getItems().addAll(shortcutItem);
        return helpMenu;
    }

    private Menu getLangMenu() {
        Menu langMenu = new Menu(Messages.get("language"));
        ImageView geoImageView = new ImageView(ImageFactory.getImage("geo.png"));
        geoImageView.setFitWidth(25);
        geoImageView.setFitHeight(20);
        MenuItem geoMenuItem = new MenuItem(Messages.get("geo"), geoImageView);
        geoMenuItem.setOnAction(e -> {
            Messages.setLanguageCode("KA");
            controlPanel.initComponents();
        });
        ImageView engImageView = new ImageView(ImageFactory.getImage("eng.png"));
        engImageView.setFitWidth(25);
        engImageView.setFitHeight(20);
        MenuItem engMenuItem = new MenuItem(Messages.get("eng"), engImageView);
        engMenuItem.setOnAction(e -> {
            Messages.setLanguageCode("EN");
            controlPanel.initComponents();
        });
        langMenu.getItems().addAll(geoMenuItem, engMenuItem);
        return langMenu;
    }

    private Menu getControlPanelMenu() {
        Menu controlPanelMenu = new Menu(Messages.get("controlPanel"));
        MenuItem sysParamsItem = new MenuItem(Messages.get("systemParameters"));
        sysParamsItem.setOnAction(e -> {
            Stage sysParamStage = new Stage();
            sysParamStage.setHeight(650);
            sysParamStage.setWidth(1100);
            sysParamStage.setScene(new Scene(new SystemParametersPane(sysParamStage.widthProperty().subtract(10))));
            sysParamStage.setTitle(Messages.get("systemParameters"));
            sysParamStage.show();
        });
        MenuItem playedGameItem = new MenuItem(Messages.get("playedGame"));
        playedGameItem.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new PlayedGamePane(stage.widthProperty().subtract(20))));
            stage.setMaximized(true);
            stage.setTitle(Messages.get("playedGame"));
            stage.show();
        });
        MenuItem groupedPlayedGameItem = new MenuItem(Messages.get("groupedPlayedGame"));
        groupedPlayedGameItem.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new GroupedPlayedGamePane(stage.widthProperty().subtract(20))));
            stage.setMaximized(true);
            stage.setTitle(Messages.get("groupedPlayedGame"));
            stage.show();
        });
        MenuItem groupedOpponentPlaysItem = new MenuItem(Messages.get("groupedOpponentPlays"));
        groupedOpponentPlaysItem.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(new GroupedOpponentPlaysPane(stage.widthProperty().subtract(20))));
            stage.setMaximized(true);
            stage.setTitle(Messages.get("groupedOpponentPlays"));
            stage.show();
        });
        controlPanelMenu.getItems().addAll(sysParamsItem, playedGameItem, groupedPlayedGameItem, groupedOpponentPlaysItem);
        return controlPanelMenu;
    }

    private Menu getP2PMenu() {
        Menu p2pMenu = new Menu(Messages.get("p2p"));
        MenuItem serverMenuItem = new MenuItem(Messages.get("p2pServer"));
        serverMenuItem.setOnAction(e -> {
            new P2PServerWindow() {

                @Override
                public void onStart(int pointOfWin) {
                    GameProperties gameProperties = new GameProperties();
                    gameProperties.setOpponentName(P2P_GAME_OPPONENT_NAME);
                    gameProperties.setWebsite(P2P_GAME_WEBSITE);
                    gameProperties.setPointsForWin(pointOfWin);
                    new Thread(() -> ServiceExecutor.execute(() -> p2PServerService.startServer(gameProperties))).start();
                }

                @Override
                public void onStop() {
                    ServiceExecutor.execute(p2PServerService::stopServer);
                }
            }.showWindow(controlPanel.getStage());
        });

        MenuItem clientMenuItem = new MenuItem(Messages.get("p2pClient"));
        clientMenuItem.setOnAction(e -> {

            Timer timer = new Timer();
            TimerTask task = new TimerTask() {

                public void run() {
                    p2PClientWindow.setGameInfos(playedGameService.getGameInfosBeforeId(lastPlayedGameId));
                }
            };
            p2PClientWindow = new P2PClientWindow() {

                @Override
                public void onStart(int count) {
                    new Thread(() -> {
                        lastPlayedGameId = playedGameService.getLastPlayedGameId();
                        try {
                            for (int i = 0; i < count; i++) {
                                p2PClientService.startClient();
                                Thread.sleep(SLEEP_BETWEEN_P2P_GAME);
                            }
                        } catch (Exception ignore) {
                            timer.cancel();
                        }
					}).start();

                    timer.schedule(task, P2P_GAME_RELOAD_INTERVAL, P2P_GAME_RELOAD_INTERVAL);
                }

                @Override
                public void onClose() { }
            };
            p2PClientWindow.showWindow(controlPanel.getStage());
        });

        p2pMenu.getItems().addAll(serverMenuItem, clientMenuItem);
        return p2pMenu;
    }

    private Menu getFileMenu() {
        Menu fileMenu = new Menu(Messages.get("file"));
        MenuItem newItem = new MenuItem(Messages.get("new"));
        newItem.setOnAction(e -> {
            if (AppController.round != null) {
                new SaveGameWindow() {
                    @Override
                    public void onSave() {
                        AppController.round = null;
                        controlPanel.initComponents();
                    }

                    @Override
                    public void onCancel() {}
                }.showWindow(false);
            } else {
                controlPanel.initComponents();
            }
        });
        MenuItem closeItem = new MenuItem(Messages.get("close"));
        closeItem.setOnAction(e -> {
            if (AppController.round != null) {
                new SaveGameWindow() {
                    @Override
                    public void onSave() {
                        AppController.round = null;
                        controlPanel.getStage().close();
                    }

                    @Override
                    public void onCancel() {}
                }.showWindow(false);
            }
        });
        fileMenu.getItems().addAll(newItem, closeItem);
        return fileMenu;
    }
}
