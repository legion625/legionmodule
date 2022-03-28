package legion.datasource.manager;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.BusinessServiceFactory;

/**
 * 提供資料來源服務的窗口，包含Connection和Transaction管控機制。
 * 目前主要以單一Thread範疇下的交易控制。
 *  - 若是多執行緒環境進行單一交易，還需要進行驗證。
 *  - 支援交易的連線類型只有資料庫連線型態，其他連線型態就算是處與交易狀態下，仍不屬於交易範疇。
 * @author Min-Hua Chao
 *
 */
public class DSManager {
	private static Logger log = LoggerFactory.getLogger(DSManager.class);
	
	// -------------------------------------------------------------------------------
	/* Singleton */
	private static volatile DSManager instance = new DSManager();
	
	private DSManager() {
		dsDao = DSManagerDao.getInstance();
	}
	
	public static DSManager getInstance() {
		return instance;
	}
	
	// -------------------------------------------------------------------------------
	// 交易記錄暫存區
	private volatile ConcurrentHashMap<String, Transaction> transactionMap = new ConcurrentHashMap<>();
	// URL對應UrlDs物件的暫存區，可避免重覆產生UrlDS物件。
	private volatile ConcurrentHashMap<String, UrlDS> urlDsMap = new ConcurrentHashMap<>();
	
	private DSManagerDao dsDao;
	// 偵測Transaction建立後警告通知時間臨界點
	private long maxTsCreateTime = 3000;
	// 偵測Transaction初始後警告通知時間臨界點
	private long maxTsStartTime = 2000;
	// 是否啟動偵測Transaction機制
	private boolean debugTransaction = false;
	// 偵測Transaction週期
	private long debugTransactionPeriod = 2000;
	// 偵測異常通知信收信者
	private String[] alertMails;
	// Transaction偵測器
	private Thread tsMonitor;
	
	protected String hostIp = LegionConext.getInstance().getSystemInfo().getHostIp();
	
//	/* mail service */ TODO mailService
//	protected static MailService mailService;
//	
//	static {
//		// 註冊服務完成通知，可以透過Service Factory取得所需服務(mail,service)
//		BusinessServiceFactory.getInstance().addListener((iClass, iService, factory)->{
//			log.debug("事件觸發由BusinessServiceFactory再次取得MailService實體。");
//			if(iClass.equals(MailService.class))
//				mailService = factory.getService(MailService.class);
//		});
//	}
	
	/**
	 * 註冊Datasource資訊，並清空原有的註冊資訊。
	 * 
	 * @param _datasourceXmlStream
	 */
	public void registerDatasourceXml(InputStream _datasourceXmlStream) {
		dsDao.registerXml(_datasourceXmlStream);
	}

	/**
	 * 註冊Datasource資訊，可以透過參數進行清空或是累加模式
	 * 
	 * @param _datasourceXmlStream
	 */
	public void registerDatasourceXml(InputStream _datasourceXmlStream, boolean _rebuild) {
		dsDao.registerXml(_datasourceXmlStream, _rebuild);
	}
	
	/**
	 * 釋放指定的資料來源
	 * @param _url
	 * @return
	 */
	public boolean releaseDatasource(String _url) {
		if(urlDsMap.containsKey(_url))
			return releaseDatasource(urlDsMap.get(_url));
		else
			return dsDao.releaseDatasource(_url);
	}
	
	/**
	 * 釋放指定的資料來源
	 * @param _urlDs
	 * @return
	 */
	public boolean releaseDatasource(UrlDs _urlDs) {
		return dsDao.releaseDatasource(_urlDs);
	}
	
	public boolean releaseAllDatasources() {
		return dsDao.releaseAllDatasources();
	}
	
	public List<DataSourceInfo> getDatasourceInfos(){
		return dsDao.getDatasourceInfos();
	}
	
	/** 取得所需的Datasource Connection並以目前Thread物件的hashCode為登記使用標的。 */
	public Object getConn(String _url) {
		return getConn(_url, Integer.toString(Thread.currentThread().hashCode()));
	}

	/** 取得所需的Datasource Connection並以目前Thread物件的hashCode為登記使用標的。 */
	public Object getConn(UrlDS _urlDs) {
		return getConn(_urlDs, Integer.toString(Thread.currentThread().hashCode()));
	}
	
	/**
	 * 取得所需的Datasource Connection並以指定的ID為登記使用標的
	 * @param _url
	 * @param _id
	 * @return Object
	 * @deprecated 不支援多執行緒進行單一交易
	 */
	@Deprecated
	public Object getConn(String _url, String _id) {
		UrlDS urlDs;
		if (urlDsMap.containsKey(_url))
			urlDs = urlDsMap.get(_url);
		else {
			synchronized (urlDsMap) {
				if(urlDsMap.containsKey(_url))
					urlDs = urlDsMap.get(_url);
				else {
					urlDs = new UrlDS(_url);
					urlDsMap.put(_url, urlDs);
				}
			}
		}
		return getConn(urlDs, _id);
	}
	
	/**
	 * 取得所需的Datasource Connection並以指定的ID為登記使用標的
	 * 
	 * @param _urlDs
	 * @param _id
	 * @return Object
	 * @deprecated 不支援多執行緒進行單一交易
	 */
	private Object getConn(UrlDS _urlDs, String _id) {
		Object conn = null;
		// 是否處理交易狀態下取得Connection
		conn = getTransactionCacheConn(_urlDs, _id);
		if (conn == null) {
			// 由DsDao取得連線
			conn = dsDao.getConn(_urlDs);
			if (conn == null)
				return null;
			else if (getTransactionState(_id)) {
				// 交易狀態下，進行connection註冊
				conn = registerTransactionCacheConn(_urlDs, _id, conn);
				return conn;
			}
		}
		return conn;
	}
	
	private synchronized Object getTransactionCacheConn(UrlDS _urlDs, String _id) {
		Object conn = null;
		if (!getTransactionState(_id))
			return null;
		
		Transaction t = transactionMap.get(_id);
		conn = t.getConnection(_urlDs);
		return conn;
	}

}
