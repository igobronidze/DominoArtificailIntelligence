package ge.ai.domino.console.debug.operation;

import ge.ai.domino.domain.exception.DAIException;

import java.util.Scanner;

public interface GameDebuggerOperation {

	void process(Scanner scanner) throws DAIException;
}
