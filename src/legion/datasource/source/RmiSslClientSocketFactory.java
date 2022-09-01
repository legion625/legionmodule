package legion.datasource.source;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.text.Normalizer.Form;
import java.util.Hashtable;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import legion.LegionContext;
import legion.datasource.manager.ResourceInfo;
import legion.datasource.manager.SourceConfiguration;
import legion.util.DataFO;
import legion.util.LogUtil;

public class RmiSslClientSocketFactory implements RMIClientSocketFactory, Serializable {
	private static final String KEYSTORE_DATASOURCE_NAME = "rmi.socketClientSecureFactory.legion.keyStoreDatasourceName";
//	private static final String DEFAULT_STORE_SOURCE = XssUtil.normalize(XssUtil.cleanXSS(System.getProperty(KEYSTORE_DATASOURCE_NAME)), Form.NFKD);
	private static final String DEFAULT_STORE_SOURCE = System.getProperty(KEYSTORE_DATASOURCE_NAME);

	private static final long serialVersionUID = -4831018188065967647L;
	private static Logger log = LoggerFactory.getLogger(RmiSslClientSocketFactory.class);

	private String keystorePass, keystoreType;
	private byte[] keystoreStream;

	// -------------------------------------------------------------------------------
	// ---------------------------------constructors----------------------------------
	public RmiSslClientSocketFactory() {
		this((Hashtable<?, ?>) null);
	}

	public RmiSslClientSocketFactory(Hashtable<?, ?> attributes) {
		// 1. 預先使用attributes中所指定的Datasource名稱（定義在DSManager範疇中），並以此啟始相關內容。
		if (attributes != null && attributes.containsKey(ResourceInfo.KEYSTORE_CONF_DS_NAME)) {
			String ssl_keyStore_conf_name = (String) attributes.get(ResourceInfo.KEYSTORE_CONF_DS_NAME);
			log.debug("Use Datasource[{}] init", ssl_keyStore_conf_name);
			initial(SourceConfiguration.getInstance().getSource(ssl_keyStore_conf_name));
		}
		// 2. 使用完成setup的AxisJsseSocketFactoryProperties資訊
		else if (RmiSslClientSocketFactoryProperties.isSetup()) {
			log.debug("Use RmiSslClientSocketFactoryProperties init");
			initial(RmiSslClientSocketFactoryProperties.keystoreByte, RmiSslClientSocketFactoryProperties.keystorePass,
					RmiSslClientSocketFactoryProperties.keystoreType);
		}
		// 3.透過系統參數取所指定的Datasource名稱（定義在DSManager範疇中），並以此啟始相關內容。
		else if (!DataFO.isEmptyString(DEFAULT_STORE_SOURCE)) {
			String ssl_keyStore_conf_name = DEFAULT_STORE_SOURCE;
			log.debug("Use system property[{}] - [{}] init", KEYSTORE_DATASOURCE_NAME, ssl_keyStore_conf_name);
			initial(SourceConfiguration.getInstance().getSource(ssl_keyStore_conf_name));
		}
	}

	public RmiSslClientSocketFactory(String _sslKeyStoreConfName) {
		log.debug("Use Datasource[{}] init", _sslKeyStoreConfName);
		// 初始化資料
		initial(SourceConfiguration.getInstance().getSource(_sslKeyStoreConfName));
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
		keystoreType = _keystoreType;
		keystoreStream = _keystoreByte;
	}

	protected void initial(ResourceInfo _cfg) {
		try {
			log.debug("initial and read keystore stream");
			if (_cfg == null)
				throw new Exception("無設定檔資訊。");
			// keystorePath
			String keystorePath = "";
			if (_cfg.getParameter(ResourceInfo.KEYSTORE_PATH) != null)
				keystorePath = _cfg.getParameter(ResourceInfo.KEYSTORE_PATH);
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
			if (keystorePath.startsWith("{webRoot}/")) {
				keystorePath = LegionContext.getInstance().getContextPath() + keystorePath.substring(9); // FIXME
//				keyStorePath = XssUtil.normalizeFilePath(keyStorePath);
				fileStream = new FileInputStream(keystorePath);
			}
			// {classPath}開頭表示ClassPath路徑
			else if (keystorePath.startsWith("{classPath}/")) {
				fileStream = getClass().getResourceAsStream(keystorePath.substring(11)); // FIXME 為什麼是11???
			}
			// 以檔案路徑取得檔案
			else {
				fileStream = new FileInputStream(keystorePath);
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

	// -------------------------------------------------------------------------------
	@Override
	public Socket createSocket(String host, int port) throws IOException {
		// 註冊keystore
		try {
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			KeyStore ks = KeyStore.getInstance(keystoreType);
			char[] keystorePpwwdd = keystorePass.toCharArray();
			ks.load(new ByteArrayInputStream(keystoreStream), keystorePpwwdd);
			kmf.init(ks, keystorePpwwdd);
			tmf.init(ks);
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
			return context.getSocketFactory().createSocket(host, port);
		} catch (Exception e) {
			log.warn("createSocket [SunX509][TLS][Keystore:{}]", keystoreType);
			LogUtil.log(log, e, Level.ERROR);
			return null;
		}
	}

	// -------------------------------------------------------------------------------
	/**
	 * 用來描述JSSE連結參數設定，是一個資料工具類別，不允許產生物件，資料都是記錄在類別層級。
	 * 
	 * @author Min-Hua Chao
	 *
	 */
	public static class RmiSslClientSocketFactoryProperties {
		private static byte[] keystoreByte;
		private static String keystorePass = "";
		private static String keystoreType = "";
		private static boolean setup = false;

		private RmiSslClientSocketFactoryProperties() {
		}

		public static boolean isSetup() {
			return setup;
		}

		public static void setInfo(byte[] _keystoreByte, String _keystorePass, String _keystoreType) {
			keystoreByte = _keystoreByte;
			keystorePass = _keystorePass;
			keystoreType = _keystoreType;
		}

		public static void setup(boolean _setup) {
			setup = _setup;
		}

	}

}
