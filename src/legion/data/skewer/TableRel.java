package legion.data.skewer;

public class TableRel {
	private TableColPack tcp1, tcp2;
	private String col1, col2;

	private TableRel(TableColPack tcp1, TableColPack tcp2, String col1, String col2) {
		this.tcp1 = tcp1;
		this.tcp2 = tcp2;
		this.col1 = col1;
		this.col2 = col2;
	}

	public static TableRel of(TableColPack tcp1, TableColPack tcp2, String col1, String col2) {
		return new TableRel(tcp1, tcp2, col1, col2);
	}

	public TableColPack getTcp1() {
		return tcp1;
	}

	public TableColPack getTcp2() {
		return tcp2;
	}

	public String getCol1() {
		return col1;
	}

	public String getCol2() {
		return col2;
	}

}
