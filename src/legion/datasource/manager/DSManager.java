package legion.datasource.manager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.BusinessServiceFactory;
import legion.DebugLogMark;
import legion.LegionContext;
import legion.datasource.DatasourceInfo;
import legion.datasource.DefaultTransactionInfoDto;
import legion.datasource.TransactionInfo;
import legion.datasource.UrlDs;
import legion.util.DateFormatUtil;
import legion.util.DateUtil;

/**
 * 提供資料來源服務的窗口，包含Connection和Transaction管控機制。
 * 目前主要以單一Thread範疇下的交易控制。
 *  - 若是多執行緒環境進行單一交易，還需要進行驗證。
 *  - 支援交易的連線類型只有資料庫連線型態，其他連線型態就算是處與交易狀態下，仍不屬於交易範疇。
 * @author Min-Hua Chao
 *
 */
public class DSManager {
//	private static Logger log = LoggerFactory.getLogger(DSManager.class);
	private static Logger log = LoggerFactory.getLogger(DebugLogMark.class);
	
	// -------------------------------------------------------------------------------
	/* Singleton */
	private static volatile DSManager instance = new DSManager();
	
	private DSManager() {
		dsDao = DSManagerDao.getInstance();
		log.debug("建構DSManager物件");
	}
	
	public static DSManager getInstance() {
		log.debug("instance: {}", instance);
		return instance;
	}
	
	// -------------------------------------------------------------------------------
	// 交易記錄暫存區
	private volatile ConcurrentHashMap<String, Transaction> transactionMap = new ConcurrentHashMap<>();
	// URL對應UrlDs物件的暫存區，可避免重覆產生UrlDS物件。
	private volatile ConcurrentHashMap<String, UrlDs> urlDsMap = new ConcurrentHashMap<>();
	
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
	
	protected String hostIp = LegionContext.getInstance().getSystemInfo().getHostIp();
	
	/* mail service */ 
//	protected static MailService mailService; // TODO mailService
//	
//	// TODO mailService
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
		log.debug("DSManager::registerDatasourceXml start");
		dsDao.registerDatasourceXml(_datasourceXmlStream);
		log.debug("DSManager::registerDatasourceXml end");
	}

	/**
	 * 註冊Datasource資訊，可以透過參數進行清空或是累加模式
	 * 
	 * @param _datasourceXmlStream
	 */
	public void registerDatasourceXml(InputStream _datasourceXmlStream, boolean _rebuild) {
		dsDao.registerDatasourceXml(_datasourceXmlStream, _rebuild);
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

	public void releaseAllDatasources() {
		dsDao.releaseAllDatasources();
	}

	public DatasourceInfo getDatasourceInfo(String _resourceName) {
		return dsDao.getDatasourceInfo(_resourceName);
	}
	
	public List<DatasourceInfo> getDatasourceInfos(){
		return dsDao.getDatasourceInfos();
	}
	
	/** 取得所需的Datasource Connection並以目前Thread物件的hashCode為登記使用標的。 */
	public Object getConn(String _url) {
		return getConn(_url, Integer.toString(Thread.currentThread().hashCode()));
	}

	/** 取得所需的Datasource Connection並以目前Thread物件的hashCode為登記使用標的。 */
	@Deprecated
	public Object getConn(UrlDs _urlDs) {
		return getConn(_urlDs, Integer.toString(Thread.currentThread().hashCode()));
	}
	
	/**
	 * 取得所需的Datasource Connection並以指定的ID為登記使用標的
	 * @param _resourceName
	 * @param _id
	 * @return Object
	 */
	public Object getConn(String _resourceName, String _id) {
		log.debug("_resourceName: {}\t id: {}", _resourceName, _id);
		UrlDs urlDs;
		if (urlDsMap.containsKey(_resourceName))
			urlDs = urlDsMap.get(_resourceName);
		else {
			synchronized (urlDsMap) {
				if(urlDsMap.containsKey(_resourceName))
					urlDs = urlDsMap.get(_resourceName);
				else {
					urlDs = new UrlDs(_resourceName);
					urlDsMap.put(_resourceName, urlDs);
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
	private Object getConn(UrlDs _urlDs, String _id) {
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
				conn = registerTransactionCacheConn(_id, _urlDs, conn);
				return conn;
			}
		}
		return conn;
	}
	
	/**
	 * 判別該ID是否處於交易狀態下，若是則會在該交易狀態下取得相對Connection。
	 * 
	 * @param _urlDs
	 * @param _id
	 * @return
	 */
	private synchronized Object getTransactionCacheConn(UrlDs _urlDs, String _id) {
		Object conn = null;
		if (!getTransactionState(_id))
			return null;
		
		Transaction t = transactionMap.get(_id);
		conn = t.getConnection(_urlDs);
		return conn;
	}
	
	/**
	 * 在該ID處於交易狀態下，註冊相關連線物件。
	 * 
	 * @param _id
	 * @param _urlDs
	 * @param _conn
	 * @return
	 */
	private Object registerTransactionCacheConn(String _id, UrlDs _urlDs, Object _conn) {
		if (!getTransactionState(_id))
			return null;
		
		try {
			// 取得交易物件
			Transaction t = transactionMap.get(_id);
			synchronized (t) {
				// 建立交易連線封裝
				TransactionConnection conn = TransactionConnection.newInstance(_conn, t);
				if(conn!=null) {
					t.putConnection(_urlDs, conn);
					// 若是有相對TransactionConnection，則回傳該TransactionConnection instance。
					return conn;
				}else {
					// 無相對TransactionConnection表示不支援transaction機制，因此回傳原本的connection物件運作。
					return _conn;
				}
			}
		}catch (Exception e) {
			log.error("registerTransactionCacheConn [source:{}] Error", _urlDs.getName());
			return null;
		}
	}
	
	/** 取得目前交易資訊 */
	public List<TransactionInfo> getTransactionInfos(){
		List<TransactionInfo> list = new ArrayList<>();
		for (Transaction t : transactionMap.values()) {
			DefaultTransactionInfoDto item = new DefaultTransactionInfoDto();
			item.setUid(t.getId());
			item.setNonTransactionChain(t.isNonTransactionChain());
			item.setRegular(t.isRegular());
			item.setConnSize(t.getConnSize());
			item.setTransactionChain(t.transactionChain());
			list.add(item);
		}
		return list;
	}
	
	/** 判別該Thread是否已處在交易狀態 */
	public boolean getTransactionState() {
		return getTransactionState(Integer.toString(Thread.currentThread().hashCode()));
	}
	
	/** 判別是否已處在DB交易狀態 */
	public boolean getTransactionState(String _id) {
		if(transactionMap.get(_id)==null)
			return false;
		return true;
	}
	
	
	
	/** 加入交易項的事件監聽處理 */
	public <T> boolean addTransactionEventListener(TransactionEventType _event, TransactionEventHandler<T> _handler,
			T _data) {
		return addTransactionEventListener(Integer.toString(Thread.currentThread().hashCode()), _event, _handler,
				_data);
	}
	
	/** 加入交易項的事件監聽處理 */
	public <T> boolean addTransactionEventListener(String _uid, TransactionEventType _event, TransactionEventHandler<T> _handler,
			T _data) {
		try {
			Transaction t= transactionMap.get(_uid);
			if(t!=null)
				t.addEventListener(_event, _handler, _data);
		}catch (Throwable e) {
			log.error("addTransactionEventListener [_uid:{}] Errror ", _uid);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** 加入交易項的事件監聽處理 */
	public <T> boolean addTransactionEventListener(TransactionEventType _event, TransactionEventHandler<T> _handler) {
		return addTransactionEventListener(Integer.toString(Thread.currentThread().hashCode()), _event, _handler);
	}
	
	/** 加入交易項的事件監聽處理 */
	public <T> boolean addTransactionEventListener(String _uid, TransactionEventType _event, TransactionEventHandler<T> _handler) {
		try {
			Transaction t= transactionMap.get(_uid);
			if(t!=null)
				t.addEventListener(_event, _handler);
		}catch (Throwable e) {
			log.error("addTransactionEventListener [_uid:{}] Errror ", _uid);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** 啟動Thread交易 */
	public boolean beginTransaction() {
		return beginTransaction(Integer.toString(Thread.currentThread().hashCode()));
	}
	
	/** 啟動Thread交易 */
	public boolean beginTransaction(String _id) {
		try {
			Transaction t = transactionMap.get(_id);
			if (t == null) {
				// 初始交易，建構交易物件
				synchronized (transactionMap) {
					// 取得鎖後，再次進行驗證...
					t = transactionMap.get(_id);
					if (t == null) {
						t = new Transaction(_id, debugTransaction);
						transactionMap.put(_id, t);
					}
				}
			}
			t.beginTransaction();
		} catch (Throwable e) {
			log.error("beginTransaction [_uid:{}], Error", _id);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** 交易失敗進行rollback */
	public void failTransaction() {
		failTransaction(Integer.toString(Thread.currentThread().hashCode()));
	}
	
	/** 交易失敗進行rollback */
	public void failTransaction(String _id) {
		Transaction t = transactionMap.get(_id);
		if(t!=null) {
			t.failTransaction();
			// 進行transaction暫存移除
			transactionMap.remove(_id);
		}
	}

	/** 確認該階段運作成功，會進行交易狀態判斷。若是已經完成該交易的所有階段，則進行整體交易運作commit。 */
	public void endTransaction() {
		endTransaction(Integer.toString(Thread.currentThread().hashCode()));
	}
	
	/** 確認該階段運作成功，會進行交易狀態判斷。若是已經完成該交易的所有階段，則進行整體交易運作commit。 */
	public void endTransaction(String _id) {
		Transaction t = transactionMap.get(_id);
		if (t != null) {
			synchronized (t) {
				if (t.endTransaction())
					transactionMap.remove(_id);
			}
			// 判斷整個交易階段是否已結束，並進行transaction暫存移除
			if(t.isNonTransactionChain())
				transactionMap.remove(_id);
		}
	}

	public long getMaxTsCreateTime() {
		return maxTsCreateTime;
	}

	public long getMaxTsStartTime() {
		return maxTsStartTime;
	}

	public boolean isDebugTransaction() {
		return debugTransaction;
	}

	public void setDebugTransaction(boolean debugTransaction) {
		this.debugTransaction = debugTransaction;
	}

	public long getDebugTransactionPeriod() {
		return debugTransactionPeriod;
	}

	public void setDebugTransactionPeriod(long debugTransactionPeriod) {
		this.debugTransactionPeriod = debugTransactionPeriod;
	}

	public String[] getAlertMails() {
		return alertMails;
	}

	public void setAlertMails(String[] alertMails) {
		this.alertMails = alertMails;
	}

	public void setMaxTsCreateTime(long maxTsCreateTime) {
		this.maxTsCreateTime = maxTsCreateTime;
	}

	public void setMaxTsStartTime(long maxTsStartTime) {
		this.maxTsStartTime = maxTsStartTime;
	}
	
	/** 交易監控：會依據目前設定的debugTransaction，進行交易監控的啟動或是停止。 */
	public void monitorTransaction() {
		if (debugTransaction)
			startMonitor();
		else
			stopMonitor();
	}
	
	private void startMonitor() {
		// 先確認停止目前運行的偵測執行緒
		stopMonitor();
		tsMonitor = new Thread(new TransactionMonitor(debugTransactionPeriod));
		tsMonitor.start();
		log.info("DSManager Start Transaction Monitor");
	}

	public void stopMonitor() {
		if (tsMonitor != null) {
			// 關閉偵測
			tsMonitor.interrupt();
			tsMonitor = null;
			log.info("DSManager Stop Transaction Monitor");
		}
	}
	
	// -------------------------------------------------------------------------------
	class TransactionMonitor implements Runnable {
		private long period;

		TransactionMonitor(long period) {
			super();
			this.period = period;
		}

		@Override
		public void run() {
			log.debug("TransactionMonitor run [{}][{}]", period, Thread.currentThread().getId());
			try {
				while(!Thread.interrupted()) {
					log.debug("TransactionMonitor[{}]: Check...", Thread.currentThread().getId());
					List<Transaction> alerts = new ArrayList<>();
					if(transactionMap.size()>0) {
						if(alerts.size()>0)
							alerts.clear();
						for(Transaction t: transactionMap.values()) {
							long st = System.currentTimeMillis();
							if((t.getCreateTime()>0 && (st-t.getCreateTime())>=maxTsCreateTime)
								||(t.getStartTime()>0 && (st-t.getStartTime())>=maxTsStartTime))
								alerts.add(t);
						}
						// 警告通知
						for(Transaction t:alerts)
							alertTransaction(t);
						alerts.clear();
					}
					TimeUnit.MILLISECONDS.sleep(period);
				}
			}catch (InterruptedException e) {
				log.debug("Thread[TransactionMonitor] interrupt...");
			}
			log.debug("Thread[TransactionMonitor] End...");
		}
		
		void alertTransaction(Transaction _t) {
			// 提供log輸出
			// 另外也提供mail通知，並且以另一緒來進行該動作
			
			// TODO mailService
			// 寄mail
			if(alertMails!=null && alertMails.length>0) {
				// TODO mailService
//				Collection msg = new ArrayList<>();
//				log.warn("alertTransaction:[now:{}][{}][create:{}][start:{}]", _t.getId(),
//						DateFormatUtil.transToTime(System.currentTimeMillis()),
//						DateFormatUtil.transToTime(_t.getCreateTime()), DateFormatUtil.transToTime(_t.getStartTime()));
//				msg.add("alertTransaction:[now:" + DateFormatUtil.transToTime(System.currentTimeMillis()) + "]["
//						+ _t.getId() + "][create:" + DateFormatUtil.transToTime(_t.getCreateTime()) + "][start:"
//						+ DateFormatUtil.transToTime(_t.getStartTime()) + "]");
//				
//				List<StackTraceElement[]> traces = _t.getTransactionTraces();
//				log.warn("**************  Transaction Trace [{}]  **************", _t.getId());
//				msg.add("**************  Transaction Trace ["+ _t.getId()+"]  **************");
//				if(traces!=null && !traces.isEmpty()) {
//					for (int i = 0, count = traces.size(); i < count; i++) {
//						log.warn("Trace [{}] ------------------------------------------------", (i + 1));
//						msg.add("Trace [" + (i + 1) + "]");
//						StackTraceElement[] se = traces.get(i);
//						for(StackTraceElement item:se) {
//							log.warn(item.toString());
//							msg.add(item.toString());
//						}
//					}
//				}
//				
//				try {
//					MailTargetDto[] to = null;
//					if(alertMails != null && alertMails.length>0) {
//						to = new MailTargetDto[alertMails.length];
//						int count = 0;
//						for(String mail: alertMails)
//							to[count++]=new MailTargetDto(mail, "");
//					}
//					MailHeaderContextDto mailContext = new MailHeaderContextDto();
//					mailContext.setSubject("Transaction Alert - "+hostIp);
//					mailContext.addToReceivers(to);
//					mailService.send(mailContext, msg, null, true);
//				}catch (Exception e) {
//					e.printStackTrace();
//					log.error(e.getMessage());
//				}
			}else {
				log.warn("alertTransaction:[now:{}][{}][create:{}][start:{}]", _t.getId(),
						DateFormatUtil.transToTime(System.currentTimeMillis()),
						DateFormatUtil.transToTime(_t.getCreateTime()), DateFormatUtil.transToTime(_t.getStartTime()));
				List<StackTraceElement[]> traces = _t.getTransactionTraces();
				log.warn("**************  Transaction Trace [{}]  **************", _t.getId());
				if(traces!=null && !traces.isEmpty()) {
					for (int i = 0, count = traces.size(); i < count; i++) {
						log.warn("Trace [{}] ------------------------------------------------", (i + 1));
						StackTraceElement[] se = traces.get(i);
						for(StackTraceElement item:se)
							log.warn(item.toString());
					}
				}
				log.warn("******************************************************");
			}
		}
	}
}
