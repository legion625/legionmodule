package legion;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import legion.util.DataFO;

public class SystemInfoDefault implements ISystemWebInfo{
	// 單位:小時
	private int localeCookieAge = 192; // 預設8天
	private String[] classAnalyseClasspath;
	private AspectManager aspectManager;
	private ServletContext servletContext;
	private Map<String, String> attrs = new HashMap<>();
	private String systemId = "";
	private String systemName = "";
	private String hostIp = "";
	
	// -------------------------------------------------------------------------------
	private final static SystemInfoDefault INSTANCE = new SystemInfoDefault();
	
	private SystemInfoDefault(){
		LegionContext.getInstance().registerSystemInfo(this);
		hostIp = InetAddress.getLocalHost().getHostAddress();
	}
	
	public final static SystemInfoDefault getInstance() {
		return INSTANCE;
	}
	
	// -------------------------------------------------------------------------------
	@Override
	public String getAttribute(String _key) {
		return attrs.get(_key);
	}

	@Override
	public Map<String, String> getAttributes() {
		return new HashMap<>(attrs);
	}

	@Override
	public String[] getClassAnalyseClasspath() {
		return classAnalyseClasspath;
	}

	@Override
	public AspectManager getAspectManager() {
		return aspectManager;
	}

	@Override
	public String getName() {
		if(!DataFO.isEmptyString(systemName))
			return systemName;
		systemName = getAttribute("system.name");
		if(DataFO.isEmptyString(systemName))
			systemName = getAttribute("APP_NAME");
		if(DataFO.isEmptyString(systemName))
			systemName = hostIp;
		return systemName;
	}

	@Override
	public String getId() {
		if(!DataFO.isEmptyString(systemId))
			return systemId;
		systemId = getAttribute("system.id");
		if(DataFO.isEmptyString(systemName))
			systemId = getAttribute("APP_NAME");
		if(DataFO.isEmptyString(systemName))
			systemId = hostIp;
		return systemId;
	}

	@Override
	public String getHostIp() {
		return hostIp;
	}

	@Override
	public String getVersion() {
		return getAttribute("Version");
	}

	@Override
	public void putAttribute(String _key, String _attr) {
		attrs.put(_key, _attr);
		if ("system.main.locale.cookie.age".equals(_key))
			if (DataFO.isInt(_attr))
				localeCookieAge = Integer.parseInt(_attr);
	}

	@Override
	public void setAspectManager(AspectManager aspectManager) {
		this.aspectManager = aspectManager;
	}

	@Override
	public void setClassAnalyseClasspath(String[] classAnalyseClasspath) {
		this.classAnalyseClasspath = classAnalyseClasspath;
		
	}

	// -------------------------------------------------------------------------------
	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	@Override
	public int getLocaleCookieAge() {
		return localeCookieAge;
	}
	
	public void setLocaleCookieAge(int localeCookieAge) {
		this.localeCookieAge = localeCookieAge;
	}
	

}