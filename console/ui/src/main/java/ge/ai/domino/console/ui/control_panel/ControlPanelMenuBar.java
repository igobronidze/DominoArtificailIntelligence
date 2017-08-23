package ge.ai.domino.console.ui.control_panel;

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

    public ControlPanelMenuBar() {
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
            ControlPanel.initComponents();
        });
        ImageView engImageView =  new ImageView(ImageFactory.getImage("eng.png"));
        engImageView.setFitWidth(25);
        engImageView.setFitHeight(20);
        MenuItem engMenuItem = new MenuItem(Messages.get("eng"), engImageView);
        engMenuItem.setOnAction(e -> {
            Messages.setLanguageCode("EN");
            ControlPanel.initComponents();
        });
        langMenu.getItems().addAll(geoMenuItem, engMenuItem);
        return langMenu;
    }

    private Menu getSysParamMenu() {
        Menu sysParamMenu = new Menu(Messages.get("systemParameters"));
        MenuItem showItem = new MenuItem(Messages.get("show"));
        showItem.setOnAction(e -> {
            Stage sysParamStage = new Stage();
            sysParamStage.setHeight(650);
            sysParamStage.setWidth(1100);
            sysParamStage.setScene(new Scene(new SystemParametersPane(sysParamStage.widthProperty().subtract(10))));
            sysParamStage.setTitle(Messages.get("systemParameters"));
            sysParamStage.show();
        });
        sysParamMenu.getItems().addAll(showItem);
        return sysParamMenu;
    }

    private Menu getFileMenu() {
        Menu fileMenu = new Menu(Messages.get("file"));
        MenuItem newItem = new MenuItem(Messages.get("new"));
        MenuItem closeItem = new MenuItem(Messages.get("close"));
        closeItem.setOnAction(e -> {
            ControlPanel.getStage().close();
        });
        fileMenu.getItems().addAll(newItem, closeItem);
        return fileMenu;
    }
}
