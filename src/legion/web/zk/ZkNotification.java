package legion.web.zk;

import org.zkoss.zk.ui.util.Clients;

public class ZkNotification {
	
	public static void info(String _msg) {
		Clients.showNotification(_msg, Clients.NOTIFICATION_TYPE_INFO, null, "middle_center", 1500);
	}

	public static void warning(String _msg) {
		Clients.showNotification(_msg, Clients.NOTIFICATION_TYPE_WARNING, null, "middle_center", 1500);
	}

	public static void error() {
		error("非預期的錯誤，請連絡系統管理員。");
	}
	
	public static void error(String _msg) {
		Clients.showNotification(_msg, Clients.NOTIFICATION_TYPE_ERROR, null, "middle_center", 1500);
	}
}
