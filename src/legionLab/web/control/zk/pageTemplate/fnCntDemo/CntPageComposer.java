package legionLab.web.control.zk.pageTemplate.fnCntDemo;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Include;

import legion.util.LogUtil;
import legion.web.control.zk.legionmodule.pageTemplate.FnCntProxy;
import legion.web.zk.ZkUtil;

public class CntPageComposer extends SelectorComposer<Component> {
	public final static String URI = "/legionLab/pageTemplate/fnCntDemo/cntPage.zul";

	public static CntPageComposer of(Include _icd) {
		return ZkUtil.of(_icd, URI, "wdCntPage");
	}

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			FnCntProxy.register(this);

		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}

	String getValue() {
		return "CntPageValue";
	}
}
