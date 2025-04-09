package legionLab.web.control.zk.pageTemplate.stepbarDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;

import legion.util.LogUtil;

public class StepbarDemoComposer extends SelectorComposer<Component> {
	public final static String URI = "/legionLab/pageTemplate/stepbarDemo/stepbarDemo.zul";

	private Logger log = LoggerFactory.getLogger(StepbarDemoComposer.class);



	@Wire
	private Include icdDemo0, icdDemo0a;
	@Wire
	private Include icdDemo1;

	@Wire
	private Include icdDemoCustom;

	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			init();
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}

	private void init() {
		StepbarDemo0PageComposer.of(icdDemo0);
		StepbarDemo0aPageComposer.of(icdDemo0a);
		StepbarDemo1PageComposer.of(icdDemo1);
		StepbarDemoCustomPageComposer.of(icdDemoCustom);
	}

}
