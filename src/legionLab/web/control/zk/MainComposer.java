package legionLab.web.control.zk;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;

import legion.web.control.zk.legionmodule.pageTemplate.FnCntTemplateComposer;

public class MainComposer extends SelectorComposer<Component> {
	@Wire
	private Include iclMain;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}

	@Listen(Events.ON_CLICK + "=#miFnLeftTemplateDemo")
	public void miFnLeftTemplateDemo_clicked() {
//		iclMain.setSrc("legionmodule/pageTemplate/fnLeftTemplate.zul?fnUri=pageTemplate/fnCntDemo/fnPage.zul&cntUri=pageTemplate/fnCntDemo/cntPage.zul");
//		iclMain.setSrc("/legionmodule/pageTemplate/fnLeftTemplate.zul?fnUri=/legionLab/pageTemplate/fnCntDemo/fnPage.zul");
		iclMain.setSrc(
				FnCntTemplateComposer.FN_LEFT_TEMPLATE_URI + "?fnUri=/legionLab/pageTemplate/fnCntDemo/fnPage.zul");
//		FnCntTemplateComposer.of(iclMain, "/legionLab/pageTemplate/fnCntDemo/fnPage.zul", "/legionLab/pageTemplate/fnCntDemo/cntPage.zul");
	}

	
	@Listen(Events.ON_CLICK + "=#miGaStaffShiftDemo")
	public void miGaStaffShiftDemo_clicked() {
//		iclMain.setSrc("/legionmodule/pageTemplate/fnLeftTemplate.zul?fnUri=/legionLab/gaStaffShiftDemo/fnPage.zul");
		iclMain.setSrc(FnCntTemplateComposer.FN_LEFT_TEMPLATE_URI + "?fnUri=/legionLab/gaStaffShiftDemo/fnPage.zul");
//		FnCntTemplateComposer.of(iclMain, "/legionLab/gaStaffShiftDemo/fnPage.zul");
	}
}
