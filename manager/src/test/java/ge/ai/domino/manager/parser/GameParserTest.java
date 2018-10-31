package ge.ai.domino.manager.parser;

import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.manager.game.logging.GameLogger;
import org.junit.Assert;
import org.junit.Test;

public class GameParserTest {

	private static final int GAME_ID = -108;

	private static final String OPPONENT_NAME = "name";

	private static final String CHANNEL_NAME = "test";

	private static final int POINT_FOR_WIN = 255;

	@Test
	public void testParseGame() {
		Game game = new Game();
		game.setId(GAME_ID);

		GameProperties gameProperties = new GameProperties();
		gameProperties.setOpponentName(OPPONENT_NAME);
		gameProperties.setPointsForWin(POINT_FOR_WIN);
		Channel channel = new Channel();
		channel.setName(CHANNEL_NAME);
		gameProperties.setChannel(channel);
		game.setProperties(gameProperties);

		String log = GameLogger.getGameInfo(game);

		GameParserManager gameParserManager = new GameParserManager();
		Game parsedGame = gameParserManager.parseGame(log);
		Assert.assertEquals(game.getId(), parsedGame.getId());
		Assert.assertEquals(game.getProperties().getOpponentName(), parsedGame.getProperties().getOpponentName());
		Assert.assertEquals(game.getProperties().getPointsForWin(), parsedGame.getProperties().getPointsForWin());
	}
}
