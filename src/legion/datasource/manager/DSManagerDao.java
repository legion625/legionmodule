package legion.datasource.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.datasource.UrlDs;
import legion.util.DataFO;

public class DSManagerDao {
	private static Logger log = LoggerFactory.getLogger(DSManagerDao.class);

	/* 若是SourceConfiguration沒有Resource註冊資料，則預設以該檔案進行初始資料來源。 */
	private static final String DEF_CFG_FILE_DATASOURCE = "/opt/DataSource/datasource.xml";
	/* 資料來源物件儲存區，以Resource Name為索引 */
	private static volatile ConcurrentHashMap<String, DSO> dsCache = new ConcurrentHashMap<>();
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
			DSO dso = dsCache.get(_urlDs.getName());
			if (dso = null) {
				if (sourceCfg == null) {
					// 進行預設資料來源定義初始
					sourceCfg = SourceConfiguration.getInstance();
					registDefaultDS();
				}
				// 取得source設定資訊
				ResourceInfo resourceCfg = sourceCfg.getSource(_urlDs.getName());
				if (resourceCfg == null)
					return null;

				//
				String cls = resourceCfg.getParameter(ResourceInfo.Resource_IMP_CLASS);
				dso = (DSO) Class.forName(cls).newInstance();
				dso.initial(resourceCfg);
				dsCache.put(_urlDs.getName(), dso);
			}
			return dso.getConn(_urlDs);
		} catch (Exception e) {
			log.error("getConn[{}] Error: {}", _urlDs.getName(), e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

}
