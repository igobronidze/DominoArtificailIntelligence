package ge.ai.domino.dao.query;

public enum  FilterCondition {

	EQUAL("="),
	LIKE("LIKE");

	private String value;

	FilterCondition(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
