package legion.datasource.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.event.Level;

import legion.BusinessServiceFactory;
import legion.LegionContext;
import legion.datasource.DatasourceInfo;
import legion.datasource.UrlDs;
import legion.datasource.manager.ResourceInfo;
import legion.util.LogUtil;

public abstract class Dso implements DatasourceInfo {
//	protected static MailService mailService; // TODO not implemented yet...
	protected String alertMail = "";
	protected String[] alertMailArray = null;
	// 資料連結異常通知頻率，單位為秒，預設為5分鐘(300秒)
	protected int alertMailPeriod = 300;
	// 上次通知信寄發時間
	private long lastMailTime = 0;
	protected String hostIp = "";
	protected String name = "";
	protected String url = "";

//	// XXX not implemented yet...
//	static {
//		mailService = BusinessServiceFactory.getInstance().getService(MailService.class);
//		/* 透過ServiceFactory找不到時，則以直接建立實作物件。(這是暫時性為了舊專案沒有透過LegiomoduleService環境下的暫時作法。 */
//		if (mailService == null)
//			mailService = new MailServiceImp();
//	}

	// -------------------------------------------------------------------------------
	@Override
	public String getAlertMail() {
		return alertMail;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUrl() {
		return url;
	}

	// -------------------------------------------------------------------------------
	/**
	 * 關閉連結，並釋放相關資源
	 * 
	 * @throws Exception
	 */
	public abstract void close();

	/**
	 * 取得連線物件
	 * 
	 * @param _urlDs
	 * @return
	 * @throws Exception
	 */
	public abstract Object getConn(UrlDs _urlDs);

	// -------------------------------------------------------------------------------
	public void mailConnectionError(String _msg) {
		// 寄mail...
		// 先判斷是否在寄信週期內
		if (System.currentTimeMillis() - lastMailTime < alertMailPeriod * 1000) {
			// 距上次寄發信件時間還在週期範圍內，不需要再寄通知信。
			return;
		}
		lastMailTime = System.currentTimeMillis();
		if (alertMailArray != null && alertMailArray.length > 0) {
			// TODO not implemented yet...
//			Map<String, String> msg = new HashMap<>();
//			msg.put("Title", _msg);
//			String[] toArray = alertMailArray;
//			MailTargetDto[] to = null;
//			if (toArray != null && toArray.length > 0) {
//				to = new MailTargetDto[toArray.length];
//				int count = 0;
//				for (String mail : toArray)
//					to[count++] = new MailTargetDto(mail, "");
//			}
//
//			try {
//				MailHeaderContextDto mailContext = new MailHeaderContextDto();
//				mailContext.setSubject("Datasource Message From " + hostIp);
//				mailContext.addToReceivers(to);
//				mailService.send(mailContext, msg, null, true);
//			} catch (Exception e) {
//				LogUtil.log(e, Level.ERROR);
//			}
		}
	}

	public boolean initial(ResourceInfo _cfg) {
		name = _cfg.getName();
		// url
		if (_cfg.getParameter(ResourceInfo.Resource_URL) != null)
			url = _cfg.getParameter(ResourceInfo.Resource_URL);
		// alertMail
		if (_cfg.getParameter(ResourceInfo.Resource_ALERT_MAIL) != null) {
			alertMail = _cfg.getParameter(ResourceInfo.Resource_ALERT_MAIL);
			alertMailArray = alertMail.split("\\s*[;]\\s*"); // 去除空白型字元
			if (alertMailArray == null)
				alertMailArray = new String[0];
		}
		// alertMailPeriod
		if (_cfg.getParameter(ResourceInfo.Resource_ALERT_MAIL_PERIOD) != null) {
			String alertMailPeriodStr = _cfg.getParameter(ResourceInfo.Resource_ALERT_MAIL_PERIOD);
			int alertMailPeriodVal = alertMailPeriod;
			try {
				alertMailPeriodVal = Integer.parseInt(alertMailPeriodStr);
			} catch (Throwable e) {
				alertMailPeriodVal = alertMailPeriod;
			}
			alertMailPeriod = alertMailPeriodVal;
		}
		hostIp = LegionContext.getInstance().getSystemInfo().getHostIp();
		return true;
	}

}
