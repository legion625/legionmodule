package legion.data;

public enum CompareOp {
	EQUAL("="), LIKE("like");
	private String oper;

	private CompareOp(String oper) {
		this.oper = oper;
	}

	public String getOper() {
		return oper;
	}
	
	
}
