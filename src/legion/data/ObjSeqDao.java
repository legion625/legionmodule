package legion.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjSeqDao extends MySqlDao {
	public ObjSeqDao(MySqlDataSource _ds) {
		super(_ds);
	}

	// -------------------------------------------------------------------------------
	private final static String TABLE_OBJ_SEQ = "obj_seq";
	private final static String COL_OBJ_SEQ_KEY = "obj_key";
	private final static String COL_OBJ_SEQ_INDEX = "obj_index";

	public int getObjSeqIndex(String _key) {
		int v = loadObjSeqIndex(_key);
		if (v <= 0)
			v = 1;
		saveObjSeq(_key, v + 1);
		return v;
	}

	private boolean saveObjSeq(String _key, int _value) {
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		try {
			String qstr = "update " + TABLE_OBJ_SEQ;
			qstr += " set " + COL_OBJ_SEQ_INDEX + "=?";
			qstr += " where " + COL_OBJ_SEQ_KEY + "=?";
			pstmt = conn.prepareStatement(qstr);
			int colIndex = 1;
			pstmt.setInt(colIndex++, _value);
			pstmt.setString(colIndex++, _key);
			if (pstmt.executeUpdate() == 1)
				return true;
			else
				return createObjSeq(_key, _value);
		} catch (SQLException e) {
			// Log.e("TAG", e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			close(conn, pstmt, null);
		}
	}

	private boolean createObjSeq(String _key, int _value) {
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		try {
			String qstr = "insert into " + TABLE_OBJ_SEQ + " (";
			qstr += COL_OBJ_SEQ_KEY + "," + COL_OBJ_SEQ_INDEX;
			qstr += ") values(?,?)";
			pstmt = conn.prepareStatement(qstr);
			int colIndex = 1;
			pstmt.setString(colIndex++, _key);
			pstmt.setInt(colIndex++, _value);

			return pstmt.executeUpdate() == 1;
		} catch (SQLException e) {
			// Log.e("TAG", e.getMessage());
			e.printStackTrace();
		} finally {
			close(conn, pstmt, null);
		}
		return false;
	}

	private int loadObjSeqIndex(String _key) {
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String qstr = "select * from " + TABLE_OBJ_SEQ + " where " + COL_OBJ_SEQ_KEY + " = '" + _key + "'";
			System.out.println("qstr: " + qstr);
			pstmt = conn.prepareStatement(qstr);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				int value = rs.getInt(COL_OBJ_SEQ_INDEX);
				return value;
			}
		} catch (SQLException e) {
			// Log.e("TAG", e.getMessage());
			e.printStackTrace();
			return -1;
		} finally {
			close(conn, pstmt, rs);
		}
		return -1;
	}
}
