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

	synchronized String getSeq(String _itemId) {
		String simpleSeq = getSimpleSeq(_itemId);
		if (DataFO.isEmptyString(simpleSeq)) {
			log.error("getSimpleSeq return null: itemId:{}", _itemId);
			return null;
		}
		return simpleSeq + "!" + serverId;
		
//		long lgSeq = 1;
//		Connection conn = null;
//		PreparedStatement pstmt1 = null, pstmt2 = null;
//		ResultSet rs = null;
//		try {
//			conn = (Connection) dsManager.getConn(source);
//			pstmt1 = conn.prepareStatement("select * from system_seq where item_id = ?");
//			pstmt1.setString(1, _itemId);
//			rs = pstmt1.executeQuery();
//
//			if (rs.next()) {
//				if (rs.getLong("current_num") < rs.getLong("max_num"))
//					lgSeq = rs.getLong("current_num") + 1;
//
//				pstmt2 = conn.prepareStatement("update system_seq set current_num=?, last_num=? where item_id = ?");
//				pstmt2.setLong(1, lgSeq);
//				pstmt2.setLong(2, (lgSeq + 1));
//				pstmt2.setString(3, _itemId);
//				pstmt2.execute();
//			} else {
//				pstmt2 = conn.prepareStatement("insert into " + TB_SYSTEM_SEQ
//						+ " (item_id, current_num, last_num, max_num) values (?,1,2,99999999)");
//				pstmt2.setString(1, _itemId);
//				pstmt2.execute();
//			}
//		} catch (Throwable e) {
//			DSManager.getInstance().failTransaction();
//			log.error("GenSeqDao.getSeq error.");
//			return null;
//		} finally {
//			try {
//				if (rs != null)
//					rs.close();
//				if (pstmt1 != null)
//					pstmt1.close();
//				if (pstmt2 != null)
//					pstmt2.close();
//				if (conn != null)
//					conn.close();
//			} catch (Throwable e) {
//				log.warn("{}", e.getMessage());
//			}
//
//		}
//		return lgSeq + "!" + serverId;
	}
	
	synchronized String getSimpleSeq(String _itemId) {
		long lgSeq = 1;
		Connection conn = null;
		PreparedStatement pstmt1 = null, pstmt2 = null;
		ResultSet rs = null;
		try {
			conn = (Connection) dsManager.getConn(source);
			pstmt1 = conn.prepareStatement("select * from system_seq where item_id = ?");
			pstmt1.setString(1, _itemId);
			rs = pstmt1.executeQuery();

			if (rs.next()) {
				if (rs.getLong("current_num") < rs.getLong("max_num"))
					lgSeq = rs.getLong("current_num") + 1;

				pstmt2 = conn.prepareStatement("update system_seq set current_num=?, last_num=? where item_id = ?");
				pstmt2.setLong(1, lgSeq);
				pstmt2.setLong(2, (lgSeq + 1));
				pstmt2.setString(3, _itemId);
				pstmt2.execute();
			} else {
				pstmt2 = conn.prepareStatement("insert into " + TB_SYSTEM_SEQ
						+ " (item_id, current_num, last_num, max_num) values (?,1,2,99999999)");
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
