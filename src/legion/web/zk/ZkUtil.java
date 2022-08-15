package legion.web.zk;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Include;

public class ZkUtil {
	public static <T extends SelectorComposer> T of(Include _icd, String _cpnId) {
		return (T) _icd.getFellow(_cpnId).getAttribute("$composer");
	}
	
	public static void showNotificationError() {
		Notification.show("Unexpected error. Please inform the system manager.");
	}
	
	public static void showNotificationError(String _errorMsg) {
		Notification.show(_errorMsg, Notification.TYPE_ERROR, null, "middle_center", 0);
	}
}
