package legion.web.control.zk.pageTemplate.fnCntDemo;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;

import legion.web.control.zk.legionmodule.pageTemplate.FnCntProxy;

public class CntPageComposer extends SelectorComposer<Component> {
	public final static String URI = "pageTemplate/fnCntDemo/cntPage.zul";

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		System.out.println(this.getClass().getSimpleName() + ".doAfterCompose");
		FnCntProxy.register();

		/**/
		String value2 = (String) Executions.getCurrent().getAttribute("key2");
		System.out.println("value2: " + value2);
	}
}
