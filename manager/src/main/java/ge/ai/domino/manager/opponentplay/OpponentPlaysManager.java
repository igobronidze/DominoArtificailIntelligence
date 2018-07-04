package ge.ai.domino.manager.opponentplay;

import ge.ai.domino.domain.game.opponentplay.GroupedOpponentPlay;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.dao.opponentplay.OpponentPlayDAO;
import ge.ai.domino.dao.opponentplay.OpponentPlayDAOImpl;
import ge.ai.domino.manager.opponentplay.guess.GuessRateCounter;
import org.apache.log4j.Logger;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OpponentPlaysManager {

    private Logger logger = Logger.getLogger(OpponentPlaysManager.class);

    private OpponentPlayDAO opponentPlayDAO = new OpponentPlayDAOImpl();

    public List<GroupedOpponentPlay> getGroupedOpponentPlays(Integer gameId, String version, boolean groupByGame, boolean groupByVersion) {
        List<OpponentPlay> opponentPlays = opponentPlayDAO.getOpponentPlays(version, gameId);
        Reflections reflections = new Reflections("ge.ai.domino");
        Set<Class<? extends GuessRateCounter>> classes = reflections.getSubTypesOf(GuessRateCounter.class);
        List<GroupedOpponentPlay> groupedOpponentPlays = new ArrayList<>();
        for (OpponentPlay opponentPlay : opponentPlays) {
            GroupedOpponentPlay groupedOpponentPlay = new GroupedOpponentPlay();
            groupedOpponentPlay.setGameId(opponentPlay.getGameId());
            groupedOpponentPlay.setVersion(opponentPlay.getVersion());
            for (Class<? extends GuessRateCounter> clazz : classes) {
                try {
                    Method getGuessRateMethod = clazz.getMethod("getGuessRate", OpponentPlay.class);
                    Double rate = (Double)getGuessRateMethod.invoke(clazz.newInstance(), opponentPlay);
                    groupedOpponentPlay.getAverageGuess().put(clazz.getSimpleName(), rate);
                } catch (Exception ex) {
                    logger.error("Error while execute getGuessRate method", ex);
                }
            }
            groupedOpponentPlays.add(groupedOpponentPlay);
        }
        if (groupByVersion) {
            Map<String, Map<String, Double>> guessesSum = new HashMap<>();
            Map<String, Integer> guessesCount = new HashMap<>();
            for (GroupedOpponentPlay groupedOpponentPlay : groupedOpponentPlays) {
                String ver = groupedOpponentPlay.getVersion();
                guessesCount.putIfAbsent(ver, 0);
                guessesSum.putIfAbsent(ver, new HashMap<>());
                guessesCount.put(ver, guessesCount.get(ver) + 1);
                Map<String, Double> sum = guessesSum.get(ver);
                for (Map.Entry<String, Double> guesses : groupedOpponentPlay.getAverageGuess().entrySet()) {
                    sum.put(guesses.getKey(), (sum.get(guesses.getKey()) == null ? 0.0 : sum.get(guesses.getKey())) + guesses.getValue());
                }
            }

            List<GroupedOpponentPlay> result = new ArrayList<>();
            for (String ver : guessesSum.keySet()) {
                GroupedOpponentPlay groupedOpponentPlay = new GroupedOpponentPlay();
                groupedOpponentPlay.setVersion(ver);
                for (Map.Entry<String, Double> guesses : guessesSum.get(ver).entrySet()) {
                    groupedOpponentPlay.getAverageGuess().put(guesses.getKey(), guesses.getValue() / guessesCount.get(ver));
                }
                result.add(groupedOpponentPlay);
            }
            result.sort((o1, o2) -> o2.getGameId() - o1.getGameId());
            return result;
        } else if (groupByGame) {
            Map<Integer, Map<String, Double>> guessesSum = new HashMap<>();
            Map<Integer, Integer> guessesCount = new HashMap<>();
            Map<Integer, String> versionGameIdMap = new HashMap<>();
            for (GroupedOpponentPlay groupedOpponentPlay : groupedOpponentPlays) {
                int id = groupedOpponentPlay.getGameId();
                versionGameIdMap.put(id, groupedOpponentPlay.getVersion());
                guessesCount.putIfAbsent(id, 0);
                guessesSum.putIfAbsent(id, new HashMap<>());
                guessesCount.put(id, guessesCount.get(id) + 1);
                Map<String, Double> sum = guessesSum.get(id);
                for (Map.Entry<String, Double> guesses : groupedOpponentPlay.getAverageGuess().entrySet()) {
                    sum.put(guesses.getKey(), (sum.get(guesses.getKey()) == null ? 0.0 : sum.get(guesses.getKey())) + guesses.getValue());
                }
            }

            List<GroupedOpponentPlay> result = new ArrayList<>();
            for (int id : versionGameIdMap.keySet()) {
                GroupedOpponentPlay groupedOpponentPlay = new GroupedOpponentPlay();
                groupedOpponentPlay.setGameId(id);
                groupedOpponentPlay.setVersion(versionGameIdMap.get(id));
                for (Map.Entry<String, Double> guesses : guessesSum.get(id).entrySet()) {
                    groupedOpponentPlay.getAverageGuess().put(guesses.getKey(), guesses.getValue() / guessesCount.get(id));
                }
                result.add(groupedOpponentPlay);
            }
            result.sort((o1, o2) -> o2.getGameId() - o1.getGameId());
            return result;
        } else {
            groupedOpponentPlays.sort((o1, o2) -> o2.getGameId() - o1.getGameId());
            return groupedOpponentPlays;
        }
    }
}
