package ge.ai.domino.console.debug;

import ge.ai.domino.console.debug.util.RoundParser;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;

import java.util.Scanner;

public class RoundCreator {

    private static final String LOG_END = "END";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Parse round(please type " + LOG_END + " for finish)");
            System.out.println("2. exit");
            String line = scanner.nextLine();
            try {
                if (Integer.parseInt(line.trim()) == 1) {
                    String s;
                    StringBuilder log = new StringBuilder();
                    while (!(s = scanner.nextLine()).equals(LOG_END)) {
                        log.append(s).append(GameLoggingProcessor.END_LINE);
                    }
                    Round round = RoundParser.parseRound(log.toString());
                    //noinspection ResultOfMethodCallIgnored
                    round.getMyTiles();
                } else if (Integer.parseInt(line.trim()) == 2) {
                    break;
                }
            } catch (Exception ignore) {}
        }
    }
}
