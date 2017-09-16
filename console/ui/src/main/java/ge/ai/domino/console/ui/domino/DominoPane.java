package ge.ai.domino.console.ui.domino;

import ge.ai.domino.console.ui.TCHcomponents.TCHLabel;
import ge.ai.domino.console.ui.control_panel.ControlPanel;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.domain.domino.GameInfo;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.util.domino.DominoService;
import ge.ai.domino.util.domino.DominoServiceImpl;
import ge.ai.domino.util.tile.TileUtil;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

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
                    if (DominoPane.this.hand.getTableInfo().isMyTurn()) {
                        DominoPane.this.hand = dominoService.addTileForMe(DominoPane.this.hand, tile.getX(), tile.getY());
                        reload();
                    } else {
                        DominoPane.this.hand = dominoService.playForHim(DominoPane.this.hand, tile.getX(), tile.getY(), direction);
                        reload();
                    }
                }
            }

            @Override
            public void onAddTileEntered() {
                DominoPane.this.hand = dominoService.addTileForHim(DominoPane.this.hand);
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
                    DominoPane.this.hand = dominoService.playForMe(DominoPane.this.hand, tile.getX(), tile.getY(), direction);
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
                return;
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
        hand = dominoService.getLastPlayedHand(hand);
        reload();
    }

    private void showIncorrectTurnMessage() {
        WarnDialog.showWarnDialog(Messages.get("incorrectTurn"));
    }
}