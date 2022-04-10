package legion.datasource.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import legion.util.BeanUtil;
import legion.util.DataFO;
import legion.util.LogUtil;

public class SourceConfiguration {
	private static Logger log = LoggerFactory.getLogger(SourceConfiguration.class);
	Map<String, ResourceInfo> sourceMap = null;
	private static SourceConfiguration instance = new SourceConfiguration();

	private SourceConfiguration() {
		sourceMap = new HashMap<>();
	}

	public static SourceConfiguration getInstance() {
		return instance;
	}

	// -------------------------------------------------------------------------------
	public ResourceInfo getSource(String _sourceName) {
		return sourceMap.get(_sourceName);
	}

	/**
	 * 解析Resource描述檔資訊，並且建立相關資訊保存，將以ResourceName為索引唯一，若是有重複宣告相同名稱的Resource則以解析順序後蓋前的方式記錄。
	 * 解析過程不會保證順序。
	 * 
	 * <對照datasource.xml設定檔格式>
	 * 
	 * @param _inputStream
	 */
	private void parseSourceMap(InputStream _inputStream) {
		SAXBuilder saxBuilder = null;
		Document doc = null;
		List<Element> resources = null;
		try {
			saxBuilder = new SAXBuilder();
			doc = saxBuilder.build(_inputStream);
			/* 取得Transaction環境設定 */
			Element transaction = doc.getRootElement().getChild("Transaction");
			if (transaction != null) {
				Element param;
				// MaxCreateTime
				param = transaction.getChild("MaxCreateTime");
				if (param != null)
					DSManager.getInstance().setMaxTsCreateTime(Long.parseLong(param.getTextTrim()));
				// MaxStartTime
				param = transaction.getChild("MaxStartTime");
				if (param != null)
					DSManager.getInstance().setMaxTsStartTime(Long.parseLong(param.getTextTrim()));
				// AlertMail
				param = transaction.getChild("AlertMail");
				if (param != null) {
					String[] mails = param.getTextTrim().split("\\s*[;]\\s*"); // 去除空白字元
					DSManager.getInstance().setAlertMails(mails);
				}

				// attribute: debug
				String debug = transaction.getAttributeValue("debug");
				if (!DataFO.isEmptyString(debug))
					DSManager.getInstance().setDebugTransaction(Boolean.valueOf(debug));

				// attribute: period
				String period = transaction.getAttributeValue("period");
				if (!DataFO.isEmptyString(period))
					DSManager.getInstance().setDebugTransactionPeriod(Long.parseLong(period));
			}

			/* 取得Resource區段 */
			resources = doc.getRootElement().getChildren("Resource");
			if (resources == null || resources.isEmpty()) {
				// 無相關Resource定義
				return;
			}

			for (Element source : resources) {
				// 解析每個Resource定義
				// 名稱
				String name = source.getAttributeValue(ResourceInfo.Resource_NAME);
				if (DataFO.isEmptyString(name)) {
					log.error("Resource無設定名稱(name)...");
					continue;
				}

				ResourceInfo info = new ResourceInfo(name);
				List<Element> params = source.getChildren("parameter");
				if (params != null && !params.isEmpty()) {
					for (Element parEle : params) {
						// 參數若有重複宣告，則以解析順序，後蓋前方式記錄。
						info.setParameter(parEle.getChildText("name"), parEle.getChildText("value"));
					}
				}
				sourceMap.put(info.getName(), info);
			}
			
			log.debug("sourceMap.size(): {}", sourceMap.size());
			for(String key: sourceMap.keySet()) {
				log.debug("{}", key);
				ResourceInfo resourceInfo = sourceMap.get(key);
				Map<String, Object> map = PropertyUtils.describe(resourceInfo);
				for (String k : map.keySet()) {
					log.debug("{}\t{}", k, map.get(k));
				}
				
			}
			
		} catch (Exception e) {
			log.error("datasource XML parsing fail: {}", e.getMessage());
			LogUtil.log(log, e, Level.ERROR);
		} finally {
			saxBuilder = null;
			doc = null;
		}
	}

	/** 輸入資訊串流進行解析，若原有已有的註冊資訊將會被清空，重新依據輸入資訊建構。 */
	protected void registerDsXml(InputStream _dsXmlStream) {
		registerDsXml(_dsXmlStream, true);
	}

	/**
	 * 輸入資訊串流進行解析
	 * 
	 * @param _dsXmlStream
	 * @param _rebuild     是否清空原有記錄，重新設定
	 */
	protected void registerDsXml(InputStream _dsXmlStream, boolean _rebuild) {
		if (_rebuild)
			sourceMap = new HashMap<>();
		parseSourceMap(_dsXmlStream);
	}

	/** 指定檔案名稱進行解析註冊 */
	protected void registerDxXml(String _cfgFile) throws Exception {
		if (!DataFO.isEmptyString(_cfgFile))
			parseSourceMap(new FileInputStream(_cfgFile));
	}

}
