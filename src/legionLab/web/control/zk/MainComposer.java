package legionLab.web.control.zk;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;

import legion.util.LogUtil;
import legion.web.control.zk.legionmodule.pageTemplate.FnCntTemplateComposer;
import legion.web.control.zk.legionmodule.system.SysAttrPageComposer;

public class MainComposer extends SelectorComposer<Component> {
	@Wire
	private Include iclMain;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
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
	
	// -------------------------------------------------------------------------------
	@Listen(Events.ON_CLICK + "=#miSysAttr")
	public void miSysAttr_clicked() {
		iclMain.setSrc(SysAttrPageComposer.URI);
	}

}
