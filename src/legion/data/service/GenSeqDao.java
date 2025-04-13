package legion.data.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.datasource.manager.DSManager;
import legion.util.DataFO;

public class GenSeqDao {
	private Logger log = LoggerFactory.getLogger(getClass());

	private final DSManager dsManager = DSManager.getInstance();

	// -------------------------------------------------------------------------------
	private String source;
	private String serverId;

	GenSeqDao(String source, String serverId) {
		this.source = source;
		this.serverId = serverId;
	}

	// -------------------------------------------------------------------------------
	private final static String TB_SYSTEM_SEQ = "system_seq";
	private final static String COL_SYSTEM_SEQ_ITEM_ID = "item_id";
	private final static String COL_SYSTEM_SEQ_CURRENT_NUM = "current_num";
	private final static String COL_SYSTEM_SEQ_LAST_NUM = "last_num";
	private final static String COL_SYSTEM_SEQ_MAX_NUM = "max_num";

	synchronized String getSeq(String _itemId) {
		String simpleSeq = getSimpleSeq(_itemId);
		if (DataFO.isEmptyString(simpleSeq)) {
			log.error("getSimpleSeq return null: itemId:{}", _itemId);
			return null;
		}
		return simpleSeq + "!" + serverId;
	}

	synchronized String getSimpleSeq(String _itemId) {
		long lgSeq = 1;
		Connection conn = null;
		PreparedStatement pstmt1 = null, pstmt2 = null;
		ResultSet rs = null;
		try {
			conn = (Connection) dsManager.getConn(source);
			pstmt1 = conn
					.prepareStatement("select * from " + TB_SYSTEM_SEQ + " where " + COL_SYSTEM_SEQ_ITEM_ID + " = ?");
			pstmt1.setString(1, _itemId);
			rs = pstmt1.executeQuery();

			if (rs.next()) {
				if (rs.getLong(COL_SYSTEM_SEQ_CURRENT_NUM) < rs.getLong(COL_SYSTEM_SEQ_MAX_NUM))
					lgSeq = rs.getLong(COL_SYSTEM_SEQ_CURRENT_NUM) + 1;

				pstmt2 = conn.prepareStatement("update " + TB_SYSTEM_SEQ + " set " + COL_SYSTEM_SEQ_CURRENT_NUM + "=?, "
						+ COL_SYSTEM_SEQ_LAST_NUM + "=? where " + COL_SYSTEM_SEQ_ITEM_ID + " = ?");
				pstmt2.setLong(1, lgSeq);
				pstmt2.setLong(2, (lgSeq + 1));
				pstmt2.setString(3, _itemId);
				pstmt2.execute();
			} else {
				pstmt2 = conn.prepareStatement("insert into " + TB_SYSTEM_SEQ + " (" + COL_SYSTEM_SEQ_ITEM_ID + ", "
						+ COL_SYSTEM_SEQ_CURRENT_NUM + ", " + COL_SYSTEM_SEQ_LAST_NUM + ", " + COL_SYSTEM_SEQ_MAX_NUM
						+ ") values (?,1,2,99999999)");
				pstmt2.setString(1, _itemId);
				pstmt2.execute();
			}
		} catch (Throwable e) {
			DSManager.getInstance().failTransaction();
			log.error("GenSeqDao.getSeq error.");
			return null;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt1 != null)
					pstmt1.close();
				if (pstmt2 != null)
					pstmt2.close();
				if (conn != null)
					conn.close();
			} catch (Throwable e) {
				log.warn("{}", e.getMessage());
			}

		}
		return lgSeq + "";
	}
}
