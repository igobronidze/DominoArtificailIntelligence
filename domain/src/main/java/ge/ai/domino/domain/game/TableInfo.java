package ge.ai.domino.domain.game;

import ge.ai.domino.domain.played.PlayedTile;

public class TableInfo {

    private PlayedTile top;

    private PlayedTile right;

    private PlayedTile bottom;

    private PlayedTile left;

    private boolean withCenter;

    private boolean myMove;

    private RoundBlockingInfo roundBlockingInfo = new RoundBlockingInfo();

    private boolean firstRound;

    private float opponentTilesCount;

    private float bazaarTilesCount;

    private float lastPlayedProb;

    private int tilesFromBazaar;

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

    public float getOpponentTilesCount() {
        return opponentTilesCount;
    }

    public void setOpponentTilesCount(float opponentTilesCount) {
        this.opponentTilesCount = opponentTilesCount;
    }

    public float getBazaarTilesCount() {
        return bazaarTilesCount;
    }

    public void setBazaarTilesCount(float bazaarTilesCount) {
        this.bazaarTilesCount = bazaarTilesCount;
    }

    public float getLastPlayedProb() {
        return lastPlayedProb;
    }

    public void setLastPlayedProb(float lastPlayedProb) {
        this.lastPlayedProb = lastPlayedProb;
    }

    public int getTilesFromBazaar() {
        return tilesFromBazaar;
    }

    public void setTilesFromBazaar(int tilesFromBazaar) {
        this.tilesFromBazaar = tilesFromBazaar;
    }

    public RoundBlockingInfo getRoundBlockingInfo() {
        return roundBlockingInfo;
    }

    public void setRoundBlockingInfo(RoundBlockingInfo roundBlockingInfo) {
        this.roundBlockingInfo = roundBlockingInfo;
    }

    public boolean isFirstRound() {
        return firstRound;
    }

    public void setFirstRound(boolean firstRound) {
        this.firstRound = firstRound;
    }
}
