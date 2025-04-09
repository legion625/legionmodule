package legion.web.zk;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Include;

import legion.type.IdxEnum;
import legion.util.DataFO;

public class ZkUtil {
	public static <T extends SelectorComposer> T of(Include _icd, String _src, String _cpnId) {
		if (!DataFO.isEmptyString(_src))
			_icd.setSrc(_src);
		return (T) _icd.getFellow(_cpnId).getAttribute("$composer");
	}
	
	public static <T extends SelectorComposer> T of(Include _icd, String _cpnId) {
		return of(_icd, null, _cpnId);
	}
	
	// -------------------------------------------------------------------------------
	@Deprecated
	private  static void showNotification(String _msg, String _type) {
		Notification.show(_msg, _type, null, "middle_center", 0);
	}
	@Deprecated
	public static void showNotificationInfo(String _msg) {
		showNotification(_msg, Notification.TYPE_INFO);
	}
	
	@Deprecated
	public static void showNotificationWarning(String _msg) {
		showNotification(_msg, Notification.TYPE_WARNING);
	}
	@Deprecated
	public static void showNotificationError() {
		showNotificationError("Unexpected error. Please report to the system manager.");
	}

	@Deprecated
	public static void showNotificationError(String _msg) {
		showNotification(_msg, Notification.TYPE_ERROR);
	}
	
	// -------------------------------------------------------------------------------
	public static void initCbb(Combobox _cbb, IdxEnum[] _idxEnums, boolean _containsBlank) {
		if (_cbb == null)
			return;
		_cbb.getChildren().clear();

		if (_containsBlank) {
			_cbb.appendChild(new Comboitem());
		}

		for (IdxEnum _idxEnum : _idxEnums) {
			if (_idxEnum.getIdx() <= 0)
				continue;
			Comboitem cbi = new Comboitem(_idxEnum.getName());
			cbi.setValue(_idxEnum);
			_cbb.appendChild(cbi);
		}
	}
	
	
}
