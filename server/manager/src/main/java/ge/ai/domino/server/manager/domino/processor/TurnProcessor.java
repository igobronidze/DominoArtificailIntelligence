package ge.ai.domino.server.manager.domino.processor;

import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.PlayedTile;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.server.manager.domino.minmax.MinMax;
import ge.ai.domino.util.tile.TileUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class TurnProcessor {

    protected static final MinMax minMax = new MinMax();

    /**
     * ქვის დამატება
     * @param hand კონკრეტული ხელი
     * @param x დასამატებელი ქვის პირველი ელემენტი
     * @param y დასამატებელი ქვის მეორე ელემენტი
     * @param virtual გამოიძახებს კლიენტი(real) თუ რომელიმე ალგორითმი(virtual)
     * @return ხელს გათამაშების შემდეგ
     * @throws DAIException გაუთვალისწინებელი შეცდომა
     */
    public abstract Hand addTile(Hand hand, int x, int y, boolean virtual) throws DAIException;

    /**
     * ქვის გათამაშება
     * @param hand კონკრეტული ხელი
     * @param x სათამაშო ქვის პირველი ელემენტი
     * @param y სათამაშო ქვის მეორე ელემენტი
     * @param direction ქვის მიმართულება
     * @param virtual გამოიძახებს კლიენტი(real) თუ რომელიმე ალგორითმი(virtual)
     * @return ხელს გათამაშების შემდეგ
     * @throws DAIException გაუთვალისწინებელი შეცდომა
     */
    public abstract Hand play(Hand hand, int x, int y, PlayDirection direction, boolean virtual) throws DAIException;

    /**
     * ყველა წყვილი ქვა რომლის ელემენტიც მეტია a-ზე, ცხადდება როგორც ბაზარში არსებულად და მისი ალბათობები უნდაწილდება სხვას
     * @param hand კონკრეტული ხელი
     * @param a რიცხვი, რომელზე მეტებსაც ეხება აღნიშნული ცვლილება
     */
    void makeDoubleTilesAsInBazaar(Hand hand, int a) {
        double himSum = 0.0;
        Set<String> mayHaveTiles = new HashSet<>();
        for (Tile tile : hand.getTiles().values()) {
            if (tile.getX() == tile.getY() && tile.getX() > a) {
                himSum += tile.getHim();
                tile.setHim(0);
                tile.setMine(false);
            } else {
                mayHaveTiles.add(TileUtil.getTileUID(tile.getX(), tile.getY()));
            }
        }
        addProbabilitiesForHimProportional(hand.getTiles(), mayHaveTiles, himSum);
    }

    /**
     * კონკრეტული სვლის გათამაშება
     * @param tableInfo მაგიდა, სადაც უნდა გათამაშდეს სვლა
     * @param x ქვის პირვლეი ელემენტი
     * @param y ქვის მეორე ელემენტი
     * @param direction ქვის მიმართულება(მარცხნივ, მარჯვნივ, ზემოთ, ქვემოთ)
     */
    void playTile(TableInfo tableInfo, int x, int y, PlayDirection direction) {
        // თუ პირველი სვლაა
        if (tableInfo.getLeft() == null) {
            if (x == y) {  // თუ წყვილია
                tableInfo.setTop(new PlayedTile(x, true, false, true));
                tableInfo.setBottom(new PlayedTile(x, true, false, true));
                tableInfo.setLeft(new PlayedTile(x, true, true, true));
                tableInfo.setRight(new PlayedTile(x, true, true, true));
            } else {
                tableInfo.setLeft(new PlayedTile(x, false, true, false));
                tableInfo.setRight(new PlayedTile(y, false, true, false));
            }
        } else {
            switch (direction) {
                case TOP:
                    tableInfo.setTop(new PlayedTile(tableInfo.getTop().getOpenSide() == x ? y : x, x == y, true, false));
                    break;
                case RIGHT:
                    if (!tableInfo.isWithCenter()) {   // შემოწმება ხომ არ შეიქმნა ახალი ცენტრი
                        PlayedTile right = tableInfo.getRight();
                        if (right.isDouble()) {
                            tableInfo.setTop(new PlayedTile(right.getOpenSide(), true, false, true));
                            tableInfo.setBottom(new PlayedTile(right.getOpenSide(), true, false, true));
                            tableInfo.setWithCenter(true);
                        }
                    }
                    tableInfo.setRight(new PlayedTile(tableInfo.getRight().getOpenSide() == x ? y : x, x == y, true, false));
                    break;
                case BOTTOM:
                    tableInfo.setBottom(new PlayedTile(tableInfo.getBottom().getOpenSide() == x ? y : x, x == y, true, false));
                    break;
                case LEFT:
                    if (!tableInfo.isWithCenter()) {   // შემოწმება ხომ არ შეიქმნა ახალი ცენტრი
                        PlayedTile left = tableInfo.getLeft();
                        if (left.isDouble()) {
                            tableInfo.setTop(new PlayedTile(left.getOpenSide(), true, false, true));
                            tableInfo.setBottom(new PlayedTile(left.getOpenSide(), true, false, true));
                            tableInfo.setWithCenter(true);
                        }
                    }
                    tableInfo.setLeft(new PlayedTile(tableInfo.getLeft().getOpenSide() == x ? y : x, x == y, true, false));
                    break;
            }
        }
        tableInfo.setLastPlayedUID(TileUtil.getTileUID(x, y));
    }

    void makeTileAsPlayed(Tile tile) {
        tile.setMine(false);
        tile.setHim(0);
        tile.setPlayed(true);
    }

    int countScore(Hand hand) {
        int count = 0;
        TableInfo tableInfo = hand.getTableInfo();
        if (tableInfo.getLeft().getOpenSide() == tableInfo.getRight().getOpenSide() && tableInfo.getLeft().isDouble() && tableInfo.getRight().isDouble()) {
            count = tableInfo.getLeft().getOpenSide() * 2;  // პირველი ჩამოსვლა, 5X5 შემთხვევა
        } else if (tableInfo.getMyTilesCount() + tableInfo.getHimTilesCount() == 13 && tableInfo.getBazaarTilesCount() == 14) {
            return 0;   // პირველი ჩამოსვლა გარდა 5X5-სა
        } else {
            count += tableInfo.getLeft().isDouble() ? (tableInfo.getLeft().getOpenSide() * 2) : tableInfo.getLeft().getOpenSide();
            count += tableInfo.getRight().isDouble() ? (tableInfo.getRight().getOpenSide() * 2) : tableInfo.getRight().getOpenSide();
            if (tableInfo.getTop() != null && tableInfo.getTop().isCountInSum()) {
                count += tableInfo.getTop().isDouble() ? (tableInfo.getTop().getOpenSide() * 2) : tableInfo.getTop().getOpenSide();
            }
            if (tableInfo.getBottom() != null && tableInfo.getBottom().isCountInSum()) {
                count += tableInfo.getBottom().isDouble() ? (tableInfo.getBottom().getOpenSide() * 2) : tableInfo.getBottom().getOpenSide();
            }
        }
        // ითვლება პირველი 5-ს ჯერადი რიცხვი რომელიც მეტია ან ტოლია count-ზე
        if (count > 0 && count % 5 == 0) {
            return count;
        } else {
            return 0;
        }
    }

    void addProbabilitiesForHimProportional(Map<String, Tile> tiles, Set<String> possibleTiles, double probability) {
        double sum = 0.0;
        for (String key : possibleTiles) {
            sum += tiles.get(key).getHim();
        }
        for (String key : possibleTiles) {
            Tile tile = tiles.get(key);
            double add = probability * tile.getHim() / sum;
            tile.setHim(tile.getHim() + add);
        }
    }
    void addProbabilitiesForBazaarProportional(Map<String, Tile> tiles, Set<String> possibleTiles, double probability) {
        double sum = 0.0;
        for (String key : possibleTiles) {
            sum += (1 - tiles.get(key).getHim());
        }
        for (String key : possibleTiles) {
            Tile tile = tiles.get(key);
            double add = probability * (1 - tile.getHim()) / sum;
            tile.setHim(tile.getHim() + add);
        }
    }


    Set<String> getNotPlayedMineOrBazaarTiles(Map<String, Tile> tiles) {
        Set<String> keys = new HashSet<>();
        for (String key : tiles.keySet()) {
            Tile tile = tiles.get(key);
            if (!tile.isPlayed() && !tile.isMine() && tile.getHim() != 0.0) {
                keys.add(key);
            }
        }
        return keys;
    }
}
