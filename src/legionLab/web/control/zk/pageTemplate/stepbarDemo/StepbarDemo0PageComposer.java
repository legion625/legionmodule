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

	// -------------------------------------------------------------------------------
	private StepbarProxy stepbarProxy;

	// -------------------------------------------------------------------------------
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
		Step[] steps = new Step[] { //  
				Step.of("Page 1", "fa fa-file-archive", Page1Composer.URI), //
				Step.of("Page 2", "fa fa-eye", Page2Composer.URI), //
				Step.of("Page 3", "fa fa-smile", Page3Composer.URI), //
		};

		stepbarProxy = StepbarProxy.initStepbar(icdStepbar, steps, this, pnStepbarDemo0, true);

		StepbarDemo0PageComposer stepbarDemo0PageComposer = getComposer(StepbarDemo0PageComposer.class);
		Page1Composer p1c = getComposer(Page1Composer.class);
		Page2Composer p2c = getComposer(Page2Composer.class);
		Page3Composer p3c = getComposer(Page3Composer.class);
		log.debug("this: {}", this);
		log.debug("stepbarDemo0PageComposer: {}", stepbarDemo0PageComposer);
		log.debug("p1c: {}", p1c);
		log.debug("p2c: {}", p2c);
		log.debug("p3c: {}", p3c);

	}
	
	public <T> T getComposer(Class<T> _composerClass) {
		return stepbarProxy == null ? null : stepbarProxy.getComposer(_composerClass);
	}

}
