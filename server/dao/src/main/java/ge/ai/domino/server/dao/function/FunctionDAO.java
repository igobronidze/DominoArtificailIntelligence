package ge.ai.domino.server.dao.function;

import ge.ai.domino.domain.function.FunctionArgsAndValues;

import java.util.Map;

public interface FunctionDAO {

	Map<String, FunctionArgsAndValues> getFunctionArgsAndValues(String namePrefix);
}
