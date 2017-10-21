package ge.ai.domino.domain.game;

import ge.ai.domino.domain.tile.PlayedTile;

public class TableInfo {

    private boolean withCenter;

    private PlayedTile top;

    private PlayedTile right;

    private PlayedTile bottom;

    private PlayedTile left;

    private boolean myMove;

    private double opponentTilesCount;

    private double myTilesCount;

    private double bazaarTilesCount;

    private double lastPlayedProb;

    private boolean needToAddLeftTiles;

    private int tilesFromBazaar;

    private boolean omittedMe;

    private boolean omittedOpponent;

    private boolean firstRound;

    public boolean isWithCenter() {
        return withCenter;
    }

    public void setWithCenter(boolean withCenter) {
        this.withCenter = withCenter;
    }

    public PlayedTile getTop() {
        return top;
    }

    public void setTop(PlayedTile top) {
        this.top = top;
    }

    public PlayedTile getRight() {
        return right;
    }

    public void setRight(PlayedTile right) {
        this.right = right;
    }

    public PlayedTile getBottom() {
        return bottom;
    }

    public void setBottom(PlayedTile bottom) {
        this.bottom = bottom;
    }

    public PlayedTile getLeft() {
        return left;
    }

    public void setLeft(PlayedTile left) {
        this.left = left;
    }

    public boolean isMyMove() {
        return myMove;
    }

    public void setMyMove(boolean myMove) {
        this.myMove = myMove;
    }

    public double getOpponentTilesCount() {
        return opponentTilesCount;
    }

    public void setOpponentTilesCount(double opponentTilesCount) {
        this.opponentTilesCount = opponentTilesCount;
    }

    public double getMyTilesCount() {
        return myTilesCount;
    }

    public void setMyTilesCount(double myTilesCount) {
        this.myTilesCount = myTilesCount;
    }

    public double getBazaarTilesCount() {
        return bazaarTilesCount;
    }

    public void setBazaarTilesCount(double bazaarTilesCount) {
        this.bazaarTilesCount = bazaarTilesCount;
    }

    public double getLastPlayedProb() {
        return lastPlayedProb;
    }

    public void setLastPlayedProb(double lastPlayedProb) {
        this.lastPlayedProb = lastPlayedProb;
    }

    public boolean isNeedToAddLeftTiles() {
        return needToAddLeftTiles;
    }

    public void setNeedToAddLeftTiles(boolean needToAddLeftTiles) {
        this.needToAddLeftTiles = needToAddLeftTiles;
    }

    public int getTilesFromBazaar() {
        return tilesFromBazaar;
    }

    public void setTilesFromBazaar(int tilesFromBazaar) {
        this.tilesFromBazaar = tilesFromBazaar;
    }

    public boolean isOmittedMe() {
        return omittedMe;
    }

    public void setOmittedMe(boolean omittedMe) {
        this.omittedMe = omittedMe;
    }

    public boolean isOmittedOpponent() {
        return omittedOpponent;
    }

    public void setOmittedOpponent(boolean omittedOpponent) {
        this.omittedOpponent = omittedOpponent;
    }

    public boolean isFirstRound() {
        return firstRound;
    }

    public void setFirstRound(boolean firstRound) {
        this.firstRound = firstRound;
    }
}
