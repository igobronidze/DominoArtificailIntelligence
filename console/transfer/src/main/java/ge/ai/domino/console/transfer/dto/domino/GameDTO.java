package ge.ai.domino.console.transfer.dto.domino;

import ge.ai.domino.domain.domino.Game;

public class GameDTO {

    private int id;

    private GamePropertiesDTO gameProperties;

    private HandDTO currHand;

    private int myPoints;

    private int himPoints;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GamePropertiesDTO getGameProperties() {
        return gameProperties;
    }

    public void setGameProperties(GamePropertiesDTO gameProperties) {
        this.gameProperties = gameProperties;
    }

    public HandDTO getCurrHand() {
        return currHand;
    }

    public void setCurrHand(HandDTO currHand) {
        this.currHand = currHand;
    }

    public int getMyPoints() {
        return myPoints;
    }

    public void setMyPoints(int myPoints) {
        this.myPoints = myPoints;
    }

    public int getHimPoints() {
        return himPoints;
    }

    public void setHimPoints(int himPoints) {
        this.himPoints = himPoints;
    }

    public static GameDTO toGameDTO(Game game) {
        GameDTO dto = new GameDTO();
        dto.setId(game.getId());
        dto.setMyPoints(game.getMyPoints());
        dto.setHimPoints(game.getHimPoints());
        dto.setCurrHand(HandDTO.toHandDTO(game.getCurrHand()));
        dto.setGameProperties(GamePropertiesDTO.toGamePropertiesDTO(game.getGameProperties()));
        return dto;
    }

    public static Game toGame(GameDTO dto) {
        Game game = new Game();
        game.setId(dto.getId());
        game.setMyPoints(dto.getMyPoints());
        game.setHimPoints(dto.getHimPoints());
        game.setCurrHand(HandDTO.toHand(dto.getCurrHand()));
        game.setGameProperties(GamePropertiesDTO.toGameProperties(dto.getGameProperties()));
        return game;
    }
}
