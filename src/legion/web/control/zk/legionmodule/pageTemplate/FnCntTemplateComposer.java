package legion.web.control.zk.legionmodule.pageTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;
import org.zkoss.zul.LayoutRegion;

import legion.util.DataFO;
import legion.util.LogUtil;

public class FnCntTemplateComposer extends SelectorComposer<Component> {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	// -------------------------------------------------------------------------------
	public final static String FN_LEFT_TEMPLATE_URI = "/legionmodule/pageTemplate/fnLeftTemplate.zul";

	// -------------------------------------------------------------------------------
	@Wire
	private LayoutRegion layoutRegionFn;
	@Wire
	private Include iclFn;
	@Wire
	private Include iclCnt;

	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			String fnOpen = Executions.getCurrent().getParameter("fnOpen");
			String fnUri = Executions.getCurrent().getParameter("fnUri");
			String cntUri = Executions.getCurrent().getParameter("cntUri");

			if (!DataFO.isEmptyString(fnOpen) && fnOpen.equalsIgnoreCase("false"))
				setFnOpen(false);
			else
				setFnOpen(true);

			if (!DataFO.isEmptyString(fnUri))
				refreshFnUri(fnUri);

			if (!DataFO.isEmptyString(cntUri))
				refreshCntUri(cntUri);
		} catch (Throwable e) {
			LogUtil.log(log, e, Level.ERROR);
		}
	}

	// -------------------------------------------------------------------------------
	void setFnOpen(boolean _open) {
		layoutRegionFn.setOpen(_open);
	}

	// -------------------------------------------------------------------------------
	void refreshFnUri(String _uri) {
		refreshFnUri(_uri, null);
	}

	/** @deprecated _r may not be required? */
	@Deprecated
	void refreshFnUri(String _uri, Runnable _r) {
		refreshPage(iclFn, _uri, _r);
	}

	void refreshCntUri(String _uri) {
		refreshCntUri(_uri, null);
	}

	/** @deprecated _r may not be required? */
	@Deprecated
	void refreshCntUri(String _uri, Runnable _r) {
		refreshPage(iclCnt, _uri, _r);
	}

	void refreshPage(Include _iclSubpage, String _uri) {
		refreshPage(_iclSubpage, _uri, null);
	}

	/** @deprecated _r may not be required? */
	@Deprecated
	void refreshPage(Include _iclSubpage, String _uri, Runnable _r) {
		_iclSubpage.setSrc("");
		_iclSubpage.setSrc(_uri);

		if (_r != null) {
			Thread thread = new Thread(_r);
			thread.run();
		}

	}

}
