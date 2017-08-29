package ge.ai.domino.console.transfer.dto.domino;

import ge.ai.domino.domain.domino.AIPrediction;
import ge.ai.domino.domain.domino.PlayDirection;

public class AIPredictionDTO {

    private int x;

    private int y;

    private PlayDirectionDTO direction;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public PlayDirectionDTO getDirection() {
        return direction;
    }

    public void setDirection(PlayDirectionDTO direction) {
        this.direction = direction;
    }

    public static AIPredictionDTO toBestPredictionDTO(AIPrediction AIPrediction) {
        if (AIPrediction == null) {
            return null;
        }
        AIPredictionDTO dto = new AIPredictionDTO();
        dto.setX(AIPrediction.getX());
        dto.setY(AIPrediction.getY());
        dto.setDirection(PlayDirectionDTO.valueOf(AIPrediction.getDirection().name()));
        return dto;
    }

    public static AIPrediction toBestPrediction(AIPredictionDTO dto) {
        if (dto == null) {
            return null;
        }
        AIPrediction AIPrediction = new AIPrediction();
        AIPrediction.setX(dto.getX());
        AIPrediction.setY(dto.getY());
        AIPrediction.setDirection(PlayDirection.valueOf(dto.getDirection().name()));
        return AIPrediction;
    }
}
