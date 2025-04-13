package legion.datasource.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import legion.DebugLogMark;
import legion.TestLogMark;
import legion.datasource.DatasourceInfo;
import legion.datasource.DefaultDatasourceInfoDto;
import legion.datasource.UrlDs;
import legion.datasource.source.Dso;
import legion.util.DataFO;
import legion.util.LogUtil;

public class DSManagerDao {
	private static Logger log = LoggerFactory.getLogger(DSManagerDao.class);
//	private static Logger log = LoggerFactory.getLogger(DebugLogMark.class);

	/* 若是SourceConfiguration沒有Resource註冊資料，則預設以該檔案進行初始資料來源。 */
	private static final String DEF_CFG_FILE_DATASOURCE = "/opt/DataSource/datasource.xml";
	/* 資料來源物件儲存區，以Resource Name為索引 */
	private static volatile ConcurrentHashMap<String, Dso> dsCache = new ConcurrentHashMap<>();
	private static SourceConfiguration sourceCfg;
	private static volatile DSManagerDao instance = new DSManagerDao();
	private Lock lock;

	private DSManagerDao() {
		log.debug("建構 DSManager 物件");
		lock = new ReentrantLock();
	}

	protected static DSManagerDao getInstance() {
		return instance;
	}

	protected Object getConn(UrlDs _urlDs) {
		/**
		 * 由UrlDs取得SourceName，依據SourceName取得相對已註冊的資料來源。
		 * 未來考量若是UrlDs沒有指定的SourceName，則藉由本身的UrlDs資訊動態建構相對DSO，並進行註冊。
		 */
		if (DataFO.isEmptyString(_urlDs.getName())) {
			log.warn("Source Name is Empty [{}]", _urlDs.getUrl());
			return null;
		}
		try {
			Dso dso = dsCache.get(_urlDs.getName());
			if (dso == null) {
				if (sourceCfg == null) {
					// 進行預設資料來源定義初始
					sourceCfg = SourceConfiguration.getInstance();
					registDefaultDatasource();
				}
				// 取得source設定資訊
				ResourceInfo resourceCfg = sourceCfg.getSource(_urlDs.getName());
				if (resourceCfg == null)
					return null;

				//
				String cls = resourceCfg.getParameter(ResourceInfo.Resource_IMP_CLASS);
//				log.error("cls: {} ", cls);
				dso = (Dso) Class.forName(cls).newInstance();
				log.debug("dso.getUrl(): {} ", dso.getUrl());
				if (!dso.initial(resourceCfg)) {
					log.error("dso.initial return false.");
					return null;
				}
				log.debug("dso.getUrl(): {} ", dso.getUrl());
				dsCache.put(_urlDs.getName(), dso);
				log.debug("dso.getUrl(): {} ", dso.getUrl());
			}
			log.debug("dso.getUrl(): {} ", dso.getUrl());
			
			
			return dso.getConn(_urlDs);
		} catch (Exception e) {
			log.error("getConn[{}] Error: {}", _urlDs.getName(), e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	
	/** 取得已經建構的DataSource */
	protected DatasourceInfo getDatasourceInfo(String _resourceName) {
		Dso dso = dsCache.get(_resourceName);
		DefaultDatasourceInfoDto item = new DefaultDatasourceInfoDto();
		item.setName(dso.getName());
		item.setUrl(dso.getUrl());
		item.setActive(dso.getActive());
		item.setIdle(dso.getIdle());
		item.setMaxActive(dso.getMaxActive());
		item.setMaxIdle(dso.getMaxIdle());
		item.setMaxWait(dso.getMaxWait());
		item.setValidationQuery(dso.getValidationQuery());
		item.setAlertMail(dso.getAlertMail());
		return item;
	}
	
	/** 取得已經建構的DataSource列表 */
	protected List<DatasourceInfo> getDatasourceInfos() {
		List<DatasourceInfo> list = new ArrayList<>();
		lock.lock();
		try {
			for (Dso dso : dsCache.values()) {
				DefaultDatasourceInfoDto item = new DefaultDatasourceInfoDto();
				item.setName(dso.getName());
				item.setUrl(dso.getUrl());
				item.setActive(dso.getActive());
				item.setIdle(dso.getIdle());
				item.setMaxActive(dso.getMaxActive());
				item.setMaxIdle(dso.getMaxIdle());
				item.setMaxWait(dso.getMaxWait());
				item.setValidationQuery(dso.getValidationQuery());
				item.setAlertMail(dso.getAlertMail());
				list.add(item);
			}
		} finally {
			lock.unlock();
		}
		return list;
	}

	private void registDefaultDatasource() {
		try {
			FileInputStream fis = new FileInputStream(DEF_CFG_FILE_DATASOURCE);
			if (fis != null) {
				registerDatasourceXml(fis, false);
			}
		} catch (FileNotFoundException e) {
			log.error("regisDefaultDs[{}] Error", DEF_CFG_FILE_DATASOURCE);
			e.printStackTrace();
		}
	}

	/** 註冊資料來源資訊，並且清空原有的註冊資訊 */
	protected void registerDatasourceXml(InputStream _dsXmlStream) {
		registerDatasourceXml( _dsXmlStream, true);
	}

	/**
	 * 註冊資料來源資訊，可以透過參數進行清空或是累加模式
	 * 
	 * @param _dsXmlStream
	 * @param _reBuild
	 */
	protected void registerDatasourceXml(InputStream _dsXmlStream, boolean _reBuild) {
		log.debug("DSManagerDao::registerDatasourceXml start");
		if (sourceCfg == null)
			sourceCfg = SourceConfiguration.getInstance();
		sourceCfg.registerDsXml(_dsXmlStream, _reBuild);
		// 清空目前的資料來源暫存區，並且關閉各Dso物件。
		if (dsCache != null) {
			lock.lock();
			try {
				for (Object key : dsCache.keySet()) {
					try {
						dsCache.get(key).close();
					} catch (Exception e) {
						LogUtil.log(log, e, Level.ERROR);
					}
				}
			} finally {
				dsCache.clear();
				lock.unlock();
			}
		}
		log.debug("DSManagerDao::registerDatasourceXml end");
	}
	
	/** 釋放指定的資料來源 */
	protected boolean releaseDatasource(UrlDs _urlDs) {
		return releaseDatasource(_urlDs.getName());
	}
	
	/** 釋放指定的資料來源 */
	protected boolean releaseDatasource(String _datasourceName) {
		if (DataFO.isEmptyString(_datasourceName))
			return false;
		Dso dso = dsCache.get(_datasourceName);
		if (dso == null)
			return true;

		if (dso.getActive() > 0) {
			log.error("Datasource[{}]有運作中Connection，無法進行釋放。", _datasourceName);
			return false;
		}

		lock.lock();

		try {
			dsCache.remove(_datasourceName);
			dso.close();
			return true;
		} catch (Exception e) {
			log.error("[{}] Dso.close error.", _datasourceName);
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return false;
	}

	protected void releaseAllDatasources() {
		lock.lock();
		try {
			List<String> sourceNames = new ArrayList<>(dsCache.keySet());
			for (String sourceName : sourceNames) {
				Dso dso = dsCache.get(sourceName);
				if (dso == null)
					continue;

				if (dso.getActive() > 0)
					log.error("Datasource[{}]有運作中Connection，無法進行釋放。", sourceName);

				dsCache.remove(sourceName);
				try {
					dso.close();
				} catch (Exception e) {
					log.error("[{}] Dso.close error.", sourceName);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

}
