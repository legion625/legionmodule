package legion.datasource.source;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;
import java.security.KeyStore;
import java.util.Hashtable;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import legion.LegionContext;
import legion.datasource.manager.ResourceInfo;
import legion.datasource.manager.SourceConfiguration;
import legion.util.DataFO;
import legion.util.LogUtil;

public class RmiSslServerSocketFactory implements RMIServerSocketFactory {
	private static Logger log = LoggerFactory.getLogger(RmiSslServerSocketFactory.class);
	private static final String KEY_STORE_DATASOURCE_NAME = "rmi.socketServerSecureFactory.Legion.keyStoreDatasourceName";
//	private static final String DEFAULT_STORE_SOURCE = XssUtil.normalize(XssUtil.cleanXSS(System.getProperty(KEYSTORE_DATASOURCE_NAME)), Form.NFKD);
	private static final String DEFAULT_STORE_SOURCE = System.getProperty(KEY_STORE_DATASOURCE_NAME);

	private String keystorePass, keystoreType;
	private byte[] keystoreStream;

	private SSLServerSocketFactory ssf = null;

	// -------------------------------------------------------------------------------
	// ---------------------------------constructors----------------------------------
	public RmiSslServerSocketFactory() {
		this((Hashtable) null);
	}

	public RmiSslServerSocketFactory(Hashtable attributes) {
		// 1. 預先使用attributes中所指定的Datasource名稱（定義在DSManager範疇中），並以此啟始相關內容。
		if (attributes != null && attributes.containsKey(ResourceInfo.KEYSTORE_CONF_DS_NAME)) {
			String ssl_keyStore_conf_name = (String) attributes.get(ResourceInfo.KEYSTORE_CONF_DS_NAME);
			log.debug("Use Datasource[{}] init", ssl_keyStore_conf_name);
			initial(SourceConfiguration.getInstance().getSource(ssl_keyStore_conf_name));
		}
		// 2. 使用完成setup的AxisJsseSocketFactoryProperties資訊
		else if (RmiSslServerSocketFactoryProperties.isSetup()) {
			log.debug("Use RmiSslServerSocketFactoryProperties init");
			initial(RmiSslServerSocketFactoryProperties.keyStoreByte, RmiSslServerSocketFactoryProperties.keyStorePass,
					RmiSslServerSocketFactoryProperties.keyStoreType);
		}
		// 3.透過系統參數取所指定的Datasource名稱（定義在DSManager範疇中），並以此啟始相關內容。
		else if (!DataFO.isEmptyString(DEFAULT_STORE_SOURCE)) {
			String ssl_keyStore_conf_name = DEFAULT_STORE_SOURCE;
			log.debug("Use system property[{}] - [{}] init", KEY_STORE_DATASOURCE_NAME, ssl_keyStore_conf_name);
			initial(SourceConfiguration.getInstance().getSource(ssl_keyStore_conf_name));
		}

		// 建立factory
		initFactory();
	}

	public RmiSslServerSocketFactory(String _sslKeyStoreConfName) {
		log.debug("Use Datasource[{}] init", _sslKeyStoreConfName);
		// 初始化資料
		initial(SourceConfiguration.getInstance().getSource(_sslKeyStoreConfName));

		// 建立factory
		initFactory();
	}

	// -------------------------------------------------------------------------------
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		else if (obj == null || getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	// -------------------------------------------------------------------------------
	private void initial(byte[] _keystoreByte, String _keystorePass, String _keystoreType) {
		keystorePass = _keystorePass;
		keystoreType= _keystoreType;
		keystoreStream = _keystoreByte;
	}
	
	protected void initial(ResourceInfo _cfg) {
		try {
			log.debug("initial and read keystore stream");
			if (_cfg == null)
				throw new Exception("無設定檔資訊。");
			// keystorePath
			String keyStorePath = "";
			if (_cfg.getParameter(ResourceInfo.KEYSTORE_PATH) != null)
				keyStorePath = _cfg.getParameter(ResourceInfo.KEYSTORE_PATH);
			// keystorePassword
			if (_cfg.getParameter(ResourceInfo.KEYSTORE_PASSWORD) != null) {
				String sourcePpwwdd = _cfg.getParameter(ResourceInfo.KEYSTORE_PASSWORD);
				String realPpwwdd = null;
				// 若有指定加密檔，則進行密碼解碼處理程序。
				if (_cfg.getParameter(ResourceInfo.Resource_MASK_YEK) != null) {
//					String maskYekSource = _cfg.getParameter(ResourceInfo.Resource_MASK_YEK);
//					log.debug("指定加密金鑰檔來源 {}", maskYekSource);
//					realPpwwdd = YekManage.deMask(sourcePpwwdd, maskYekSource);
					// FIXME
				} else {
					realPpwwdd = sourcePpwwdd;
				}
				keystorePass = realPpwwdd;
			}
			// keystoreType
			if (_cfg.getParameter(ResourceInfo.KEYSTORE_TYPE) != null)
				keystoreType = _cfg.getParameter(ResourceInfo.KEYSTORE_TYPE);

			/* */
			InputStream fileStream = null;
			// {webRoot}開頭表示WebApp路徑
			if (keyStorePath.startsWith("{webRoot}/")) {
				keyStorePath = LegionContext.getInstance().getContextPath() + keyStorePath.substring(9); // FIXME
																											// 為什麼是9???
//				keyStorePath = XssUtil.normalizeFilePath(keyStorePath);
				fileStream = new FileInputStream(keyStorePath);
			}
			// {classPath}開頭表示ClassPath路徑
			else if (keyStorePath.startsWith("{classPath}/")) {
				fileStream = getClass().getResourceAsStream(keyStorePath.substring(11)); // FIXME 為什麼是11???
			}
			// 以檔案路徑取得檔案
			else {
				fileStream = new FileInputStream(keyStorePath);
			}

			/* */
			if (fileStream != null) {
				keystoreStream = new byte[fileStream.available()];
				fileStream.read(keystoreStream);
				fileStream.close();
			}
		} catch (Exception e) {
			LogUtil.log(log, e, Level.ERROR, "initial and read keyStore stream.");
		}
	}

	protected void initFactory() {
		log.debug("initFactory [SunX509][TLS][KeyStore:{}]", keystoreType);
		try {
			// setup key manager to do server authentication
			SSLContext ctx;
			KeyManagerFactory kmf;
			KeyStore ks;

			kmf = KeyManagerFactory.getInstance("SunX509");
			ks = KeyStore.getInstance(keystoreType);
			char[] keyStorePpwwdd = keystorePass.toCharArray();
			ks.load(new ByteArrayInputStream(keystoreStream), keyStorePpwwdd);
			kmf.init(ks, keyStorePpwwdd);
			ctx = SSLContext.getInstance("TLS");
			ctx.init(kmf.getKeyManagers(), null, null);
			ssf = ctx.getServerSocketFactory();
		} catch (Exception e) {
			log.error("initFactory [SunX509][TLS][KeyStore:{}]", keystoreType);
			LogUtil.log(log, e, Level.ERROR);
		}
	}
	
	// -------------------------------------------------------------------------------
	@Override
	public ServerSocket createServerSocket(int port) throws IOException {
		return ssf.createServerSocket(port);
	}

	// -------------------------------------------------------------------------------
	/**
	 * 用來描述JSSE連結參數設定，是一個資料工具類別，不允許產生物件，資料都是記錄在類別層級。
	 * 
	 * @author Min-Hua Chao
	 *
	 */
	public static class RmiSslServerSocketFactoryProperties {
		private static byte[] keyStoreByte;
		private static String keyStorePass = "";
		private static String keyStoreType = "";
		private static boolean setup = false;

		private RmiSslServerSocketFactoryProperties() {
		}

		public static boolean isSetup() {
			return setup;
		}

		public static void setInfo(byte[] _keyStoreByte, String _keyStorePass, String _keyStoreType) {
			keyStoreByte = _keyStoreByte;
			keyStorePass = _keyStorePass;
			keyStoreType = _keyStoreType;
		}

		public static void setup(boolean _setup) {
			setup = _setup;
		}

	}

}
