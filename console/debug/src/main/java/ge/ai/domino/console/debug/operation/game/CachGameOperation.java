package ge.ai.domino.console.debug.operation.game;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameProperties;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class CachGameOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(CachGameOperation.class);

	public void process(Scanner scanner) {
		GameProperties gameProperties = new GameProperties();
		logger.info("Game ID:");
		int gameId = Integer.parseInt(scanner.nextLine());
		logger.info("Opponent name:");
		gameProperties.setOpponentName(scanner.nextLine());
		logger.info("Channel:");
		Channel channel = new Channel();
		channel.setName(scanner.nextLine());
		gameProperties.setChannel(channel);
		logger.info("Point for win:");
		gameProperties.setPointsForWin(Integer.parseInt(scanner.nextLine()));
		Game game = new Game();
		game.setId(gameId);
		game.setProperties(gameProperties);
		CachedGames.addGame(game);
		logger.info("Game cached successfully");
	}
}
