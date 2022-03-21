package legion;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.util.BeanUtil;

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
		SAXBuilder saxBuilder = null;
		Document doc = null;

		try {
			saxBuilder = new SAXBuilder();
			doc = saxBuilder.build(_inputStream);
			// 處理Service
			parseService(doc);
		} catch (Exception e) {
			throw e;
		} finally {
			saxBuilder = null;
			doc = null;
		}
	}

	// -------------------------------------------------------------------------------
	/**
	 * 用來暫存ServiceModule.xml的BusinessService中，每一個<Service>標籤內的資料。
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
	/**
	 * ServiceModule.xml用一個jdom2.Document載入，取出當中的ServiceInfo。
	 * @param doc
	 * @throws Exception
	 */
	private void parseService(Document doc) throws Exception {
		// 處理Service
		List<Element> serviceElemList = doc.getRootElement().getChild("BusinessService").getChildren("Service");

		if (serviceElemList == null)
			throw new Exception("註冊檔沒有指定Business Service!!");
		for (Element serviceElem : serviceElemList) {
			// service info
			String sName = serviceElem.getAttributeValue("name");
			String sInterClassName = serviceElem.getAttributeValue("interface");
			String sImpClassName = serviceElem.getAttributeValue("imp");
			ServiceInfo serviceInfo = new ServiceInfo(sName, sInterClassName, sImpClassName);
			serviceInfoMap.put(sInterClassName, serviceInfo);
			// param
			List<Element> paramElemList = serviceElem.getChildren("Parameter");
			if (paramElemList != null && paramElemList.isEmpty()) {
				for (Element paramElem : paramElemList) {
					serviceInfo.addParam(paramElem.getAttributeValue("name"), paramElem.getAttributeValue("value"));
				}
			}
		}
	}
	
	public synchronized boolean regiesterService(InputStream _is) throws Exception{
		try {
			serviceInstanceMap.clear();
			serviceInfoMap.clear();
			parse(_is);
			initService();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	private synchronized void initService() throws Exception{
		for(ServiceInfo serviceInfo: serviceInfoMap.values()) {
			initService(serviceInfo, false);
		}
	}

	private synchronized void reInitService(String _name, String _interface, String _imp,
			Map<String, String> _parameters) throws Exception {
		ServiceInfo serviceInfo = new ServiceInfo(_name, _interface, _imp);
		serviceInfoMap.put(_interface, serviceInfo);
		
		if(_parameters!=null && !_parameters.isEmpty()) {
			_parameters.forEach(serviceInfo::addParam);
		}
		initService(serviceInfo, true);
	}
	
	private void initService(ServiceInfo serviceInfo, boolean _reInit)throws Exception{
		// 判別是否已註冊
		Class<?> serviceItf = Class.forName(serviceInfo.itf);
		if(!_reInit && serviceInstanceMap.containsKey(serviceItf)) {
			log.info("[{}]", serviceInfo.itf);
			return; // 已經註冊，且_reInit=false，不重覆註冊，結束。
		}
		
		// 釋放原有服務
		destroyService(serviceItf);
		
		// 此時一定是未註冊狀態->初始服務
		Class<?> serviceImp = Class.forName(serviceInfo.imp);
		// 驗證ServiceImp是否為實作指定的BusinessService介面。
		if (!serviceItf.isAssignableFrom(serviceImp)) {
			log.error("BusinessService.xml.Service[{} - {}]沒有實作指定的服務[{}]介面!!", serviceInfo.name, serviceInfo.imp,
					serviceInfo.itf);
			throw new Exception("BusinessService.xml.Service[" + serviceInfo.name + " - " + serviceInfo.imp
					+ "]沒有實作指定的服務[" + serviceInfo.itf + "]介面!!");
		}
		// 驗證ServiceImp是否實作BusinessService介面
		if(!BusinessService.class.isAssignableFrom(serviceImp)) {
			log.error("BusinessService.xml.Service[{} - {}]沒有實作BusinessService介面!!", serviceInfo.name, serviceInfo.imp);
			throw new Exception("BusinessService.xml.Service[" + serviceInfo.name + " - " + serviceInfo.imp
					+ "]沒有實作BusinessService介面!!");
		}
		
		// 建構Service物件
		BusinessService service = null;
		BusinessService serviceInstance =(BusinessService) BeanUtil.serviceInstance(serviceInfo.imp);
		log.warn("serviceInstance: {}", serviceInstance);
		log.info("**.建構 Business Service[{}]-Interface[{}]", serviceInfo.imp, serviceItf);
		
		// 註冊參數
		if (serviceInfo.params != null && !serviceInfo.params.isEmpty()) {
			for (String key : serviceInfo.params.keySet()) {
				log.info("****.parameter[{}][{}]", key, serviceInfo.params.get(key));
			}
			serviceInstance.register(serviceInfo.params);
		} else
			serviceInstance.register(new HashMap<>());
		
		// 判別是否要做DynamicAspectLogic Proxy
		// TODO 先略過...  有需要再說
		
		service = serviceInstance;
		
		/* 註冊 */
		// 註冊前再次判斷是否該服務已經透過其他執行緒完成建構。
		// 若有，則保留原註冊，廢棄目前建構的服務實體；否則，保留此次建構的服務實體。
		synchronized (serviceInstanceMap) {
			if (serviceInstanceMap.containsKey(serviceItf))
				return;
			else
				serviceInstanceMap.put(serviceItf, serviceInstance);
		}
		
		// 觸發事件
		fireRegistedResource(serviceItf, service);

	}
	
	public void destroyService(Class<?> _serviceInter) {
		BusinessService serviceInstance = serviceInstanceMap.get(_serviceInter);
		if(serviceInstance==null)
			return;
		else {
			try {
				serviceInstance.destroy();
			}catch (Exception e) {
				log.info("destroy business service [{}] fail: {}", _serviceInter.getName(), e.getMessage());
				e.printStackTrace();
			}
		}
		// 移除註冊
		serviceInstanceMap.remove(_serviceInter);
		return;
	}
	
	public void destroyAllServices() {
		synchronized (serviceInstanceMap) {
			List<Class<?>> serviceList = new ArrayList<>(serviceInstanceMap.keySet());
			for(Class<?> service: serviceList) {
				destroyService(service);
			}
		}
	}

}
