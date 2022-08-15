package legion.web.zk;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Include;

public class ZkUtil {
	public static <T extends SelectorComposer> T of(Include _icd, String _cpnId) {
		return (T) _icd.getFellow(_cpnId).getAttribute("$composer");
	}
	
	// -------------------------------------------------------------------------------
	private  static void showNotification(String _msg, String _type) {
		Notification.show(_msg, _type, null, "middle_center", 0);
	}
	
	public static void showNotificationInfo(String _msg) {
		showNotification(_msg, Notification.TYPE_INFO);
	}
	
	
	public static void showNotificationWarning(String _msg) {
		showNotification(_msg, Notification.TYPE_WARNING);
	}

	public static void showNotificationError() {
		showNotificationError("Unexpected error. Please report to the system manager.");
	}

	public static void showNotificationError(String _msg) {
		showNotification(_msg, Notification.TYPE_ERROR);
	}
	
	
	
}
