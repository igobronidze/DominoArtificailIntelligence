package ge.ai.domino.console.ui.domino;

import ge.ai.domino.console.transfer.dto.domino.GameDTO;
import ge.ai.domino.console.transfer.dto.domino.PlayDirectionDTO;
import ge.ai.domino.console.transfer.dto.domino.PlayTypeDTO;
import ge.ai.domino.console.transfer.dto.domino.TableInfoDTO;
import ge.ai.domino.console.transfer.dto.domino.TileDTO;
import ge.ai.domino.console.transfer.manager.domino.DominoManager;
import ge.ai.domino.console.transfer.manager.domino.DominoMangerImpl;
import ge.ai.domino.console.ui.TCHcomponents.TCHLabel;
import ge.ai.domino.console.ui.control_panel.ControlPanel;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.util.string.StringUtil;
import ge.ai.domino.util.tile.TileUtil;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class DominoPane extends BorderPane {

    private static final DominoManager dominoManager = new DominoMangerImpl();

    private PlayTypeDTO playType;

    private int startedTiles = 0;

    private GameDTO game;

    private HimTilesPane himTilesPane;

    private MyTilesPane myTilesPane;

    private boolean pressedOnCtrl;

    private int firstPressedNumber;

    public DominoPane(GameDTO game) {
        this.game = game;
        this.playType = PlayTypeDTO.GET_STARTED_TILES;
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
        TableInfoDTO tableInfo = game.getCurrHand().getTableInfo();
        Label myPointsLabel = new TCHLabel(Messages.get("me") + " - " + game.getMyPoints() + " (" + tableInfo.getMyTilesCount() + ")");
        String opponentName = game.getGameProperties().getOpponentName();
        Label himPointLabel = new TCHLabel((StringUtil.isEmpty(opponentName) ? Messages.get("unknown") : opponentName) + " - " + game.getHimPoints() + " (" + tableInfo.getHimTilesCount() + ")");
        Label bazaarCountLabel = new TCHLabel("(" + tableInfo.getBazaarTilesCount() + ")");
        FlowPane flowPane = new FlowPane(30, 10);
        flowPane.setPadding(new Insets(0, 4, 8, 4));
        flowPane.getChildren().addAll(myPointsLabel, himPointLabel, bazaarCountLabel);
        this.setTop(flowPane);
    }

    private void initCenterPane() {
        himTilesPane = new HimTilesPane(DominoPane.this.game, playType) {
            @Override
            public void onTileEntered(TileDTO tile, PlayDirectionDTO direction) {
                if (direction == PlayDirectionDTO.INCORRECT) {
                    showIncorrectTurnMessage();
                    reload();
                } else {
                    if (DominoPane.this.playType == PlayTypeDTO.GET_STARTED_TILES) {
                        DominoPane.this.game.setCurrHand(dominoManager.addTileForMe(DominoPane.this.game.getCurrHand(), tile.getX(), tile.getY()));
                        startedTiles++;
                        if (startedTiles == 7) {
                            startedTiles = 100;
                            DominoPane.this.playType = game.getCurrHand().getTableInfo().isMyTurn() ? PlayTypeDTO.ME : PlayTypeDTO.HIM;
                        }
                        reload();
                    } else if (DominoPane.this.playType == PlayTypeDTO.ME) {
                        if (DominoPane.this.game.getCurrHand().getTableInfo().getBazaarTilesCount() == 2) {
                            DominoPane.this.playType = PlayTypeDTO.HIM;
                        }
                        DominoPane.this.game.setCurrHand(dominoManager.addTileForMe(DominoPane.this.game.getCurrHand(), tile.getX(), tile.getY()));
                        reload();
                    } else if (DominoPane.this.playType == PlayTypeDTO.HIM) {
                        DominoPane.this.game.setCurrHand(dominoManager.playForHim(DominoPane.this.game.getCurrHand(), tile.getX(), tile.getY(), direction, DominoPane.this.game.getId()));
                        DominoPane.this.playType = PlayTypeDTO.ME;
                        reload();
                    }
                }
            }

            @Override
            public void onAddTileEntered() {
                if (DominoPane.this.game.getCurrHand().getTableInfo().getBazaarTilesCount() == 2) {
                    DominoPane.this.playType = PlayTypeDTO.ME;
                } else {
                    DominoPane.this.game.setCurrHand(dominoManager.addTileForHim(DominoPane.this.game.getCurrHand(), DominoPane.this.game.getId()));
                    reload();
                }
            }
        };
        this.setCenter(himTilesPane);
    }

    private void initBottomPane() {
        myTilesPane = new MyTilesPane(DominoPane.this.game, playType) {
            @Override
            public void onTileEntered(TileDTO tile, PlayDirectionDTO direction) {
                if (direction == PlayDirectionDTO.INCORRECT) {
                    showIncorrectTurnMessage();
                    reload();
                } else {
                    DominoPane.this.game.setCurrHand(dominoManager.playForMe(DominoPane.this.game.getCurrHand(), tile.getX(), tile.getY(), direction));
                    DominoPane.this.playType = PlayTypeDTO.HIM;
                    reload();
                }
            }
        };
        this.setBottom(myTilesPane);
    }

    private void initKeyboardListener() {
        ControlPanel.getScene().setOnKeyPressed(e -> {
            int secondPressedNumber = 0;
            if (e.getCode() == KeyCode.CONTROL) {
                pressedOnCtrl = true;
            } else if (pressedOnCtrl && firstPressedNumber == 0) {
                try {
                    firstPressedNumber = Integer.parseInt(e.getText());
                } catch (NumberFormatException ignore) {}
            } else if (pressedOnCtrl) {
                try {
                    secondPressedNumber = Integer.parseInt(e.getText());
                } catch (NumberFormatException ignore) {}
            }
            if (pressedOnCtrl && TilesPane.isArrowsVisible()) {
                if (e.getCode() == KeyCode.UP) {
                    TilesPane.onUpArrowPressed();
                }
                if (e.getCode() == KeyCode.LEFT) {
                    TilesPane.onRightArrowPressed();
                }
                if (e.getCode() == KeyCode.DOWN) {
                    TilesPane.onDownArrowPressed();
                }
                if (e.getCode() == KeyCode.RIGHT) {
                    TilesPane.onLeftArrowPressed();
                }
            }
            if (pressedOnCtrl && e.getCode() == KeyCode.ADD) {
                himTilesPane.onAddTileEntered();
            }
            if (secondPressedNumber != 0 && firstPressedNumber != 0) {
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
                firstPressedNumber = 0;
                secondPressedNumber = 0;
            }
        });
        this.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                pressedOnCtrl = false;
                firstPressedNumber = 0;
            }
        });
    }

    private void showIncorrectTurnMessage() {
        WarnDialog.showWarnDialog(Messages.get("incorrectTurn"));
    }
}
