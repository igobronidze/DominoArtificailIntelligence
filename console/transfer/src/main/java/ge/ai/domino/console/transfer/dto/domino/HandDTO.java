package ge.ai.domino.console.transfer.dto.domino;

import ge.ai.domino.domain.ai.AIExtraInfo;
import ge.ai.domino.domain.domino.Hand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandDTO {

    private Map<String, TileDTO> tiles = new HashMap<>();

    private TableInfoDTO tableInfo;

    private AIPredictionDTO aiPrediction;

    public Map<String, TileDTO> getTiles() {
        return tiles;
    }

    public void setTiles(Map<String, TileDTO> tiles) {
        this.tiles = tiles;
    }

    public TableInfoDTO getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfoDTO tableInfo) {
        this.tableInfo = tableInfo;
    }

    public AIPredictionDTO getAiPrediction() {
        return aiPrediction;
    }

    public void setAiPrediction(AIPredictionDTO aiPrediction) {
        this.aiPrediction = aiPrediction;
    }

    public static HandDTO toHandDTO(Hand hand) {
        HandDTO dto = new HandDTO();
        dto.setTiles(TileDTO.toTileDTOMap(hand.getTiles()));
        dto.setTableInfo(TableInfoDTO.toTableInfoDTO(hand.getTableInfo()));
        dto.setAiPrediction(AIPredictionDTO.toBestPredictionDTO(hand.getAiPrediction()));
        return dto;
    }

    public static Hand toHand(HandDTO dto) {
        Hand hand = new Hand();
        hand.setTiles(TileDTO.toTileMap(dto.getTiles()));
        hand.setTableInfo(TableInfoDTO.toTableInfo(dto.getTableInfo()));
        hand.setAiPrediction(AIPredictionDTO.toBestPrediction(dto.getAiPrediction()));
        hand.setAiExtraInfo(new AIExtraInfo());
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
