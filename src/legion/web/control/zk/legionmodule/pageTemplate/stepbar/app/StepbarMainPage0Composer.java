package legion.web.control.zk.legionmodule.pageTemplate.stepbar.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panel;

import legion.util.LogUtil;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.Step;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.Stepbar;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.StepbarProxy;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.ctrl.StepbarCtrl0Composer;
import legion.web.zk.ZkUtil;

public class StepbarMainPage0Composer extends SelectorComposer<Component> implements Stepbar {
	public final static String URI = "/legionmodule/pageTemplate/stepbar/app/stepbarMainPage0.zul";

	private Logger log = LoggerFactory.getLogger(StepbarMainPage0Composer.class);

	public final static StepbarMainPage0Composer of(Include _icd) {
		return ZkUtil.of(_icd, URI, "pnStepbarMainPage0");
	}
	
	// -------------------------------------------------------------------------------
	@Wire
	private Panel pnStepbarMainPage0;

	@Wire
	private Label lbPageTitle, lbPageSubtitle;
	
	@Wire
	private Include icdStepbarMainPage0;
	@Wire
	private Include icdStepbarMainPage0Ctrl;
	private StepbarCtrl0Composer stepbarCtrlComposer;

	// -------------------------------------------------------------------------------
	private StepbarProxy stepbarProxy;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}

	// -------------------------------------------------------------------------------
	/** recommended at most time */
	public void initStepbar(String _pageTitle, String _pageSubtitle, Step[] _steps) {
		initStepbar(_pageTitle, _pageSubtitle, _steps,  null);
	}

	public void initStepbar(String _pageTitle, String _pageSubtitle,Step[] _steps, SelectorComposer<Component> _stepMainComposer) {
		lbPageTitle.setValue(_pageTitle);
		lbPageSubtitle.setValue(_pageSubtitle);
		
		stepbarProxy = StepbarProxy.initStepbar(icdStepbarMainPage0, _steps, _stepMainComposer);

		/**/
		stepbarCtrlComposer = StepbarCtrl0Composer.of(icdStepbarMainPage0Ctrl);
		stepbarCtrlComposer.init(stepbarProxy);
	}
	
	public void setRunBack(Runnable _runBack) {
		stepbarCtrlComposer.setRunBack(_runBack);
	}
	
	public void setRunNext(Runnable _runNext) {
		stepbarCtrlComposer.setRunNext(_runNext);
	}
	
	// -------------------------------------------------------------------------------
	public <T> T getComposer(Class<T> _composerClass) {
		return stepbarProxy == null ? null : stepbarProxy.getComposer(_composerClass);
	}

	@Override
	public int getCurrentIndex() {
		return stepbarProxy.getCurrentIndex();
	}

	@Override
	public int getStepSize() {
		return stepbarProxy.getStepSize();
	}

	@Override
	public void back() {
		stepbarProxy.back();
		stepbarCtrlComposer.toggleBtnVisible();
	}

	@Override
	public void next() {
		stepbarProxy.next();
		stepbarCtrlComposer.toggleBtnVisible();
	}

	@Override
	public void navigateTo(int _index) {
		stepbarProxy.navigateTo(_index);
		stepbarCtrlComposer.toggleBtnVisible();
	}

}
