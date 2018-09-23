package ge.ai.domino.domain.game;

import ge.ai.domino.domain.played.PlayedTile;

import java.io.Serializable;

public class TableInfo implements Serializable {

    private PlayedTile top;

    private PlayedTile right;

    private PlayedTile bottom;

    private PlayedTile left;

    private boolean withCenter;

    private boolean myMove;

    private boolean firstRound;

    private RoundBlockingInfo roundBlockingInfo = new RoundBlockingInfo();

    private double opponentTilesCount;

    private double bazaarTilesCount;

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

    public double getOpponentTilesCount() {
        return opponentTilesCount;
    }

    public void setOpponentTilesCount(double opponentTilesCount) {
        this.opponentTilesCount = opponentTilesCount;
    }

    public double getBazaarTilesCount() {
        return bazaarTilesCount;
    }

    public void setBazaarTilesCount(double bazaarTilesCount) {
        this.bazaarTilesCount = bazaarTilesCount;
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
