package ge.ai.domino.console.transfer.dto.domino;

import ge.ai.domino.domain.domino.TableInfo;

public class TableInfoDTO {

    private boolean withCenter;

    private PlayedTileDTO top;

    private PlayedTileDTO right;

    private PlayedTileDTO bottom;

    private PlayedTileDTO left;

    private boolean myTurn;

    private double himTilesCount;

    private double myTilesCount;

    private double bazaarTilesCount;

    private String lastPlayedUID;

    public boolean isWithCenter() {
        return withCenter;
    }

    public void setWithCenter(boolean withCenter) {
        this.withCenter = withCenter;
    }

    public PlayedTileDTO getTop() {
        return top;
    }

    public void setTop(PlayedTileDTO top) {
        this.top = top;
    }

    public PlayedTileDTO getRight() {
        return right;
    }

    public void setRight(PlayedTileDTO right) {
        this.right = right;
    }

    public PlayedTileDTO getBottom() {
        return bottom;
    }

    public void setBottom(PlayedTileDTO bottom) {
        this.bottom = bottom;
    }

    public PlayedTileDTO getLeft() {
        return left;
    }

    public void setLeft(PlayedTileDTO left) {
        this.left = left;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public double getHimTilesCount() {
        return himTilesCount;
    }

    public void setHimTilesCount(double himTilesCount) {
        this.himTilesCount = himTilesCount;
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

    public String getLastPlayedUID() {
        return lastPlayedUID;
    }

    public void setLastPlayedUID(String lastPlayedUID) {
        this.lastPlayedUID = lastPlayedUID;
    }

    public static TableInfoDTO toTableInfoDTO(TableInfo tableInfo) {
        TableInfoDTO dto = new TableInfoDTO();
        dto.setWithCenter(tableInfo.isWithCenter());
        dto.setTop(PlayedTileDTO.toPlayedTileDTO(tableInfo.getTop()));
        dto.setRight(PlayedTileDTO.toPlayedTileDTO(tableInfo.getRight()));
        dto.setBottom(PlayedTileDTO.toPlayedTileDTO(tableInfo.getBottom()));
        dto.setLeft(PlayedTileDTO.toPlayedTileDTO(tableInfo.getLeft()));
        dto.setMyTurn(tableInfo.isMyTurn());
        dto.setHimTilesCount(tableInfo.getHimTilesCount());
        dto.setBazaarTilesCount(tableInfo.getBazaarTilesCount());
        dto.setMyTilesCount(tableInfo.getMyTilesCount());
        dto.setLastPlayedUID(tableInfo.getLastPlayedUID());
        return dto;
    }

    public static TableInfo toTableInfo(TableInfoDTO dto) {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setWithCenter(dto.isWithCenter());
        tableInfo.setTop(PlayedTileDTO.toPlayedTile(dto.getTop()));
        tableInfo.setRight(PlayedTileDTO.toPlayedTile(dto.getRight()));
        tableInfo.setBottom(PlayedTileDTO.toPlayedTile(dto.getBottom()));
        tableInfo.setBottom(PlayedTileDTO.toPlayedTile(dto.getBottom()));
        tableInfo.setMyTurn(dto.isMyTurn());
        tableInfo.setHimTilesCount(dto.getHimTilesCount());
        tableInfo.setBazaarTilesCount(dto.getBazaarTilesCount());
        tableInfo.setMyTilesCount(dto.getMyTilesCount());
        tableInfo.setLastPlayedUID(dto.getLastPlayedUID());
        return tableInfo;
    }
}
