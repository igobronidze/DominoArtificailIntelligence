package ge.ai.domino.manager.parser;

import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameFromLog;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.manager.channel.ChannelManager;
import ge.ai.domino.manager.game.logging.GameLogger;
import ge.ai.domino.manager.game.logging.RoundLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GameParserManager {

	private static final String INFO = "INFO";

	private static final String START_NEW_GAME_LINE = "Started new game";

	private static final String ROUND_LOG_START_LINE = "____________________________________________Round Info____________________________________________";

	private static final int ROUND_LOG_LINES_COUNT = 18;

	private static final int GAME_ID_ADDITION = -10000;

	private static final ChannelManager channelManager = new ChannelManager();

	private static final RoundParserManager roundParserManager = new RoundParserManager();

	public List<GameFromLog> parseAllGameInFile(File logFile) throws DAIException {
		try {
			List<GameFromLog> games = new ArrayList<>();

			BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
			String line;

			GameFromLog gameFromLog = null;
			while (true) {
				line = bufferedReader.readLine();
				if (line == null) {
					break;
				}

				if (line.contains(START_NEW_GAME_LINE)) {
					if (gameFromLog != null) {
						games.add(gameFromLog);
					}

					line = line + System.lineSeparator() + bufferedReader.readLine();
					Game game = parseGame(line);
					gameFromLog = new GameFromLog();
					gameFromLog.setGameId(game.getId() + GAME_ID_ADDITION);
					gameFromLog.setPointForWin(game.getProperties().getPointsForWin());
					gameFromLog.setChannel(game.getProperties().getChannel());
				}

				if (line.equals(ROUND_LOG_START_LINE)) {
					StringBuilder log = new StringBuilder(line);
					for (int i = 0; i < ROUND_LOG_LINES_COUNT - 1; i++) {
						log.append(System.lineSeparator());
						log.append(bufferedReader.readLine());
					}
					if (log.lastIndexOf(RoundLogger.NO_TILES) == -1) {
						Round round = roundParserManager.parseRound(log.toString());
						round.getGameInfo().setGameId(gameFromLog.getGameId());
						gameFromLog.getRounds().add(round);
					}
				}
			}
			if (gameFromLog != null) {
				games.add(gameFromLog);
			}

			return games;
		} catch (Exception ex) {
			throw new DAIException("cantReadLogFile", ex);
		}
	}

	/**
	 * Parse round from log
	 *
	 * Log Example
	 *
	 ------------Started new game[223]------------
	 Opponent Name: tmp     |     Channel: BetLive     |     Point for win: 255
	 *
	 * @param log - Game log
	 * @return Parsed game
	 */
	public Game parseGame(String log) {
		Game game = new Game();

		String[] lines = log.split(Pattern.quote(GameLogger.END_LINE));
		game.setId(getGameId(getWithoutInfo(lines[0])));
		game.setProperties(getGameProperties(getWithoutInfo(lines[1])));
		return game;
	}

	private int getGameId(String line) {
		int from = line.indexOf(GameLogger.GAME_ID_LEFT_CHARACTER);
		int to = line.indexOf(GameLogger.GAME_ID_RIGHT_CHARACTER);
		return Integer.valueOf(line.substring(from + 1, to));
	}

	private GameProperties getGameProperties(String line) {
		GameProperties gameProperties = new GameProperties();

		String[] params = line.split(Pattern.quote(GameLogger.DELIMITER));
		gameProperties.setOpponentName(params[0].split(Pattern.quote(GameLogger.EQUAL_CHARACTER))[1].trim());
		String channelName = params[1].split(Pattern.quote(GameLogger.EQUAL_CHARACTER))[1].trim();
		if (channelName.equals("test")) {
			gameProperties.setChannel(new Channel());
		} else {
			gameProperties.setChannel(channelManager.getChannelByName(channelName));
		}
		gameProperties.setPointsForWin(Integer.parseInt(params[2].split(Pattern.quote(GameLogger.EQUAL_CHARACTER))[1].trim()));
		return gameProperties;
	}

	private String getWithoutInfo(String line) {
		if (line.contains(INFO)) {
			return line.split(INFO)[1];
		} else {
			return line;
		}
	}
}
