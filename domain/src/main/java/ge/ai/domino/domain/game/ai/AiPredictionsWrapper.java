package ge.ai.domino.domain.game.ai;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AiPredictionsWrapper implements Serializable {

    private List<AiPrediction> aiPredictions = new ArrayList<>();

    private String warnMsgKey;
}
