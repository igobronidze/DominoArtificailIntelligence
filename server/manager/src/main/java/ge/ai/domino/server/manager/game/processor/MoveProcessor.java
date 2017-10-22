package ge.ai.domino.server.manager.game.processor;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.domain.tile.OpponentTile;
import ge.ai.domino.domain.tile.PlayedTile;
import ge.ai.domino.server.manager.game.minmax.MinMax;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class MoveProcessor {

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam epsilonForProbabilities = new SysParam("epsilonForProbabilities", "0.000001");

    protected static final MinMax minMax = new MinMax();

    /**
     * ქვის დამატება
     * @param round კონკრეტული ხელი
     * @param left დასამატებელი ქვის პირველი ელემენტი
     * @param right დასამატებელი ქვის მეორე ელემენტი
     * @param virtual გამოიძახებს კლიენტი(real) თუ რომელიმე ალგორითმი(virtual)
     * @return ხელს გათამაშების შემდეგ
     * @throws DAIException გაუთვალისწინებელი შეცდომა
     */
    public abstract Round addTile(Round round, int left, int right, boolean virtual) throws DAIException;

    /**
     * ქვის გათამაშება
     * @param round კონკრეტული ხელი
     * @param virtual გამოიძახებს კლიენტი(real) თუ რომელიმე ალგორითმი(virtual)
     * @return ხელს გათამაშების შემდეგ
     * @throws DAIException გაუთვალისწინებელი შეცდომა
     */
    public abstract Round play(Round round, Move move, boolean virtual) throws DAIException;

    /**
     * ყველა წყვილი ქვა რომლის ელემენტიც მეტია a-ზე, ცხადდება როგორც ბაზარში არსებულად და მისი ალბათობები უნდაწილდება სხვას
     * @param twinNumber რიცხვი, რომელზე მეტებსაც ეხება აღნიშნული ცვლილება
     */
    void makeTwinTilesAsInBazaar(Collection<OpponentTile> tiles, int twinNumber) {
        double sum = 0.0;
        Set<OpponentTile> mayHaveTiles = new HashSet<>();
        for (OpponentTile tile : tiles) {
            if (tile.getLeft() == tile.getRight() && tile.getLeft() > twinNumber) {
                sum += tile.getProb();
                tile.setProb(0);
            } else {
                mayHaveTiles.add(tile);
            }
        }
        addProbabilitiesForOpponentProbsProportional(mayHaveTiles, sum);
    }

    /**
     * კონკრეტული სვლის გათამაშება
     */
    void playTile(Round round, Move move) {
        TableInfo tableInfo = round.getTableInfo();
        int left = move.getLeft();
        int right = move.getRight();
        // თუ პირველი სვლაა
        if (tableInfo.getLeft() == null) {
            if (left == right) {  // თუ წყვილია
                tableInfo.setTop(new PlayedTile(left, true, false, true));
                tableInfo.setBottom(new PlayedTile(left, true, false, true));
                tableInfo.setLeft(new PlayedTile(left, true, true, true));
                tableInfo.setRight(new PlayedTile(left, true, true, true));
            } else {
                tableInfo.setLeft(new PlayedTile(left, false, true, false));
                tableInfo.setRight(new PlayedTile(right, false, true, false));
            }
        } else {
            switch (move.getDirection()) {
                case TOP:
                    tableInfo.setTop(new PlayedTile(tableInfo.getTop().getOpenSide() == left ? right : left, left == right, true, false));
                    break;
                case RIGHT:
                    if (!tableInfo.isWithCenter()) {   // შემოწმება ხომ არ შეიქმნა ახალი ცენტრი
                        PlayedTile rightTile = tableInfo.getRight();
                        if (rightTile.isTwin()) {
                            tableInfo.setTop(new PlayedTile(rightTile.getOpenSide(), true, false, true));
                            tableInfo.setBottom(new PlayedTile(rightTile.getOpenSide(), true, false, true));
                            tableInfo.setWithCenter(true);
                        }
                    }
                    tableInfo.setRight(new PlayedTile(tableInfo.getRight().getOpenSide() == left ? right : left, left == right, true, false));
                    break;
                case BOTTOM:
                    tableInfo.setBottom(new PlayedTile(tableInfo.getBottom().getOpenSide() == left ? right : left, left == right, true, false));
                    break;
                case LEFT:
                    if (!tableInfo.isWithCenter()) {   // შემოწმება ხომ არ შეიქმნა ახალი ცენტრი
                        PlayedTile leftTile = tableInfo.getLeft();
                        if (leftTile.isTwin()) {
                            tableInfo.setTop(new PlayedTile(leftTile.getOpenSide(), true, false, true));
                            tableInfo.setBottom(new PlayedTile(leftTile.getOpenSide(), true, false, true));
                            tableInfo.setWithCenter(true);
                        }
                    }
                    tableInfo.setLeft(new PlayedTile(tableInfo.getLeft().getOpenSide() == left ? right : left, left == right, true, false));
                    break;
            }
        }
    }

    int countScore(Round round) {
        int count = 0;
        TableInfo tableInfo = round.getTableInfo();
        if (tableInfo.getLeft().getOpenSide() == tableInfo.getRight().getOpenSide() && tableInfo.getLeft().isTwin() && tableInfo.getRight().isTwin()) {
            count = tableInfo.getLeft().getOpenSide() * 2;  // პირველი ჩამოსვლა, 5X5 შემთხვევა
        } else if (round.getMyTiles().size() + tableInfo.getOpponentTilesCount() == 13 && tableInfo.getBazaarTilesCount() == 14) {
            return 0;   // პირველი ჩამოსვლა გარდა 5X5-სა
        } else {
            count += tableInfo.getLeft().isTwin() ? (tableInfo.getLeft().getOpenSide() * 2) : tableInfo.getLeft().getOpenSide();
            count += tableInfo.getRight().isTwin() ? (tableInfo.getRight().getOpenSide() * 2) : tableInfo.getRight().getOpenSide();
            if (tableInfo.getTop() != null && tableInfo.getTop().isConsiderInSum()) {
                count += tableInfo.getTop().isTwin() ? (tableInfo.getTop().getOpenSide() * 2) : tableInfo.getTop().getOpenSide();
            }
            if (tableInfo.getBottom() != null && tableInfo.getBottom().isConsiderInSum()) {
                count += tableInfo.getBottom().isTwin() ? (tableInfo.getBottom().getOpenSide() * 2) : tableInfo.getBottom().getOpenSide();
            }
        }
        // ითვლება პირველი 5-ს ჯერადი რიცხვი რომელიც მეტია ან ტოლია count-ზე
        if (count > 0 && count % 5 == 0) {
            return count;
        } else {
            return 0;
        }
    }

    /**
     * possibleTiles ქვებისთვის ნაწილდება probability ალბათობა. ქვას რაც მეტი აქვს შანსი, რომ ქონდეს მოწინააღმდეგე მით მეტი წილი ხვდება probability-დან
     * @param possibleTiles ქვები რომლებსაც უნდა გადაუნაწილდეს ალბათობები
     * @param probability ალბათობა რომელიც უნდა გადაუნაწილდეს სხვებს
     */
    void addProbabilitiesForOpponentProbsProportional(Set<OpponentTile> possibleTiles, double probability) {
        double sum = 0.0;
        for (OpponentTile tile : possibleTiles) {
            sum += tile.getProb();
        }
        double epsilon = sysParamManager.getFloatParameterValue(epsilonForProbabilities);
        if (sum + probability + epsilon >= possibleTiles.size()) {
            for (OpponentTile tile : possibleTiles) {
                tile.setProb(1.0);
            }
        } else {
            for (OpponentTile tile : possibleTiles) {
                double add = probability * tile.getProb() / sum;
                tile.setProb(tile.getProb() + add);
            }
        }
    }

    /**
     * possibleTiles ქვებისთვის ნაწილდება probability ალბათობა. ქვას რაც მეტი აქვს შანსი, რომ იყოს ბაზარში მით მეტი წილი ხვდება probability-დან
     * @param tiles ყველა ქვა
     * @param probability ალბათობა რომელიც უნდა გადაუნაწილდეს სხვებს
     */
    void addProbabilitiesForBazaarProportional(Collection<OpponentTile> tiles, double probability) {
        double sum = 0.0;
        for (OpponentTile tile : tiles) {
            sum += (1 - tile.getProb());
        }
        double epsilon = sysParamManager.getFloatParameterValue(epsilonForProbabilities);
        if ((tiles.size() - sum) + probability + epsilon >= tiles.size()) {
            for (OpponentTile tile : tiles) {
                tile.setProb(1.0);
            }
        } else {
            for (OpponentTile tile : tiles) {
                double add = probability * (1 - tile.getProb()) / sum;
                tile.setProb(tile.getProb() + add);
            }
        }
    }

    Set<OpponentTile> tileSelection(Collection<OpponentTile> tiles, boolean notOpoonent, boolean notBazaar) {
        Set<OpponentTile> result = new HashSet<>();
        for (OpponentTile tile : tiles) {
            if (notOpoonent && tile.getProb() == 1.0) {
                continue;
            }
            if (notBazaar && tile.getProb() == 0.0) {
                continue;
            }
            result.add(tile);
        }
        return result;
    }
}
