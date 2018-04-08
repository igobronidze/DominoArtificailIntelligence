package ge.ai.domino.server.manager.game.helper;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;

import java.util.Map;
import java.util.Set;

public class ProbabilitiesDistributor {

    private static final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private static final SysParam distributedProbabilityMaxRate = new SysParam("distributedProbabilityMaxRate", "0.75");

    // TODO[IG] need comments
    public static void distributeProbabilitiesOpponentProportional(Map<Tile, Double> tiles, double probability) {
        OpponentTilesFilter opponentTilesFilter = new OpponentTilesFilter()
                .notOpponent(true)
                .notBazaar(true);

        int count = 0;
        double sum = 0.0;
        for (Map.Entry<Tile, Double> entry : tiles.entrySet()) {
            if (opponentTilesFilter.filter(entry)) {
                sum += entry.getValue();
                count++;
            }
        }

//        int sumInt = (int)Math.round(sum);
//        if (sumInt - sum > 0.0000001 && sumInt - sum < 0.0001 && probability > 0.0001) {
//            distributeProbabilitiesOpponentProportional(tiles, sumInt - sum);
//        }

        if (probability < 0.0) {
            if (ComparisonHelper.equal(sum, -1 * probability)) {
                setStaticProbabilities(tiles, opponentTilesFilter, 0.0);
                return;
            }
        } else {
            if (ComparisonHelper.equal(sum + probability, count)) {
                setStaticProbabilities(tiles, opponentTilesFilter, 1.0);
                return;
            }
        }

        double maxRate = systemParameterManager.getDoubleParameterValue(distributedProbabilityMaxRate);
        double remainingProbability = 0.0;
        for (Map.Entry<Tile, Double> entry : tiles.entrySet()) {
            if (opponentTilesFilter.filter(entry)) {
                double add = probability * entry.getValue() / sum;
                if (probability > 0.0) {
                    double maxAddedProb = (1 - entry.getValue()) * maxRate;
                    if (add > maxAddedProb) {
                        entry.setValue(entry.getValue() + maxAddedProb);
                        remainingProbability += (add - maxAddedProb);
                    } else {
                        entry.setValue(entry.getValue() + add);
                    }
                } else {
                    if (-1 * add > entry.getValue() * maxRate) {
                        entry.setValue(entry.getValue() * (1 - maxRate));
                        remainingProbability += (add + entry.getValue() * maxRate);
                    } else {
                        entry.setValue(entry.getValue() + add);
                    }
                }
            }
        }
        if (!ComparisonHelper.equal(remainingProbability, 0.0)) {
            distributeProbabilitiesOpponentProportional(tiles, remainingProbability);
        }
    }

    public static void updateProbabilitiesForLastPickedTiles(Round round, boolean played, boolean virtual) {
        double bazaarTilesCount = round.getTableInfo().getTilesFromBazaar();
        double probability = played ? bazaarTilesCount - 1 : bazaarTilesCount;

        Map<Tile, Double> tiles = round.getOpponentTiles();
        Set<Integer> notUsedNumbers = virtual ? null : GameOperations.getPossiblePlayNumbers(round.getTableInfo());

        OpponentTilesFilter opponentTilesFilter = new OpponentTilesFilter()
                .notOpponent(true)
                .notUsedNumber(notUsedNumbers);

        double sum = 0.0;
        for (Map.Entry<Tile, Double> entry : tiles.entrySet()) {
            if (opponentTilesFilter.filter(entry)) {
                sum += (1 - entry.getValue());
            }
        }

        if (ComparisonHelper.equal(probability, sum)) {
            setStaticProbabilities(tiles, opponentTilesFilter, 1.0);
        } else {
            for (Map.Entry<Tile, Double> entry : tiles.entrySet()) {
                if (opponentTilesFilter.filter(entry)) {
                    double add = probability * (1 - entry.getValue()) / sum;
                    entry.setValue(entry.getValue() + add);
                }
            }
        }

        round.getTableInfo().setTilesFromBazaar(0);
    }

    private static void setStaticProbabilities(Map<Tile, Double> tiles, OpponentTilesFilter opponentTilesFilter, double prob) {
        for (Map.Entry<Tile, Double> entry : tiles.entrySet()) {
            if (opponentTilesFilter.filter(entry)) {
                entry.setValue(prob);
            }
        }
    }
}
