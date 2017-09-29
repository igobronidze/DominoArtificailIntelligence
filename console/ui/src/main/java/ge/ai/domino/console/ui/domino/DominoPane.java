package ge.ai.domino.console.ui.domino;

import ge.ai.domino.console.ui.TCHcomponents.TCHButton;
import ge.ai.domino.console.ui.TCHcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.TCHcomponents.TCHLabel;
import ge.ai.domino.console.ui.TCHcomponents.TCHNumberTextField;
import ge.ai.domino.console.ui.control_panel.ControlPanel;
import ge.ai.domino.console.ui.control_panel.GamePropertiesPane;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.domain.domino.GameInfo;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.util.domino.DominoService;
import ge.ai.domino.util.domino.DominoServiceImpl;
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

    private static final DominoService dominoService = new DominoServiceImpl();

    private Hand hand;

    private HimTilesPane himTilesPane;

    private MyTilesPane myTilesPane;

    private boolean pressedOnCtrl;

    private Integer firstPressedNumber;

    public DominoPane(Hand hand) {
        this.hand = hand;
        reload();
    }

    private void reload() {
        initUI();
        initTopPane();
        initCenterPane();
        initBottomPane();
        initKeyboardListener();
        if (hand.getGameInfo().isFinished()) {
            showNextGameWindow();
        } else if (hand.getTableInfo().isNeedToAddLeftTiles()) {
            showAddLeftTilesCount();
        }
    }

    private void initUI() {
        this.setPadding(new Insets(8));
    }

    private void initTopPane() {
        TableInfo tableInfo = hand.getTableInfo();
        GameInfo gameInfo = hand.getGameInfo();
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
        himTilesPane = new HimTilesPane(DominoPane.this.hand) {
            @Override
            public void onTileEntered(Tile tile, PlayDirection direction) {
                if (direction == PlayDirection.INCORRECT) {
                    showIncorrectTurnMessage();
                    reload();
                } else {
                    try {
                        if (DominoPane.this.hand.getTableInfo().isMyTurn()) {
                            DominoPane.this.hand = dominoService.addTileForMe(DominoPane.this.hand, tile.getX(), tile.getY());
                            reload();
                        } else {
                            DominoPane.this.hand = dominoService.playForHim(DominoPane.this.hand, tile.getX(), tile.getY(), direction);
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
                    DominoPane.this.hand = dominoService.addTileForHim(DominoPane.this.hand);
                } catch (DAIException ex) {
                    WarnDialog.showWarnDialog(ex);
                }
                reload();
            }
        };
        this.setCenter(himTilesPane);
    }

    private void initBottomPane() {
        myTilesPane = new MyTilesPane(DominoPane.this.hand) {
            @Override
            public void onTileEntered(Tile tile, PlayDirection direction) {
                if (direction == PlayDirection.INCORRECT) {
                    showIncorrectTurnMessage();
                    reload();
                } else {
                    try {
                        DominoPane.this.hand = dominoService.playForMe(DominoPane.this.hand, tile.getX(), tile.getY(), direction);
                        DominoPane.this.hand.setAiPrediction(null);
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
        ControlPanel.getScene().setOnKeyPressed(e -> {
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
        ControlPanel.getScene().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                pressedOnCtrl = false;
                firstPressedNumber = null;
            }
        });
    }

    private void onUndo() {
        try {
            hand = dominoService.getLastPlayedHand(hand);
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
                hand = dominoService.addLeftTiles(hand, countField.getNumber().intValue());
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
                hand = dominoService.startGame(null, hand.getGameInfo().getGameId());
                reload();
            } catch (DAIException ex) {
                WarnDialog.showWarnDialog(ex);
            }
            stage.close();
        });
        TCHButton noButton = new TCHButton(Messages.get("no"));
        noButton.setOnAction(event -> {
            ControlPanel.getRoot().setCenter(new GamePropertiesPane());
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
