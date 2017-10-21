package ge.ai.domino.server.manager.game;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.domain.tile.OpponentTile;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.server.manager.game.processor.GameProcessor;
import ge.ai.domino.server.manager.game.processor.MoveProcessor;
import ge.ai.domino.server.manager.game.processor.MyMoveProcessor;
import ge.ai.domino.server.manager.game.processor.OpponentMoveProcessor;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

public class GameManager {

    private static final Logger logger = Logger.getLogger(GameManager.class);

    private final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private final GameProcessor gameProcessor = new GameProcessor();

    private final MoveProcessor myMoveProcessor = new MyMoveProcessor();

    private final MoveProcessor opponentMoveProcessor = new OpponentMoveProcessor();

    private final SysParam checkOpponentProbabilities = new SysParam("checkOpponentProbabilities", "false");

    private final SysParam epsilonForProbabilities = new SysParam("epsilonForProbabilities", "0.000001");

    public Round startGame(GameProperties gameProperties, int gameId) throws DAIException {
        Round newRound = gameProcessor.startGame(gameProperties, gameId);
        checkOpponentProbabilities(newRound, 0, "startGame");
        return newRound;
    }

    public Round addTileForMe(Round round, int x, int y, boolean virtual) throws DAIException {
        Round newRound = myMoveProcessor.addTile(round, x, y, virtual);
        checkOpponentProbabilities(newRound, 0, "addTileForMe");
        return newRound;
    }

    public Round addTileForOpponent(Round round, boolean virtual) throws DAIException {
        Round newRound = opponentMoveProcessor.addTile(round, 0, 0, virtual);
        checkOpponentProbabilities(newRound, newRound.getTableInfo().getTilesFromBazaar(), "addTileForOpponent");
        return newRound;
    }

    public Round playForMe(Round round, Move move, boolean virtual) throws DAIException {
        Round newRound = myMoveProcessor.play(round, move, virtual);
        checkOpponentProbabilities(newRound, 0, "playForMe");
        return newRound;
    }

    public Round playForOpponent(Round round, Move move, boolean virtual) throws DAIException {
        Round newRound = opponentMoveProcessor.play(round, move, virtual);
        checkOpponentProbabilities(newRound, 0, "playForOpponent");
        return newRound;
    }

    public Round getLastPlayedRound(Round round) throws DAIException {
        Round newRound = gameProcessor.getLastPlayedRound(round);
        checkOpponentProbabilities(newRound, newRound.getTableInfo().getTilesFromBazaar(), "getLastPlayedRound");
        return newRound;
    }

    public Round addLeftTilesForMe(Round round, int count) throws DAIException {
        Round newRound = gameProcessor.addLeftTiles(round, count);
        checkOpponentProbabilities(newRound, 0, "addLeftTilesForMe");
        return newRound;
    }

    /**
     * მოწმდება თუ რამდენად სწორია ქვების გადანაწილება. ყბელა აბლათობა უნდა იყოს 0-1 შალედში და მათი ჯამი უნდა იყოს მთლიანი ქვების რაოდენობის ტოლი
     * @param round კოკრეტული ხელი
     * @param addProb შესაძლოა ჯერ არ იყოს გადანაწილებული გარკვეული ალბათობები, შესაბამისად ვითვალისწინებთ მას(მაგლითან მოწინააღმდეგე რომ იღებს ქვებს, იქამდე არ ფიქსირდება სანამ
     *                არ ჩამოვა ან არ გაივლის)
     * @throws DAIException გაისვრის თუ არ აღმოჩნდა ტოლი
     */
    private void checkOpponentProbabilities(Round round, double addProb, String method) throws DAIException {
        if (systemParameterManager.getBooleanParameterValue(checkOpponentProbabilities)) {
            double epsilon = systemParameterManager.getFloatParameterValue(epsilonForProbabilities);
            double sum = 0.0;
            for (OpponentTile tile : round.getOpponentTiles().values()) {
                if (tile.getProb() > 1.0 + epsilon) {
                    logger.warn("Opponent tile probability is more than one, tile[" + tile.getLeft() + "-" + tile.getRight() + "] method[" + method + "]");
                    GameLoggingProcessor.logRoundFullInfo(round, false);   // შეიძლება ვირტუალური იყოს, მაგრამ აუციელებელია რომ დაიბეჭდოს
                    throw new DAIException("opponentTileProbabilityIsMoreThanOne");
                } else if (tile.getProb() < 0.0 - epsilon) {
                    logger.warn("Opponent tile probability is less than zero, tile[" + tile.getLeft() + "-" + tile.getRight() + "] method[" + method + "]");
                    GameLoggingProcessor.logRoundFullInfo(round, false);   // შეიძლება ვირტუალური იყოს, მაგრამ აუციელებელია რომ დაიბეჭდოს
                    throw new DAIException("opponentTileProbabilityIsLessThanZero");
                }
                sum += tile.getProb();
            }
            if (Math.abs(sum - round.getTableInfo().getOpponentTilesCount() + addProb) > epsilon) {
                logger.warn("Opponent tile count and probabilities sum is not same... count:" + round.getTableInfo().getOpponentTilesCount() + "  sum:" + sum + "  addProb:" + addProb + ", method[" + method + "]");
                GameLoggingProcessor.logRoundFullInfo(round, false);   // შეიძლება ვირტუალური იყოს, მაგრამ აუციელებელია რომ დაიბეჭდოს
                throw new DAIException("probabilitiesSumIsNoEqualToOpponentTilesCount");
            }
        }
    }
}
