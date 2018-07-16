package ge.ai.domino.p2pclient;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.domain.p2p.Command;
import ge.ai.domino.manager.function.FunctionManager;
import ge.ai.domino.manager.game.GameManager;
import ge.ai.domino.manager.played.PlayedGameManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class P2PGame {

    private static final Logger logger = Logger.getLogger(P2PGame.class);

    private static final int SLEEP_INTERVAL_BETWEEN_MOVES = 3_000;

    private static final int UPDATE_GAME_PER_ITERATION = 10;

    private static final GameManager gameManager = new GameManager();

    private static final FunctionManager functionManager = new FunctionManager();

    private static final PlayedGameManager playedGameManager = new PlayedGameManager();

    private ObjectInputStream ois;

    private ObjectOutputStream oos;

    private Round round;

    private int gameId;

    public P2PGame(ObjectInputStream ois, ObjectOutputStream oos) {
        this.ois = ois;
        this.oos = oos;

        functionManager.initFunctions();
    }

    public GameInfo start() throws DAIException {
        try {
            createRound();

            addInitialSevenTile(true);

            boolean firstPlay = specifyRoundBeginner();

            int iteration = 0;

            while (true) {
                iteration++;
                if (iteration % UPDATE_GAME_PER_ITERATION == 0) {
                    playedGameManager.updateGameInfo(round.getGameInfo());
                }
                if (round.getTableInfo().isMyMove()) {
                    if (firstPlay) {
                        Tile firstTile = getStarterTile(round.getMyTiles());
                        playMove(new Move(firstTile.getLeft(), firstTile.getRight(), MoveDirection.LEFT));
                        firstPlay = false;
                    } else {
                        if (round.getAiPredictions() == null || round.getAiPredictions().getAiPredictions().isEmpty()) {
                            if (round.getTableInfo().getRoundBlockingInfo().isOmitOpponent()) {
                                specifyLeftTiles(gameId, round);
                                addRandomTileForMe();
                                if (!round.getGameInfo().isFinished()) {
                                    oos.writeObject(Command.RESET_GAME_DATA_OPPONENT_TILES);
                                    oos.writeObject(Command.RESET_GAME_DATA_BAZAAR);
                                    addInitialSevenTile(false);
                                }
                            } else {
                                addRandomTileForMe();
                            }
                        } else {
                            if (round.getMyTiles().size() == 1) {
                                specifyLeftTiles(gameId, round);
                                playBestMove();
                                if (!round.getGameInfo().isFinished()) {
                                    oos.writeObject(Command.RESET_GAME_DATA_OPPONENT_TILES);
                                    oos.writeObject(Command.RESET_GAME_DATA_BAZAAR);
                                    addInitialSevenTile(false);
                                }
                            } else {
                                playBestMove();
                            }
                        }
                    }
                } else {
                    MoveType moveType = (MoveType) ois.readObject();
                    Move move = (Move) ois.readObject();
                    if (moveType == MoveType.PLAY_FOR_ME) {
                        if (round.getTableInfo().getOpponentTilesCount() == 1) {
                            oos.writeObject(Command.RESET_GAME_DATA_OPPONENT_TILES);
                            round = gameManager.playForOpponent(gameId, move);
                            if (!round.getGameInfo().isFinished()) {
                                addInitialSevenTile(false);
                            }
                        } else {
                            round = gameManager.playForOpponent(gameId, move);
                        }
                    } else if (moveType == MoveType.ADD_FOR_ME) {
                        if (round.getTableInfo().getRoundBlockingInfo().isOmitMe()) {
                            specifyLeftTiles(gameId, round);
                            oos.writeObject(Command.RESET_GAME_DATA_OPPONENT_TILES);
                            round = gameManager.addTileForOpponent(gameId);
                            if (!round.getGameInfo().isFinished()) {
                                addInitialSevenTile(false);
                            }
                        } else {
                            round = gameManager.addTileForOpponent(gameId);
                        }
                    }
                }

                if (round.getGameInfo().isFinished()) {
                    playedGameManager.finishGame(gameId, true, true, false);
                    oos.writeObject(Command.FINISH);
                    closeConnection();
                    break;
                }
                Thread.sleep(SLEEP_INTERVAL_BETWEEN_MOVES);
            }

        }  catch (ClassNotFoundException | IOException | InterruptedException ex) {
            logger.error("Error occurred while play p2p game", ex);
            throw new DAIException("p2pGameError");
        }
        return round.getGameInfo();
    }

    private void addRandomTileForMe() throws DAIException, IOException, ClassNotFoundException {
        Tile tile = getRandomTile(round.getOpponentTiles());
        round = gameManager.addTileForMe(gameId, tile.getLeft(), tile.getRight());
        oos.writeObject(Command.PLAY);
        oos.writeObject(MoveType.ADD_FOR_ME);
        oos.writeObject(new Move(tile.getLeft(), tile.getRight(), MoveDirection.LEFT));
    }

    private void playBestMove() throws DAIException, IOException {
        Move bestMove = null;
        for (AiPrediction aiPrediction : round.getAiPredictions().getAiPredictions()) {
            if (aiPrediction.isBestMove()) {
                bestMove = aiPrediction.getMove();
            }
        }
        playMove(bestMove);
    }

    private void playMove(Move move) throws IOException, DAIException {
        round = gameManager.playForMe(gameId, move);
        oos.writeObject(Command.PLAY);
        oos.writeObject(MoveType.PLAY_FOR_ME);
        oos.writeObject(move);
    }

    private void addInitialSevenTile(boolean withSpecifyBeginner) throws DAIException, ClassNotFoundException, IOException {
        oos.writeObject(Command.CAN_NOT_LISTEN_PLAY_COMMAND);
        for (int i = 0; i < 7; i++) {
            Tile tile = getRandomTile(round.getOpponentTiles());
            if (withSpecifyBeginner && i == 6) {
                specifyRoundBeginner();
            }
            round = gameManager.addTileForMe(gameId, tile.getLeft(), tile.getRight());
        }
        oos.writeObject(Command.CAN_LISTEN_PLAY_COMMAND);
    }

    private boolean specifyRoundBeginner() throws IOException, ClassNotFoundException {
        oos.writeObject(Command.GET_GAME_BEGINNER);
        Boolean startMe = (Boolean) ois.readObject();
        gameManager.specifyRoundBeginner(gameId, startMe);
        return startMe;
    }

    private void createRound() throws IOException, ClassNotFoundException {
        oos.writeObject(Command.CAN_NOT_LISTEN_PLAY_COMMAND);

        oos.writeObject(Command.GET_GAME_PROPERTIES);
        GameProperties gameProperties = (GameProperties) ois.readObject();

        round = gameManager.startGame(gameProperties);
        gameId = round.getGameInfo().getGameId();
    }

    private Tile getRandomTile(Map<Tile, Double> opponentTiles) throws IOException, ClassNotFoundException {
        boolean first = true;
        while (true) {
            if (first) {
                oos.writeObject(Command.GET_RANDOM_TILE);
            } else {
                oos.writeObject(Command.GET_RANDOM_TILE_ADDITIONAL_TRY);
            }
            Tile tile = (Tile) ois.readObject();
            if (opponentTiles.get(tile) != 1.0) {
                return tile;
            }
            first = false;
        }
    }

    private void specifyLeftTiles(int gameId, Round round) throws IOException, ClassNotFoundException {
        oos.writeObject(Command.GET_OPPONENT_TILES);
        Set<Tile> initTiles = (HashSet<Tile>) ois.readObject();
        int count = 0;
        for (Tile tile : initTiles) {
            if (round.getOpponentTiles().containsKey(tile)) {
                count += tile.getLeft() + tile.getRight();
            }
        }
        gameManager.specifyOpponentLeftTiles(gameId, count);
    }

    private Tile getStarterTile(Set<Tile> myTiles) {
        for (int i = 6; i >= 0; i--) {
            Tile tile = new Tile(i, i);
            if (myTiles.contains(tile)) {
                return tile;
            }
        }
        for (int i = 6; i >= 0; i--) {
            for (int j = i; j >= 0; j-- ) {
                Tile tile = new Tile(i, j);
                if (myTiles.contains(tile)) {
                    return tile;
                }
            }
        }
        return new Tile(0, 0);
    }

    private void closeConnection() {
        try {
            ois.close();
            oos.close();
        } catch (IOException ex) {
            logger.error("Can't close connection", ex);
        }
    }
}
