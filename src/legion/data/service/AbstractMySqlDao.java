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
import java.util.stream.Collectors;

import org.slf4j.event.Level;

import legion.ObjectModel;
import legion.ObjectSkewer;
import legion.data.AbstractDao;
import legion.data.skewer.TableColPack;
import legion.data.skewer.TableRel;
import legion.util.DataFO;
import legion.util.DatabaseFO;
import legion.util.LogUtil;
import legion.util.query.QueryOperation;
import legion.util.query.QueryOperation.ConjunctiveOp;
import legion.util.query.QueryOperation.QueryValue;
import legion.util.query.QueryParam;

public class AbstractMySqlDao extends AbstractDao {
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
		private int strDataLength; // 檢查字串長度限制

		public DbColumn(String name, ColType colType, Function<T, Object> fnGetValue, int strDataLength) {
			this.name = name;
			this.colType = colType;
			this.fnGetValue = fnGetValue;
			this.strDataLength = strDataLength;
		}

		public static <T extends ObjectModel> DbColumn<T> of(String name, ColType colType,
				Function<T, Object> fnGetValue) {
			return of(name, colType, fnGetValue, 0);
		}

		public static <T extends ObjectModel> DbColumn<T> of(String name, ColType colType,
				Function<T, Object> fnGetValue, int strDataLength) {
			return new DbColumn<>(name, colType, fnGetValue, strDataLength);
		}

		// ---------------------------------------------------------------------------
		private void configPstmt(PreparedStatement pstmt, int _colIndex, T _obj) throws SQLException {
			switch (colType) {
			case STRING:
				String str = (String) fnGetValue.apply(_obj);
				if (str != null && strDataLength > 0 && str.length() > strDataLength)
					str = str.substring(0, strDataLength);
				pstmt.setString(_colIndex, str);
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
			case DOUBLE:
				pstmt.setDouble(_colIndex, (double) fnGetValue.apply(_obj));
				break;
			case BOOLEAN:
				pstmt.setBoolean(_colIndex, (boolean) fnGetValue.apply(_obj));
				break;
			}
		}
	}

	public enum ColType {
		STRING, INT, LONG, FLOAT, DOUBLE, BOOLEAN;
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
			qstr += " where " + COL_UID + "=?";
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

	protected <T extends ObjectModel> boolean updateOrInsertObj(String _table, DbColumn<T>[] _cols,
			DbColumn<T>[] _updateCols, T _obj) {
		if (_obj == null || DataFO.isEmptyString(_obj.getUid())) {
			log.error("Obj null or uid null.");
			return false;
		}
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("insert into ").append(_table);
			sb.append("(").append(COL_UID);
			for (DbColumn<T> _col : _cols)
				sb.append(",").append(_col.name);

			sb.append(",").append(COL_OBJECT_CREATE_TIME).append(",").append(COL_OBJECT_UPDATE_TIME);
			sb.append(") ");

			sb.append("values(?");
			for (int i = 0; i < _cols.length; i++)
				sb.append(",?");
			sb.append(",?,?)");

			// on duplicate key
			sb.append(" on duplicate key update ");
			boolean b = false;
			for (DbColumn<T> _col : _updateCols) {
				if (b == false)
					b = true;
				else
					sb.append(" , ");
				sb.append(_col.name).append(" =? ");
			}
			sb.append(" , ").append(COL_OBJECT_UPDATE_TIME).append(" =? ");

			//
			String sql = sb.toString();

			pstmt = conn.prepareStatement(sql);
			int colIndex = 1;
			pstmt.setString(colIndex++, _obj.getUid());
			for (DbColumn<T> _col : _cols)
				_col.configPstmt(pstmt, colIndex++, _obj);
			pstmt.setLong(colIndex++, System.currentTimeMillis());
			pstmt.setLong(colIndex++, System.currentTimeMillis());

			// on duplicate key update
			for (DbColumn<T> _col : _updateCols)
				_col.configPstmt(pstmt, colIndex++, _obj);
			pstmt.setLong(colIndex++, System.currentTimeMillis());

			//
			int u = pstmt.executeUpdate();
			boolean r = u > 0;
			if (r)
				return true;
			else {
				log.error("Save return false. pstmt.executeUpdate(): {}", u);
				// TODO print property attributes.
				return false;
			}
		} catch (Throwable e) {
			LogUtil.log(log, e, Level.ERROR);
			return false;
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception _e) {
				LogUtil.log(log, _e, Level.ERROR);
			}
		}
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
		if (DataFO.isEmptyString(_col) || DataFO.isEmptyString(_value))
			return null;
		Map<String, String> colValueMap = new HashMap<>();
		colValueMap.put(_col, _value);
		return loadObject(_table, colValueMap, _fnParseObj);
	}

	protected final <T extends ObjectModel> T loadObject(String _table, Map<String, String> _colValueMap,
			Function<ResultSet, T> _fnParseObj) {
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		T obj = null;
		try {
			// statement
			StringBuilder sb = new StringBuilder();
			sb.append("select * from ").append(_table);
			sb.append(" where ").append(COL_UID).append(" is not null");
			if (_colValueMap != null) {
				for (String _key : _colValueMap.keySet()) {
					sb.append(" and ").append(_key).append("='").append(_colValueMap.get(_key)).append("'");
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

	protected final <T extends ObjectModel> List<T> batchLoadObjects(String _table, List<String> _uidList,
			Function<ResultSet, T> _fnParseObj) {
		return _uidList.parallelStream().map(_uid -> loadObject(_table, _uid, _fnParseObj))
				.collect(Collectors.toList());
	}

	// -------------------------------------------------------------------------------
	protected final <T extends ObjectModel> List<T> loadObjectList(String _table, Function<ResultSet, T> _fnParseObj) {
		return loadObjectList(_table, null, null, _fnParseObj);
	}

	protected final <T extends ObjectModel> List<T> loadObjectList(String _table, String _col, String _value,
			Function<ResultSet, T> _fnParseObj) {
		Map<String, String> colValueMap = new HashMap<>();
		if (!DataFO.isEmptyString(_col) && _value != null)
			colValueMap.put(_col, _value);
		return loadObjectList(_table, colValueMap, _fnParseObj);
	}

	protected final <T extends ObjectModel> List<T> loadObjectList(String _table, Map<String, String> _colValueMap,
			Function<ResultSet, T> _fnParseObj) {
		log.debug("loadObjectList::start");
		List<T> list = new ArrayList<>();

		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// statement
			StringBuilder sb = new StringBuilder().append("select * from ").append(_table);
			sb.append(" where ").append("1=1");
			if (_colValueMap != null)
				for (String _col : _colValueMap.keySet())
					sb.append(" and ").append(_col).append("='").append(_colValueMap.get(_col)).append("'");

			String qstr = sb.toString();
			log.debug("qstr: {}", qstr);
			pstmt = conn.prepareStatement(qstr);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(_fnParseObj.apply(rs));
			}
			log.debug("list.size(): {}", list.size());
		} catch (SQLException e) {
			LogUtil.log(log, e, Level.ERROR);
			return null;
		} finally {
			close(conn, pstmt, rs);
		}
		return list;
	}

	// -------------------------------------------------------------------------------
	// --------------------------------QueryOperation---------------------------------
	protected <Q extends QueryParam, T> QueryOperation<Q, T> searchObject(String _table, QueryOperation<Q, T> _param,
			Function<Q, String> _queryParamMappingParser, Function<ResultSet, T> _fnParseObj) {
		Function<QueryValue<Q, ?>, String> sqlParser = t -> _queryParamMappingParser.apply(t.getCondition()) + " "
				+ t.getCompareOp() + " ? ";
		return searchObject(_table, _param, sqlParser, _queryParamMappingParser, _fnParseObj);
	}

	protected <Q extends QueryParam, T> QueryOperation<Q, T> searchObject(String _table, QueryOperation<Q, T> _param,
			Function<QueryValue<Q, ?>, String> _sqlParser, Function<Q, String> _queryParamMappingParser,
			Function<ResultSet, T> _fnParseObj) {
		return searchObject(_table, COL_UID, _param, _sqlParser, _queryParamMappingParser, _fnParseObj);
	}

	protected <Q extends QueryParam, T> QueryOperation<Q, T> searchObject(String _table, String _nonNullCol,
			QueryOperation<Q, T> _param, Function<QueryValue<Q, ?>, String> _sqlParser,
			Function<Q, String> _queryParamMappingParser, Function<ResultSet, T> _fnParseObj) {
		List<T> resultList = new ArrayList<>();

		if (_param == null)
			return null;

		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			ArrayList<Object> datas = new ArrayList<>();
			// joinTable/固定條件
			String wstr = "where " + _nonNullCol + " is not null";
			// 使用者定義的查詢條件
			wstr = _param.combineConditions(datas, wstr, _sqlParser, _queryParamMappingParser);
			// 主要查詢SQL
			StringBuilder sb = new StringBuilder();
			sb.append("select * from ").append(_table).append(" ").append(wstr);

			// sort
			String sort = _param.combineSorts(_queryParamMappingParser);
			if (!DataFO.isEmptyString(sort))
				sb.append(" order by ").append(sort);

			// limit
			int[] limit = _param.getLimit();
			boolean appendLimit = false;
			if (limit[0] >= 0 && limit[1] >= 0) {
				sb.append(" limit ").append(limit[0]).append(",").append(limit[1]);
				appendLimit = true;
			}

			String sql = sb.toString();

			log.debug("sql: {}", sql);
			pstmt = conn.prepareStatement(sql);
			int i = 1;
			for (Object data : datas)
				pstmt.setObject(i++, data);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				resultList.add(_fnParseObj.apply(rs));
			}
			_param.setQueryResult(resultList);

			rs.close();
			rs = null;
			pstmt.close();
			pstmt = null;

			/* set total */
			if (appendLimit) {
				StringBuilder sbTotal = new StringBuilder();
				sbTotal.append("select count(*) from ").append(_table).append(" ").append(wstr);
				// 查詢總數
				pstmt = conn.prepareStatement(sbTotal.toString());
				i = 1;
				for (Object data : datas)
					pstmt.setObject(i++, data);
				rs = pstmt.executeQuery();
				if (rs.next())
					_param.setTotal(rs.getInt(1));
				else
					_param.setTotal(0);
			} else
				_param.setTotal(resultList.size());
		} catch (Throwable e) {
			LogUtil.log(log, e, Level.ERROR);
			return null;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Throwable _e) {
				LogUtil.log(log, _e, Level.ERROR);
			}
		}
		return _param;
	}

	protected static String packMasterQueryField(String _targetMasterField, String _masterTable, String _masterKey,
			String _detailTable, String _detailMasterKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("select ").append(_targetMasterField).append(" from ").append(_masterTable);
		sb.append(" where ").append(_masterKey).append(" = ").append(_detailTable).append(".").append(_detailMasterKey);
		sb.append(")");
		return sb.toString();
	}

	protected static String packDetailQueryField(String _targetDetailField, String _masterTable, String _masterKey,
			String _detailTable, String _detailMasterKey) {
		return packDetailQueryField(_targetDetailField, _masterTable, _masterKey, _detailTable, _detailMasterKey, null);
	}

	protected static String packDetailQueryField(String _targetDetailField, String _masterTable, String _masterKey,
			String _detailTable, String _detailMasterKey, Map<String, String> _fixConditionMap) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("select group_concat( distinct( ").append(_targetDetailField).append(" )) from ")
				.append(_detailTable);
		sb.append(" where ").append(_detailMasterKey).append(" = ").append(_masterTable).append(".").append(_masterKey);
		if (_fixConditionMap != null)
			for (String _key : _fixConditionMap.keySet())
				sb.append(" and ").append(_key).append(" = ").append(_fixConditionMap.get(_key));
		sb.append(")");
		return sb.toString();
	}

	protected static String packConjQueryField(String _targetField2, String _table1, String _table2, String _conjTable,
			String _conjUid1, String _conjUid2) {
		return packConjQueryField(_targetField2, _table1, _table2, _conjTable, _conjUid1, _conjUid2, null);
	}

	protected static String packConjQueryField(String _targetField2, String _table1, String _table2, String _conjTable,
			String _conjUid1, String _conjUid2, Map<String, String> _conjFixConditionMap) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("select group_concat( distinct( ").append(_targetField2).append(" )) from ").append(_table2);
		sb.append(" where ").append(COL_UID).append(" in (");
		sb.append("select ").append(_conjUid2).append(" from ").append(_conjTable).append(" where ").append(_conjUid1)
				.append(" = ").append(_table1).append(".").append(COL_UID);
		/* ConjFixConditionMap */
		if (_conjFixConditionMap != null)
			for (String _key : _conjFixConditionMap.keySet()) {
				sb.append(" and ").append(_key).append(" = ").append(_conjFixConditionMap.get(_key));
			}
		sb.append(")");
		return sb.toString();
	}

	private static <Q extends QueryParam, T> String combineQueryValue(Function<Q, String> _queryParamMappingParser,
			QueryValue<Q, T>[] _qvs, ConjunctiveOp _qvsConjunctiveOp) {
		Function<QueryValue<Q, ?>, String> sqlParser = t -> {
			StringBuilder sqlSb = new StringBuilder();
			sqlSb.append(_queryParamMappingParser.apply(t.getCondition()));
			Object v = t.getValue().orElse(null);
			if (v == null)
				sqlSb.append(" is null");
			else {
				sqlSb.append(" ").append(t.getCompareOp()).append(" ");
				if (v.getClass() == String.class) {
					// TODO sqlSb.append("'").append( XssUtil.stripSqlInjection(
					// (String)v)).append("'");
					sqlSb.append("'").append((String) v).append("'");
				} else
					sqlSb.append(v);
			}
			return sqlSb.toString();
		};
		StringBuilder sb = new StringBuilder();
		boolean b = false;
		for (QueryValue<Q, T> _qv : _qvs) {
			if (b)
//				sb.append(" and ");
				sb.append(" ").append(_qvsConjunctiveOp.getId()).append(" ");
			else
				b = true;
			sb.append(sqlParser.apply(_qv));
		}
		return sb.toString();
	}

	protected static <Q extends QueryParam, T, P extends QueryParam> String packExistsField(String _tb1,
			String _keyCol1, String _tb2, String _keyCol2, Function<Q, String> _queryParamMappingParser,
			Map<P, QueryValue[]> _inSelectQueryValueMap, P _p) {
		return packExistsField(_tb1, _keyCol1, _tb2, _keyCol2, _queryParamMappingParser, _inSelectQueryValueMap,
				ConjunctiveOp.and, _p);
	}

	protected static <Q extends QueryParam, T, P extends QueryParam> String packExistsField(String _tb1,
			String _keyCol1, String _tb2, String _keyCol2, Function<Q, String> _queryParamMappingParser,
			Map<P, QueryValue[]> _inSelectQueryValueMap, ConjunctiveOp _qvsConjunctiveOp, P _p) {
		String conditionSql = "";
		if (_inSelectQueryValueMap != null) {
			QueryValue[] _queryValues = _inSelectQueryValueMap.get(_p);
			conditionSql = _queryValues == null ? ""
					: combineQueryValue(_queryParamMappingParser, _queryValues, _qvsConjunctiveOp);
		}
		return packExistsField(_tb1, _keyCol1, _tb2, _keyCol2, conditionSql);
	}

	protected static <Q extends QueryParam, T, P extends QueryParam> String packExistsField(String _tb1,
			String _keyCol1, String _tb2, String _keyCol2, String _conditionSql) {
		return packExistsField(_tb1, _keyCol1, _tb2, _keyCol2, _conditionSql, true);
	}

	protected static <Q extends QueryParam, T, P extends QueryParam> String packExistsField(String _tb1,
			String _keyCol1, String _tb2, String _keyCol2, String _conditionSql, boolean _equalTrue) {
		StringBuilder sb = new StringBuilder();
		sb.append(" exists (select 1 from ").append(_tb2);
		sb.append(" where ").append(_keyCol2).append(" = ").append(_tb1).append(".").append(_keyCol1);
		if (!DataFO.isEmptyString(_conditionSql)) {
			sb.append(" and ").append(_conditionSql);
			if (_equalTrue)
				sb.append(" = true");
			else
				sb.append(" = false");
		}
		sb.append(")");
		return sb.toString();
	}

	protected static String packYearField(String _colTimeL) {
		StringBuilder sb = new StringBuilder();
		sb.append("year(").append("from_unixtime(").append(_colTimeL).append("/1000)").append(")");
		return sb.toString();
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------Skewer-------------------------------------
	protected <T extends ObjectSkewer> T loadSkewer(TableColPack[] _tableColPacks, TableRel[] _tableRels, String _col,
			String _value, Function<ResultSet, T> _fnParseObj) {
		String sql = packLoadSkewerSql(_tableColPacks, _tableRels, _col, _value);
		return loadSkewerBySql(sql, _fnParseObj);
	}

	protected <T extends ObjectSkewer> T loadSkewerBySql(String _sql, Function<ResultSet, T> _fnParseObj) {
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		T t = null;
		try {
			String sql = _sql;
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next())
				t = _fnParseObj.apply(rs);
		} catch (Throwable e) {
			LogUtil.log(log, e, Level.ERROR);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Throwable _e) {
				LogUtil.log(log, _e, Level.ERROR);
			}
		}
		return t;
	}

	protected <T extends ObjectSkewer> List<T> loadSkewerList(TableColPack[] _tableColPacks, TableRel[] _tableRels,
			String _col, String _value, Function<ResultSet, T> _fnParseObj) {
		String sql = packLoadSkewerSql(_tableColPacks, _tableRels, _col, _value);
		return loadSkewerListBySql(sql, _fnParseObj);
	}

	protected <T extends ObjectSkewer> List<T> loadSkewerListBySql(String _sql, Function<ResultSet, T> _fnParseObj) {
		List<T> list = new ArrayList<>();
		if (DataFO.isEmptyString(_sql))
			return list;

		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = _sql;
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(_fnParseObj.apply(rs));
		} catch (Throwable e) {
			LogUtil.log(log, e, Level.ERROR);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Throwable _e) {
				LogUtil.log(log, _e, Level.ERROR);
			}
		}
		return list;
	}

	
	protected <Q extends QueryParam, T extends ObjectSkewer> QueryOperation<Q, T> searchSkewer(
			TableColPack[] _tableColPacks, TableRel[] _tableRels,
			QueryOperation<Q, T> _param, Function<Q, String> _queryParamMappingParser,
			Function<ResultSet, T> _fnParseObj) {
		Function<QueryValue<Q,?>, String>  sqlParser = t->_queryParamMappingParser.apply(t.getCondition())+" " +t.getCompareOp()+" ? ";
		return searchSkewer(_tableColPacks, _tableRels, _param, sqlParser, _queryParamMappingParser, _fnParseObj);
	}
	
	protected <Q extends QueryParam, T extends ObjectSkewer> QueryOperation<Q, T> searchSkewer(
			TableColPack[] _tableColPacks, TableRel[] _tableRels,
			QueryOperation<Q, T> _param, Function<QueryValue<Q,?>, String> _sqlParser , Function<Q, String> _queryParamMappingParser,
			Function<ResultSet, T> _fnParseObj){
		List<T> resultList = new ArrayList<>();
		if(_param==null)
			return null;
		
		// TableColPacks
		if(_tableColPacks==null || _tableColPacks.length<=1)
			return null;
		int mainCount = 0;
		for(TableColPack _tcp: _tableColPacks)
			if(_tcp.isMain())
				mainCount++;
		if(mainCount!=1) {
			log.error("mainCount[{}] error.", mainCount);
			return null;
		}
		
		// TableRels
		if(_tableRels==null || _tableRels.length<=0)
			return null;
		
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			ArrayList<Object> datas = new ArrayList<>();
			String qstr = packSearchSkewerSql(datas, _tableColPacks, _tableRels, _param, _sqlParser, _queryParamMappingParser);
			StringBuilder sb = new StringBuilder(qstr);
			
			// sort
			String sort = _param.combineSorts(_queryParamMappingParser);
			if(!DataFO.isEmptyString(sort))
				sb.append(" order by ").append(sort);
			
			// limit
			int[] limit = _param.getLimit();
			boolean appendLimit = false;
			if(limit[0]>=0 && limit[1] >=0) {
				sb.append(" limit ").append(limit[0]).append(",").append(limit[1]);
				appendLimit = true;
			}
			
			qstr = sb.toString();
			log.debug("sql: {}", qstr);
			
			pstmt = conn.prepareStatement(qstr);
			int i=1;
			for(Object data: datas)
				pstmt.setObject(i++,data);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				resultList.add(_fnParseObj.apply(rs));
			}
			_param.setQueryResult(resultList);
			
			rs.close();
			rs = null;
			pstmt.close();
			pstmt = null;
			
			/* set total */
			if(appendLimit) {
				StringBuilder sbTotal = new StringBuilder();
				sbTotal.append(packSearchSkewerCountSql(datas, _tableColPacks, _tableRels, _param, _sqlParser, _queryParamMappingParser));
				// 查詢總數
				pstmt =conn.prepareStatement(sbTotal.toString());
				
				i=1;
				for(Object data:datas) {
					log.debug("{}\t{}", i, data);
					pstmt.setObject(i++, data);
				}
				
				rs = pstmt.executeQuery();
				if(rs.next())
					_param.setTotal(rs.getInt(1));
				else
					_param.setTotal(0);
			}else
				_param.setTotal(resultList.size());
		}catch (Throwable e) {
			LogUtil.log(log, e, Level.ERROR);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Throwable _e) {
				LogUtil.log(log, _e, Level.ERROR);
			}
		}
		return _param;
	}
	
	// -------------------------------------------------------------------------------
	protected final String packLoadSkewerSql(TableColPack[] _tableColPacks, TableRel[] _tableRels) {
		try {
			return packSearchSkewerSql(null, _tableColPacks, _tableRels, null, null, null);
		} catch (Throwable e) {
			LogUtil.log(log, e, Level.ERROR);
			return null;
		}
	}
	
	private String packLoadSkewerSql(TableColPack[] _tableColPacks, TableRel[] _tableRels, String _col, String _value) {
		String sql = packLoadSkewerSql(_tableColPacks, _tableRels);
		StringBuilder sb = new StringBuilder(sql);
		sb.append(" and ").append(_col).append(" = ").append(_value).append("'");
		sql = sb.toString();
		return sql;
	}

	private <Q extends QueryParam, T extends ObjectSkewer> String packSearchSkewerSql(ArrayList<Object> _datas, TableColPack[] _tableColPacks, TableRel[] _tableRels,
			QueryOperation<Q, T> _param, Function<QueryValue<Q,?>, String> _sqlParser, Function<Q, String> _queryParamMappingParser
			) throws Exception{
		// joinTable/固定條件
		String wstr = "where";
		String relStr = "";
		for (TableRel rel : _tableRels) {
			if (!DataFO.isEmptyString(relStr))
				relStr += " and";
			relStr+=" " +rel.getTcp1().getAlias()+"."+rel.getCol1()+" = "+rel.getTcp2().getAlias()+"."+rel.getCol2();
		}
		wstr +=relStr;
		
		// 使用者定義的查詢條件
		if(_datas !=null && _param !=null && _sqlParser!=null && _queryParamMappingParser!=null)
			wstr = _param.combineConditions(_datas, wstr, _sqlParser, _queryParamMappingParser);
		
		// 主要查詢SQL
		String qstr = "select ";
		int i;
		/* col */
		for(TableColPack tcp: _tableColPacks) {
			if(tcp.isMain())
				qstr += tcp.getAlias() + ".*, ";
			else {
				for (String col : tcp.getCols())
					qstr += tcp.getAlias() + "." + col + " as " + tcp.getNewCol(col) + ", ";
			}
		}
		i = qstr.lastIndexOf(',');
		if(i>0)
			qstr = qstr.substring(0,i);
		qstr += "from";
		
		/* table */
		for(TableColPack tcp: _tableColPacks)
			qstr +=" " +tcp.getTable()+" "+tcp.getAlias()+",";
		i = qstr.lastIndexOf(',');
		if(i>0)
			qstr = qstr.substring(0,i);
		
		qstr+= " "+wstr;
		
		return qstr;
	}
	
	private <Q extends QueryParam, T extends ObjectSkewer> String packSearchSkewerCountSql(ArrayList<Object> _datas, TableColPack[] _tableColPacks, TableRel[] _tableRels,
			QueryOperation<Q, T> _param, Function<QueryValue<Q,?>, String> _sqlParser, Function<Q, String> _queryParamMappingParser){
		// joinTable/固定條件
		String wstr = "where";
		String relStr = "";
		for (TableRel rel : _tableRels) {
			if (!DataFO.isEmptyString(relStr))
				relStr += " and";
			relStr+=" " +rel.getTcp1().getAlias()+"."+rel.getCol1()+" = "+rel.getTcp2().getAlias()+"."+rel.getCol2();
		}
		wstr +=relStr;
		
		// 使用者定義的查詢條件
		if(_datas !=null && _param !=null && _sqlParser!=null && _queryParamMappingParser!=null) {
			ArrayList<Object> datasCopy = new ArrayList<>(_datas);
			wstr = _param.combineConditions(datasCopy, wstr, _sqlParser, _queryParamMappingParser);
		}
		
		// 主要查詢SQL
		String qstr = "select count(*) from";
		int i;
		/* table */
		for(TableColPack tcp: _tableColPacks) {
			qstr += " "+tcp.getTable() + " "+tcp.getAlias() +",";
		}
		i = qstr.lastIndexOf(',');
		if(i>0)
			qstr = qstr.substring(0,i);
		
		qstr+= " "+wstr;
		
		return qstr;
	}
	
	
	// -------------------------------------------------------------------------------
	// ------------------------------------SkewerL------------------------------------

	// -------------------------------------------------------------------------------
	// -------------------------------------close-------------------------------------
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
