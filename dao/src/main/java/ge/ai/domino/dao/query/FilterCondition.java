package ge.ai.domino.dao.query;

public enum  FilterCondition {

	EQUAL("="),
	LIKE("LIKE"),
	GREAT(">"),
	GREAT_OR_EQUAL(">="),
	LESS_OR_EQUAL("<=");

	private final String value;

	FilterCondition(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
