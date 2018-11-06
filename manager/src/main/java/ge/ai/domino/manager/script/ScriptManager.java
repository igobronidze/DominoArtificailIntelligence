package ge.ai.domino.manager.script;

import ge.ai.domino.dao.script.ScriptExecutor;
import ge.ai.domino.dao.script.ScriptExecutorImpl;

public class ScriptManager {

	private static final ScriptExecutor scriptExecutor = new ScriptExecutorImpl();

	public void executeUpdateScript(String script) {
		scriptExecutor.executeUpdate(script);
	}
}
