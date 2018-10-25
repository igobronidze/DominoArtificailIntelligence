package ge.ai.domino.manager.game.ai.minmax.bfs;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.game.ai.minmax.CachedPrediction;
import ge.ai.domino.manager.game.ai.minmax.NodeRound;
import ge.ai.domino.manager.game.ai.predictor.MinMaxPredictor;
import ge.ai.domino.manager.game.helper.game.GameOperations;
import ge.ai.domino.manager.multithreadingserver.ClientSocket;
import ge.ai.domino.manager.multithreadingserver.MultithreadingServer;
import org.apache.log4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class MultithreadingMinMaxBFS extends MinMaxBFS {

	private final Logger logger = Logger.getLogger(MultithreadingMinMaxBFS.class);

	private Map<Integer, NodeRound> roundsForProcess = new HashMap<>();

	@Override
	public AiPredictionsWrapper solve(Round round) throws DAIException {
		long ms = System.currentTimeMillis();
		List<Move> moves = GameOperations.getPossibleMoves(round, false);
		logger.info("Start MultithreadedMinMaxBFS solve method, movesCount[" + moves.size() + "]");
		if (moves.size() <= 1) {
			super.setThreadCount(systemParameterManager.getIntegerParameterValue(minMaxForCachedNodeRoundIterationRate)); // For minmax performance time);
			return super.solve(round);
		}

		NodeRound nodeRound = new NodeRound();
		nodeRound.setRound(round);
		createNodeRoundWithHeightTwo(nodeRound);

		invokeRemoteMinMaxes();
		applyBottomUpMinMax();

		logger.info("MinMaxBFS took " + (System.currentTimeMillis() - ms) + " ms");
		AiPredictionsWrapper aiPredictionsWrapper = getAiPredictionsWrapper(nodeRound);
		if (new MinMaxPredictor().usePredictor()) {
			CachedMinMax.setCachedPrediction(nodeRound.getRound().getGameInfo().getGameId(), CachedPrediction.getCachedPrediction(nodeRound, 2), true);
		}
		return aiPredictionsWrapper;
	}

	@Override
	public void minMaxForCachedNodeRound(Round round) throws DAIException {
		super.minMaxForCachedNodeRound(round);
	}

	private NodeRound createNodeRoundWithHeightTwo(NodeRound nodeRound) throws DAIException {
		addMyPlaysForNodeRound(nodeRound, false);

		nodeRoundsByHeight.put(1, new ArrayList<>());

		for (NodeRound child : nodeRound.getChildren()) {
			nodeRoundsByHeight.get(1).add(child);

			addOpponentPlaysForNodeRound(child);
			for (NodeRound grandchild : child.getChildren()) {
				nodeRoundsByHeight.putIfAbsent(2, new ArrayList<>());
				nodeRoundsByHeight.get(2).add(grandchild);
				roundsForProcess.put(grandchild.getId(), grandchild);
			}
			for (NodeRound grandchild : child.getBazaarNodeRounds()) {
				nodeRoundsByHeight.putIfAbsent(2, new ArrayList<>());
				nodeRoundsByHeight.get(2).add(grandchild);
				roundsForProcess.put(grandchild.getId(), grandchild);
			}
		}

		return nodeRound;
	}

	private void invokeRemoteMinMaxes() throws DAIException {
		MultithreadingServer server = MultithreadingServer.getInstance();
		List<ClientSocket> clients = server.getClients();

		List<ClientInfo> clientInfos = new ArrayList<>();
		for (ClientSocket clientSocket : clients) {
			ClientInfo clientInfo = new ClientInfo();
			clientInfo.clientSocket = clientSocket;
			clientInfos.add(clientInfo);
		}

		for (NodeRound nodeRound : roundsForProcess.values()) {
			ClientInfo best = null;
			for (ClientInfo clientInfo : clientInfos) {
				if (best == null || clientInfo.clientSocket.getRank() / (clientInfo.nodeRounds.size() + 1)
						> best.clientSocket.getRank() / (best.nodeRounds.size() + 1)) {
					best = clientInfo;
				}
			}
			best.nodeRounds.add(nodeRound);
		}

		List<Callable<Map.Entry<List<Integer>, List<AiPredictionsWrapper>>>> callableList = new ArrayList<>();
		for (ClientInfo clientInfo : clientInfos) {
			callableList.add(() -> new AbstractMap.SimpleEntry<>(clientInfo.getIds(), clientInfo.clientSocket.executeMinMax(clientInfo.getRounds())));
		}

		ExecutorService executorService = Executors.newFixedThreadPool(100);
		try {
			List<Future<Map.Entry<List<Integer>, List<AiPredictionsWrapper>>>> aiPredictionWrappers = executorService.invokeAll(callableList);

			for (Future<Map.Entry<List<Integer>, List<AiPredictionsWrapper>>> future : aiPredictionWrappers) {
				Map.Entry<List<Integer>, List<AiPredictionsWrapper>> aiPredictionsWrapperEntry = future.get();

				for (int i = 0; i < aiPredictionsWrapperEntry.getKey().size(); i++) {
					NodeRound nodeRound = roundsForProcess.get(aiPredictionsWrapperEntry.getKey().get(i));
					AiPredictionsWrapper aiPredictionsWrapper = aiPredictionsWrapperEntry.getValue().get(i);
					if (aiPredictionsWrapper.getAiPredictions().isEmpty()) {
						nodeRound.setHeuristic(roundHeuristic.getHeuristic(nodeRound.getRound(), false));
					} else {
						nodeRound.setHeuristic(getHeuristicFromPrediction(aiPredictionsWrapper));
					}
				}
			}

		} catch (Exception ex) {
			logger.error("Error occurred while execute multithreaded minmax", ex);
			throw new DAIException("unexpectedError");
		} finally {
			executorService.shutdown();
		}
	}

	private double getHeuristicFromPrediction(AiPredictionsWrapper aiPredictionsWrapper) {
		double best = Integer.MIN_VALUE;
		for (AiPrediction aiPrediction : aiPredictionsWrapper.getAiPredictions()) {
			if (aiPrediction.getHeuristicValue() > best) {
				best = aiPrediction.getHeuristicValue();
			}
		}
		return best;
	}

	private class ClientInfo {

		private ClientSocket clientSocket;

		private List<NodeRound> nodeRounds = new ArrayList<>();

		private List<Integer> getIds() {
			return nodeRounds.stream().map(NodeRound::getId).collect(Collectors.toList());
		}

		private List<Round> getRounds() {
			return nodeRounds.stream().map(NodeRound::getRound).collect(Collectors.toList());
		}
	}
}
