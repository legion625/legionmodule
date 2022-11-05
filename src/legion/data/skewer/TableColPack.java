package legion.data.skewer;

public class TableColPack {
	private boolean main;
	private String alias;
	private String table;
	private String[] cols;
	private TableColPack(boolean main, String alias, String table, String[] cols) {
		this.main = main;
		this.alias = alias;
		this.table = table;
		this.cols = cols;
	}
	
	public static TableColPack of(String _tbMain, String[] _cols) {
		return new TableColPack(true, "main", _tbMain, _cols);
	}
	
	public static TableColPack of(String _alias, String _tb, String[] _cols) {
		return new TableColPack(false, _alias, _tb, _cols);
	}

	// -------------------------------------------------------------------------------
	public boolean isMain() {
		return main;
	}

	public String getAlias() {
		return alias;
	}

	public String getTable() {
		return table;
	}

	public String[] getCols() {
		return cols;
	}
	
	// -------------------------------------------------------------------------------
	public String getNewCol(String _col) {
		return getAlias() + "_" + _col;
	}
}
