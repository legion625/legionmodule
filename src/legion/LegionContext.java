package legion;

import java.text.Normalizer.Form;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class LegionContext {
	private static Logger log = LoggerFactory.getLogger(LegionContext.class);
	private String version = "0.0.0";
	private ISystemInfo systemInfo;
	
	// -------------------------------------------------------------------------------
	private final static LegionContext INSTANCE = new LegionContext();

	private LegionContext() {
		try {
			PropertiesConfiguration cfg = new PropertiesConfiguration();
			cfg.setEncoding("UTF-8");
			log.debug("LegionContext.class.getResource(\"legionmodule.properties\"): {}", LegionContext.class.getResource("legionmodule.properties"));
			
			
//			 String resolveName(String name) {
			String name = "legionmodule.properties";
			if (!name.startsWith("/")) {
				String baseName = LegionContext.class.getPackageName();
				if (!baseName.isEmpty()) {
					int len = baseName.length() + 1 + name.length();
					StringBuilder sb = new StringBuilder(len);
					name = sb.append(baseName.replace('.', '/')).append('/').append(name).toString();
				}
			} else {
				name = name.substring(1);
			}
//		        return name;
//			}
			
			log.debug("resolveName: {}", name);
			
			
			cfg.load(LegionContext.class.getResource("legionmodule.properties"));
			version = cfg.getString("Version");		
			log.debug("legionmodule version: {}", version);
//		}catch (ConfigurationException e) {
		}catch(Throwable e){
			e.printStackTrace();
			log.error("設定Legionmodule資料屬性異常。 {}", e.getMessage());
		}
	}
	
	
	
	
	
	public final static LegionContext getInstance() {
		return INSTANCE;
	}
	
	// -------------------------------------------------------------------------------
	public String getVersion() {
		return version;
	}

	public ISystemInfo getSystemInfo() {
		return systemInfo;
	}
	
	public void registerSystemInfo(ISystemInfo systemInfo) {
		this.systemInfo = systemInfo;
	}
	
	// -------------------------------------------------------------------------------
	public String getContextPath() {
		if(systemInfo instanceof ISystemWebInfo) {
			if(((ISystemWebInfo)systemInfo).getServletContext()!=null) {
				return (((ISystemWebInfo) systemInfo).getServletContext()).getRealPath("/");
			}
		}
//		return XssUtil.normalizaFilePath(XssUtil.normalize(XssUtil.cleanXss(System.getProperty("user.dir")), Form.NFKD));
		return System.getProperty("user.dir");
	}
	
	
	
	
	
	
	

}
