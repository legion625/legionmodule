package legion.datasource.source;

import java.lang.reflect.Method;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import legion.datasource.UrlDs;
import legion.datasource.manager.ResourceInfo;
import legion.util.BeanUtil;
import legion.util.DataFO;
import legion.util.LogUtil;
import legion.util.RetryUtil;

public class RmiDso extends Dso {
	private static Logger log = LoggerFactory.getLogger(RmiDso.class);

	// -------------------------------------------------------------------------------
	private String serviceName = "";
	private String rmiSecurityManager = "";
	private String testOnConnectServiceName = "";
	private boolean testOnConnectService = false;
	private Remote rmiService;

	// -------------------------------------------------------------------------------
	@Override
	public int getActive() {
		return -1;
	}

	@Override
	public int getIdle() {
		return -1;
	}

	@Override
	public int getMaxActive() {
		return testOnConnectService() ? -1 : 0;
	}

	@Override
	public int getMaxIdle() {
		return -1;
	}

	@Override
	public int getMaxWait() {
		return -1;
	}

	@Override
	public String getValidationQuery() {
		return "-1";
	}

	// -------------------------------------------------------------------------------
	@Override
	public void close() {
		rmiService = null;
	}

	@Override
	public Object getConn(UrlDs _urlDs) {
		if (rmiService == null || (testOnConnectService && !testOnConnectService())) {
			/* 設定嘗試取得連線次數3次，間隔200毫秒。 */
			Remote conn = RetryUtil.workRetryWithException(3, 200, () -> initRmiService(), _conn -> _conn != null);
			if (conn != null)
				return conn;

			log.error("Get [{}] remote service error!!", name);
			mailConnectionError("Get " + name + " remote service error!!");
			return null;
		}

		return rmiService;
	}

	// -------------------------------------------------------------------------------
	@Override
	public synchronized boolean initial(ResourceInfo _cfg) {
		if(!super.initial(_cfg))
			return false;
		
		// url
		if (_cfg.getParameter(ResourceInfo.Resource_URL) != null)
			url = _cfg.getParameter(ResourceInfo.Resource_URL);
		
		// serviceName
		if (_cfg.getParameter(ResourceInfo.SERVICE_NAME) != null)
			serviceName = _cfg.getParameter(ResourceInfo.SERVICE_NAME);
		
		// rmiSecurityManager
		if (_cfg.getParameter(ResourceInfo.RMI_SECURITY_MANAGER) != null)
			rmiSecurityManager = _cfg.getParameter(ResourceInfo.RMI_SECURITY_MANAGER);
		
		// testOnConnectServiceName
		if (_cfg.getParameter(ResourceInfo.SERVICE_TEST_ON_CONNECT_SERVICE_NAME) != null) {
			testOnConnectServiceName = _cfg.getParameter(ResourceInfo.SERVICE_TEST_ON_CONNECT_SERVICE_NAME);
			testOnConnectService = true;
		}
		if (initRmiService() == null)
			return false;
		return true;
	}
	
	// -------------------------------------------------------------------------------
	private boolean testOnConnectService() {
		try {
			Method m = rmiService.getClass().getMethod(testOnConnectServiceName, null);
//		return (boolean) m.invoke(rmiService, null);
			return (boolean) m.invoke(rmiService);
		} catch (Exception e) {
			LogUtil.log(log, e, Level.ERROR);
			return false;
		}
	}
	
	private Remote initRmiService() {
		// 建立RmiService...
		
		// 若是有指定SecurityManager，則初始系統環境。
		RMISecurityManager sm;
		if (!DataFO.isEmptyString(rmiSecurityManager)) {
			if(rmiSecurityManager.equalsIgnoreCase("default")) {
				sm = new RMISecurityManager();
				System.setSecurityManager(sm);
			}else {
				sm = BeanUtil.serviceInstance(rmiSecurityManager);
				System.setSecurityManager(sm);
			}
		}
		
		// 取得RMI Registry
		UrlDs urlDs = new UrlDs(url);
		try {
			Registry registry = LocateRegistry.getRegistry(urlDs.getHost(), Integer.parseInt(urlDs.getPort()));
			if (registry == null) {
				log.error("No RMI registry: {}", url);
				return null;
			}
			// 取得服務物件
			rmiService = registry.lookup(serviceName);
			if(rmiService == null)
				log.error("No RMI Registry [service]: {} - [{}]", url, serviceName);
			return rmiService;
		} catch (Exception e) {
			LogUtil.log(log, e, Level.ERROR);
			return null;
		}
	}
}
