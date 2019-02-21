package ge.ai.domino.manager.game.ai.predictor;

import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

public class OpponentTilesPredictorFactory {

    private static final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private static final SysParam opponentTilesPredictorSysParam = new SysParam("opponentTilesPredictor", "MIN_MAX");

    private static OpponentTilesPredictor opponentTilesPredictor;

    public static boolean useMinMaxPredictor() {
        getOpponentTilesPredictor(false);

        return opponentTilesPredictor instanceof MinMaxOpponentTilesPredictor;
    }

    public static OpponentTilesPredictor getOpponentTilesPredictor(boolean reInitialize) {
        if (opponentTilesPredictor == null || reInitialize) {
            OpponentTilesPredictorType opponentTilesPredictorType = OpponentTilesPredictorType.valueOf(systemParameterManager.getStringParameterValue(opponentTilesPredictorSysParam));
            switch (opponentTilesPredictorType) {
                case ONE_MOVE_HEURISTIC:
                    opponentTilesPredictor = new OneMoveHeuristicOpponentTilesPredictor();
                    break;
                case MIN_MAX:
                    opponentTilesPredictor = new MinMaxOpponentTilesPredictor();
                    break;
                case NONE:
                    default:
                        opponentTilesPredictor = new WithoutOpponentTilesPredictor();
                        break;
            }
        }

        return opponentTilesPredictor;
    }
}
