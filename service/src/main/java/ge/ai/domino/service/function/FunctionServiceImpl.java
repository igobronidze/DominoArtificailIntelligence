package ge.ai.domino.service.function;

import ge.ai.domino.manager.function.FunctionManager;

public class FunctionServiceImpl implements FunctionService {

	private FunctionManager functionManager = new FunctionManager();

	@Override
	public void initFunctions() {
		functionManager.initFunctions();
	}
}
