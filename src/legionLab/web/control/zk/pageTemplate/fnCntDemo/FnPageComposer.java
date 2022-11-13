package legionLab.web.control.zk.pageTemplate.fnCntDemo;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;

import legion.util.LogUtil;
import legion.web.control.zk.legionmodule.pageTemplate.FnCntProxy;
import legion.web.zk.ZkNotification;

public class FnPageComposer extends SelectorComposer<Component> {
	public final static String URI = "/legionLab/pageTemplate/fnCntDemo/fnPage.zul";

	@Wire
	private Include iclSubpage;

	private FnCntProxy proxy;

	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			proxy = FnCntProxy.register(this);
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}

	@Listen(Events.ON_CLICK + "=#btn1")
	public void btn1_clicked() {
		proxy.refreshFnUri(FnPageComposer.URI);
	}

	@Listen(Events.ON_CLICK + "=#btn2")
	public void btn2_clicked() {
		proxy.refreshFnUri(FnPageComposer.URI);
		FnPageComposer c = proxy.getComposer(FnPageComposer.class);
		ZkNotification.info("btn2_clicked: " + c.getValue());
	}

	private String getValue() {
		return "FnPageValue";
	}

	@Listen(Events.ON_CLICK + "=#btn3")
	public void btn3_clicked() {
		proxy.refreshCntUri(CntPageComposer.URI);
	}

	@Listen(Events.ON_CLICK + "=#btn4")
	public void btn4_clicked() {
		proxy.refreshCntUri(CntPageComposer.URI);
		CntPageComposer c = proxy.getComposer(CntPageComposer.class);
		ZkNotification.info("btn4_clicked: " + c.getValue());
	}

	@Listen(Events.ON_CLICK + "=#btn5")
	public void btn5_clicked() {
		proxy.refreshPage(iclSubpage, CntPageComposer.URI);
	}

	@Listen(Events.ON_CLICK + "=#btn6")
	public void btn6_clicked() {
		CntPageComposer c = CntPageComposer.of(iclSubpage);
		ZkNotification.info("btn6_clicked: " + c.getValue());
	}

	@Listen(Events.ON_CLICK + "=#btn7")
	public void btn7_clicked() {
		proxy.refreshCntUri(CntPageComposer.URI);
		proxy.setFnOpen(false);
	}

}
