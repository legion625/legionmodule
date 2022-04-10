package legionLab.web.control.zk.pageTemplate.fnCntDemo;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;

import legion.web.control.zk.legionmodule.pageTemplate.FnCntProxy;

public class FnPageComposer extends SelectorComposer<Component> {
	public final static String URI = "/legionLab/pageTemplate/fnCntDemo/fnPage.zul";

	@Wire
	private Include iclSubpage;

	private FnCntProxy proxy;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		System.out.println(this.getClass().getSimpleName() + ".doAfterCompose");
		proxy = FnCntProxy.register();

		/**/
		String value1 = (String) Executions.getCurrent().getAttribute("key1");
		System.out.println("value1: " + value1);

	}

	@Listen(Events.ON_CLICK + "=#btn1")
	public void btn1_clicked() {
		proxy.refreshFnUri(FnPageComposer.URI);
	}

	@Listen(Events.ON_CLICK + "=#btn2")
	public void btn2_clicked() {
		Map<String, Object> map = new HashMap<>();
		map.put("key1", "value1");
		proxy.refreshFnUri(FnPageComposer.URI, map);
	}

	@Listen(Events.ON_CLICK + "=#btn3")
	public void btn3_clicked() {
		proxy.refreshCntUri(CntPageComposer.URI);
	}

	@Listen(Events.ON_CLICK + "=#btn4")
	public void btn4_clicked() {
		Map<String, Object> map = new HashMap<>();
		map.put("key2", "value2");
		proxy.refreshCntUri(CntPageComposer.URI, map);
	}

	@Listen(Events.ON_CLICK + "=#btn5")
	public void btn5_clicked() {
		proxy.refreshPage(iclSubpage, CntPageComposer.URI);
	}

	@Listen(Events.ON_CLICK + "=#btn6")
	public void btn6_clicked() {
		Map<String, Object> map = new HashMap<>();
		map.put("key2", "value2");
		proxy.refreshPage(iclSubpage, CntPageComposer.URI, map);
	}
	
	@Listen(Events.ON_CLICK + "=#btn7")
	public void btn7_clicked() {
		System.out.println("btn7_clicked");
		proxy.refreshCntUri(CntPageComposer.URI);
		System.out.println("test1");
		proxy.setFnOpen(false);
		System.out.println("test2");
	}
	
}
