package ge.ai.domino.server.dao.function;

import ge.ai.domino.domain.function.FunctionArgsAndValues;

public interface FunctionDAO {

	FunctionArgsAndValues getFunctionArgsAndValues(String name);
}
