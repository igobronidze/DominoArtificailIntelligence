package ge.ai.domino.console.ui.domino;

import ge.ai.domino.console.ui.controlpanel.AppController;
import ge.ai.domino.console.ui.controlpanel.ControlPanel;
import ge.ai.domino.console.ui.gameproperties.GamePropertiesPane;
import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.tchcomponents.TCHNumberTextField;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.domain.domino.game.GameInfo;
import ge.ai.domino.domain.domino.game.PlayDirection;
import ge.ai.domino.domain.domino.game.TableInfo;
import ge.ai.domino.domain.domino.game.Tile;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.service.domino.DominoService;
import ge.ai.domino.service.domino.DominoServiceImpl;
import ge.ai.domino.util.tile.TileUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DominoPane extends BorderPane {

    private final DominoService dominoService = new DominoServiceImpl();

    private HimTilesPane himTilesPane;

    private MyTilesPane myTilesPane;

    private boolean pressedOnCtrl;

    private Integer firstPressedNumber;

    private final ControlPanel controlPanel;

    public DominoPane(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
        reload();
    }

    private void reload() {
        initUI();
        initTopPane();
        initCenterPane();
        initBottomPane();
        initKeyboardListener();
        if (AppController.hand.getGameInfo().isFinished()) {
            new SaveGameWindow() {
                @Override
                public void onYes() {
                    showNextGameWindow();
                }

                @Override
                public void onNo() {
                    showNextGameWindow();
                }

                @Override
                public void onCancel() {
                    showNextGameWindow();
                }
            }.showWindow();

        } else if (AppController.hand.getTableInfo().isNeedToAddLeftTiles()) {
            showAddLeftTilesCount();
        }
    }

    private void initUI() {
        this.setPadding(new Insets(8));
    }

    private void initTopPane() {
        TableInfo tableInfo = AppController.hand.getTableInfo();
        GameInfo gameInfo = AppController.hand.getGameInfo();
        Label myPointsLabel = new TCHLabel(Messages.get("me") + " - " + gameInfo.getMyPoints() + " (" + (int)tableInfo.getMyTilesCount() + ")");
        Label himPointLabel = new TCHLabel(Messages.get("opponent") + " - " + gameInfo.getHimPoints() + " (" + (int)tableInfo.getHimTilesCount() + ")");
        Label bazaarCountLabel = new TCHLabel(Messages.get("bazaar") + " (" + (int)tableInfo.getBazaarTilesCount() + ")");
        ImageView undoImage = new ImageView(ImageFactory.getImage("undo.png"));
        undoImage.setOnMouseClicked(event -> onUndo());
        FlowPane flowPane = new FlowPane(30, 10);
        flowPane.setPadding(new Insets(0, 4, 8, 4));
        flowPane.getChildren().addAll(myPointsLabel, himPointLabel, bazaarCountLabel, undoImage);
        this.setTop(flowPane);
    }

    private void initCenterPane() {
        himTilesPane = new HimTilesPane(AppController.hand) {
            @Override
            public void onTileEntered(Tile tile, PlayDirection direction) {
                if (direction == PlayDirection.INCORRECT) {
                    showIncorrectTurnMessage();
                    reload();
                } else {
                    try {
                        if (AppController.hand.getTableInfo().isMyTurn()) {
                            TableInfo tableInfo = AppController.hand.getTableInfo();
                            if (tableInfo.getLeft() == null && tableInfo.isFirstHand() && tableInfo.getMyTilesCount() == 6) {
                                Stage stage = new Stage();
                                stage.setResizable(false);
                                stage.setTitle(Messages.get("gameStarter"));
                                TCHLabel label = new TCHLabel(Messages.get("whoStartGame"));
                                TCHButton meButton = new TCHButton(Messages.get("me"));
                                meButton.setOnAction(event -> {
                                    try {
                                        AppController.hand.getTableInfo().setMyTurn(true);
                                        AppController.hand = dominoService.addTileForMe(AppController.hand, tile.getX(), tile.getY());
                                        reload();
                                        stage.close();
                                    } catch (DAIException ex) {
                                        WarnDialog.showWarnDialog(ex);
                                    }
                                });
                                TCHButton himButton = new TCHButton(Messages.get("he"));
                                himButton.setOnAction(event -> {
                                     try {
                                        AppController.hand.getTableInfo().setMyTurn(false);
                                        AppController.hand = dominoService.addTileForMe(AppController.hand, tile.getX(), tile.getY());
                                        reload();
                                        stage.close();
                                    } catch (DAIException ex) {
                                        WarnDialog.showWarnDialog(ex);
                                    }
                                });
                                HBox hBox = new HBox(25);
                                hBox.setAlignment(Pos.TOP_CENTER);
                                hBox.getChildren().addAll(meButton, himButton);
                                VBox vBox = new VBox(30);
                                vBox.setPadding(new Insets(20));
                                vBox.setAlignment(Pos.TOP_CENTER);
                                vBox.getChildren().addAll(label, hBox);
                                stage.setScene(new Scene(vBox));
                                stage.setWidth(350);
                                stage.setHeight(140);
                                stage.showAndWait();
                            } else {
                                AppController.hand = dominoService.addTileForMe(AppController.hand, tile.getX(), tile.getY());
                                reload();
                            }
                        } else {
                            AppController.hand = dominoService.playForHim(AppController.hand, tile.getX(), tile.getY(), direction);
                            reload();
                        }
                    } catch (DAIException ex) {
                        WarnDialog.showWarnDialog(ex);
                    }
                }
            }

            @Override
            public void onAddTileEntered() {
                try {
                    AppController.hand = dominoService.addTileForHim(AppController.hand);
                } catch (DAIException ex) {
                    WarnDialog.showWarnDialog(ex);
                }
                reload();
            }
        };
        this.setCenter(himTilesPane);
    }

    private void initBottomPane() {
        myTilesPane = new MyTilesPane(AppController.hand) {
            @Override
            public void onTileEntered(Tile tile, PlayDirection direction) {
                if (direction == PlayDirection.INCORRECT) {
                    showIncorrectTurnMessage();
                    reload();
                } else {
                    try {
                        AppController.hand = dominoService.playForMe(AppController.hand, tile.getX(), tile.getY(), direction);
                        AppController.hand.setAiPrediction(null);
                    } catch (DAIException ex) {
                        WarnDialog.showWarnDialog(ex);
                    }
                    reload();
                }
            }
        };
        this.setBottom(myTilesPane);
    }

    private void initKeyboardListener() {
        controlPanel.getScene().setOnKeyPressed(e -> {
            Integer secondPressedNumber = null;
            if (e.getCode() == KeyCode.BACK_SPACE) {
                reload();
                return;
            }
            if (e.getCode() == KeyCode.CONTROL) {
                pressedOnCtrl = true;
                return;
            }
            if (pressedOnCtrl && firstPressedNumber == null) {
                try {
                    firstPressedNumber = Integer.parseInt(e.getText());
                    return;
                } catch (NumberFormatException ignore) {}
            } else if (pressedOnCtrl) {
                try {
                    secondPressedNumber = Integer.parseInt(e.getText());
                    int tmp = secondPressedNumber;
                    if (secondPressedNumber > firstPressedNumber) {
                        secondPressedNumber = firstPressedNumber;
                        firstPressedNumber = tmp;
                    }
                    String uid = TileUtil.getTileUID(firstPressedNumber, secondPressedNumber);
                    if (myTilesPane.showTile(uid)) {
                        myTilesPane.onTilePressed(uid);
                    } else if (himTilesPane.showTile(uid)) {
                        himTilesPane.onTilePressed(uid);
                    }
                    firstPressedNumber = null;
                    secondPressedNumber = null;
                    return;
                } catch (NumberFormatException ignore) {}
            }
            if (pressedOnCtrl && TilesPane.isArrowsVisible()) {
                if (e.getCode() == KeyCode.UP) {
                    TilesPane.onUpArrowPressed();
                    return;
                }
                if (e.getCode() == KeyCode.LEFT) {
                    TilesPane.onLeftArrowPressed();
                    return;
                }
                if (e.getCode() == KeyCode.DOWN) {
                    TilesPane.onDownArrowPressed();
                    return;
                }
                if (e.getCode() == KeyCode.RIGHT) {
                    TilesPane.onRightArrowPressed();
                    return;
                }
            }
            if (pressedOnCtrl && e.getCode() == KeyCode.ADD) {
                himTilesPane.onAddTileEntered();
                return;
            }
            if (pressedOnCtrl && e.getCode() == KeyCode.Z) {
                onUndo();
            }
        });
        controlPanel.getScene().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                pressedOnCtrl = false;
                firstPressedNumber = null;
            }
        });
    }

    private void onUndo() {
        try {
            AppController.hand = dominoService.getLastPlayedHand(AppController.hand);
        } catch (DAIException ex) {
            WarnDialog.showWarnDialog(ex);
        }
        reload();
    }

    private void showIncorrectTurnMessage() {
        WarnDialog.showWarnDialog(Messages.get("incorrectTurn"));
    }

    private void showAddLeftTilesCount() {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle(Messages.get("addLeftCount"));
        TCHNumberTextField countField = new TCHNumberTextField(TCHComponentSize.MEDIUM);
        TCHButton button = new TCHButton(Messages.get("add"));
        button.setOnAction(event -> {
            try {
                AppController.hand = dominoService.addLeftTiles(AppController.hand, countField.getNumber().intValue());
            } catch (DAIException ex) {
                WarnDialog.showWarnDialog(ex);
            }
            stage.close();
            reload();
        });
        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(countField, button);
        stage.setScene(new Scene(vBox));
        stage.setWidth(300);
        stage.setHeight(100);
        stage.showAndWait();
    }

    private void showNextGameWindow() {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(Messages.get("newGame"));
        TCHLabel label = new TCHLabel(Messages.get("startNewHand"));
        TCHButton yesButton = new TCHButton(Messages.get("yes"));
        yesButton.setOnAction(event -> {
            try {
                AppController.hand = dominoService.startGame(null, AppController.hand.getGameInfo().getGameId());
                reload();
            } catch (DAIException ex) {
                WarnDialog.showWarnDialog(ex);
            }
            stage.close();
        });
        TCHButton noButton = new TCHButton(Messages.get("no"));
        noButton.setOnAction(event -> {
            controlPanel.getRoot().setCenter(new GamePropertiesPane(controlPanel));
            AppController.startedGame = false;
            stage.close();
        });
        HBox hBox = new HBox(25);
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.getChildren().addAll(yesButton, noButton);
        VBox vBox = new VBox(30);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(label, hBox);
        stage.setScene(new Scene(vBox));
        stage.setWidth(400);
        stage.setHeight(140);
        stage.showAndWait();
    }
}
