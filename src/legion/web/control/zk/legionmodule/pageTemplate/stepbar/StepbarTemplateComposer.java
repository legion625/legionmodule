package legion.web.control.zk.legionmodule.pageTemplate.stepbar;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.Cardlayout;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Separator;
import org.zkoss.zul.South;
import org.zkoss.zul.Toolbarbutton;

import legion.util.LogUtil;
import legion.web.control.zk.legionmodule.pageTemplate.stepbar.Step.StepStatus;
import legion.web.zk.ZkUtil;

public class StepbarTemplateComposer extends SelectorComposer<Component> implements Stepbar {
	public final static String SRC = "/legionmodule/pageTemplate/stepbar/stepbarTemplate.zul";
//	public final static String MAIN_PAGE_ID = "main";
	
	// -------------------------------------------------------------------------------
	@Wire
	private Div divStepbar;
	
	@Wire
	private Cardlayout card;
	
//	@Wire
//	private South southCtrlBar;
//	@Wire
//	private Toolbarbutton btnBack;
//	@Wire
//	private Toolbarbutton btnNext;
	
	// -------------------------------------------------------------------------------
	private ListModelList<Step> stepModel;
//	private boolean useDefaultCtrlBar = true;
	
	// -------------------------------------------------------------------------------
	static StepbarTemplateComposer getInstance(Include _icd) {
		return ZkUtil.of(_icd, SRC, "main");
	}
	
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
//	void initialize(Step[] _steps, StepbarProxy _stepbarProxy, boolean _useDefaultCtrlBar) {
	void initialize(Step[] _steps, StepbarProxy _stepbarProxy) {
		/* init stepModel */
		stepModel = new ListModelList<>();
		stepModel.setMultiple(false);
		
		/* addStep */
		for(Step _step: _steps)
			stepModel.add(stepModel.size(), _step);
		
		/* init stepbar */
		while (divStepbar.getLastChild() != null)
			divStepbar.removeChild(divStepbar.getLastChild());
		for(Step step:stepModel.getInnerList())
			divStepbar.appendChild(step.getDiv());

		/* init cardlayout */
		while(card.getChildren().size()>0)
			card.getLastChild().detach();
		for(Step step: stepModel.getInnerList()) {
			Panel panel = new Panel();
			panel.setVflex("1");
			Panelchildren pc = new Panelchildren();
			panel.appendChild(pc);
			Separator sep = new Separator();
			sep.setBar(true);
			pc.appendChild(sep);
			
			Include icdStep = new Include(step.getUri());
			icdStep.setDynamicProperty(StepbarProxy.STEPBAR_PROXY, _stepbarProxy);
			icdStep.setVflex("1");
			pc.appendChild(icdStep);
			
			card.appendChild(panel);
			icdStep.afterCompose(); // Invokes after ZK loader creates this component, initializes it and composes
									// all its children, if any.
		}
		
		if(stepModel.size()>0)
			navigateTo(stepModel.get(0));
	
//		/* attributes */
//		useDefaultCtrlBar = _useDefaultCtrlBar;
//		southCtrlBar.setVisible(useDefaultCtrlBar);
	}
	
	// -------------------------------------------------------------------------------
	private void navigateTo(Step step) {
		stepModel.addToSelection(step);
		
		/**/
		step.updateStatus(StepStatus.CURRENT);
		boolean temp = false;
		for(int i=0;i<stepModel.getSize();i++) {
			if(stepModel.get(i).equals(step))
				temp = true;
			else
				stepModel.get(i).updateStatus(temp?StepStatus.FOLLOWING:StepStatus.PREVIOUS);
		}
		
		/**/
		card.setSelectedIndex(getCurrentIndex());
		
		/**/
//		btnBack.setDisabled(!(getCurrentIndex() != 0 && getCurrentIndex() != stepModel.getSize() - 1));
//		btnBack.setDisabled(!(getCurrentIndex() != 0));
//		btnNext.setDisabled(!(getCurrentIndex() != stepModel.getSize() - 1));
	}
	
	private Step getCurrent() {
		return stepModel.getSelection().iterator().next();
	}
	
	// -------------------------------------------------------------------------------
	@Override
	public int getCurrentIndex() {
		return stepModel.indexOf(getCurrent());
	}

	@Override
	public int getStepSize() {
		return stepModel.getSize();
	}

	@Override
	public void back() {
		navigateTo(getCurrentIndex() - 1);
	}

	@Override
	public void next() {
		navigateTo(getCurrentIndex() + 1);
	}

	@Override
	public void navigateTo(int _index) {
		if(_index >=0 && _index <stepModel.size())
			navigateTo(stepModel.get(_index));
	}
	
	// -------------------------------------------------------------------------------
//	@Listen(Events.ON_CLICK+"=#btnBack")
//	public void btnBack_clicked() {
//		back();
//	}
//	
//	@Listen(Events.ON_CLICK+"=#btnNext")
//	public void btnNext_clicked() {
//		next();
//	}

}
