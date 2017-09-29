package ge.ai.domino.server.manager.domino;

import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.domino.helper.DominoLoggingProcessor;
import ge.ai.domino.server.manager.domino.processor.GameProcessor;
import ge.ai.domino.server.manager.domino.processor.HimTurnProcessor;
import ge.ai.domino.server.manager.domino.processor.MyTurnProcessor;
import ge.ai.domino.server.manager.domino.processor.TurnProcessor;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

public class DominoManager {

    private static final Logger logger = Logger.getLogger(DominoManager.class);

    private static final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private static final GameProcessor gameProcessor = new GameProcessor();

    private static final TurnProcessor myTurnProcessor = new MyTurnProcessor();

    private static final TurnProcessor himTurnProcessor = new HimTurnProcessor();

    private static final SysParam checkHimProbabilities = new SysParam("checkHimProbabilities", "false");

    public Hand startGame(GameProperties gameProperties, int gameId) throws DAIException {
        Hand newHand = gameProcessor.startGame(gameProperties, gameId);
        checkHimProbabilities(newHand, 0);
        return newHand;
    }

    public Hand addTileForMe(Hand hand, int x, int y, boolean virtual) throws DAIException {
        Hand newHand = myTurnProcessor.addTile(hand, x, y, virtual);
        checkHimProbabilities(newHand, 0);
        return newHand;
    }

    public Hand addTileForHim(Hand hand, boolean virtual) throws DAIException {
        Hand newHand = himTurnProcessor.addTile(hand, 0, 0, virtual);
        checkHimProbabilities(newHand, newHand.getTableInfo().getTileFromBazaar());
        return newHand;
    }

    public Hand playForMe(Hand hand, int x, int y, PlayDirection direction, boolean virtual) throws DAIException {
        Hand newHand = myTurnProcessor.play(hand, x, y, direction, virtual);
        checkHimProbabilities(newHand, 0);
        return newHand;
    }

    public Hand playForHim(Hand hand, int x, int y, PlayDirection direction, boolean virtual) throws DAIException {
        Hand newHand = himTurnProcessor.play(hand, x, y, direction, virtual);
        checkHimProbabilities(newHand, 0);
        return newHand;
    }

    public Hand getLastPlayedHand(Hand hand) throws DAIException {
        Hand newHand = gameProcessor.getLastPlayedHand(hand);
        checkHimProbabilities(newHand, 0);
        return newHand;
    }

    public Hand addLeftTilesForMe(Hand hand, int count) throws DAIException {
        Hand newHand = gameProcessor.addLeftTiles(hand, count);
        checkHimProbabilities(newHand, 0);
        return newHand;
    }

    /**
     * მოწმდება თუ რამდენად ტოლია მოწინააღმდეგის ქვების ალბათობების ჯამი მისი ქვების რაოდენობის
     * @param hand კოკრეტული ხელი
     * @param addProb შესაძლოა ჯერ არ იყოს გადანაწილებული გარკვეული ალბათობები, შესაბამისად ვითვალისწინებთ მას(მაგლითან მოწინააღმდეგე რომ იღებს ქვებს, იქამდე არ ფიქსირდება სანამ
     *                არ ჩამოვა ან არ გაივლის)
     * @throws DAIException გაისვრის თუ არ აღმოჩნდა ტოლი
     */
    private void checkHimProbabilities(Hand hand, double addProb) throws DAIException {
        if (systemParameterManager.getBooleanParameterValue(checkHimProbabilities)) {
            double sum = 0.0;
            for (Tile tile : hand.getTiles().values()) {
                sum += tile.getHim();
            }
            if (Math.abs(sum - hand.getTableInfo().getHimTilesCount() + addProb) > 0.001) {
                logger.warn("Him tile count and probabilities sum is not same... count:" + hand.getTableInfo().getHimTilesCount() + "  sum:" + sum + "  addProb:" + addProb);
                DominoLoggingProcessor.logHandFullInfo(hand, false);   // შეიძლება ვირტუალური იყოს, მაგრამ აუციელებელია რომ დაიბეჭდოს
                throw new DAIException("somethingWrongInHimProbabilities");
            }
        }
    }
}
