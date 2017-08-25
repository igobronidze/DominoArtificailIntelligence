package ge.ai.domino.console.transfer.dto.domino;

import ge.ai.domino.domain.domino.GameProperties;

public class GamePropertiesDTO {

    private String opponentName;

    private String website;

    private int pointForWin;

    private boolean start;

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getPointForWin() {
        return pointForWin;
    }

    public void setPointForWin(int pointForWin) {
        this.pointForWin = pointForWin;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public static GameProperties toGameProperties(GamePropertiesDTO dto) {
        GameProperties gameProperties = new GameProperties();
        gameProperties.setOpponentName(dto.getOpponentName());
        gameProperties.setWebsite(dto.getWebsite());
        gameProperties.setPointForWin(dto.getPointForWin());
        gameProperties.setStart(dto.isStart());
        return gameProperties;
    }

    public static GamePropertiesDTO toGamePropertiesDTO(GameProperties gameProperties) {
        GamePropertiesDTO dto = new GamePropertiesDTO();
        dto.setOpponentName(gameProperties.getOpponentName());
        dto.setWebsite(gameProperties.getWebsite());
        dto.setPointForWin(gameProperties.getPointForWin());
        dto.setStart(gameProperties.isStart());
        return dto;
    }
}
