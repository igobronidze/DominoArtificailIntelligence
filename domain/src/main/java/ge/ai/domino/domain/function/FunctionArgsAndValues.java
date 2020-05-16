package ge.ai.domino.domain.function;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FunctionArgsAndValues implements Serializable {

	private String name;

	private List<Double> args = new ArrayList<>();

	private List<Double> values = new ArrayList<>();

	public FunctionArgsAndValues(String name) {
		this.name = name;
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
