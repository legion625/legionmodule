package legion.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

import legion.BusinessServiceFactory;
import legion.IntegrationService;

public class InitWebAppsListener implements ServletContextListener {

	private Logger log = LoggerFactory.getLogger(InitWebAppsListener.class);

	// -------------------------------------------------------------------------------
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("InitWebAppListener.contextInitialized......");
		log.warn("InitWebAppListener.contextInitialized......");
//		initLog(sce); TODO
//		initSystemInfo(sce); TODO
//		registerClientRmiSSL(sce); TODO
//		initDataSource(sce); TODO
//		initIntegrationServiceModule(sce); TODO
		initBusinessServiceModule(sce);
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
//		DataServiceFactory.getInstance().destoryAllServices(); TODO
		
		log.info("Destroy datasources......");
//		destroyDataSources(); TODO
		log.info("Destroy personal notifications......");
//		destroyPersonalNotifications(); TODO
		
	}
	
	// -------------------------------------------------------------------------------
	protected void initBusinessServiceModule(ServletContextEvent sce) {
		log.info("initBusinessServiceModule......");
		ServletContext sc = sce.getServletContext();
		FileInputStream fis = null;
		String filePath = sc.getRealPath("/")+sc.getInitParameter("service-module-file");
		log.debug("sc.getInitParameter(\"service-module-file\"): {}", sc.getInitParameter("service-module-file"));
		log.info("BusinessServiceModule filePath: {}", filePath);
		// Business Service
		try {
			fis = new FileInputStream(filePath);
			BusinessServiceFactory.getInstance().regiesterService(fis);
			
//			log.info("*.Legion Module Version [{}]", LegionContext.getInstance().getVersion()); // XXX 這裡在讀BusinessServiceModule，有需要SHOW版本嗎?
//			LegionContext.getInstance().getSystemInfo().putAttribute("legionmodule.version"
//					,LegionContext.getInstance().getVersion()
//					);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}finally {
			try {
				if(fis!=null)fis.close();
			}catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		//
	}
	
	// -------------------------------------------------------------------------------
	@Deprecated
	private void initServiceModule() {
//		log.warn("initBusinessService");
		System.out.println("initBusinessService");
		
//		String path1 =  getClass().getProtectionDomain().getCodeSource().getLocation().toString();
//		System.out.println("path1: " + path1);
		
		String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
		System.out.println("path: " + path);
		 path=path.replace('/', '\\'); // 將/換成\    
         path=path.replace("file:", ""); //去掉file:    
         path=path.replace("classes\\", ""); //去掉class\    
         path=path.substring(1); //去掉第一個\,如 \D:\JavaWeb...    
         path+="ServiceModule.xml";
         
         System.out.println("path: " + path);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			File file = new File(path);
			Document document = db.parse(file);
            document.getDocumentElement().normalize();
            
            Element rootElement =document.getDocumentElement(); 
            System.out.println("Root Element: " + rootElement.getNodeName());
//            Node n = rootElement.getNextSibling();
			NodeList serviceNl = rootElement.getChildNodes();
			for(int i=0;i<serviceNl.getLength();i++) {
			Node n = serviceNl.item(i);
			
			/* BusinessService */
			if (n != null && "BusinessService".equals(n.getNodeName())) {
				// TODO
			}
			
			/* IntegrationService */
			if (n != null && "IntegrationService".equals(n.getNodeName())) {
				NodeList itgServiceNodeList = n.getChildNodes();
				for(int j=0;j<itgServiceNodeList.getLength();j++) {
					/* each service */
					Node itgServiceNode = itgServiceNodeList.item(j);
					if(itgServiceNode.getNodeType()==Node.ELEMENT_NODE) {
						Element eElement = (Element) itgServiceNode;
						String serviceClass = eElement.getAttribute("class");
						String serviceImpClass = eElement.getAttribute("imp");
	                    System.out.println("serviceClass: " + serviceClass);
	                    System.out.println("serviceImpClass: " + serviceImpClass);
	                    
	                    /* dynamic load class */
	                    Class c = Class.forName(serviceImpClass);
	                    IntegrationService s = (IntegrationService) c.getDeclaredConstructor().newInstance();
	                    System.out.println("s: " +s);
	                    
	                    /* config params */
						Map<String, Object> params = new HashMap<>();
						
						NodeList paramNl = eElement.getElementsByTagName("param");
						for(int k=0;k<paramNl.getLength();k++) {
							Node paramNode = paramNl.item(k);
							params.put(((Element) paramNode).getAttribute("name"),
									((Element) paramNode).getAttribute("src"));
						}
						
						System.out.println("params: " + params);
	                    
					}
				}
			}
			}
			
			
			
			
            
//            
//            System.out.println("document.getElementById(\"businessService\"): " + document.getElementById("businessService"));
//            System.out.println("document.getElementById(\"BusinessService\"): " + document.getElementById("BusinessService"));
//            
//            
//            Element bizRootElem = document.getElementById("businessService");
//            NodeList bizServiceNodeList = bizRootElem.getChildNodes();
//            for(int i=0;i<bizServiceNodeList.getLength();i++) {
//				Node n = bizServiceNodeList.item(i);
//				if(n.getNodeType()==Node.ELEMENT_NODE) {
//					Element eElement = (Element) n;
//                    System.out.println("class: " + eElement.getAttribute("class"));
//                    System.out.println("imp: " + eElement.getAttribute("imp"));
//				}
//            }
            
            
            
            
            
            
            
            
            
//            NodeList nList = document.getElementsByTagName("IntegrationService");
//            System.out.println("----------------------------");
//            for (int temp = 0; temp < nList.getLength(); temp++) {
//                Node nNode = nList.item(temp);
//                System.out.println("\nCurrent Element :" + nNode.getNodeName());
//                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element eElement = (Element) nNode;
                    
//                    System.out.println("Employee id : " + eElement.getAttribute("id"));
//                    System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
//                    System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
//                    System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());
//                }
//            }
			
		} catch (ParserConfigurationException | SAXException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
				
	}

	
	
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
