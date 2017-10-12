package ge.ai.domino.console.ui.controlpanel;

import ge.ai.domino.console.ui.domino.SaveGameWindow;
import ge.ai.domino.console.ui.playedgame.PlayedGamePane;
import ge.ai.domino.console.ui.shortcut.ShortcutPane;
import ge.ai.domino.console.ui.sysparam.SystemParametersPane;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ControlPanelMenuBar extends MenuBar {

    private final ControlPanel controlPanel;

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
        Menu sysParamMenu = getSysParamMenu();
        Menu langMenu = getLangMenu();
        Menu helpMenu = getHelpMenu();
        this.getMenus().addAll(fileMenu, sysParamMenu, langMenu, helpMenu);
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
        ImageView geoImageView =  new ImageView(ImageFactory.getImage("geo.png"));
        geoImageView.setFitWidth(25);
        geoImageView.setFitHeight(20);
        MenuItem geoMenuItem = new MenuItem(Messages.get("geo"), geoImageView);
        geoMenuItem.setOnAction(e -> {
            Messages.setLanguageCode("KA");
            controlPanel.initComponents();
        });
        ImageView engImageView =  new ImageView(ImageFactory.getImage("eng.png"));
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

    private Menu getSysParamMenu() {
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
        controlPanelMenu.getItems().addAll(sysParamsItem, playedGameItem);
        return controlPanelMenu;
    }

    private Menu getFileMenu() {
        Menu fileMenu = new Menu(Messages.get("file"));
        MenuItem newItem = new MenuItem(Messages.get("new"));
        newItem.setOnAction(e -> controlPanel.initComponents());
        MenuItem closeItem = new MenuItem(Messages.get("close"));
        closeItem.setOnAction(e -> new SaveGameWindow() {
            @Override
            public void onYes() {
                controlPanel.getStage().close();
            }

            @Override
            public void onNo() {
                controlPanel.getStage().close();
            }

            @Override
            public void onCancel() {
                e.consume();
            }
        }.showWindow());
        fileMenu.getItems().addAll(newItem, closeItem);
        return fileMenu;
    }
}
