package ge.ai.domino.domain.game;

public class RoundBlockingInfo {

    private boolean omitMe;

    private boolean omitOpponent;

    private boolean lastNotTwinPlayedTileMy;

    public boolean isOmitMe() {
        return omitMe;
    }

    public void setOmitMe(boolean omitMe) {
        this.omitMe = omitMe;
    }

    public boolean isOmitOpponent() {
        return omitOpponent;
    }

    public void setOmitOpponent(boolean omitOpponent) {
        this.omitOpponent = omitOpponent;
    }

    public boolean isLastNotTwinPlayedTileMy() {
        return lastNotTwinPlayedTileMy;
    }

    public void setLastNotTwinPlayedTileMy(boolean lastNotTwinPlayedTileMy) {
        this.lastNotTwinPlayedTileMy = lastNotTwinPlayedTileMy;
    }
}