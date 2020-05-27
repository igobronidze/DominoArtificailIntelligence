package ge.ai.domino.console.debug;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.move.MoveType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameDebuggerHelper {

	private static final Logger logger = Logger.getLogger(GameDebuggerHelper.class);

	public static final String LOG_END = "END";

	public static final int GAME_ID = -1;

	public static Round round;

	public static int readInt(Logger logger, Scanner scanner, String text) {
		logger.info(text);
		return Integer.parseInt(scanner.nextLine());
	}

	public static List<Integer> getIdsForProcess(Scanner scanner) {
		logger.info("Game ids( Format example - 1_20/3,15 ):");
		String inputIds = scanner.nextLine();

		int idFrom = Integer.parseInt(inputIds.split("/")[0].split("_")[0]);
		int idTo = Integer.parseInt(inputIds.split("/")[0].split("_")[1]);

		String[] notUsedIdsString = inputIds.split("/")[1].split(",");
		List<Integer> notUsedIds = new ArrayList<>();
		for (String id : notUsedIdsString) {
			notUsedIds.add(Integer.parseInt(id));
		}

		return IntStream.range(idFrom, idTo + 1).filter(i -> !notUsedIds.contains(i)).boxed().collect(Collectors.toList());
	}

	public static List<OpponentPlay> removeExtraPlays(List<OpponentPlay> opponentPlays) {
		List<OpponentPlay> result = new ArrayList<>();
		boolean lastAdd = false;
		for (OpponentPlay opponentPlay : opponentPlays) {
			if (!lastAdd) {
				result.add(opponentPlay);
			}
			lastAdd = opponentPlay.getMoveType() == MoveType.ADD_FOR_OPPONENT;
		}
		return result;
	}
}
