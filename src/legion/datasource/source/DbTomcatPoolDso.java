package legion.datasource.source;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import legion.datasource.UrlDs;
import legion.datasource.manager.ResourceInfo;
import legion.util.LogUtil;
import legion.util.RetryUtil;

/**
 * 建立資料庫類型的資料來源，底層是以Tomcat JDBC Pool 相關實作套件來運作
 * 
 * @author Min-Hua Chao
 *
 */
public class DbTomcatPoolDso extends Dso {
	private Logger log = LoggerFactory.getLogger(DbTomcatPoolDso.class);

	private volatile DataSource ds;

	// -------------------------------------------------------------------------------
	public DataSource getDataSource() {
		return ds;
	}

	// -------------------------------------------------------------------------------
	@Override
	public int getActive() {
		if (ds != null)
			return ds.getActive();
		return -1;
	}

	@Override
	public int getIdle() {
		if (ds != null)
			return ds.getIdle();
		return -1;
	}

	@Override
	public int getMaxActive() {
		if (ds != null)
			return ds.getMaxActive();
		return -1;
	}

	@Override
	public int getMaxIdle() {
		if (ds != null)
			return ds.getMaxIdle();
		return -1;
	}

	@Override
	public int getMaxWait() {
		if (ds != null)
			return ds.getMaxWait();
		return -1;
	}

	@Override
	public String getValidationQuery() {
		if (ds != null)
			return ds.getValidationQuery();
		return "-1";
	}

	// -------------------------------------------------------------------------------
	@Override
	public void close() {
		if (ds != null)
			try {
				ds.close();
			} catch (Throwable e) {
				LogUtil.log(log, e, Level.ERROR);
			}
	}

	@Override
	public Object getConn(UrlDs _urlDs) {
		if (ds == null)
			return null;

		/* 設定嘗試取得連線次數3次，嘗試間隔0.2秒。 */
		Connection conn = RetryUtil.workRetryWithException(3, 200, () -> {
			Connection _conn = null;
			try {
				_conn = ds.getConnection();
			} catch (Exception e) {
			}
			return _conn;
		}, _conn -> {
			if (_conn != null)
				return true;
			return false;
		});

		if (conn != null)
			return conn;

		log.error("Get [{}] Connection Error!!", name);
		mailConnectionError("Get " + name + "Connection Error!!");
		return null;
	}

	// -------------------------------------------------------------------------------
	@Override
	public synchronized boolean initial(ResourceInfo _cfg) {
		if(!super.initial(_cfg))
			return false;
		// 先取得基本預設設定
		final Properties properties = createDefaultProperties();
		Map<String, String> params = _cfg.getParameters();
		
		// DataSourceFactory::url
		if (params.get(ResourceInfo.Resource_URL) != null) {
			url = params.get(ResourceInfo.Resource_URL);
			properties.setProperty("url", url);
			params.remove(ResourceInfo.Resource_URL);
		}
		
		// DataSourceFactory::driverClassName
		if (params.get(ResourceInfo.SOURCE_DRIVER_CLASS_NAME) != null) {
			properties.setProperty("driverClassName", params.get(ResourceInfo.SOURCE_DRIVER_CLASS_NAME));
			params.remove(ResourceInfo.SOURCE_DRIVER_CLASS_NAME);
		}
		
		// DataSourceFactory::username
		if(params.get(ResourceInfo.Resource_USER_NAME)!=null) {
			properties.setProperty("username", params.get(ResourceInfo.Resource_USER_NAME));
			params.remove(ResourceInfo.Resource_USER_NAME);
		}
		
		// DataSourceFactory::password
		if(params.get(ResourceInfo.Resource_PASSWORD)!=null) {
			String sourcePpqwwdd = params.get(ResourceInfo.Resource_PASSWORD);
			String realPpqwwdd = null;
			if (params.get(ResourceInfo.Resource_MASK_YEK) != null) {
//				// 有指定加密檔，進行密碼解碼處理程序
//				String maskYekSource = params.get(ResourceInfo.Resource_MASK_YEK);
//				log.debug("指定加密金鑰檔來源: {}", maskYekSource);
//				realPpqwwdd = YekManage.deMask(sourcePpqwwdd, maskYekSource); // TODO not implemented yet...
				log.error("Resource_MASK_YEK not implemented yet.");
				return false;
			} else {
				realPpqwwdd = sourcePpqwwdd;
			}
			properties.setProperty("password", realPpqwwdd);
			params.remove(ResourceInfo.Resource_PASSWORD);
		}
		
		// DataSourceFactory::validationQuery
		if (params.get(ResourceInfo.SOURCE_VALIDATION_QUERY) != null) {
			properties.setProperty("validationQuery", params.get(ResourceInfo.SOURCE_VALIDATION_QUERY));
			params.remove(ResourceInfo.SOURCE_VALIDATION_QUERY);
		}
		
		// DataSourceFactory::maxActive
		if (params.get(ResourceInfo.SOURCE_MAX_ACTIVE) != null) {
			properties.setProperty("maxActive", params.get(ResourceInfo.SOURCE_MAX_ACTIVE));
			params.remove(ResourceInfo.SOURCE_MAX_ACTIVE);
		}
		
		// DataSourceFactory::maxIdle
		if (params.get(ResourceInfo.SOURCE_MAX_IDLE) != null) {
			properties.setProperty("maxIdle", params.get(ResourceInfo.SOURCE_MAX_IDLE));
			params.remove(ResourceInfo.SOURCE_MAX_IDLE);
		}
		
		// DataSourceFactory::maxWait
		if (params.get(ResourceInfo.SOURCE_MAX_WAIT) != null) {
			properties.setProperty("maxWait", params.get(ResourceInfo.SOURCE_MAX_WAIT));
			params.remove(ResourceInfo.SOURCE_MAX_WAIT);
		}
		
		// DataSourceFactory::testOnBorrow
		if (params.get(ResourceInfo.SOURCE_TEST_ON_BORROW) != null) {
			properties.setProperty("testOnBorrow", params.get(ResourceInfo.SOURCE_TEST_ON_BORROW));
			params.remove(ResourceInfo.SOURCE_TEST_ON_BORROW);
		}
		
		// 移除正規參數
		params.remove(ResourceInfo.Resource_NAME);
		params.remove(ResourceInfo.Resource_ALERT_MAIL);
		params.remove(ResourceInfo.Resource_IMP_CLASS);
		
		// 剩下未名確定義的參數，直接以其key-value作為對應參數
		for (String key : params.keySet())
			properties.setProperty(key, params.get(key));
		
		// TODO
		
		// 初始
		DataSourceFactory factory = new DataSourceFactory();
		try {
			ds = (DataSource) factory.createDataSource(properties);
		} catch (Exception e) {
			LogUtil.log(log, e, Level.ERROR);
			return false;
		}
		
		return true;
	}

	private static Properties createDefaultProperties() {
		Properties p = new Properties();
		/*
		 * The maximum number of active connections that can be allocated from this pool
		 * as the same time. The default value is 100.
		 */
		p.setProperty("maxActive", "100");

		/*
		 * The maximum number of connections that should be kept in the pool at all
		 * times. Default value is maxActive:100. Idle connections are checked
		 * periodically (if enabled) and connections that been idle for longer than
		 * minEvictableIdleTimeMillis will be released. (also see testWhileIdle)
		 */
		p.setProperty("maxIdle", "10");

		/*
		 * The minimum number of established connections that should be kept in the pool
		 * at all times. The connection pool can shrink below this number if validation
		 * queries fail. Default value is derived from initialSize:10 (also see
		 * testWhileIdle)
		 */
		p.setProperty("minIdle", "5");

		/*
		 * The minimum amount of time an object may sit idle in the pool before it is
		 * eligible for eviction. The default value is 60000 (60 seconds).
		 */
		p.setProperty("minEvictableIdleTimeMillis", "30000");

		/*
		 * The initial number of connections that are created when the pool is started.
		 * Default value is 10.
		 */
		p.setProperty("initialSize", "0");

		/*
		 * The maximum number of milliseconds that the pool will wait (when there are no
		 * available connections) for a connection to be returned before throwing an
		 * exception. Default value is 30000 (30 seconds).
		 */
		p.setProperty("maxWait", "10000");

		/*
		 * The indication of whether objects will be validated before being borrowed
		 * from the pool. If the object fails to validate, it will be dropped from the
		 * pool, and we will attempt to borrow another. NOTE - for a true value to have
		 * any effect, the validationQuery parameter must be set to a non-null string.
		 * Default value is false in order to have a more efficient validation (see
		 * validationInterval). Default value is false.
		 */
		p.setProperty("testOnBorrow", "true");

		/*
		 * The indication of whether objects will be validated before being returned to
		 * the pool. NOTE - for a true value to have any effect, the validationQuery
		 * parameter must be set to a non-null string. The default value is false.
		 */
		p.setProperty("testOnReturn", "false");

		/*
		 * The indication of whether objects will be validated by the idle object
		 * evictor (if any). If an object fails to validate, it will be dropped from the
		 * pool. NOTE - for a true value to have any effect, the validationQuery
		 * parameter must be set to a non-null string. The default value is false and
		 * this property has to be set in order for the pool cleaner/test thread is to
		 * run (also see timeBetweenEvictionRunsMillis).
		 */
		p.setProperty("testWhileIdle", "false");

		/*
		 * The number of milliseconds to sleep between runs of the idle connection
		 * validation/cleaner thread. This value should not be set under 1 second. It
		 * dictates how often we check for idle, abandoned connections, and how often we
		 * validate idle connections. The default value if 5000 (5 seconds).
		 */
		p.setProperty("timeBetweenEvictionRunsMillis", "30000");

		/*
		 * Flag to log stack traces for aplication code which abandoned a connection.
		 * Logging of abandoned connections adds overhead for every connection borrow
		 * because a stack trace has to be generated. The default value is false.
		 */
		p.setProperty("logAbandoned", "true");

		/*
		 * Flag to remove abandoned connections if they exceed the
		 * removeAbandonedTimeout. If set to true, a connection is considered abandoned
		 * and eligible for removal if it has been in use longer than the
		 * removeAbandonedTimeout. Setting this to true can recover db connections from
		 * applications that fail to close a connection. (See also logAbandoned.) The
		 * default value if false. (See also suspectTimeout.)
		 */
		p.setProperty("removeAbandoned", "false");

		/*
		 * Timeout in seconds before an abandoned (in use) connection can be removed.
		 * The default value is 60 (60 seconds). The value should be set to the longest
		 * running query your applications might have. (See also suspectTimeout.)
		 */
		p.setProperty("removeAbandonedTimeout", "600");

		// Tomcat JDBC Enhanced Attributes

		/*
		 * Avoid excess validation, only run validation at most at this frequency - time
		 * in milliseconds. If a connection is due for validation, but has been
		 * validated previously within this interval, it will not be validated again.
		 * The default value is 30000 (30 seconds).
		 */
		p.setProperty("validationInterval", "30000");

		/* Register the pool with JMX or not. The default value is true. */
		p.setProperty("jmxEnabled", "true");

		/*
		 * Timeout value in seconds. Default value is 0. Similar to the
		 * removeAbandonedTimeout value but instead of treating the connection as
		 * abandoned, and potentially closing the connection, this simply logs the
		 * warning if logAbandoned is set to true. If this value is equal or less than
		 * 0, no suspect checking will be performed. Suspect checking only takes place
		 * if the timeout value is larger than 0 and the connection was not abandoned of
		 * if abandon check is disabled. If a connection is suspect a WARN message gets
		 * logged and a JMX notification gets sent once. (See also logAbandoned,
		 * removeAbandoned, and removeAbandonedTimeout.)
		 */
		p.setProperty("suspectTimeout", "600");

		return p;
	}

}
