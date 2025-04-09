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

public class StepbarDemo0PageComposer extends SelectorComposer<Component> {
	public final static String URI = "/legionLab/pageTemplate/stepbarDemo/stepbarDemo0Page.zul";

	private Logger log = LoggerFactory.getLogger(StepbarDemo0PageComposer.class);

	static StepbarDemo0PageComposer of(Include _icd) {
		return ZkUtil.of(_icd, URI, "pnStepbarDemo0");
	}

	// -------------------------------------------------------------------------------
	@Wire
	private Panel pnStepbarDemo0;

	@Wire
	private Include icdStepbar;
	private StepbarMainPage0Composer stepbarComposer;

	// -------------------------------------------------------------------------------
//	private StepbarProxy stepbarProxy;

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
		stepbarComposer.initStepbar("Demo 0", "", steps, this);
		
		StepbarDemo0PageComposer stepbarDemo0PageComposer = stepbarComposer.getComposer(StepbarDemo0PageComposer.class);
		StepbarMainPage0Composer stepbarMainPage0Composer = stepbarComposer.getComposer(StepbarMainPage0Composer.class);
		Page1Composer p1c = stepbarComposer.getComposer(Page1Composer.class);
		Page2Composer p2c = stepbarComposer.getComposer(Page2Composer.class);
		Page3Composer p3c = stepbarComposer.getComposer(Page3Composer.class);
		log.debug("this: {}", this);
		log.debug("stepbarDemo0PageComposer: {}", stepbarDemo0PageComposer);
		log.debug("stepbarMainPage0Composer: {}", stepbarMainPage0Composer);
		log.debug("p1c: {}", p1c);
		log.debug("p2c: {}", p2c);
		log.debug("p3c: {}", p3c);
		
	}
	
//	void initStepbar(Step[] _steps,
//			SelectorComposer<Component> _stepMainComposer, Component _stepMainComponent) {
//		initStepbar(_steps,_stepMainComposer,_stepMainComponent,  null, null);
//	}
//
//	void initStepbar(Step[] _steps,
//			SelectorComposer<Component> _stepMainComposer, Component _stepMainComponent,
//			Runnable _runBack, Runnable _runNext) {
//		
//
////		stepbarProxy = StepbarProxy.initStepbar(icdStepbar, steps, this, pnStepbarDemo0);
//		stepbarProxy = StepbarProxy.initStepbar(icdStepbar, _steps, _stepMainComposer, _stepMainComponent);
//		
//		
//
//		
//
//		/**/
//		stepbarCtrlComposer = StepbarCtrl0Composer.of(icdStepbarCtrl);
//		stepbarCtrlComposer.init(stepbarProxy, _runBack, _runNext);
//	}


}
