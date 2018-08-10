package ge.ai.domino.domain.channel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Channel implements Serializable {

	private int id;

	private String name;

	private Map<String, String> params = new HashMap<>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
}
