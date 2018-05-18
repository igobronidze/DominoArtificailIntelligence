package ge.ai.domino.console.ui.opponentplays;

import ge.ai.domino.domain.game.opponentplay.GroupedOpponentPlay;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupedOpponentPlaysProperty {

	public static final String GAME_ID_KEY = "gameId";

	public static final String VERSION_KEY = "version";

	public static ObservableList<Map<String, String>> generateDataInMap(List<GroupedOpponentPlay> groupedOpponentPlays) {
		ObservableList<Map<String, String>> allData = FXCollections.observableArrayList();
		for (GroupedOpponentPlay groupedOpponentPlay : groupedOpponentPlays) {
			Map<String, String> dataRow = new HashMap<>();
			dataRow.put(GAME_ID_KEY, String.valueOf(groupedOpponentPlay.getGameId()));
			dataRow.put(VERSION_KEY, groupedOpponentPlay.getVersion());
			for (Map.Entry<String, Double> entry : groupedOpponentPlay.getAverageGuess().entrySet()) {
				dataRow.put(entry.getKey(), String.valueOf(entry.getValue()));
			}
			allData.add(dataRow);
		}
		return allData;
	}
}
