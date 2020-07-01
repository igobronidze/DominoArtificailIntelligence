package ge.ai.domino.domain.played;

import ge.ai.domino.domain.channel.Channel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupedPlayedGame {

    private String version;

    private String opponentName;

    private Channel channel;

    private Integer pointForWin;

    private Integer level;

    private int finished;

    private int win;

    private int lose;

    private int stopped;

    private Double profit;
}
