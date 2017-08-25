package ge.ai.domino.console.transfer.dto.domino;

import ge.ai.domino.domain.domino.BestPrediction;
import ge.ai.domino.domain.domino.PlayDirection;

public class BestPredictionDTO {

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

    public static BestPredictionDTO toBestPredictionDTO(BestPrediction bestPrediction) {
        if (bestPrediction == null) {
            return null;
        }
        BestPredictionDTO dto = new BestPredictionDTO();
        dto.setX(bestPrediction.getX());
        dto.setY(bestPrediction.getY());
        dto.setDirection(PlayDirectionDTO.valueOf(bestPrediction.getDirection().name()));
        return dto;
    }

    public static BestPrediction toBestPrediction(BestPredictionDTO dto) {
        if (dto == null) {
            return null;
        }
        BestPrediction bestPrediction = new BestPrediction();
        bestPrediction.setX(dto.getX());
        bestPrediction.setY(dto.getY());
        bestPrediction.setDirection(PlayDirection.valueOf(dto.getDirection().name()));
        return bestPrediction;
    }
}
