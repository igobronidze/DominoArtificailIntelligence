package ge.ai.domino.console.transfer.dto.domino;

import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.Hand;

import java.util.ArrayList;
import java.util.List;

public class GameDTO {

    private int id;

    private GamePropertiesDTO gameProperties;

    private HandDTO currHand;

    private int myPoint;

    private int himPoint;

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

    public int getMyPoint() {
        return myPoint;
    }

    public void setMyPoint(int myPoint) {
        this.myPoint = myPoint;
    }

    public int getHimPoint() {
        return himPoint;
    }

    public void setHimPoint(int himPoint) {
        this.himPoint = himPoint;
    }

    public static GameDTO toGameDTO(Game game) {
        GameDTO dto = new GameDTO();
        dto.setId(game.getId());
        dto.setMyPoint(game.getMyPoint());
        dto.setHimPoint(game.getHimPoint());
        dto.setCurrHand(HandDTO.toHandDTO(game.getCurrHand()));
        dto.setGameProperties(GamePropertiesDTO.toGamePropertiesDTO(game.getGameProperties()));
        return dto;
    }

    public static Game toGame(GameDTO dto) {
        Game game = new Game();
        game.setId(dto.getId());
        game.setMyPoint(dto.getMyPoint());
        game.setHimPoint(dto.getHimPoint());
        game.setCurrHand(HandDTO.toHand(dto.getCurrHand()));
        game.setGameProperties(GamePropertiesDTO.toGameProperties(dto.getGameProperties()));
        return game;
    }
}
