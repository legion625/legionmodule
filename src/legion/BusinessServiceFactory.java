package legion;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Min-Hua Chao
 *
 */
public class BusinessServiceFactory {
	private static Logger log = LoggerFactory.getLogger(BusinessServiceFactory.class);

	// -------------------------------------------------------------------------------
	/* Singleton */
	private static final BusinessServiceFactory INSTANCE = new BusinessServiceFactory();

	private BusinessServiceFactory() {
	}

	public static BusinessServiceFactory getInstance() {
		return INSTANCE;
	}

	// -------------------------------------------------------------------------------
	private volatile ConcurrentHashMap<Class<?>, BusinessService> serviceInstanceMap = new ConcurrentHashMap<>();
	private volatile ConcurrentHashMap<String, ServiceInfo> serviceInfoMap = new ConcurrentHashMap<>();
	private volatile ConcurrentLinkedQueue<BusinessFactoryListener> listeners = new ConcurrentLinkedQueue<>();

	// -------------------------------------------------------------------------------
	public boolean addListener(BusinessFactoryListener _listener) {
		return listeners.add(_listener);
	}

	public boolean removeListener(BusinessFactoryListener _listener) {
		return listeners.remove(_listener);
	}

	private void fireRegistedResource(Class<?> _iClass, BusinessService _iService) {
		for (BusinessFactoryListener listener : listeners) {
			listener.registedResource(_iClass, _iService, this);
		}
	}

	public <T> T getService(Class<T> _class) {
		if (!serviceInstanceMap.containsKey(_class)) {
			// 檢查是否有服務資訊，無則先初始該服務。
			if (serviceInfoMap.containsKey(_class.getName())) {
				try {
					initService(serviceInfoMap.get(_class.getName()), false);
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
			}
		}

		T result = (T) serviceInstanceMap.get(_class);
		if (result == null) {
			log.error("[Business]無此服務[{}]", _class);
		}
		return result;
	}

	private void parse(InputStream _inputStream) throws Exception {
		// TODO
	}
	
	// -------------------------------------------------------------------------------
	/**
	 * @author Min-Hua Chao
	 *
	 */
	private static class ServiceInfo {
		private String name = "", interf = "", imp = "";
		private Map<String, String> params;

		ServiceInfo(String name, String interf, String imp) {
			this.name = name;
			this.interf = interf;
			this.imp = imp;
		}
		
		void addParam(String _key, String _value) {
			
		}

	}

}
