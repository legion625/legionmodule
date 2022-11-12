package legionLab.web.control.zk.pageTemplate.stepbarDemo;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;

import legion.util.LogUtil;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.StepbarProxy;

public class Page2Composer extends SelectorComposer<Component>{
	public final static String URI = "/legionLab/pageTemplate/stepbarDemo/page2.zul";

	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			StepbarProxy.register(this);
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}
}
