package legionLab.web.control.zk.pageTemplate.stepbarDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;
import org.zkoss.zul.Panel;

import legion.util.LogUtil;
import legion.web.control.zk.legionmodule.pageTemplate.Step;
import legion.web.control.zk.legionmodule.pageTemplate.StepbarProxy;
import legion.web.control.zk.legionmodule.pageTemplate.StepbarTemplateComposer;

public class StepbarMainPageComposer extends SelectorComposer<Component> {
	public final static String URI = "/legionLab/pageTemplate/stepbarDemo/stepbarMainPage.zul";

	private Logger log = LoggerFactory.getLogger(StepbarMainPageComposer.class);

	// -------------------------------------------------------------------------------
	@Wire
	private Panel pnStepbarMain;
	
	@Wire
	private Include icdStepbar;
	private StepbarTemplateComposer stepbarComposer;

	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			initStepbar();
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}
	
	private void initStepbar() {
		Step[] steps = new Step[] {
			Step.of("Page 1", "fa fa-file-archive", Page1Composer.URI), //
			Step.of("Page 2", "fa fa-eye", Page2Composer.URI), //
			Step.of("Page 3", "fa fa-smile", Page3Composer.URI), //
		};
		
		StepbarProxy stepbarProxy = 	StepbarProxy.initStepbar(icdStepbar, steps, this, pnStepbarMain, true);
		
		StepbarMainPageComposer stepbarMainPageComposer = stepbarProxy.getComposer(StepbarMainPageComposer.class);
		Page1Composer p1c = stepbarProxy.getComposer(Page1Composer.class);
		Page2Composer p2c = stepbarProxy.getComposer(Page2Composer.class);
		Page3Composer p3c = stepbarProxy.getComposer(Page3Composer.class);
		log.debug("stepbarMainPageComposer: {}", stepbarMainPageComposer);
		log.debug("p1c: {}", p1c);
		log.debug("p2c: {}", p2c);
		log.debug("p3c: {}", p3c);
		
	}

}
