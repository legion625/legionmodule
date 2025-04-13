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
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.StepbarProxy;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.StepbarTemplateComposer;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.app.StepbarMainPage1Composer;
import legion.web.zk.ZkMsgBox;
import legion.web.zk.ZkNotification;
import legion.web.zk.ZkUtil;

public class StepbarDemo1PageComposer extends SelectorComposer<Component> {
	public final static String URI = "/legionLab/pageTemplate/stepbarDemo/stepbarDemo1Page.zul";

	private Logger log = LoggerFactory.getLogger(StepbarDemo1PageComposer.class);

	static StepbarDemo1PageComposer of(Include _icd) {
		return ZkUtil.of(_icd, URI, "pnStepbarDemo1");
	}

	// -------------------------------------------------------------------------------
	@Wire
	private Panel pnStepbarDemo1;

	@Wire
	private Include icdStepbar;
	private StepbarMainPage1Composer stepbarComposer;

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
		stepbarComposer = StepbarMainPage1Composer.of(icdStepbar);

		Step[] steps = new Step[] { //
				Step.of("Page 1", "fa fa-file-archive", Page1Composer.URI), //
				Step.of("Page 2", "fa fa-eye", Page2Composer.URI), //
				Step.of("Page 3", "fa fa-smile", Page3Composer.URI), //
		};
		stepbarComposer.initStepbar("Demo 1", "add btn event", steps, this);

		StepbarDemo1PageComposer stepbarDemo1PageComposer = stepbarComposer.getComposer(StepbarDemo1PageComposer.class);
		Page1Composer p1c = stepbarComposer.getComposer(Page1Composer.class);
		Page2Composer p2c = stepbarComposer.getComposer(Page2Composer.class);
		Page3Composer p3c = stepbarComposer.getComposer(Page3Composer.class);
		log.debug("this: {}", this);
		log.debug("stepbarDemo1PageComposer: {}", stepbarDemo1PageComposer);
		log.debug("p1c: {}", p1c);
		log.debug("p2c: {}", p2c);
		log.debug("p3c: {}", p3c);

		/**/
		stepbarComposer.setRunBack(() -> {
			ZkNotification.info("Run back custom");
			stepbarComposer.back();
		});

		stepbarComposer.setRunNext(() -> {
			ZkNotification.info("Run next custom");
			stepbarComposer.next();
		});

		stepbarComposer.setRunSubmit(() -> {
			ZkMsgBox.confirm("Run next custom?", () -> {
				ZkNotification.info("Run next custom");
				stepbarComposer.next();
			});
		});
	}

}
