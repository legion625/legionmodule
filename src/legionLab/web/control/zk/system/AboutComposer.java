package legionLab.web.control.zk.system;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;

import legion.util.LogUtil;
import legion.web.zk.ZkNotification;
import legion.web.zk.ZkUtil;

public class AboutComposer extends SelectorComposer<Component>{
	public final static String URI = "/legionLab/system/about.zul";

	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			ZkNotification.error();
		} catch (Throwable e) {
			ZkNotification.error();
			LogUtil.log(e, Level.ERROR);
		}
	}
}
