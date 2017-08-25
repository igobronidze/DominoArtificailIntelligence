package ge.ai.domino.console.transfer.dto.domino;

import ge.ai.domino.domain.domino.Hand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandDTO {

    private boolean myTurn;

    private Map<String, TileDTO> tiles = new HashMap<>();

    private int tilesInBazaar;

    private boolean hasCenter;

    private Integer top;

    private Integer right;

    private Integer bottom;

    private Integer left;

    private BestPredictionDTO bestPrediction;

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public Map<String, TileDTO> getTiles() {
        return tiles;
    }

    public void setTiles(Map<String, TileDTO> tiles) {
        this.tiles = tiles;
    }

    public int getTilesInBazaar() {
        return tilesInBazaar;
    }

    public void setTilesInBazaar(int tilesInBazaar) {
        this.tilesInBazaar = tilesInBazaar;
    }

    public boolean isHasCenter() {
        return hasCenter;
    }

    public void setHasCenter(boolean hasCenter) {
        this.hasCenter = hasCenter;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public Integer getRight() {
        return right;
    }

    public void setRight(Integer right) {
        this.right = right;
    }

    public Integer getBottom() {
        return bottom;
    }

    public void setBottom(Integer bottom) {
        this.bottom = bottom;
    }

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    public BestPredictionDTO getBestPrediction() {
        return bestPrediction;
    }

    public void setBestPrediction(BestPredictionDTO bestPrediction) {
        this.bestPrediction = bestPrediction;
    }

    public static HandDTO toHandDTO(Hand hand) {
        HandDTO dto = new HandDTO();
        dto.setMyTurn(hand.isMyTurn());
        dto.setTiles(TileDTO.toTileDTOMap(hand.getTiles()));
        dto.setTilesInBazaar(hand.getTilesInBazaar());
        dto.setHasCenter(hand.isHasCenter());
        dto.setTop(hand.getTop());
        dto.setRight(hand.getRight());
        dto.setBottom(hand.getBottom());
        dto.setLeft(hand.getLeft());
        dto.setBestPrediction(BestPredictionDTO.toBestPredictionDTO(hand.getBestPrediction()));
        return dto;
    }

    public static Hand toHand(HandDTO dto) {
        Hand hand = new Hand();
        hand.setMyTurn(dto.isMyTurn());
        hand.setTiles(TileDTO.toTileMap(dto.getTiles()));
        hand.setTilesInBazaar(dto.getTilesInBazaar());
        hand.setHasCenter(dto.isHasCenter());
        hand.setTop(dto.getTop());
        hand.setRight(dto.getRight());
        hand.setBottom(dto.getBottom());
        hand.setLeft(dto.getLeft());
        hand.setBestPrediction(BestPredictionDTO.toBestPrediction(dto.getBestPrediction()));
        return hand;
    }

    public static List<HandDTO> toHandDTOList(List<Hand> hands) {
        List<HandDTO> dtoList = new ArrayList<>();
        for (Hand hand : hands) {
            dtoList.add(toHandDTO(hand));
        }
        return dtoList;
    }

    public static List<Hand> toHandList(List<HandDTO> dtoList) {
        List<Hand> hands = new ArrayList<>();
        for (HandDTO hand : dtoList) {
            hands.add(toHand(hand));
        }
        return hands;
    }
}
