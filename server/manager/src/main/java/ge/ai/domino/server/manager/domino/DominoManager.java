package ge.ai.domino.server.manager.domino;

import ge.ai.domino.domain.domino.game.GameProperties;
import ge.ai.domino.domain.domino.game.Hand;
import ge.ai.domino.domain.domino.game.PlayDirection;
import ge.ai.domino.domain.domino.game.Tile;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.domino.logging.DominoLoggingProcessor;
import ge.ai.domino.server.manager.domino.processor.GameProcessor;
import ge.ai.domino.server.manager.domino.processor.HimTurnProcessor;
import ge.ai.domino.server.manager.domino.processor.MyTurnProcessor;
import ge.ai.domino.server.manager.domino.processor.TurnProcessor;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

public class DominoManager {

    private static final Logger logger = Logger.getLogger(DominoManager.class);

    private final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private final GameProcessor gameProcessor = new GameProcessor();

    private final TurnProcessor myTurnProcessor = new MyTurnProcessor();

    private final TurnProcessor himTurnProcessor = new HimTurnProcessor();

    private final SysParam checkHimProbabilities = new SysParam("checkHimProbabilities", "false");

    private final SysParam epsilonForProbabilities = new SysParam("epsilonForProbabilities", "0.000001");

    public Hand startGame(GameProperties gameProperties, int gameId) throws DAIException {
        Hand newHand = gameProcessor.startGame(gameProperties, gameId);
        checkHimProbabilities(newHand, 0, "startGame");
        return newHand;
    }

    public Hand addTileForMe(Hand hand, int x, int y, boolean virtual) throws DAIException {
        Hand newHand = myTurnProcessor.addTile(hand, x, y, virtual);
        checkHimProbabilities(newHand, 0, "addTileForMe");
        return newHand;
    }

    public Hand addTileForHim(Hand hand, boolean virtual) throws DAIException {
        Hand newHand = himTurnProcessor.addTile(hand, 0, 0, virtual);
        checkHimProbabilities(newHand, newHand.getTableInfo().getTileFromBazaar(), "addTileForHim");
        return newHand;
    }

    public Hand playForMe(Hand hand, int x, int y, PlayDirection direction, boolean virtual) throws DAIException {
        Hand newHand = myTurnProcessor.play(hand, x, y, direction, virtual);
        checkHimProbabilities(newHand, 0, "playForMe");
        return newHand;
    }

    public Hand playForHim(Hand hand, int x, int y, PlayDirection direction, boolean virtual) throws DAIException {
        Hand newHand = himTurnProcessor.play(hand, x, y, direction, virtual);
        checkHimProbabilities(newHand, 0, "playForHim");
        return newHand;
    }

    public Hand getLastPlayedHand(Hand hand) throws DAIException {
        Hand newHand = gameProcessor.getLastPlayedHand(hand);
        checkHimProbabilities(newHand, newHand.getTableInfo().getTileFromBazaar(), "getLastPlayedHand");
        return newHand;
    }

    public Hand addLeftTilesForMe(Hand hand, int count) throws DAIException {
        Hand newHand = gameProcessor.addLeftTiles(hand, count);
        checkHimProbabilities(newHand, 0, "addLeftTilesForMe");
        return newHand;
    }

    /**
     * მოწმდება თუ რამდენად სწორია ქვების გადანაწილება. ყბელა აბლათობა უნდა იყოს 0-1 შალედში და მათი ჯამი უნდა იყოს მთლიანი ქვების რაოდენობის ტოლი
     * @param hand კოკრეტული ხელი
     * @param addProb შესაძლოა ჯერ არ იყოს გადანაწილებული გარკვეული ალბათობები, შესაბამისად ვითვალისწინებთ მას(მაგლითან მოწინააღმდეგე რომ იღებს ქვებს, იქამდე არ ფიქსირდება სანამ
     *                არ ჩამოვა ან არ გაივლის)
     * @throws DAIException გაისვრის თუ არ აღმოჩნდა ტოლი
     */
    private void checkHimProbabilities(Hand hand, double addProb, String method) throws DAIException {
        if (systemParameterManager.getBooleanParameterValue(checkHimProbabilities)) {
            double epsilon = systemParameterManager.getFloatParameterValue(epsilonForProbabilities);
            double sum = 0.0;
            for (Tile tile : hand.getTiles().values()) {
                if (tile.getHim() > 1.0 + epsilon) {
                    logger.warn("Him tile probability is more than one, tile[" + tile.getX() + "-" + tile.getY() + "] method[" + method + "]");
                    DominoLoggingProcessor.logHandFullInfo(hand, false);   // შეიძლება ვირტუალური იყოს, მაგრამ აუციელებელია რომ დაიბეჭდოს
                    throw new DAIException("HimTileProbabilityIsMoreThanOne");
                } else if (tile.getHim() < 0.0 - epsilon) {
                    logger.warn("Him tile probability is less than zero, tile[" + tile.getX() + "-" + tile.getY() + "] method[" + method + "]");
                    DominoLoggingProcessor.logHandFullInfo(hand, false);   // შეიძლება ვირტუალური იყოს, მაგრამ აუციელებელია რომ დაიბეჭდოს
                    throw new DAIException("HimTileProbabilityIsLessThanZero");
                }
                sum += tile.getHim();
            }
            if (Math.abs(sum - hand.getTableInfo().getHimTilesCount() + addProb) > epsilon) {
                logger.warn("Him tile count and probabilities sum is not same... count:" + hand.getTableInfo().getHimTilesCount() + "  sum:" + sum + "  addProb:" + addProb + ", method[" + method + "]");
                DominoLoggingProcessor.logHandFullInfo(hand, false);   // შეიძლება ვირტუალური იყოს, მაგრამ აუციელებელია რომ დაიბეჭდოს
                throw new DAIException("probabilitiesSumIsNoEqualToHimTilesCount");
            }
        }
    }
}
