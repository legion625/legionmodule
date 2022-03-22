package legion;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.util.BeanUtil;

public class DataServiceFactory {
	private static Logger log = LoggerFactory.getLogger(DataServiceFactory.class);

	// -------------------------------------------------------------------------------
	/* Singleton */
	private static final DataServiceFactory INSTANCE = new DataServiceFactory();

	private DataServiceFactory() {
	}

	public static DataServiceFactory getInstance() {
		return INSTANCE;
	}

	// -------------------------------------------------------------------------------
	private volatile ConcurrentHashMap<Class<?>, IntegrationService> serviceInstanceMap = new ConcurrentHashMap<>();
	private volatile ConcurrentHashMap<String, ServiceInfo> serviceInfoMap = new ConcurrentHashMap<>();
	private volatile ConcurrentLinkedQueue<DataFactoryListener> listeners = new ConcurrentLinkedQueue<>();

	/**
	 * 用來暫存ServiceModule.xml的IntegrationService中，每一個<Service>標籤內的資料。
	 * 
	 * @author Min-Hua Chao
	 *
	 */
	private static class ServiceInfo {
		private String name = "", itf = "", imp = "";
		private Map<String, String> params;

		ServiceInfo(String name, String itf, String imp) {
			this.name = name;
			this.itf = itf;
			this.imp = imp;
		}

		void addParam(String _key, String _value) {
			if (params == null)
				params = new HashMap<>();
			params.put(_key, _value);
		}
	}

	// -------------------------------------------------------------------------------
	public boolean addListener(DataFactoryListener _listener) {
		return listeners.add(_listener);
	}

	public boolean removeListener(DataFactoryListener _listener) {
		return listeners.remove(_listener);
	}

	// -------------------------------------------------------------------------------
	private void fireRegistedResource(Class<?> _iClass, IntegrationService _iService) {
		for (DataFactoryListener listener : listeners)
			listener.registedResource(_iClass, _iService, this);
	}
	
	// -------------------------------------------------------------------------------
	public synchronized boolean registerService(InputStream _is) {
		serviceInstanceMap.clear();
		serviceInfoMap.clear();
		
		/* parse InputStream */
		SAXBuilder saxBuilder = null;
		Document doc = null;
		try {
			saxBuilder = new SAXBuilder();
			doc = saxBuilder.build(_is);
			parseService(serviceInfoMap, doc);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
			log.error("saxBuilder.build exception: {}", e.getMessage());
			return false;
		} finally {
			saxBuilder = null;
			doc = null;
		}
		
		/* init services */
		for (ServiceInfo serviceInfo : serviceInfoMap.values()) {
			try {
				initService(serviceInfo, false);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
				return false;
			}
		}
		
		return true;
	}
	
	private boolean parseService(ConcurrentHashMap<String, ServiceInfo> _serviceInfoMap, Document _doc) {
		if (_serviceInfoMap == null) {
			log.error("_serviceInfoMap null");
			return false;
		}

		// 處理Service
		List<Element> serviceElemList = _doc.getRootElement().getChild("IntegrationService").getChildren("Service");

		if (serviceElemList == null) {
			log.warn("註冊檔沒有指定Integration Service!!");
			return false;
		}
		for (Element serviceElem : serviceElemList) {
			// service info
			String sName = serviceElem.getAttributeValue("name");
			String sInterClassName = serviceElem.getAttributeValue("interface");
			String sImpClassName = serviceElem.getAttributeValue("imp");

			ServiceInfo serviceInfo = new ServiceInfo(sName, sInterClassName, sImpClassName);
			_serviceInfoMap.put(sInterClassName, serviceInfo);
			// param
			List<Element> paramElemList = serviceElem.getChildren("Parameter");
			log.debug("paramElemList.size(): {}", paramElemList.size());
			if (paramElemList != null && !paramElemList.isEmpty()) {
				for (Element paramElem : paramElemList)
					serviceInfo.addParam(paramElem.getAttributeValue("name"), paramElem.getAttributeValue("value"));
			}
			log.debug("{}\t{}\t{}\t{}", serviceInfo.name, serviceInfo.itf, serviceInfo.imp, serviceInfo.params);

		}
		return true;
	}
	
	private void initService(ServiceInfo serviceInfo, boolean _reInit) throws Exception{
		// 判別是否已註冊
		Class<?> sInfClass = Class.forName(serviceInfo.itf);
		if (!_reInit && serviceInstanceMap.containsKey(sInfClass)) {
			log.info("[{}]", serviceInfo.itf);
			return; // 已經註冊，且_reInit=false，不重覆註冊，結束。
		}

		// 釋放原有服務
		destroyService(sInfClass);
		
		// 此時一定是未註冊狀態->初始服務
		Class<?> sImpClass = Class.forName(serviceInfo.imp);
		// 驗證ServiceImp是否為實作指定的IntegrationService介面。
		if (!sInfClass.isAssignableFrom(sImpClass)) {
			log.error("IntegrationService.xml.Service[{} - {}]沒有實作指定的服務[{}]介面!!", serviceInfo.name, serviceInfo.imp,
					serviceInfo.itf);
			throw new Exception("IntegrationService.xml.Service[" + serviceInfo.name + " - " + serviceInfo.imp
					+ "]沒有實作指定的服務[" + serviceInfo.itf + "]介面!!");
		}
		// 驗證ServiceImp是否實作IntegrationService介面
		if (!IntegrationService.class.isAssignableFrom(sImpClass)) {
			log.error("IntegrationService.xml.Service[{} - {}]沒有實作IntegrationService介面!!", serviceInfo.name, serviceInfo.imp);
			throw new Exception("IntegrationService.xml.Service[" + serviceInfo.name + " - " + serviceInfo.imp
					+ "]沒有實作IntegrationService介面!!");
		}
		
		// 建構Service物件
		IntegrationService service = BeanUtil.serviceInstance(serviceInfo.imp);
		log.info("**.建構 Integration Service[{}]-Interface[{}]", serviceInfo.imp, sInfClass);
		
		// 註冊參數
		if (serviceInfo.params != null && !serviceInfo.params.isEmpty()) {
			for (String key : serviceInfo.params.keySet()) {
				log.info("****.parameter[{}][{}]", key, serviceInfo.params.get(key));
			}
			service.register(serviceInfo.params);
		} else
			service.register(new HashMap<>());
		
		/* 註冊 */
		// 註冊前再次判斷是否該服務已經透過其他執行緒完成建構。
		// 若有，則保留原註冊，廢棄目前建構的服務實體；否則，保留此次建構的服務實體。
		synchronized (serviceInstanceMap) {
			if (serviceInstanceMap.containsKey(sInfClass))
				return;
			else
				serviceInstanceMap.put(sInfClass, service);
		}

		// 觸發事件
		fireRegistedResource(sInfClass, service);
	}
	
	// -------------------------------------------------------------------------------
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
			log.error("[Integration]無此服務[{}]", _class);
		}
		return result;
	}
	
	// -------------------------------------------------------------------------------
	public void destroyService(Class<?> _sItfClass) {
		IntegrationService serviceInstance = serviceInstanceMap.get(_sItfClass);
		if (serviceInstance == null)
			return;
		else {
			try {
				serviceInstance.destroy();
			} catch (Exception e) {
				log.info("destroy integration service [{}] fail: {}", _sItfClass.getName(), e.getMessage());
				e.printStackTrace();
			}
		}
		// 移除註冊
		serviceInstanceMap.remove(_sItfClass);
		return;
	}

	public void destroyAllServices() {
		synchronized (serviceInstanceMap) {
			List<Class<?>> serviceList = new ArrayList<>(serviceInstanceMap.keySet());
			for (Class<?> service : serviceList)
				destroyService(service);
		}
	}
}
