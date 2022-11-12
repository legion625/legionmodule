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
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.Step;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.Stepbar;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.StepbarProxy;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.StepbarTemplateComposer;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.app.StepbarMainPage0Composer;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.ctrl.StepbarCtrl0Composer;
import legion.web.zk.ZkUtil;

public class StepbarDemo0aPageComposer extends SelectorComposer<Component> {
	public final static String URI = "/legionLab/pageTemplate/stepbarDemo/stepbarDemo0aPage.zul";

	private Logger log = LoggerFactory.getLogger(StepbarDemo0aPageComposer.class);

	static StepbarDemo0aPageComposer of(Include _icd) {
		return ZkUtil.of(_icd, URI, "pnStepbarDemo0a");
	}

	// -------------------------------------------------------------------------------
	@Wire
	private Panel pnStepbarDemo0a;

	@Wire
	private Include icdStepbar;
	private StepbarMainPage0Composer stepbarComposer;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			initStepbar() ;
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}

	private void initStepbar() {
		stepbarComposer = StepbarMainPage0Composer.of(icdStepbar);
		Step[] steps = new Step[] { //
				Step.of("Page 1", "fa fa-file-archive", Page1Composer.URI), //
				Step.of("Page 2", "fa fa-eye", Page2Composer.URI), //
				Step.of("Page 3", "fa fa-smile", Page3Composer.URI), //
		};
		stepbarComposer.initStepbar("Demo 0", "add btn event", steps, this);
		
		StepbarDemo0aPageComposer stepbarDemo0PageComposer = stepbarComposer.getComposer(StepbarDemo0aPageComposer.class);
		Page1Composer p1c = stepbarComposer.getComposer(Page1Composer.class);
		Page2Composer p2c = stepbarComposer.getComposer(Page2Composer.class);
		Page3Composer p3c = stepbarComposer.getComposer(Page3Composer.class);
		log.debug("this: {}", this);
		log.debug("stepbarDemo0PageComposer: {}", stepbarDemo0PageComposer);
		log.debug("p1c: {}", p1c);
		log.debug("p2c: {}", p2c);
		log.debug("p3c: {}", p3c);
		
		/**/
		stepbarComposer.setRunBack(()->{
			ZkUtil.showNotificationInfo("Run back custom");
			stepbarComposer.back();
		});
		
		stepbarComposer.setRunNext(()->{
			ZkUtil.showNotificationInfo("Run next custom");
			stepbarComposer.next();
		});
	}


}
