package legion.web.control.zk.legionmodule.pageTemplate.stepbar.ctrl;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.Toolbarbutton;

import legion.util.LogUtil;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.Stepbar;
import legion.web.zk.ZkUtil;

public class StepbarCtrl1Composer extends SelectorComposer<Component> {
	public final static String SRC = "/legionmodule/pageTemplate/stepbar/ctrl/stepbarCtrl1.zul";

	public final static StepbarCtrl1Composer of(Include _icd) {
		return ZkUtil.of(_icd, SRC, "stepbarCtrl1");
	}

	// -------------------------------------------------------------------------------
	@Wire
	private Toolbarbutton btnBack;
	@Wire
	private Toolbarbutton btnNext;
	@Wire
	private Button btnSubmit;

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
	private final Runnable runBackDefault = () -> ZkUtil.showNotificationError();
	private final Runnable runNextDefault = () -> ZkUtil.showNotificationError();
	private final Runnable runSubmitDefault = () -> ZkUtil.showNotificationError();

	private Runnable runBack;
	private Runnable runNext;
	private Runnable runSubmit;

	public void setRunBack(Runnable runBack) {
		this.runBack = runBack;
	}

	public void setRunNext(Runnable runNext) {
		this.runNext = runNext;
	}

	public void setRunSubmit(Runnable runSubmit) {
		this.runSubmit = runSubmit;
	}

	public void init(Stepbar _stepbar) {
		this.stepbar = _stepbar;
		setRunBack(runBackDefault);
		setRunNext(runNextDefault);
		setRunSubmit(runSubmitDefault);
		updateBtnVisible();
	}

	public void updateBtnVisible() {
		int i = stepbar.getCurrentIndex();
		int n = stepbar.getStepSize();
		btnBack.setVisible(i != 0 && i != n - 1);
		btnNext.setVisible(i != n - 1 && i != n - 2);
		btnSubmit.setVisible(i == n - 2);
	}

	@Listen(Events.ON_CLICK + "=#btnBack")
	public void btnBack_clicked() {
		runBack.run();
//		navigate();
	}

	@Listen(Events.ON_CLICK + "=#btnNext")
	public void btnNext_clicked() {
		runNext.run();
//		navigate();
	}

	@Listen(Events.ON_CLICK + "=#btnSubmit")
	public void btnSubmit_clicked() {
		runSubmit.run();
//		navigate();
	}

}
