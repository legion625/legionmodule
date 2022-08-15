package legion.data.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.event.Level;

import legion.ObjectModel;
import legion.data.AbstractDao;
import legion.util.DataFO;
import legion.util.DatabaseFO;
import legion.util.LogUtil;

public  class AbstractMySqlDao extends AbstractDao {
	protected final static String COL_UID = "uid";
	protected final static String COL_OBJECT_CREATE_TIME = "object_create_time";
	protected final static String COL_OBJECT_UPDATE_TIME = "object_update_time";

	private String source;

	protected AbstractMySqlDao(String source) {
		this.source = source;
	}

	protected Connection getConn() {
		log.debug("source: {}", source);
		return DataFO.isEmptyString(source) ? null : (Connection) getDsManager().getConn(source);
	}

	// -------------------------------------------------------------------------------
	protected String parseUid(ResultSet _rs) throws SQLException {
		return _rs.getString(COL_UID);
	}

	protected long parseObjectCreateTime(ResultSet _rs) throws SQLException {
		return _rs.getLong(COL_OBJECT_CREATE_TIME);
	}

	protected long parseObjectUpdateTime(ResultSet _rs) throws SQLException {
		return _rs.getLong(COL_OBJECT_UPDATE_TIME);
	}
	
	// -------------------------------------------------------------------------------
	public static class DbColumn<T extends ObjectModel> {
		private String name;
		private ColType colType;
		private Function<T, Object> fnGetValue;

		public DbColumn(String name, ColType colType, Function<T, Object> fnGetValue) {
			this.name = name;
			this.colType = colType;
			this.fnGetValue = fnGetValue;
		}

		public static <T extends ObjectModel> DbColumn<T> of(String name, ColType colType, Function<T, Object> fnGetValue) {
			return new DbColumn<>(name, colType, fnGetValue);
		}
		
		// ---------------------------------------------------------------------------
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
		STRING, INT, LONG, FLOAT, BOOLEAN;
	}

	// -------------------------------------------------------------------------------
	protected final <T extends ObjectModel> boolean saveObject(String _table, DbColumn<T>[] _cols, T _obj) {
		log.debug("_obj.getUid(): {}", _obj.getUid());
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		try {
			// statement
			String qstr = "update " + _table;
			qstr += " set ";
			for (DbColumn<T> _col : _cols)
				qstr += _col.name + "=?,";
			qstr += COL_OBJECT_UPDATE_TIME + "=?";
//			qstr += " where " + COL_UID + "='" + _obj.getUid() + "'";
			qstr += " where "+COL_UID+"=?";
			log.debug("qstr: {}", qstr);
			pstmt = conn.prepareStatement(qstr);
			int colIndex = 1;
			for (DbColumn<T> _col : _cols) {
				_col.configPstmt(pstmt, colIndex++, _obj);
			}
			pstmt.setLong(colIndex++, System.currentTimeMillis());
			
			pstmt.setString(colIndex++, _obj.getUid());

			if (pstmt.executeUpdate() == 1)
				return true;
			else
				return createObject(_table, _cols, _obj);
		} catch (SQLException e) {
			LogUtil.log(log, e, Level.ERROR);
			return false;
		} finally {
			close(conn, pstmt, null);
		}
	}

	private final <T extends ObjectModel> boolean createObject(String _table, DbColumn<T>[] _cols, T _obj) {
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

			log.debug("qstr: {}", qstr);
			
			pstmt = conn.prepareStatement(qstr);
			int colIndex = 1;
			pstmt.setString(colIndex++, _obj.getUid());
			for (DbColumn<T> _col : _cols) {
				_col.configPstmt(pstmt, colIndex++, _obj);
			}
			pstmt.setLong(colIndex++, System.currentTimeMillis());
			pstmt.setLong(colIndex++, System.currentTimeMillis());
			return pstmt.executeUpdate() == 1;
		} catch (SQLException e) {
			LogUtil.log(log, e, Level.ERROR);
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
			LogUtil.log(log, e, Level.ERROR);
		} finally {
			close(conn, pstmt, null);
		}
		return false;
	}

	// -------------------------------------------------------------------------------
	protected final <T extends ObjectModel> T loadObject(String _table, String _uid,
			Function<ResultSet, T> _fnParseObj) {
		return loadObject(_table, COL_UID, _uid, _fnParseObj);
	}

	protected final <T extends ObjectModel> T loadObject(String _table, String _col, String _value,
			Function<ResultSet, T> _fnParseObj) {
		Map<String, String> keyValueMap = new HashMap<>();
		keyValueMap.put(_col, _value);
		return loadObject(_table, keyValueMap, _fnParseObj);
	}

	protected final <T extends ObjectModel> T loadObject(String _table, Map<String, String> _keyValueMap,
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
			LogUtil.log(log, e, Level.ERROR);
			return null;
		} finally {
			close(conn, pstmt, rs);
		}
		return obj;
	}

	// -------------------------------------------------------------------------------
	protected final <T extends ObjectModel> List<T> loadObjectList(String _table, Function<ResultSet, T> _fnParseObj) {
		return loadObjectList(_table, null, null, _fnParseObj);
	}

	protected final <T extends ObjectModel> List<T> loadObjectList(String _table, String _col, String _key,
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
			LogUtil.log(log, e, Level.ERROR);
			return null;
		} finally {
			close(conn, pstmt, rs);
		}
		return list;
	}

	protected final void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
		} catch (SQLException e) {
			LogUtil.log(log, e, Level.ERROR);
		}
	}
}
