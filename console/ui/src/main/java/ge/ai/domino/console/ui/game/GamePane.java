package ge.ai.domino.console.ui.game;

import ge.ai.domino.console.ui.controlpanel.AppController;
import ge.ai.domino.console.ui.controlpanel.ControlPanel;
import ge.ai.domino.console.ui.gameproperties.GamePropertiesPane;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.console.ui.util.service.ServiceExecutor;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.serverutil.TileAndMoveHelper;
import ge.ai.domino.service.game.GameService;
import ge.ai.domino.service.game.GameServiceImpl;
import ge.ai.domino.service.heuristic.HeuristicService;
import ge.ai.domino.service.heuristic.HeuristicServiceImpl;
import ge.ai.domino.service.sysparam.SystemParameterService;
import ge.ai.domino.service.sysparam.SystemParameterServiceImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GamePane extends BorderPane {

    private static final int IMAGE_WIDTH = 65;

    private static final int IMAGE_HEIGHT = 110;

    private static final SysParam bestMoveAutoPlay = new SysParam("bestMoveAutoPlay", "true");

    private static final SysParam detectAddedTiles = new SysParam("detectAddedTiles", "true");

    private final SystemParameterService systemParameterService = new SystemParameterServiceImpl();

    private final GameService gameService = new GameServiceImpl();

    private final HeuristicService heuristicService = new HeuristicServiceImpl();

    private final ControlPanel controlPanel;

    private ImageView upArrow;

    private ImageView rightArrow;

    private ImageView downArrow;

    private ImageView leftArrow;

    private Map<Tile, ImageView> myTilesImages = new HashMap<>();

    private Map<Tile, ImageView> opponentTilesImages = new HashMap<>();

    private Tile pressedTile;

    private boolean pressedOnMyTile;

    private boolean arrowsVisible;

    private boolean pressedOnCtrl;

    private Integer firstPressedNumber;

    private GameProperties gameProperties;

    private AiPrediction bestAiPrediction;

    private boolean hasPrediction;

    private boolean showingAddLeftTilesWindow;

    private boolean showingHeuristic;

    public GamePane(ControlPanel controlPanel, GameProperties gameProperties) {
        this.controlPanel = controlPanel;
        this.gameProperties = gameProperties;

        showDetectTilesWindow(true);

        reload(false, false);

        initKeyboardListener();
        initFocusLoseListener();
    }

    private void showDetectTilesWindow(boolean firstRound) {
        controlPanel.getStage().setIconified(true);

        new DetectTilesWindow() {
            @Override
            public void onYes() {
                ServiceExecutor.execute(() -> {
                    AppController.round = gameService.detectAnsAddInitialTilesForMe(AppController.round.getGameInfo().getGameId(), null);
                    controlPanel.getStage().setIconified(false);
                    controlPanel.getStage().requestFocus();
                    reload(false, false);
                });
            }

            @Override
            public void onStartMe() {
                ServiceExecutor.execute(() -> {
                    AppController.round = gameService.detectAnsAddInitialTilesForMe(AppController.round.getGameInfo().getGameId(), true);
                    controlPanel.getStage().setIconified(false);
                    controlPanel.getStage().requestFocus();
                    reload(false, false);
                });
            }

            @Override
            public void onStartHe() {
                ServiceExecutor.execute(() -> {
                    AppController.round = gameService.detectAnsAddInitialTilesForMe(AppController.round.getGameInfo().getGameId(), false);
                    controlPanel.getStage().setIconified(false);
                    controlPanel.getStage().requestFocus();
                    reload(false, false);
                });
            }

            @Override
            public void onNo() {
                controlPanel.getStage().setIconified(false);
                reload(false, false);
            }
        }.showWindow(firstRound);
    }

    private void showAddedTilesDetectWindow() {
        controlPanel.getStage().setIconified(true);

        new AddTilesDetectWindow() {
            @Override
            public void onYes() {
                ServiceExecutor.execute(() -> {
                    AppController.round = gameService.detectAndAddNewTilesForMe(AppController.round.getGameInfo().getGameId());
                    controlPanel.getStage().setIconified(false);
                    controlPanel.getStage().requestFocus();
                    reload(false, false);
                });
            }

            @Override
            public void onNo() {
                controlPanel.getStage().setIconified(false);
            }
        }.showWindow();
    }

    private void reload(boolean showDetectTilesWindow, boolean specifyWinner) {
        this.setPadding(new Insets(8));
        initTopPane();
        this.setCenter(getOpponentTilesPane());
        this.setBottom(getMyTilesPane());
        if (AppController.round.getGameInfo().isFinished()) {
            new SaveGameWindow() {
                @Override
                public void onSave() {
                    AppController.round = null;
                    onNewGame();
                    controlPanel.getRoot().setCenter(new GamePropertiesPane(controlPanel));
                }

                @Override
                public void onCancel() {}
            }.showWindow(specifyWinner);
        }
        if (showDetectTilesWindow && isFirsMove() && AppController.round.getMyTiles().isEmpty()) {
            showDetectTilesWindow(false);
        }
        if (AppController.round != null && systemParameterService.getBooleanParameterValue(detectAddedTiles) && !hasPrediction && AppController.round.getTableInfo().isMyMove()
                && AppController.round.getTableInfo().getBazaarTilesCount() != 2 && AppController.round.getTableInfo().getLeft() != null) {
            showAddedTilesDetectWindow();
        }
    }

    public abstract void onNewGame();

    private void initTopPane() {
        TableInfo tableInfo = AppController.round.getTableInfo();
        GameInfo gameInfo = AppController.round.getGameInfo();
        Label myPointsLabel = new TCHLabel(Messages.get("me") + " - " + gameInfo.getMyPoint() + " (" + AppController.round.getMyTiles().size() + ")");
        Label opponentPointLabel = new TCHLabel(Messages.get("opponent") + " - " + gameInfo.getOpponentPoint() + " (" + (int) tableInfo.getOpponentTilesCount() + ")");
        Label bazaarCountLabel = new TCHLabel(Messages.get("bazaar") + " (" + (int) tableInfo.getBazaarTilesCount() + ")");
        Label opponentLabel = new TCHLabel(Messages.get("opponent") + " - " + gameProperties.getOpponentName() + "(" + gameProperties.getChannel().getName() + ")");
        Label pointWorWinLabel = new TCHLabel(Messages.get("pointForWin") + " - " + gameProperties.getPointsForWin());

        ImageView undoImage = new ImageView(ImageFactory.getImage("undo.png"));
        undoImage.setCursor(Cursor.HAND);
        undoImage.setOnMouseClicked(event -> onUndo());

        ImageView skipImage = new ImageView(ImageFactory.getImage("skip.png"));
        skipImage.setCursor(Cursor.HAND);
        skipImage.setOnMouseClicked(event -> showSkipRoundWindow());

        ImageView editImage = new ImageView(ImageFactory.getImage("edit_green.png"));
        editImage.setCursor(Cursor.HAND);
        editImage.setOnMouseClicked(event -> showEditNameWindow());

        ImageView calculatorImage = new ImageView(ImageFactory.getImage("calculator.png"));
        calculatorImage.setCursor(Cursor.HAND);
        calculatorImage.setOnMouseClicked(event -> showHeuristicsWindow());

        FlowPane flowPane = new FlowPane(30, 10);
        flowPane.setPadding(new Insets(0, 4, 8, 4));
        flowPane.getChildren().addAll(myPointsLabel, opponentPointLabel, bazaarCountLabel, opponentLabel, pointWorWinLabel,
                undoImage, skipImage, editImage, calculatorImage);
        this.setTop(flowPane);
    }

    private void initFocusLoseListener() {
        controlPanel.getStage().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (systemParameterService.getBooleanParameterValue(bestMoveAutoPlay) && oldValue && !showingHeuristic) {
                if (hasPrediction && bestAiPrediction != null && !showingAddLeftTilesWindow) {
                    ServiceExecutor.execute(() -> {
                        Move move = bestAiPrediction.getMove();
                        if (AppController.round.getMyTiles().size() == 1) {
                            showAddLeftTilesCount(new Tile(move.getLeft(), move.getRight()), move.getDirection());
                        } else {
                            AppController.round = gameService.playForMe(AppController.round.getGameInfo().getGameId(), new Move(move.getLeft(), move.getRight(), move.getDirection()));
                            reload(true, false);
                        }
                    });
                } else if (AppController.round != null && !hasPrediction && AppController.round.getTableInfo().isMyMove() && AppController.round.getTableInfo().getBazaarTilesCount() == 2) {
                    ServiceExecutor.execute(() -> {
						Tile tile = new ArrayList<>(AppController.round.getOpponentTiles().keySet()).get(0);
						AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
						reload(true, false);
					});
                }
            }
        });
    }

    private void initKeyboardListener() {
        controlPanel.getScene().setOnKeyPressed(e -> {
            Integer secondPressedNumber;
            switch (e.getCode()) {
                case BACK_SPACE:
                    reload(true, false);
                    return;
                case CONTROL:
                    pressedOnCtrl = true;
                    return;
                case ALT:
                    fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, true, true, true, true));
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
                    if (AppController.round.getMyTiles().contains(tile)) {
                        onMyTilePressed(tile);
                    } else if (AppController.round.getOpponentTiles().containsKey(tile)) {
                        onOpponentTilePressed(tile);
                    }
                    firstPressedNumber = null;
                    return;
                } catch (NumberFormatException ignore) {}
            }


            if (pressedOnCtrl) {
                if (arrowsVisible) {
                    switch (e.getCode()) {
                        case UP:
                            onUpArrowPressed();
                            return;
                        case LEFT:
                            onLeftArrowPressed();
                            return;
                        case DOWN:
                            onDownArrowPressed();
                            return;
                        case RIGHT:
                            onRightArrowPressed();
                            return;
                    }
                }
                switch (e.getCode()) {
                    case ADD:
                        ontAddTileEntered();
                        return;
                    case Z:
                        onUndo();
                        break;
                    case B:
                        showSkipRoundWindow();
                        break;
                    case E:
                        showEditNameWindow();
                        break;
                    case H:
                        showHeuristicsWindow();
                        break;
                }
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
        ServiceExecutor.execute(() -> AppController.round = gameService.getLastPlayedRound(AppController.round.getGameInfo().getGameId()));
        reload(true, false);
    }

    private void showSkipRoundWindow() {
        new SkipRoundWindow() {
            @Override
            public void onSkip(int myPoint, int opponentPoint, int leftTilesCount, boolean startMe, boolean finishGame) {
                AppController.round = gameService.skipRound(AppController.round.getGameInfo().getGameId(), myPoint, opponentPoint, leftTilesCount, startMe, finishGame);
                reload(true, finishGame);
            }
        }.showWindow();
    }

    private void showEditNameWindow() {
        new EditNameWindow() {

            @Override
            public void onSave(String opponentName) {
                gameService.editOpponentNameInCache(AppController.round.getGameInfo().getGameId(), opponentName);
                gameProperties.setOpponentName(opponentName);
                reload(false, false);
            }

            @Override
            public void onCancel() {}
        }.showWindow();
    }

    private void showHeuristicsWindow() {
        showingHeuristic = true;
        Map<String, Double> heuristics = heuristicService.getHeuristics(AppController.round.getGameInfo().getGameId());
        new HeuristicsWindow() {
            @Override
            public void onClose() {
                showingHeuristic = false;
            }
        }.showWindow(heuristics);
    }

    private void showAddLeftTilesCount(final Tile playedTile, final MoveDirection direction) {
        showingAddLeftTilesWindow = true;

        new AddLeftTilesCountWindow() {
            @Override
            public void onSave(int count) {
                ServiceExecutor.execute(() -> {
                    gameService.specifyOpponentLeftTiles(AppController.round.getGameInfo().getGameId(), count);
                    if (playedTile == null && direction == null) {
                        AppController.round = gameService.addTileForOpponent(AppController.round.getGameInfo().getGameId());
                    } else if (direction == null) {
                        AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), playedTile.getLeft(), playedTile.getRight());
                    } else {
                        AppController.round = gameService.playForMe(AppController.round.getGameInfo().getGameId(), new Move(playedTile.getLeft(), playedTile.getRight(), direction));
                    }
                });
                showingAddLeftTilesWindow = false;
                reload(true, false);
            }
        }.showWindow();
    }

    private void showGameStarterWindow(Tile tile) {
        new GameStarterWindow() {
            @Override
            public void onMe() {
                ServiceExecutor.execute(() -> {
                    gameService.specifyRoundBeginner(AppController.round.getGameInfo().getGameId(), true);
                    AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
                    reload(true, false);
                });
            }

            @Override
            public void onHe() {
                ServiceExecutor.execute(() -> {
                    gameService.specifyRoundBeginner(AppController.round.getGameInfo().getGameId(), false);
                    AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
                    reload(true, false);
                });
            }
        }.showWindow();
    }

    private FlowPane getMyTilesPane() {
        FlowPane myTilesPane = new FlowPane();
        myTilesPane.setHgap(25);
        myTilesPane.setVgap(10);
        myTilesPane.setStyle("-fx-border-color: green; -fx-border-radius: 25px; -fx-border-size: 2px;");
        myTilesPane.setPadding(new Insets(8));
        initArrows();
        initMyTilesComponents(myTilesPane);
        return myTilesPane;
    }

    private FlowPane getOpponentTilesPane() {
        FlowPane opponentTilesPane = new FlowPane();
        opponentTilesPane.setHgap(25);
        opponentTilesPane.setVgap(10);
        initOpponentTilesComponents(opponentTilesPane);
        return opponentTilesPane;
    }

    private void initArrows() {
        upArrow = new ImageView(ImageFactory.getImage("arrows/up.png"));
        setImageStyle(upArrow);
        upArrow.setOnMouseClicked(e -> onUpArrowPressed());
        rightArrow = new ImageView(ImageFactory.getImage("arrows/right.png"));
        setImageStyle(rightArrow);
        rightArrow.setOnMouseClicked(e -> onRightArrowPressed());
        downArrow = new ImageView(ImageFactory.getImage("arrows/down.png"));
        setImageStyle(downArrow);
        downArrow.setOnMouseClicked(e -> onDownArrowPressed());
        leftArrow = new ImageView(ImageFactory.getImage("arrows/left.png"));
        leftArrow.setOnMouseClicked(e -> onLeftArrowPressed());
        setImageStyle(leftArrow);
        setImageVisibility(false);
    }

    private void setImageStyle(ImageView imageView) {
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setOnMouseEntered(event -> this.setCursor(Cursor.HAND));
        imageView.setOnMouseExited(event -> this.setCursor(Cursor.DEFAULT));
    }

    private void setImageVisibility(boolean visible) {
        upArrow.setVisible(visible);
        rightArrow.setVisible(visible);
        downArrow.setVisible(visible);
        leftArrow.setVisible(visible);
        arrowsVisible = visible;
    }

    private void initMyTilesComponents(FlowPane flowPane) {
        if (AppController.round.getAiPredictions() != null) {
            String warnMsgKey = AppController.round.getAiPredictions().getWarnMsgKey();
            if (warnMsgKey != null) {
                WarnDialog.showWarnDialog(Messages.get(warnMsgKey));
            }
        }
        hasPrediction = false;
        AppController.round.getMyTiles().stream().filter(tile -> AppController.round.getMyTiles().contains(tile)).forEach(tile -> {
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.TOP_CENTER);
            ImageView imageView = getImageView(tile, AppController.round.getTableInfo().isMyMove());
            myTilesImages.put(tile, imageView);
            List<AiPrediction> tilePredictions = getAiPredictionByTile(AppController.round.getAiPredictions() == null ? null : AppController.round.getAiPredictions().getAiPredictions(), tile);
            if (!tilePredictions.isEmpty()) {
                NumberFormat formatter = new DecimalFormat("#0.0000");
                for (AiPrediction aiPrediction : tilePredictions) {
                    String heuristic = aiPrediction.getHeuristicValue() == Integer.MIN_VALUE ? "NAN" : formatter.format(aiPrediction.getHeuristicValue());
                    Label label = new Label(aiPrediction.getMove().getDirection().name() + "(" + heuristic + ")");
                    if (aiPrediction.isBestMove()) {
                        bestAiPrediction = aiPrediction;
                        hasPrediction = true;
                        label.setStyle("-fx-font-size: 14px; -fx-text-fill: green; -fx-font-weight: bold");
                    }
                    vBox.getChildren().add(label);
                }
            }
            imageView.setOnMouseClicked(e -> onMyTilePressed(tile));
            vBox.getChildren().add(imageView);
            flowPane.getChildren().add(vBox);
        });

        flowPane.getChildren().addAll(leftArrow, upArrow, downArrow, rightArrow);
    }

    private List<AiPrediction> getAiPredictionByTile(List<AiPrediction> aiPredictions, Tile tile) {
        List<AiPrediction> result = new ArrayList<>();
        if (aiPredictions == null) {
            return result;
        }
        for (AiPrediction aiPrediction : aiPredictions) {
            if (aiPrediction.getMove().getLeft() == tile.getLeft() && aiPrediction.getMove().getRight() == tile.getRight()) {
                result.add(aiPrediction);
            }
        }
        return result;
    }

    private void initOpponentTilesComponents(FlowPane flowPane) {
        NumberFormat formatter = new DecimalFormat("#0.0000");
        for (int i = 6; i >= 0; i--) {
            HBox hBox = new HBox();
            hBox.setStyle("-fx-border-color: green; -fx-border-radius: 25px; -fx-border-size: 1px;");
            hBox.setSpacing(10);
            hBox.setPadding(new Insets(8));
            for (int j = i; j >= 0; j--) {
                Tile tile = new Tile(i, j);
                Double prob = AppController.round.getOpponentTiles().get(tile);
                if (AppController.round.getOpponentTiles().containsKey(tile)) {
                    ImageView imageView = getImageView(tile, true);
                    Label opponentLabel = new Label("" + (prob == null ? "N" : formatter.format(prob)));
                    opponentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-font-weight: bold");
                    VBox vBox = new VBox(5);
                    vBox.setAlignment(Pos.TOP_CENTER);
                    vBox.getChildren().addAll(opponentLabel, imageView);
                    hBox.getChildren().add(vBox);
                    opponentTilesImages.put(tile, imageView);
                    imageView.setOnMouseClicked(e -> onOpponentTilePressed(tile));
                } else {
                    VBox vBox = new VBox();
                    vBox.setPrefHeight(IMAGE_HEIGHT);
                    vBox.setPrefWidth(IMAGE_WIDTH);
                    hBox.getChildren().addAll(vBox);
                }
            }
            flowPane.getChildren().add(hBox);
        }
        if (!AppController.round.getTableInfo().isMyMove()) {
            ImageView addImageView = new ImageView(ImageFactory.getImage("add_black.png"));
            addImageView.setOnMouseEntered(event -> this.setCursor(Cursor.HAND));
            addImageView.setOnMouseExited(event -> this.setCursor(Cursor.DEFAULT));
            setImageStyle(addImageView);
            flowPane.getChildren().add(addImageView);
            addImageView.setOnMouseClicked(e -> ontAddTileEntered());
        }
    }

    private void onMyTilePressed(Tile tile) {
        if (AppController.round.getTableInfo().isMyMove()) {
            if (isFirsMove()) {
                onMyTileEntered(tile, MoveDirection.LEFT);
            } else {
                myTilesImages.get(tile).setFitHeight(IMAGE_HEIGHT + 10);
                myTilesImages.get(tile).setFitWidth(IMAGE_WIDTH + 10);
                pressedTile = tile;
                pressedOnMyTile = true;
                setImageVisibility(true);
            }
        }
    }

    private void onOpponentTilePressed(Tile tile) {
        if (!AppController.round.getTableInfo().isMyMove()) {
            if (isFirsMove()) {
                ontOpponentTileEntered(tile, MoveDirection.LEFT);
            } else {
                opponentTilesImages.get(tile).setFitHeight(IMAGE_HEIGHT + 10);
                opponentTilesImages.get(tile).setFitWidth(IMAGE_WIDTH + 10);
                pressedTile = tile;
                pressedOnMyTile = false;
                setImageVisibility(true);
            }
        } else {
            ontOpponentTileEntered(tile, MoveDirection.LEFT);
        }
    }

    private ImageView getImageView(Tile tile, boolean clickable) {
        ImageView imageView = new ImageView(ImageFactory.getImage("tiles/" + tile.getLeft() + "-" + tile.getRight() + ".png"));
        imageView.setFitHeight(IMAGE_HEIGHT);
        imageView.setFitWidth(IMAGE_WIDTH);
        if (clickable) {
            imageView.setOnMouseEntered(event -> this.setCursor(Cursor.HAND));
            imageView.setOnMouseExited(event -> this.setCursor(Cursor.DEFAULT));
        }
        return imageView;
    }

    private boolean isFirsMove() {
        if (AppController.round == null) {
            return false;
        }
        TableInfo tableInfo = AppController.round.getTableInfo();
        return tableInfo.getTop() == null && tableInfo.getRight() == null && tableInfo.getBottom() == null && tableInfo.getLeft() == null;
    }

    private void onMyTileEntered(Tile tile, MoveDirection direction) {
        ServiceExecutor.execute(() -> {
            if (AppController.round.getMyTiles().size() == 1) {
                showAddLeftTilesCount(tile, direction);
            } else {
                AppController.round = gameService.playForMe(AppController.round.getGameInfo().getGameId(), TileAndMoveHelper.getMove(tile, direction));
                reload(true, false);
            }
        });
    }

    private void ontOpponentTileEntered(Tile tile, MoveDirection direction) {
        if (AppController.round.getTableInfo().isMyMove()) {
            TableInfo tableInfo = AppController.round.getTableInfo();
            if (tableInfo.getRoundBlockingInfo().isOmitOpponent() && tableInfo.getBazaarTilesCount() == 2) {
                showAddLeftTilesCount(tile, null);
            } else if (tableInfo.getLeft() == null && tableInfo.isFirstRound() && AppController.round.getMyTiles().size() == 6) {
                showGameStarterWindow(tile);
            } else {
                ServiceExecutor.execute(() -> {
                    AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
                    reload(true, false);
                });
            }
        } else {
            ServiceExecutor.execute(() -> AppController.round = gameService.playForOpponent(AppController.round.getGameInfo().getGameId(), TileAndMoveHelper.getMove(tile, direction)));
            reload(true, false);
        }
    }

    private void onUpArrowPressed() {
        if (pressedOnMyTile) {
            onMyTileEntered(pressedTile, MoveDirection.TOP);
        } else {
            ontOpponentTileEntered(pressedTile, MoveDirection.TOP);
        }
    }

    private void onRightArrowPressed() {
        if (pressedOnMyTile) {
            onMyTileEntered(pressedTile, MoveDirection.RIGHT);
        } else {
            ontOpponentTileEntered(pressedTile, MoveDirection.RIGHT);
        }
    }

    private void onDownArrowPressed() {
        if (pressedOnMyTile) {
            onMyTileEntered(pressedTile, MoveDirection.BOTTOM);
        } else {
            ontOpponentTileEntered(pressedTile, MoveDirection.BOTTOM);
        }
    }

    private void onLeftArrowPressed() {
        if (pressedOnMyTile) {
            onMyTileEntered(pressedTile, MoveDirection.LEFT);
        } else {
            ontOpponentTileEntered(pressedTile, MoveDirection.LEFT);
        }
    }

    private void ontAddTileEntered() {
        ServiceExecutor.execute(() -> {
            if (AppController.round.getTableInfo().getRoundBlockingInfo().isOmitMe() && AppController.round.getTableInfo().getBazaarTilesCount() == 2) {
                showAddLeftTilesCount(null, null);
            } else {
                AppController.round = gameService.addTileForOpponent(AppController.round.getGameInfo().getGameId());
            }
        });
        reload(true, false);
    }
}
