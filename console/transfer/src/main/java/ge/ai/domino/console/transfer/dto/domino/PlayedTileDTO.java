package ge.ai.domino.console.transfer.dto.domino;

import ge.ai.domino.domain.domino.PlayedTile;

public class PlayedTileDTO {

    private int openSide;

    private boolean isDouble;

    private boolean countInSum;

    private boolean center;

    public int getOpenSide() {
        return openSide;
    }

    public void setOpenSide(int openSide) {
        this.openSide = openSide;
    }

    public boolean isDouble() {
        return isDouble;
    }

    public void setDouble(boolean aDouble) {
        isDouble = aDouble;
    }

    public boolean isCountInSum() {
        return countInSum;
    }

    public void setCountInSum(boolean countInSum) {
        this.countInSum = countInSum;
    }

    public boolean isCenter() {
        return center;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }

    public static PlayedTileDTO toPlayedTileDTO(PlayedTile playedTile) {
        if (playedTile == null) {
            return null;
        }
        PlayedTileDTO dto = new PlayedTileDTO();
        dto.setOpenSide(playedTile.getOpenSide());
        dto.setCountInSum(playedTile.isCountInSum());
        dto.setDouble(playedTile.isDouble());
        dto.setCenter(playedTile.isCenter());
        return dto;
    }

    public static PlayedTile toPlayedTile(PlayedTileDTO dto) {
        if (dto == null) {
            return null;
        }
        PlayedTile playedTile = new PlayedTile();
        playedTile.setOpenSide(dto.getOpenSide());
        playedTile.setCountInSum(dto.isCountInSum());
        playedTile.setDouble(dto.isDouble());
        playedTile.setCenter(dto.isCenter());
        return playedTile;
    }
}
