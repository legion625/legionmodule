package legionLab.web.control.zk.pageTemplate.stepbarDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.Panel;

import legion.util.LogUtil;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.Step;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.StepbarProxy;
import legion.web.zk.ZkNotification;
import legion.web.zk.ZkUtil;

public class StepbarDemoCustomPageComposer extends SelectorComposer<Component> {
	public final static String URI = "/legionLab/pageTemplate/stepbarDemo/stepbarDemoCustomPage.zul";

	private Logger log = LoggerFactory.getLogger(StepbarDemoCustomPageComposer.class);

	static StepbarDemoCustomPageComposer of(Include _icd) {
		return ZkUtil.of(_icd, URI, "pnStepbarDemoCustom");
	}

	// -------------------------------------------------------------------------------
	@Wire
	private Panel pnStepbarDemoCustom;

	@Wire
	private Include icdStepbar;

	@Wire
	private Button btn1, btn2, btn3;
	
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

//		stepbarProxy = StepbarProxy.initStepbar(icdStepbar, steps, this, pnStepbarDemoCustom, false);
		stepbarProxy = StepbarProxy.initStepbar(icdStepbar, steps, this);

		StepbarDemoCustomPageComposer stepbarDemoCustomPageComposer = getComposer(StepbarDemoCustomPageComposer.class);
		Page1Composer p1c = getComposer(Page1Composer.class);
		Page2Composer p2c = getComposer(Page2Composer.class);
		Page3Composer p3c = getComposer(Page3Composer.class);
		log.debug("this: {}", this);
		log.debug("stepbarDemoCustomPageComposer: {}", stepbarDemoCustomPageComposer);
		log.debug("p1c: {}", p1c);
		log.debug("p2c: {}", p2c);
		log.debug("p3c: {}", p3c);
		
		/* step ctrl */
		

	}

	@Listen(Events.ON_CLICK + "=#btn1")
	public void btn1_clicked() {
		stepbarProxy.navigateTo(0);
		toggleBtnVisible();
		ZkNotification.info("btn1_clicked");
	}

	@Listen(Events.ON_CLICK + "=#btn2")
	public void btn2_clicked() {
		stepbarProxy.navigateTo(1);
		toggleBtnVisible();
		ZkNotification.info("btn2_clicked");
	}

	@Listen(Events.ON_CLICK + "=#btn3")
	public void btn3_clicked() {
		stepbarProxy.navigateTo(2);
		toggleBtnVisible();
		ZkNotification.info("btn3_clicked");
	}
	
	private void toggleBtnVisible() {
		btn1.setVisible(stepbarProxy.getCurrentIndex()!=0);
		btn2.setVisible(stepbarProxy.getCurrentIndex()!=1);
		btn3.setVisible(stepbarProxy.getCurrentIndex()!=2);
	}

	public <T> T getComposer(Class<T> _composerClass) {
		return stepbarProxy == null ? null : stepbarProxy.getComposer(_composerClass);
	}
}
