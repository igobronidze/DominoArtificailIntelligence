package ge.ai.domino.domain.function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FunctionArgsAndValues implements Serializable {

	private String name;

	private List<Double> args = new ArrayList<>();

	private List<Double> values = new ArrayList<>();

	public FunctionArgsAndValues(String name) {
		this.name = name;
	}

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

	@Override
	public String toString() {
		return "FunctionArgsAndValues{" +
				"name='" + name + '\'' +
				", args=" + args +
				", values=" + values +
				'}';
	}
}
