package ge.ai.domino.p2pserver;

import ge.ai.domino.domain.game.Tile;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameData {

    private final Logger logger = Logger.getLogger(GameData.class);

    private List<Tile> bazaar = new ArrayList<>();

    private Set<Tile> tiles1 = new HashSet<>();

    private Set<Tile> tiles2 = new HashSet<>();

    private Tile lastDeletedTile;

    public synchronized Set<Tile> getTiles1() {
        return tiles1;
    }

    public synchronized Set<Tile> getTiles2() {
        return tiles2;
    }

    public synchronized void setTiles1(Set<Tile> tiles1) {
        this.tiles1 = tiles1;
    }

    public synchronized void setTiles2(Set<Tile> tiles2) {
        this.tiles2 = tiles2;
    }

    public synchronized Tile getRandomTileAndAddInSet(boolean first) {
        Random random = new Random();
        logger.info("Bazaar size is " + bazaar.size());
        int index = random.nextInt(bazaar.size());
        logger.info("Index is " + index);
        lastDeletedTile = bazaar.remove(index);
        if (first) {
            tiles1.add(lastDeletedTile);
        } else {
            tiles2.add(lastDeletedTile);
        }
        return lastDeletedTile;
    }

    public synchronized void addLastDeletedTile() {
        bazaar.add(lastDeletedTile);
    }

    public synchronized boolean isFirstStarter() {
        for (int i = 6; i >= 0; i--) {
            Tile tile = new Tile(i, i);
            if (tiles1.contains(tile)) {
                return true;
            }
            if (tiles2.contains(tile)) {
                return false;
            }
        }
        for (int i = 6; i >= 0; i--) {
            for (int j = i; j >= 0; j-- ) {
                Tile tile = new Tile(i, j);
                if (tiles1.contains(tile)) {
                    return true;
                }
                if (tiles2.contains(tile)) {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized void initTiles() {
        bazaar = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            for (int j = i; j >= 0; j--) {
                bazaar.add(new Tile(i, j));
            }
        }
    }
}
