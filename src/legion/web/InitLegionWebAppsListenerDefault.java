package legion.web;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.PropertiesConfiguration;

import legion.ISystemWebInfo;
import legion.LegionContext;
import legion.SystemInfoDefault;

public class InitLegionWebAppsListenerDefault extends InitLegionWebAppsListener implements ServletContextListener {

	// -------------------------------------------------------------------------------
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		super.contextDestroyed(sce);
	}

	// -------------------------------------------------------------------------------
	@Override
	protected void initSystemInfo(ServletContextEvent sce) {
		try {
			// 註冊預設系資訊實體
			if (LegionContext.getInstance().getSystemInfo() == null)
				LegionContext.getInstance().registerSystemInfo(SystemInfoDefault.getInstance());

			log.info("init SystemInfo...");
			ServletContext context = sce.getServletContext();

			ISystemWebInfo systemInfo = (ISystemWebInfo) LegionContext.getInstance().getSystemInfo();
			systemInfo.setServletContext(context);

			// System config
			String file = context.getRealPath("/") + context.getInitParameter("system-config-file"); // TOOD
																										// 在Web.xml中設定參數
			if (file != null) {
				PropertiesConfiguration cfg = new PropertiesConfiguration();
				cfg.setEncoding("UTF-8");
				cfg.load(file);
				for (Iterator<String> it = cfg.getKeys(); it.hasNext();) {
					String key = it.next();
					systemInfo.putAttribute(key, cfg.getString(key));
				}
				String[] paths = cfg.getStringArray("CLASS_ANALYSE_CLASSPATH"); // TODO 待確認

				if (paths != null && paths.length > 0) {
					List<String> resultPaths = new ArrayList<>();
					for (String path : paths) {
						if (path.endsWith("jar")) {
							// 解析檔名結尾為jar，支援檔名以正規表示法表示。系統自動解析實際目錄的符合檔名替換
							int idx = path.lastIndexOf('/');
							String dir = path.substring(0, idx);
							String fileName = path.substring(idx + 1);
							File targetDir = new File(context.getRealPath("/") + "/" + dir);
							if (!targetDir.exists() || !targetDir.isDirectory()) {
								log.error("{} 不存在或是非目錄", dir);
								// 沒有符合就以原檔名當解析檔
								resultPaths.add("file:/" + context.getRealPath("/") + "/" + path);
								continue;
							}

							String[] realFileNames = targetDir.list((dirPath, namePath) -> namePath.matches(fileName));
							if (realFileNames != null && realFileNames.length > 0) {
								for (String realName : realFileNames)
									resultPaths.add("file:/" + context.getRealPath("/") + "/" + dir + "/" + realName);
							} else {
								// 沒有符合就以原檔名當解析檔
								resultPaths.add("file:/" + context.getRealPath("/") + "/" + path);
							}
						} else {
							resultPaths.add("file:/" + context.getRealPath("/") + "/" + path);
						}
					}

					for (String realPath : resultPaths) {
						// 解析出
						log.info("Class Analyse Classpath Urls: {}", realPath);
					}
					systemInfo.setClassAnalyseClasspath(resultPaths.toArray(new String[0]));
				}
			}
		} catch (Exception e) {
			log.error("init systemInfo Fail... {}", e.getMessage());
		}
	}

}
