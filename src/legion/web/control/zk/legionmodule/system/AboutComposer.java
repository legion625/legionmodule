package legion.web.control.zk.legionmodule.system;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

import legion.SystemInfoDefault;
import legion.util.LogUtil;
import legion.web.zk.ZkNotification;
import legion.web.zk.ZkUtil;

public class AboutComposer extends SelectorComposer<Component> {
	public final static String URI = "/legionmodule/system/about.zul";
	// -------------------------------------------------------------------------------
	@Wire
	private Label lbSysVer;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			init();
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
			ZkNotification.error();
		}
	}

	private void init() {
		lbSysVer.setValue(SystemInfoDefault.getInstance().getVersion());
	}
}
