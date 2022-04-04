package legion.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import legion.data.search.SearchCondition;
import legion.data.search.SearchOperation;
import legion.data.search.SearchParam;
import legion.kernel.LegionObject;
import legion.util.DataFO;
import legion.util.DatabaseFO;

public abstract class MySqlDao extends Dao {
	protected final static String COL_UID = "uid";
	protected final static String COL_OBJECT_CREATE_TIME = "object_create_time";
	protected final static String COL_OBJECT_UPDATE_TIME = "object_update_time";

	// private final static String IP = "localhost";
	// private final static String SCHEMA_NAME = "lab";
	// private final static String USER = "root";
	// private final static String PASSWORD = "1234";

	protected MySqlDataSource ds;

	protected MySqlDao(MySqlDataSource _ds) {
		log.debug("Start::MySqlDao... _ds: {}", _ds);
		ds = _ds;
	}

	protected Connection getConn() {
		// 1.加載JDBC驅動
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("加載JDBC驅動成功");
			// Log.d("", "加載JDBC驅動成功");
		} catch (ClassNotFoundException e) {
			System.out.println("加載JDBC驅動失敗");
			// Log.e("", "加載JDBC驅動失敗");
			return null;
		}

		// 2.設置好IP/端口/數據庫名/用戶名/密碼等必要的連接信息
		// MySqlDataSource ds = MySqlDataSource.getDataSource();
		String url = "jdbc:mysql://" + ds.getIp() + "/" + ds.getSchema()
				+ "?useUnicode=true&characterEncoding=Utf-8&useSSL=false"; // 構建連接mysql的字符串

		// 3.連接JDBC
		int i = 1;
		while (i <= 5) { // retry at most 5 times
			try {
				Connection conn = DriverManager.getConnection(url, ds.getUser(), ds.getPassword());
				System.out.println("遠程連接成功!");
				// Log.i("", "遠程連接成功!");
				return conn;
			} catch (SQLException e) {
				// Log.e("", "遠程連接失敗!");
				System.out.println("遠程連接失敗!");
				try {
					Thread.sleep(5000); // 每隔5秒嘗試連接
					i++;
					// Log.e("", "try to reconnect");
				} catch (InterruptedException e1) {
					// Log.e("", e1.toString());
				}
			}
		}
		return null;
	}

	// -------------------------------------------------------------------------------
	public class DbColumn<T extends LegionObject> {
		private String name;
		private ColType colType;
		private Function<T, Object> fnGetValue;

		public DbColumn(String name, ColType colType, Function<T, Object> fnGetValue) {
			this.name = name;
			this.colType = colType;
			this.fnGetValue = fnGetValue;
		}

		private void configPstmt(PreparedStatement pstmt, int _colIndex, T _obj) throws SQLException {
			switch (colType) {
			case STRING:
				pstmt.setString(_colIndex, (String) fnGetValue.apply(_obj));
				break;
			case INT:
				pstmt.setInt(_colIndex, (int) fnGetValue.apply(_obj));
				break;
			case LONG:
				pstmt.setLong(_colIndex, (long) fnGetValue.apply(_obj));
				break;
			case FLOAT:
				pstmt.setFloat(_colIndex, (float) fnGetValue.apply(_obj));
				break;
			case BOOLEAN:
				pstmt.setBoolean(_colIndex, (boolean) fnGetValue.apply(_obj));
				break;
			}
		}

	}

	public enum ColType {
		STRING, INT, LONG, FLOAT,BOOLEAN;
	}

	// -------------------------------------------------------------------------------
	protected final <T extends LegionObject> boolean saveObject(String _table, DbColumn<T>[] _cols, T _obj) {
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		try {
			// statement
			String qstr = "update " + _table;
			qstr += " set ";
			for (DbColumn<T> _col : _cols)
				qstr += _col.name + "=?,";
			qstr += COL_OBJECT_UPDATE_TIME + "=?";
			qstr += " where " + COL_UID + "='" + _obj.getUid() + "'";
			pstmt = conn.prepareStatement(qstr);
			int colIndex = 1;
			for (DbColumn<T> _col : _cols) {
				_col.configPstmt(pstmt, colIndex++, _obj);
			}
			pstmt.setString(colIndex++, DatabaseFO.toDbString(LocalDateTime.now()));

			if (pstmt.executeUpdate() == 1)
				return true;
			else
				return createObject(_table, _cols, _obj);
		} catch (SQLException e) {
			System.out.println("DB linking failed!");
			e.printStackTrace();
			return false;
		} finally {
			close(conn, pstmt, null);
		}
	}

	private final <T extends LegionObject> boolean createObject(String _table, DbColumn<T>[] _cols, T _obj) {
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		try {
			// statement
			String qstr = "insert into " + _table + " (";
			qstr += COL_UID;
			for (DbColumn<T> _col : _cols)
				qstr += "," + _col.name;
			qstr += "," + COL_OBJECT_CREATE_TIME + "," + COL_OBJECT_UPDATE_TIME;
			qstr += ")";
			qstr += " values(?";
			for (int i = 0; i < _cols.length; i++)
				qstr += ",?";
			qstr += ",?,?)";

			pstmt = conn.prepareStatement(qstr);
			int colIndex = 1;
			pstmt.setString(colIndex++, _obj.getUid());
			for (DbColumn<T> _col : _cols) {
				_col.configPstmt(pstmt, colIndex++, _obj);
			}
			pstmt.setString(colIndex++, DatabaseFO.toDbString(LocalDateTime.now()));
			pstmt.setString(colIndex++, DatabaseFO.toDbString(LocalDateTime.now()));
			return pstmt.executeUpdate() == 1;
		} catch (SQLException e) {
			System.out.println("DB linking failed!");
			e.printStackTrace();
		} finally {
			close(conn, pstmt, null);
		}
		return false;
	}

	// -------------------------------------------------------------------------------
	protected final boolean deleteObject(String _table, String _uid) {
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		try {
			// 連結資料庫
			// statement
			String qstr = "delete from " + _table + " where " + COL_UID + "='" + _uid + "'";
			pstmt = conn.prepareStatement(qstr);
			System.out.println("pstmt.executeUpdate(): " + pstmt.executeUpdate());
			return true;
		} catch (SQLException e) {
			System.out.println("DB linking failed!");
			e.printStackTrace();
		} finally {
			close(conn, pstmt, null);
		}
		return false;
	}
	
	// -------------------------------------------------------------------------------
	protected final <T extends LegionObject> T loadObject(String _table, String _uid,
			Function<ResultSet, T> _fnParseObj) {
//		if (DataFO.isEmptyString(_uid))
//			return null;
//
//		Connection conn = getConn();
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		T obj = null;
//		try {
//			// statement
//			String qstr = "select * from " + _table + " where " + COL_UID + " = '" + _uid + "'";
//			pstmt = conn.prepareStatement(qstr);
//			rs = pstmt.executeQuery();
//			if (rs.next()) {
//				obj = _fnParseObj.apply(rs);
//			}
//		} catch (SQLException e) {
//			System.out.println("DB linking failed!");
//			e.printStackTrace();
//			return null;
//		} finally {
//			close(conn, pstmt, rs);
//		}
//		return obj;
		return loadObject(_table, COL_UID, _uid, _fnParseObj);
	}
	
	
	protected final <T extends LegionObject> T loadObject(String _table, String _col, String _value,
			Function<ResultSet, T> _fnParseObj) {
//		if (DataFO.isEmptyString(_col) || DataFO.isEmptyString(_value))
//			return null;
//
//		Connection conn = getConn();
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		T obj = null;
//		try {
//			// statement
//			String qstr = "select * from " + _table + " where " + _col + " = '" + _value + "'";
//			pstmt = conn.prepareStatement(qstr);
//			rs = pstmt.executeQuery();
//			if (rs.next()) {
//				obj = _fnParseObj.apply(rs);
//			}
//		} catch (SQLException e) {
//			System.out.println("DB linking failed!");
//			e.printStackTrace();
//			return null;
//		} finally {
//			close(conn, pstmt, rs);
//		}
//		return obj;
		Map<String, String> keyValueMap = new HashMap<>();
		keyValueMap.put(_col, _value);
		return loadObject(_table, keyValueMap, _fnParseObj);
	}

	protected final <T extends LegionObject> T loadObject(String _table, Map<String, String> _keyValueMap,
			Function<ResultSet, T> _fnParseObj) {
		// if (DataFO.isEmptyString(_col) || DataFO.isEmptyString(_value))
		// return null;

		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		T obj = null;
		try {
			// statement
			StringBuilder sb = new StringBuilder();
			sb.append("select * from ").append(_table);
			sb.append(" where ").append(COL_UID).append(" is not null");
			if (_keyValueMap != null) {
				for (String _key : _keyValueMap.keySet()) {
					sb.append(" and ").append(_key).append("='").append(_keyValueMap.get(_key)).append("'");
				}
			}

			pstmt = conn.prepareStatement(sb.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				obj = _fnParseObj.apply(rs);
			}
		} catch (SQLException e) {
			System.out.println("DB linking failed!");
			e.printStackTrace();
			return null;
		} finally {
			close(conn, pstmt, rs);
		}
		return obj;
	}
	
	// -------------------------------------------------------------------------------
	protected final <T extends LegionObject> List<T> loadObjectList(String _table, Function<ResultSet, T> _fnParseObj) {
		return loadObjectList(_table, null, null, _fnParseObj);
	}
	
	protected final <T extends LegionObject> List<T> loadObjectList(String _table, String _col, String _key,
			Function<ResultSet, T> _fnParseObj) {
		List<T> list = new ArrayList<>();

		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// statement
			StringBuilder sb = new StringBuilder().append("select * from ").append(_table);
			if (!DataFO.isEmptyString(_col) && !DataFO.isEmptyString(_key))
				sb.append(" where ").append(_col).append("='").append(_key).append("'");
			String qstr = sb.toString();
			pstmt = conn.prepareStatement(qstr);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(_fnParseObj.apply(rs));
			}
		} catch (SQLException e) {
			System.out.println("DB linking failed!");
			e.printStackTrace();
			return null;
		} finally {
			close(conn, pstmt, rs);
		}
		return list;
	}
	
	// -------------------------------------------------------------------------------
	protected final <T extends SearchParam, U> SearchOperation<T, U> searchObject(String _table,
			SearchOperation<T, U> _param, Function<T, String> _fnParseColMapping, Function<ResultSet, U> _fnParseObj) {
		List<U> list = new ArrayList<>();

		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// statement
			String qstr = "select * from " + _table + " where " + COL_UID + " is not null";
			if (_param != null) {
				for (SearchCondition<T> c : _param.getConditions()) {
					qstr += " and " + _fnParseColMapping.apply(c.getParam()) + " " + c.getCompareOp().getOper() + " '"
							+ c.getValue() + "'";
				}
			}
			pstmt = conn.prepareStatement(qstr);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(_fnParseObj.apply(rs));
			}
			_param.setResultList(list);
		} catch (SQLException e) {
			System.out.println("DB linking failed!");
			e.printStackTrace();
			return null;
		} finally {
			close(conn, pstmt, rs);
		}
		return _param;
	}

}
