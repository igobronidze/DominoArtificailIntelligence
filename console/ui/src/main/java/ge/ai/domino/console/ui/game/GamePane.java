package ge.ai.domino.console.ui.game;

import ge.ai.domino.console.ui.controlpanel.AppController;
import ge.ai.domino.console.ui.controlpanel.ControlPanel;
import ge.ai.domino.console.ui.game.helper.GamePaneHelper;
import ge.ai.domino.console.ui.game.windows.*;
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
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.serverutil.TileAndMoveHelper;
import ge.ai.domino.service.game.GameService;
import ge.ai.domino.service.game.GameServiceImpl;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GamePane extends BorderPane {

    private static final int TILE_IMAGE_WIDTH = 65;

    private static final int TILE_IMAGE_HEIGHT = 110;

    private final GameService gameService = new GameServiceImpl();

    private final ControlPanel controlPanel;

    private final Map<Tile, ImageView> myTilesImages = new HashMap<>();

    private final Map<Tile, ImageView> opponentTilesImages = new HashMap<>();

    private final GameProperties gameProperties;

    private final GamePaneInitialData gamePaneInitialData;

    private ImageView upArrow;

    private ImageView rightArrow;

    private ImageView downArrow;

    private ImageView leftArrow;

    private Tile pressedTile;

    private boolean pressedOnMyTile;

    private boolean arrowsVisible;

    private Integer firstPressedNumber;

    private AiPrediction bestAiPrediction;

    private boolean hasPrediction;

    private boolean showingAddLeftTilesWindow;

    private boolean showingHeuristic;

    private Label mainInfoLabel;

    private Label mainInfoSecondaryLabel;

    private boolean roundWillBeBlocked;

    private boolean reloading;

    public GamePane(ControlPanel controlPanel, GameProperties gameProperties, GamePaneInitialData gamePaneInitialData) {
        this.controlPanel = controlPanel;
        this.gameProperties = gameProperties;
        this.gamePaneInitialData = gamePaneInitialData;

        showDetectTilesWindow(true);

        reload(false, false);

        initKeyboardListener();
        initFocusLoseListener();
    }

    public abstract void onNewGame();

    private void showDetectTilesWindow(boolean firstRound) {
        controlPanel.getStage().setIconified(true);

        new DetectTilesWindow() {
            @Override
            @SuppressWarnings("Duplicates")
            public void onYes(boolean withSecondParams) {
                new ServiceExecutor() {
                    @Override
                    public boolean isAsync() {
                        return true;
                    }

                    @Override
                    public void onAsyncProcessFinish() {
                        controlPanel.getStage().setIconified(false);
                        controlPanel.getStage().requestFocus();
                        reload(false, false);
                    }
                }.execute(() -> AppController.round = gameService.detectAndAddInitialTilesForMe(AppController.round.getGameInfo().getGameId(), null, withSecondParams));
            }

            @Override
            public void onStartMe(boolean withSecondParams) {
                new ServiceExecutor() {
                    @Override
                    public boolean isAsync() {
                        return true;
                    }

                    @Override
                    public void onAsyncProcessFinish() {
                        mainInfoSecondaryLabel.setText("");
                        controlPanel.getStage().setIconified(false);
                        controlPanel.getStage().requestFocus();

                        Tile highestTile = GamePaneHelper.getHighestTile();
                        onMyTileEntered(highestTile, null);
                    }
                }.execute(() -> AppController.round = gameService.detectAndAddInitialTilesForMe(AppController.round.getGameInfo().getGameId(), true, withSecondParams));
            }

            @Override
            @SuppressWarnings("Duplicates")
            public void onStartHe(boolean withSecondParams) {
                new ServiceExecutor() {
                    @Override
                    public boolean isAsync() {
                        return true;
                    }

                    @Override
                    public void onAsyncProcessFinish() {
                        controlPanel.getStage().setIconified(false);
                        controlPanel.getStage().requestFocus();
                        reload(false, false);
                    }
                }.execute(() -> AppController.round = gameService.detectAndAddInitialTilesForMe(AppController.round.getGameInfo().getGameId(), false, withSecondParams));
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
            @SuppressWarnings("Duplicates")
            public void onYes(boolean withSecondParams) {
                mainInfoSecondaryLabel.setText(Messages.get("working"));

                new ServiceExecutor() {
                    @Override
                    public boolean isAsync() {
                        return true;
                    }

                    @Override
                    public void onAsyncProcessFinish() {
                        mainInfoSecondaryLabel.setText("");
                        controlPanel.getStage().setIconified(false);
                        controlPanel.getStage().requestFocus();
                        reload(false, false);
                    }
                }.execute(() -> AppController.round = gameService.detectAndAddNewTilesForMe(AppController.round.getGameInfo().getGameId(), withSecondParams));
            }

            @Override
            public void onNo() {
                controlPanel.getStage().setIconified(false);
            }
        }.showWindow();
    }

    private void reload(boolean showDetectTilesWindow, boolean specifyWinner) {
        this.setPadding(new Insets(8));
        this.setTop(getTopPane());
        this.setCenter(getCenterPane());
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
        if (AppController.round != null && gamePaneInitialData.isDetectAddedTiles() && !hasPrediction && AppController.round.getTableInfo().isMyMove()
                && AppController.round.getTableInfo().getBazaarTilesCount() != 2 && AppController.round.getTableInfo().getLeft() != null) {
            showAddedTilesDetectWindow();
        }
        reloading = false;
    }

    private FlowPane getTopPane() {
        TableInfo tableInfo = AppController.round.getTableInfo();
        GameInfo gameInfo = AppController.round.getGameInfo();
        Label myPointsLabel = new TCHLabel(Messages.get("me") + " - " + gameInfo.getMyPoint() + " (" + AppController.round.getMyTiles().size() + ")");
        Label opponentPointLabel = new TCHLabel(Messages.get("opponent") + " - " + gameInfo.getOpponentPoint() + " (" + (int) tableInfo.getOpponentTilesCount() + ")");
        Label bazaarCountLabel = new TCHLabel(Messages.get("bazaar") + " (" + (int) tableInfo.getBazaarTilesCount() + ")");
        Label opponentLabel = new TCHLabel(Messages.get("opponent") + " - " + gameProperties.getOpponentName() + "(" + gameProperties.getChannel().getName() + ")");
        Label pointWorWinLabel = new TCHLabel(Messages.get("pointForWin") + " - " + gameProperties.getPointsForWin());

        ImageView addImage = new ImageView(ImageFactory.getImage("add_black.png"));
        addImage.setCursor(Cursor.HAND);
        addImage.setOnMouseClicked(e -> {
            if (!AppController.round.getTableInfo().isMyMove()) {
                onAddTileEntered();
            }
        });

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
                addImage, undoImage, skipImage, editImage, calculatorImage);
        return flowPane;
    }

    private void initFocusLoseListener() {
        controlPanel.getStage().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (gamePaneInitialData.isBestMoveAutoPlay() && !showingHeuristic && !reloading) {
                if (hasPrediction && bestAiPrediction != null && !showingAddLeftTilesWindow) {
                    Move move = bestAiPrediction.getMove();
                    onMyTileEntered(new Tile(move.getLeft(), move.getRight()), move.getDirection());
                }
            }
        });
    }

    private void initKeyboardListener() {
        controlPanel.getScene().setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP:
                    if (arrowsVisible) {
                        onUpArrowPressed();
                    }
                    break;
                case LEFT:
                    if (arrowsVisible) {
                        onLeftArrowPressed();
                    }
                    break;
                case DOWN:
                    if (arrowsVisible) {
                        onDownArrowPressed();
                    }
                    break;
                case RIGHT:
                    if (arrowsVisible) {
                        onRightArrowPressed();
                    }
                    break;
                case ADD:
                    onAddTileEntered();
                    break;
                case Z:
                    mainInfoLabel.setText("UNDO");
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
                case BACK_SPACE:
                    firstPressedNumber = null;
                    pressedTile = null;
                    mainInfoLabel.setText("");
                    reload(false, false);
                    break;
                case ALT:
                    fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, true, true, true, true));
                    break;
                case NUMPAD0: case NUMPAD1: case NUMPAD2: case NUMPAD3: case NUMPAD4: case NUMPAD5: case NUMPAD6:
                case DIGIT0: case DIGIT1: case DIGIT2: case DIGIT3: case DIGIT4: case DIGIT5: case DIGIT6:
                    if (firstPressedNumber == null) {
                        firstPressedNumber = Integer.parseInt(e.getText());
                        mainInfoLabel.setText(String.valueOf(firstPressedNumber));
                    } else {
                        int secondPressedNumber = Integer.parseInt(e.getText());
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
                    }
                 break;
            }
        });
    }

    private void onUndo() {
        new ServiceExecutor() {}.execute(() -> AppController.round = gameService.getLastPlayedRound(AppController.round.getGameInfo().getGameId()));
        reload(true, false);
    }

    private void showSkipRoundWindow() {
        new SkipRoundWindow() {
            @Override
            public void onSkip(int myPoint, int opponentPoint, int leftTilesCount, boolean startMe, boolean finishGame) {
                new ServiceExecutor() {}.execute(() ->
                        AppController.round = gameService.skipRound(AppController.round.getGameInfo().getGameId(), myPoint, opponentPoint, leftTilesCount, startMe, finishGame));
                reload(true, finishGame);
            }
        }.showWindow();
    }

    private void showEditNameWindow() {
        new EditNameWindow() {

            @Override
            public void onSave(String opponentName) {
                new ServiceExecutor() {}.execute(() -> gameService.editOpponentNameInCache(AppController.round.getGameInfo().getGameId(), opponentName));
                gameProperties.setOpponentName(opponentName);
                reload(false, false);
            }

            @Override
            public void onCancel() {}
        }.showWindow();
    }

    private void showHeuristicsWindow() {
        showingHeuristic = true;
        new HeuristicsWindow() {
            @Override
            public void onClose() {
                showingHeuristic = false;
            }
        }.showWindow();
    }

    private void showAddLeftTilesCount(final Tile playedTile, final MoveDirection direction, MoveType moveType) {
        showingAddLeftTilesWindow = true;

        new AddLeftTilesCountWindow() {
            @Override
            public void onSave(int count) {
                new ServiceExecutor() {}.execute(() -> {
                    gameService.specifyOpponentLeftTiles(AppController.round.getGameInfo().getGameId(), count);
                    switch (moveType) {
                        case ADD_FOR_ME:
                            AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), playedTile.getLeft(), playedTile.getRight());
                            break;
                        case ADD_FOR_OPPONENT:
                            AppController.round = gameService.addTileForOpponent(AppController.round.getGameInfo().getGameId());
                            break;
                        case PLAY_FOR_ME:
                            AppController.round = gameService.playForMe(AppController.round.getGameInfo().getGameId(), new Move(playedTile.getLeft(), playedTile.getRight(), direction));
                            break;
                        case PLAY_FOR_OPPONENT:
                            AppController.round = gameService.playForOpponent(AppController.round.getGameInfo().getGameId(), new Move(playedTile.getLeft(), playedTile.getRight(), direction));
                            break;
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
                new ServiceExecutor() {}.execute(() -> {
                    gameService.specifyRoundBeginner(AppController.round.getGameInfo().getGameId(), true);
                    AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
                });
                reload(true, false);
            }

            @Override
            public void onHe() {
                new ServiceExecutor() {}.execute(() -> {
                    gameService.specifyRoundBeginner(AppController.round.getGameInfo().getGameId(), false);
                    AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
                });
                reload(true, false);
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

    private VBox getCenterPane() {
        FlowPane opponentTilesPane = new FlowPane();
        opponentTilesPane.setHgap(25);
        opponentTilesPane.setVgap(10);
        initOpponentTilesComponents(opponentTilesPane);

        if (mainInfoLabel == null) {
            mainInfoLabel = new Label("");
            mainInfoLabel.setStyle("-fx-font-size: 70px; -fx-text-fill: green;");

            mainInfoSecondaryLabel = new Label("");
            mainInfoSecondaryLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: green;");
        }

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(opponentTilesPane, mainInfoLabel, mainInfoSecondaryLabel);

        return vBox;
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
            List<AiPrediction> tilePredictions = GamePaneHelper.getAiPredictionByTile(AppController.round.getAiPredictions() == null ? null : AppController.round.getAiPredictions().getAiPredictions(), tile);
            if (!tilePredictions.isEmpty()) {
                hasPrediction = true;
                NumberFormat formatter = new DecimalFormat("#0.0000");
                for (AiPrediction aiPrediction : tilePredictions) {
                    String heuristic = aiPrediction.getHeuristicValue() == Integer.MIN_VALUE ? "NAN" : formatter.format(aiPrediction.getHeuristicValue());
                    Label label = new Label(aiPrediction.getMove().getDirection().name() + "(" + heuristic + ")");
                    if (aiPrediction.getMoveProbability() == 1.0) {
                        bestAiPrediction = aiPrediction;
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
                    vBox.setPrefHeight(TILE_IMAGE_HEIGHT);
                    vBox.setPrefWidth(TILE_IMAGE_WIDTH);
                    hBox.getChildren().addAll(vBox);
                }
            }
            flowPane.getChildren().add(hBox);
        }
    }

    private void onMyTilePressed(Tile tile) {
        if (AppController.round.getTableInfo().isMyMove()) {
            if (isFirsMove()) {
                onMyTileEntered(tile, null);
            } else {
                myTilesImages.get(tile).setFitHeight(TILE_IMAGE_HEIGHT + 10);
                myTilesImages.get(tile).setFitWidth(TILE_IMAGE_WIDTH + 10);
                pressedTile = tile;
                pressedOnMyTile = true;
                setImageVisibility(true);
                mainInfoLabel.setText(tile.getLeft() + " - " + tile.getRight());
            }
        }
    }

    private void onOpponentTilePressed(Tile tile) {
        if (!AppController.round.getTableInfo().isMyMove()) {
            if (isFirsMove()) {
                onOpponentTileEntered(tile, null);
            } else {
                opponentTilesImages.get(tile).setFitHeight(TILE_IMAGE_HEIGHT + 12);
                opponentTilesImages.get(tile).setFitWidth(TILE_IMAGE_WIDTH + 12);
                pressedTile = tile;
                pressedOnMyTile = false;
                setImageVisibility(true);
                mainInfoLabel.setText(tile.getLeft() + " - " + tile.getRight());
            }
        } else {
            onOpponentTileEntered(tile, null);
        }
    }

    private ImageView getImageView(Tile tile, boolean clickable) {
        ImageView imageView = new ImageView(ImageFactory.getImage("tiles/" + tile.getLeft() + "-" + tile.getRight() + ".png"));
        imageView.setFitHeight(TILE_IMAGE_HEIGHT);
        imageView.setFitWidth(TILE_IMAGE_WIDTH);
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
        if (reloading) {
            return;
        }
        mainInfoLabel.setText(tile.getLeft() + " - " + tile.getRight() + " " + (direction == null ? "" : direction.name()));

        if (AppController.round.getMyTiles().size() == 1) {
            showAddLeftTilesCount(tile, direction, MoveType.PLAY_FOR_ME);
        } else {
            Move move = TileAndMoveHelper.getMove(tile, direction);

            new ServiceExecutor() {}.execute(() -> roundWillBeBlocked = gameService.roundWillBeBlocked(AppController.round.getGameInfo().getGameId(), move));

            if (roundWillBeBlocked) {
                showAddLeftTilesCount(new Tile(move.getLeft(), move.getRight()), move.getDirection(), MoveType.PLAY_FOR_ME);
            } else {
                new ServiceExecutor() {}.execute(() -> AppController.round = gameService.playForMe(AppController.round.getGameInfo().getGameId(), move));
                reload(true, false);
            }
        }
    }

    private void onOpponentTileEntered(Tile tile, MoveDirection direction) {
        if (reloading) {
            return;
        }
        mainInfoLabel.setText(tile.getLeft() + " - " + tile.getRight() + " " + (direction == null ? "" : direction.name()));

        if (AppController.round.getTableInfo().isMyMove()) {
            TableInfo tableInfo = AppController.round.getTableInfo();
            if (tableInfo.getRoundBlockingInfo().isOmitOpponent() && tableInfo.getBazaarTilesCount() == 2) {
                showAddLeftTilesCount(tile, null, MoveType.ADD_FOR_ME);
            } else if (tableInfo.getLeft() == null && tableInfo.isFirstRound() && AppController.round.getMyTiles().size() == 6) {
                showGameStarterWindow(tile);
            } else {
                mainInfoSecondaryLabel.setText(Messages.get("working"));
                new ServiceExecutor() {
                    @Override
                    public boolean isAsync() {
                        return true;
                    }

                    @Override
                    public void onAsyncProcessFinish() {
                        mainInfoSecondaryLabel.setText("");
                        reload(true, false);
                    }

                    @Override
                    public void onError() {
                        reload(false, false);
                    }
                }.execute(() -> {
                    reloading = true;
                    AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
                });
            }
        } else {
            Move move = TileAndMoveHelper.getMove(tile, direction);

            new ServiceExecutor() {}.execute(() -> roundWillBeBlocked = gameService.roundWillBeBlocked(AppController.round.getGameInfo().getGameId(), move));

            if (roundWillBeBlocked) {
                showAddLeftTilesCount(new Tile(move.getLeft(), move.getRight()), move.getDirection(), MoveType.PLAY_FOR_OPPONENT);
            } else {
                mainInfoSecondaryLabel.setText(Messages.get("working"));
                new ServiceExecutor() {
                    @Override
                    public boolean isAsync() {
                        return true;
                    }

                    @Override
                    public void onAsyncProcessFinish() {
                        mainInfoSecondaryLabel.setText("");
                        reload(true, false);
                    }

                    @Override
                    public void onError() {
                        reload(false, false);
                    }
                }.execute(() -> {
                    reloading = true;
                    AppController.round = gameService.playForOpponent(AppController.round.getGameInfo().getGameId(), move);
                });
            }
        }
    }

    private void onUpArrowPressed() {
        if (pressedTile != null) {
            if (pressedOnMyTile) {
                onMyTileEntered(pressedTile, MoveDirection.TOP);
            } else {
                onOpponentTileEntered(pressedTile, MoveDirection.TOP);
            }
        }
    }

    private void onRightArrowPressed() {
        if (pressedTile != null) {
            if (pressedOnMyTile) {
                onMyTileEntered(pressedTile, MoveDirection.RIGHT);
            } else {
                onOpponentTileEntered(pressedTile, MoveDirection.RIGHT);
            }
        }
    }

    private void onDownArrowPressed() {
        if (pressedTile != null) {
            if (pressedOnMyTile) {
                onMyTileEntered(pressedTile, MoveDirection.BOTTOM);
            } else {
                onOpponentTileEntered(pressedTile, MoveDirection.BOTTOM);
            }
        }
    }

    private void onLeftArrowPressed() {
        if (pressedTile != null) {
            if (pressedOnMyTile) {
                onMyTileEntered(pressedTile, MoveDirection.LEFT);
            } else {
                onOpponentTileEntered(pressedTile, MoveDirection.LEFT);
            }
        }
    }

    private void onAddTileEntered() {
        if (AppController.round.getTableInfo().getRoundBlockingInfo().isOmitMe() && AppController.round.getTableInfo().getBazaarTilesCount() == 2) {
            showAddLeftTilesCount(null, null, MoveType.ADD_FOR_OPPONENT);
            reload(true, false);
        } else {
            new ServiceExecutor() {}.execute(() -> AppController.round = gameService.addTileForOpponent(AppController.round.getGameInfo().getGameId()));
            mainInfoLabel.setText("ADD(" + (int)AppController.round.getTableInfo().getOpponentTilesCount() + ")");
            reload(true, false);
        }
    }
}
