package legion.util;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Include;

public class ZkUtil {
	public static <T extends SelectorComposer> T of(Include _icd, String _cpnId) {
		return (T) _icd.getFellow(_cpnId).getAttribute("$composer");
	}
}
