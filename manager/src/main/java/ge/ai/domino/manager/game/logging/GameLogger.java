package ge.ai.domino.manager.game.logging;

import ge.ai.domino.domain.game.Game;
import org.apache.log4j.Logger;

public class GameLogger {

	private static final Logger logger = Logger.getLogger(GameLogger.class);

	public static final String DELIMITER = "|";

	public static final String EQUAL_CHARACTER = ":";

	private static final String EQUAL_CHARACTER_WITH_SPACE = EQUAL_CHARACTER + " ";

	private static final String DELIMITER_WITH_SPACES = "     " + DELIMITER + "     ";

	public static final String END_LINE = "\n";

	public static final String GAME_ID_LEFT_CHARACTER = "[";

	public static final String GAME_ID_RIGHT_CHARACTER = "]";

	public static void logGameInfo(Game game) {
		logger.info(getGameInfo(game));
	}

	public static String getGameInfo(Game game) {
		return "------------Started new game[" + game.getId() + "]------------" + END_LINE +
				"Opponent Name" + EQUAL_CHARACTER_WITH_SPACE + game.getProperties().getOpponentName() + DELIMITER_WITH_SPACES +
				"Channel" + EQUAL_CHARACTER_WITH_SPACE + game.getProperties().getChannel().getName() + DELIMITER_WITH_SPACES +
				"Point for win" + EQUAL_CHARACTER_WITH_SPACE + game.getProperties().getPointsForWin();
	}
}
