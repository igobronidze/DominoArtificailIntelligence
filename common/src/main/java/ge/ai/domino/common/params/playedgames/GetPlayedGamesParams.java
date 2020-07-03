package ge.ai.domino.common.params.playedgames;

import ge.ai.domino.domain.played.GameResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPlayedGamesParams {

    private String version;

    private GameResult result;

    private String opponentName;

    private Integer channelId;
    
    private String level;
}
