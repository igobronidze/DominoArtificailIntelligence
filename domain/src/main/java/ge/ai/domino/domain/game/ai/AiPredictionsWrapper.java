package ge.ai.domino.domain.game.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AiPredictionsWrapper implements Serializable {

    private List<AiPrediction> aiPredictions = new ArrayList<>();

    private String warnMsgKey;

    public List<AiPrediction> getAiPredictions() {
        return aiPredictions;
    }

    public void setAiPredictions(List<AiPrediction> aiPredictions) {
        this.aiPredictions = aiPredictions;
    }

    public String getWarnMsgKey() {
        return warnMsgKey;
    }

    public void setWarnMsgKey(String warnMsgKey) {
        this.warnMsgKey = warnMsgKey;
    }
}
