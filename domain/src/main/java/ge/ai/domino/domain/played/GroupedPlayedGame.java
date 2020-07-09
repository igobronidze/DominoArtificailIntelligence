package ge.ai.domino.domain.played;

import ge.ai.domino.domain.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GroupedPlayedGame {

    private String version;

    private Channel channel;

    private Integer pointForWin;

    private Double level;

    private Date date;

    private int finished;

    private int win;

    private int lose;

    private int stopped;

    private Double profit;
}
