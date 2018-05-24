package ge.ai.domino.domain.function;

import java.util.ArrayList;
import java.util.List;

public class FunctionArgsAndValues {

	private String name;

	private List<Double> args = new ArrayList<>();

	private List<Double> values = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Double> getArgs() {
		return args;
	}

	public void setArgs(List<Double> args) {
		this.args = args;
	}

	public List<Double> getValues() {
		return values;
	}

	public void setValues(List<Double> values) {
		this.values = values;
	}
}
