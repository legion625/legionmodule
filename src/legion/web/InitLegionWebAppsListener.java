package legion.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.qos.logback.classic.LoggerContext;
import legion.BusinessServiceFactory;
import legion.DataServiceFactory;
import legion.IntegrationService;
import legion.datasource.manager.DSManager;
import legion.util.DataFO;

public abstract class InitLegionWebAppsListener implements ServletContextListener {
	protected Logger log = LoggerFactory.getLogger(InitLegionWebAppsListener.class);

	// -------------------------------------------------------------------------------
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("InitWebAppListener.contextInitialized......");
		initLog(sce); // ok
		initSystemInfo(sce); // ok
//		registerClientRmiSSL(sce); TODO
//		initDataSource(sce); // FIXME 還不能完全正常運作
		initIntegrationServiceModule(sce); // ok
		initBusinessServiceModule(sce); // ok
//		initAspectManager(sce); TODO
//		initMenu(sce); TODO
//		initMimeType(sce); TODO

//		initIppmCrossResourceLink(sce); TODO
//		initLinkModuleView(sce); TODO
//		initQuickLinkModuleView(sce); TODO
//		initPersonalNotification(sce); TODO

		// TODO Auto-generated method stub
//		log.warn("InitWebAppListener.sessionCreated");
//		System.out.println("InitWebAppListener.sessionCreated");
//		initServiceModule();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		log.info("InitWebAppsListener.contextDestroyed......");
		log.info("Destroy all business services......");
		BusinessServiceFactory.getInstance().destroyAllServices();
		log.info("Destroy all integration services......");
		DataServiceFactory.getInstance().destroyAllServices();

		log.info("Destroy datasources......");
		destroyDataSources();
		log.info("Destroy personal notifications......");
//		destroyPersonalNotifications(); TODO

	}
	
	// -------------------------------------------------------------------------------
	// --------------------------------------log--------------------------------------
	protected void initLog(ServletContextEvent sce) {
		// 初始Log4環境...
//		initLog4j(sce); XXX 應該不需要用到log4j...
		// 初始Logback環境
		initLogback(sce);
	}
	
	protected void initLogback(ServletContextEvent sce) {
		ServletContext sc = sce.getServletContext();
		String logfile = sc.getInitParameter("logback-init-file");
		if(DataFO.isEmptyString(logfile)) {
			log.info("inigLogback fail: logfile [{}]", logfile);
			return;
		}
		logfile = sc.getRealPath("/")+logfile;
		log.info("logback-init-file:{}", logfile);
		LogbackConfigure.initLogback(logfile);
	}
	
	private static class LogbackConfigure{
		static void initLogback(String _logfile) {
			// assume SLF4j is bound to logback in the current environment
			ch.qos.logback.classic.LoggerContext logContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			try {
				ch.qos.logback.classic.joran.JoranConfigurator c = new ch.qos.logback.classic.joran.JoranConfigurator();
				c.setContext(logContext);
				// to clear any previous configuration
				logContext.reset();
				c.doConfigure(_logfile);
			}catch (Exception e) {
				LoggerFactory.getLogger(LogbackConfigure.class).error("initLogback fail: {}" , e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	// -------------------------------------------------------------------------------
	// ------------------------------------system-------------------------------------
	/** 創建實作SYstemInfo物件，並且註冊至LegionContext。 */
	protected abstract void initSystemInfo(ServletContextEvent sce);
	
	
	// -------------------------------------------------------------------------------
	// ----------------------------------datasource-----------------------------------
	protected void initDataSource(ServletContextEvent sce) {
		log.info("initDataSource......");
		ServletContext sc = sce.getServletContext();
		FileInputStream fis = null;
		String filePath = sc.getRealPath("/") + sc.getInitParameter("datasource-file");
		log.info("DataSource filePath: {}", filePath);
		try {
			fis = new FileInputStream(filePath);
			DSManager.getInstance().registerDatasourceXml(fis);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("init datasource fail: {}", e.getMessage());
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
	}
	
	protected void destroyDataSources() {
//		try {
			DSManager.getInstance().releaseAllDatasources();
			DSManager.getInstance().stopMonitor();
//		} catch (Exception e) {
//			log.error("destroy Datasource fail .....{}", e.getMessage());
//		}
		try {
			Enumeration<Driver> drivers = DriverManager.getDrivers();
			while (drivers.hasMoreElements()) {
				Driver driver = drivers.nextElement();
				DriverManager.deregisterDriver(driver);
			}
		} catch (Exception e) {
			log.error("deregisterDriver Fail .....{}", e.getMessage());
		}
		// MySQL
		try {
			Class<?> c = getClass().getClassLoader().loadClass("com.mysql.jdbc.AbandonedConnectionCleanupThread");
			java.lang.reflect.Method shutdown = c.getDeclaredMethod("shutdown", new Class[] {});
			shutdown.invoke(null, new Object[] {});
			// AbandonedConnectionCleanupThread.shutdown();
		} catch (Exception e) {
			log.error("MySQL AbandonedConnectionCleanupThread shutdown");
		}
	}

	// -------------------------------------------------------------------------------
	// ---------------------------IntegrationServiceModule----------------------------
	protected void initIntegrationServiceModule(ServletContextEvent sce) {
		log.info("initIntegrationServiceModule......");
		ServletContext sc = sce.getServletContext();
		FileInputStream fis = null;
		String filePath = sc.getRealPath("/") + sc.getInitParameter("service-module-file");
		log.info("IntegrationServiceModule filePath: {}", filePath);
		try {
			fis = new FileInputStream(filePath);
			DataServiceFactory.getInstance().registerService(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
	}

	// -------------------------------------------------------------------------------
	// -----------------------------BusinessServiceModule-----------------------------
	protected void initBusinessServiceModule(ServletContextEvent sce) {
		log.info("initBusinessServiceModule......");
		ServletContext sc = sce.getServletContext();
		FileInputStream fis = null;
		String filePath = sc.getRealPath("/") + sc.getInitParameter("service-module-file");
		log.info("BusinessServiceModule filePath: {}", filePath);
		// Business Service
		try {
			fis = new FileInputStream(filePath);
			BusinessServiceFactory.getInstance().registerService(fis);

//			log.info("*.Legion Module Version [{}]", LegionContext.getInstance().getVersion()); // XXX 這裡在讀BusinessServiceModule，有需要SHOW版本嗎?
//			LegionContext.getInstance().getSystemInfo().putAttribute("legionmodule.version"
//					,LegionContext.getInstance().getVersion()
//					);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
	}

	// -------------------------------------------------------------------------------
//	@Deprecated
//	private void initServiceModule() {
////		log.warn("initBusinessService");
//		System.out.println("initBusinessService");
//
////		String path1 =  getClass().getProtectionDomain().getCodeSource().getLocation().toString();
////		System.out.println("path1: " + path1);
//
//		String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
//		System.out.println("path: " + path);
//		path = path.replace('/', '\\'); // 將/換成\
//		path = path.replace("file:", ""); // 去掉file:
//		path = path.replace("classes\\", ""); // 去掉class\
//		path = path.substring(1); // 去掉第一個\,如 \D:\JavaWeb...
//		path += "ServiceModule.xml";
//
//		System.out.println("path: " + path);
//
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		try {
//			DocumentBuilder db = dbf.newDocumentBuilder();
//			File file = new File(path);
//			Document document = db.parse(file);
//			document.getDocumentElement().normalize();
//
//			Element rootElement = document.getDocumentElement();
//			System.out.println("Root Element: " + rootElement.getNodeName());
////            Node n = rootElement.getNextSibling();
//			NodeList serviceNl = rootElement.getChildNodes();
//			for (int i = 0; i < serviceNl.getLength(); i++) {
//				Node n = serviceNl.item(i);
//
//				/* BusinessService */
//				if (n != null && "BusinessService".equals(n.getNodeName())) {
//					// TODO
//				}
//
//				/* IntegrationService */
//				if (n != null && "IntegrationService".equals(n.getNodeName())) {
//					NodeList itgServiceNodeList = n.getChildNodes();
//					for (int j = 0; j < itgServiceNodeList.getLength(); j++) {
//						/* each service */
//						Node itgServiceNode = itgServiceNodeList.item(j);
//						if (itgServiceNode.getNodeType() == Node.ELEMENT_NODE) {
//							Element eElement = (Element) itgServiceNode;
//							String serviceClass = eElement.getAttribute("class");
//							String serviceImpClass = eElement.getAttribute("imp");
//							System.out.println("serviceClass: " + serviceClass);
//							System.out.println("serviceImpClass: " + serviceImpClass);
//
//							/* dynamic load class */
//							Class c = Class.forName(serviceImpClass);
//							IntegrationService s = (IntegrationService) c.getDeclaredConstructor().newInstance();
//							System.out.println("s: " + s);
//
//							/* config params */
//							Map<String, Object> params = new HashMap<>();
//
//							NodeList paramNl = eElement.getElementsByTagName("param");
//							for (int k = 0; k < paramNl.getLength(); k++) {
//								Node paramNode = paramNl.item(k);
//								params.put(((Element) paramNode).getAttribute("name"),
//										((Element) paramNode).getAttribute("src"));
//							}
//
//							System.out.println("params: " + params);
//
//						}
//					}
//				}
//			}
//
//		} catch (ParserConfigurationException | SAXException | IOException | ClassNotFoundException
//				| InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
//				| NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//			log.error(e.getMessage());
//		}
//
//	}

//	@Override
//	public void sessionCreated(HttpSessionEvent se) {
//		// TODO Auto-generated method stub
//		log.debug("InitWebAppListener.sessionCreated");
//		System.out.println("InitWebAppListener.sessionCreated");
//		
//	}
//
//	@Override
//	public void sessionDestroyed(HttpSessionEvent se) {
//		// TODO Auto-generated method stub
//		
//	}

}
