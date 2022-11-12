package legion.web.control.zk.legionmodule.pageTemplate.stepbar.ctrl;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;
import org.zkoss.zul.Toolbarbutton;

import legion.util.LogUtil;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.Stepbar;
import legion.web.zk.ZkUtil;

public class StepbarCtrl0Composer extends SelectorComposer<Component> {
	public final static String SRC = "/legionmodule/pageTemplate/stepbar/ctrl/stepbarCtrl0.zul";

	public final static StepbarCtrl0Composer of(Include _icd) {
		return ZkUtil.of(_icd, SRC, "stepbarCtrl0");
	}

	// -------------------------------------------------------------------------------
	@Wire
	private Toolbarbutton btnBack;
	@Wire
	private Toolbarbutton btnNext;

	// -------------------------------------------------------------------------------
	private Stepbar stepbar;

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
	private final Runnable runBackDefault = () -> {
		stepbar.back();
		toggleBtnVisible();
	};
	private final Runnable runNextDefault = () -> {
		stepbar.next();
		toggleBtnVisible();
	};

	private Runnable runBack;
	private Runnable runNext;

	public void setRunBack(Runnable runBack) {
		this.runBack = runBack;
	}

	public void setRunNext(Runnable runNext) {
		this.runNext = runNext;
	}

	public void init(Stepbar _stepbar) {
		this.stepbar = _stepbar;
		setRunBack(runBackDefault);
		setRunNext(runNextDefault);
		toggleBtnVisible();
	}

	public void toggleBtnVisible() {
		btnBack.setVisible(stepbar.getCurrentIndex() != 0);
		btnNext.setVisible(stepbar.getCurrentIndex() != stepbar.getStepSize() - 1);
	}

	@Listen(Events.ON_CLICK + "=#btnBack")
	public void btnBack_clicked() {
		runBack.run();
	}

	@Listen(Events.ON_CLICK + "=#btnNext")
	public void btnNext_clicked() {
		runNext.run();
	}

}
