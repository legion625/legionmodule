package legion.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//import com.mysql.jdbc.Driver;
//import org.postgresql.Driver;
// import org.postgresql.jdbc.

public class PgTest {
	public static void main(String[] args) {
		// ✅ 替換成你 Render 上的實際連線資訊
//		String url = "jdbc:postgresql://YOUR_HOST:5432/YOUR_DB?currentSchema=hms_dev";
//		String url = "jdbc:postgresql://dpg-cvmvktfgi27c73be3mig-a.singapore-postgres.render.com:5432/hms_dev_viml?currentSchema=hms_dev";
		String url = "jdbc:postgresql://dpg-cvrf5vre5dus7387koag-a.singapore-postgres.render.com:5432/legion_h3u9?currentSchema=legion_pg_dev";
		
//		String user = "YOUR_USERNAME";
//		String user = "hms_dev_viml_user";
		String user = "legion_user";
		
//		String password = "YOUR_PASSWORD";
//		String password = "CU0lqsYKOodMLUUFGjmCETTH8t1sY1eY";
		String password = "JaNibhJzdkjNm62MvX7SVJt4oSPH39qW";
		
		
		try {
			// 1. 建立連線
			Connection conn = DriverManager.getConnection(url, user, password);
			System.out.println("✅ 成功連線到 PostgreSQL！");

			// 2. 查詢資料庫版本（測試用）
			PreparedStatement stmt = conn.prepareStatement("SELECT version()");
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				System.out.println("📦 PostgreSQL version: " + rs.getString(1));
			}

			// 3. 關閉資源
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
