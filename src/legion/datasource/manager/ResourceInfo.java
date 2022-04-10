package legion.datasource.manager;

import java.util.HashMap;
import java.util.Map;

public class ResourceInfo {
	/* 資料描述參數名稱定義 */
	
	/* 通用參數 */
	// Resource Name
	public final static String Resource_NAME = "name";
	// Resource URL
	public final static String Resource_URL = "url";
	
	// 密碼加密檔
	public final static String Resource_MASK_YEK = "maskYek"; // TODO not implemented yet...
	// 密碼
	public final static String Resource_PASSWORD = "password";
	// 帳號
	public final static String Resource_USER_NAME = "username";
	// 資料連結異常被通知者
	public final static String Resource_ALERT_MAIL = "alertMail";
	// 資料連結異常通知頻率
	public final static String Resource_ALERT_MAIL_PERIOD = "alertMailPeriod";
	// 資料來源實作類別
	public final static String Resource_IMP_CLASS = "dsoClassName";
	
	/* 資料庫連結相參數定義 */
	// 最大連線數
	public final static String SOURCE_MAX_ACTIVE = "maxActive";
	// 取得連線最長等待時間
	public final static String SOURCE_MAX_WAIT = "maxWait";
	// 允許最大的閒置連線數(Pool)
	public final static String SOURCE_MAX_IDLE = "maxIdle";
	// 連線實作Driver
	public final static String SOURCE_DRIVER_CLASS_NAME = "driverClassName";
	// 連線驗證語法
	public final static String SOURCE_VALIDATION_QUERY = "validationQuery";
//	// 連線驗證語法
//	public final static String SOURCE_VALIDATION_STR = "validationStr";
	// 是否在取得連現時進行連線驗證
	public final static String SOURCE_TEST_ON_BORROW = "testOnBorrow";
	
	/* RMI Service 相關參數定義 */
	// 服務名稱
	public final static String SERVICE_NAME = "serviceName";
	// 驗證服務是否正常的所呼叫的服務方法，有設定則取得服務時會進行呼驗證該服務是正常運作
	public final static String SERVICE_TEST_ON_CONNECT_SERVICE_NAME = "testOnConnectServiceName";
	// Security manager 實作類別
	public final static String RMI_SECURITY_MANAGER  = "rmiSecurityManager";

	/* 連線通道安全性設定-定義JSSE SSL所需的變數常數 */
	// 底層連線通道實作
	public final static String SOCKET_SECURE_FACTORY = "socketSecureFactory";
	// 加密資訊Resouce，指定值為存在SourceConfiguration中的Resource定義
	public final static String KEYSTORE_DS_NAME = "keyStoreDatasourceName";
	// KEYSTORE Resource 定義名稱
	public final static String KEYSTORE_CONF_DS_NAME = "keyStoreConfName";
	// KEYSTORE 加密檔路徑
	public final static String KEYSTORE_PATH = "keyStore";
	// KEYSTORE 加密檔密碼
	public final static String KEYSTORE_PASSWORD = "keyStorePass";
	// KEYSTORE 類型
	public final static String KEYSTORE_TYPE = "keyStoreType";
	
	private String name;
	private Map<String, String> parameters;

	protected ResourceInfo(String name) {
		this.name = name;
		this.parameters = new HashMap<>();
	}

	public String getName() {
		return name;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	protected String setParameter(String _key, String _value) {
		return parameters.put(_key, _value);
	}

	public String getParameter(String _key) {
		return parameters.get(_key);
	}

}
