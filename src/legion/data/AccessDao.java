package legion.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Deprecated
public abstract class AccessDao extends Dao{

//	private final static String DATA_SOURCE = "jdbc:ucanaccess://d:/MyModule/database/DatabaseAccount.accdb"; // 正式資料庫
	private final static String DATA_SOURCE = "jdbc:ucanaccess://d:/MyModule/database/DatabaseAccountTest.accdb"; // 測試資料庫
	
//	protected AccessDao(String dataSource) {
//		super(() -> {
//			try {
//				// 註冊driver
//				Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
//				return DriverManager.getConnection(dataSource);
//			} catch (ClassNotFoundException e) {
//				System.out.println("DriverClassNotFound :" + e.toString());
//				return null;
//			} catch (SQLException x) {// 有可能會產生sqlexception
//				System.out.println("SQLException :" + x.toString());
//				return null;
//			}
//		});
//	}
	protected Connection getConn() {
		try {
			// 註冊driver
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			return DriverManager.getConnection(DATA_SOURCE);
		} catch (ClassNotFoundException e) {
			System.out.println("DriverClassNotFound :" + e.toString());
			return null;
		} catch (SQLException x) {// 有可能會產生sqlexception
			System.out.println("SQLException :" + x.toString());
			return null;
		}
	}
	

}
