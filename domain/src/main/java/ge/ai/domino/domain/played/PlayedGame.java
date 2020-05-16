package ge.ai.domino.domain.played;

import ge.ai.domino.domain.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PlayedGame {

    private int id;

    private String version;

    private GameResult result;

    private Date endDate;

    private int myPoint;

    private int opponentPoint;

    private int pointForWin;

    private String opponentName;

    private Channel channel;

    private GameHistory gameHistory;

    private String marshaledGameHistory;

    private int level;
}
