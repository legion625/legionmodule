package legion.datasource.manager;

import java.sql.Connection;

public abstract class TransactionConnection {

	public static TransactionConnection newInstance(Object _conn, Transaction _t) throws Exception {
		if (_conn instanceof Connection)
			return new TransactionDbConnection((Connection)_conn, _t);
		else
			return null;
	}
	
	// -------------------------------------------------------------------------------
	/**
	 * 交易確認
	 * 
	 * @throws Exception
	 */
	public abstract void commitAll() throws Exception;

	/**
	 * 釋放連線
	 * 
	 * @throws Exception
	 */
	public abstract void release() throws Exception;

	/**
	 * 交易失敗，回復運作內容
	 * 
	 * @throws Exception
	 */
	public abstract void rollbackAll() throws Exception;
}
