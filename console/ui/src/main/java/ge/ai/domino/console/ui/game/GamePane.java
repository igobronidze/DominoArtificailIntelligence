package ge.ai.domino.console.ui.game;

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
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.tile.Tile;
import ge.ai.domino.service.game.GameService;
import ge.ai.domino.service.game.GameServiceImpl;
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

public class GamePane extends BorderPane {

    private final GameService gameService = new GameServiceImpl();

    private OpponentTilesPane opponentTilesPane;

    private MyTilesPane myTilesPane;

    private boolean pressedOnCtrl;

    private Integer firstPressedNumber;

    private final ControlPanel controlPanel;

    public GamePane(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
        reload();
    }

    private void reload() {
        initUI();
        initTopPane();
        initCenterPane();
        initBottomPane();
        initKeyboardListener();
        if (AppController.round != null && AppController.round.getGameInfo().isFinished()) {
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
        } else if (AppController.round.getTableInfo().isNeedToAddLeftTiles()) {
            showAddLeftTilesCount();
        }
    }

    private void initUI() {
        this.setPadding(new Insets(8));
    }

    private void initTopPane() {
        TableInfo tableInfo = AppController.round.getTableInfo();
        GameInfo gameInfo = AppController.round.getGameInfo();
        Label myPointsLabel = new TCHLabel(Messages.get("me") + " - " + gameInfo.getMyPoint() + " (" + AppController.round.getMyTiles().size() + ")");
        Label opponentPointLabel = new TCHLabel(Messages.get("opponent") + " - " + gameInfo.getOpponentPoint() + " (" + (int)tableInfo.getOpponentTilesCount() + ")");
        Label bazaarCountLabel = new TCHLabel(Messages.get("bazaar") + " (" + (int)tableInfo.getBazaarTilesCount() + ")");
        ImageView undoImage = new ImageView(ImageFactory.getImage("undo.png"));
        undoImage.setOnMouseClicked(event -> onUndo());
        FlowPane flowPane = new FlowPane(30, 10);
        flowPane.setPadding(new Insets(0, 4, 8, 4));
        flowPane.getChildren().addAll(myPointsLabel, opponentPointLabel, bazaarCountLabel, undoImage);
        this.setTop(flowPane);
    }

    private void initCenterPane() {
        opponentTilesPane = new OpponentTilesPane(AppController.round) {
            @Override
            public void onTileEntered(Tile tile, MoveDirection direction) {
                if (direction == null) {
                    showIncorrectMoveMessage();
                    reload();
                } else {
                    try {
                        if (AppController.round.getTableInfo().isMyMove()) {
                            TableInfo tableInfo = AppController.round.getTableInfo();
                            if (tableInfo.getLeft() == null && tableInfo.isFirstRound() && AppController.round.getMyTiles().size() == 6) {
                                Stage stage = new Stage();
                                stage.setResizable(false);
                                stage.setTitle(Messages.get("gameStarter"));
                                TCHLabel label = new TCHLabel(Messages.get("whoStartGame"));
                                TCHButton meButton = new TCHButton(Messages.get("me"));
                                meButton.setOnAction(event -> {
                                    try {
                                        AppController.round.getTableInfo().setMyMove(true);
                                        AppController.round = gameService.addTileForMe(AppController.round, tile.getLeft(), tile.getRight());
                                        reload();
                                        stage.close();
                                    } catch (DAIException ex) {
                                        WarnDialog.showWarnDialog(ex);
                                    }
                                });
                                TCHButton opponentButton = new TCHButton(Messages.get("he"));
                                opponentButton.setOnAction(event -> {
                                     try {
                                        AppController.round.getTableInfo().setMyMove(false);
                                        AppController.round = gameService.addTileForMe(AppController.round, tile.getLeft(), tile.getRight());
                                        reload();
                                        stage.close();
                                    } catch (DAIException ex) {
                                        WarnDialog.showWarnDialog(ex);
                                    }
                                });
                                HBox hBox = new HBox(25);
                                hBox.setAlignment(Pos.TOP_CENTER);
                                hBox.getChildren().addAll(meButton, opponentButton);
                                VBox vBox = new VBox(30);
                                vBox.setPadding(new Insets(20));
                                vBox.setAlignment(Pos.TOP_CENTER);
                                vBox.getChildren().addAll(label, hBox);
                                stage.setScene(new Scene(vBox));
                                stage.setWidth(350);
                                stage.setHeight(140);
                                stage.showAndWait();
                            } else {
                                AppController.round = gameService.addTileForMe(AppController.round, tile.getLeft(), tile.getRight());
                                reload();
                            }
                        } else {
                            AppController.round = gameService.playForOpponent(AppController.round, new Move(tile.getLeft(), tile.getRight(), direction));
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
                    AppController.round = gameService.addTileForOpponent(AppController.round);
                } catch (DAIException ex) {
                    WarnDialog.showWarnDialog(ex);
                }
                reload();
            }
        };
        this.setCenter(opponentTilesPane);
    }

    private void initBottomPane() {
        myTilesPane = new MyTilesPane(AppController.round) {
            @Override
            public void onTileEntered(Tile tile, MoveDirection direction) {
                if (direction == null) {
                    showIncorrectMoveMessage();
                    reload();
                } else {
                    try {
                        AppController.round = gameService.playForMe(AppController.round, new Move(tile.getLeft(), tile.getRight(), direction));
                        AppController.round.setAiPrediction(null);
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
                    Tile tile = new Tile(firstPressedNumber, secondPressedNumber);
                    if (myTilesPane.showTile(tile)) {
                        myTilesPane.onTilePressed(tile);
                    } else if (opponentTilesPane.showTile(tile)) {
                        opponentTilesPane.onTilePressed(tile);
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
                opponentTilesPane.onAddTileEntered();
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
            AppController.round = gameService.getLastPlayedRound(AppController.round);
        } catch (DAIException ex) {
            WarnDialog.showWarnDialog(ex);
        }
        reload();
    }

    private void showIncorrectMoveMessage() {
        WarnDialog.showWarnDialog(Messages.get("incorrectMove"));
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
                AppController.round = gameService.addLeftTiles(AppController.round, countField.getNumber().intValue());
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
        TCHLabel label = new TCHLabel(Messages.get("startNewRound"));
        TCHButton yesButton = new TCHButton(Messages.get("yes"));
        yesButton.setOnAction(event -> {
            try {
                AppController.round = gameService.startGame(null, AppController.round.getGameInfo().getGameId());
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
