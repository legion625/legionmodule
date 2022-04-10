package legion.web.control.zk.legionmodule.pageTemplate;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;
import org.zkoss.zul.LayoutRegion;
import org.zkoss.zul.West;

import legion.util.DataFO;

public class FnCntTemplateComposer extends SelectorComposer<Component> {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Wire
	private LayoutRegion layoutRegionFn;
	@Wire
	private Include iclFn;
	@Wire
	private Include iclCnt;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		 System.out.println(this.getClass().getSimpleName() + ".doAfterCompose");

		String fnOpen = Executions.getCurrent().getParameter("fnOpen");
		
		System.out.println("fnOpen: " + fnOpen);

		String fnUri = Executions.getCurrent().getParameter("fnUri");
		System.out.println("fnUri: " + fnUri);
		String cntUri = Executions.getCurrent().getParameter("cntUri");
		System.out.println("cntUri: " + cntUri);

		if (!DataFO.isEmptyString(fnOpen) && fnOpen.equalsIgnoreCase("false"))
			setFnOpen(false);
		else
			setFnOpen(true);

		if (!DataFO.isEmptyString(fnUri))
			refreshFnUri(fnUri);

		if (!DataFO.isEmptyString(cntUri))
			refreshCntUri(cntUri);

	}

	// -------------------------------------------------------------------------------
	void setFnOpen(boolean _open) {
		layoutRegionFn.setOpen(_open);
	}

	// -------------------------------------------------------------------------------
	void refreshFnUri(String _uri) {
		refreshFnUri(_uri, null);
	}

	void refreshFnUri(String _uri, Map<String, Object> _dynamicProperties) {
		refreshPage(iclFn, _uri, _dynamicProperties);
	}

	void refreshCntUri(String _uri) {
		refreshCntUri(_uri, null);
	}

	void refreshCntUri(String _uri, Map<String, Object> _dynamicProperties) {
		refreshPage(iclCnt, _uri, _dynamicProperties);
	}

	void refreshPage(Include _iclSubpage, String _uri) {
		refreshPage(_iclSubpage, _uri, null);
	}

	void refreshPage(Include _iclSubpage, String _uri, Map<String, Object> _dynamicProperties) {
		_iclSubpage.setSrc("");
		if (_dynamicProperties != null)
			for (String _key : _dynamicProperties.keySet())
				_iclSubpage.setDynamicProperty(_key, _dynamicProperties.get(_key));
		_iclSubpage.setSrc(_uri);
	}

}
