package legion.web.control.zk.legionmodule.pageTemplate;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Include;

public class FnCntProxy {
	private FnCntTemplateComposer composer;

	private FnCntProxy(FnCntTemplateComposer composer) {
		this.composer = composer;
	}

	public static FnCntProxy register() {
		Page page = Executions.getCurrent().getDesktop().getPage("fnCntTemplateRoot");
		System.out.println("page: " + page);
		Component rootCpn = page.getFellowIfAny("fnCntTemplateRoot");
		System.out.println("rootCpn: " + rootCpn);
		FnCntTemplateComposer composer = (FnCntTemplateComposer) rootCpn.getAttribute("$composer");
		System.out.println("composer: " + composer);
		System.out.println("composer.getClass().getSimpleName(): " + composer.getClass().getSimpleName());

		FnCntProxy proxy = new FnCntProxy(composer);
		return proxy;
	}

	// -------------------------------------------------------------------------------
	public void setFnOpen(boolean _open) {
		composer.setFnOpen(_open);
	}

	// -------------------------------------------------------------------------------
	public void refreshFnUri(String _uri) {
		composer.refreshFnUri(_uri);
	}

	public void refreshFnUri(String _uri, Map<String, Object> _dynamicProperties) {
		composer.refreshFnUri(_uri, _dynamicProperties);
	}

	public void refreshCntUri(String _uri) {
		composer.refreshCntUri(_uri);
	}

	public void refreshCntUri(String _uri, Map<String, Object> _dynamicProperties) {
		composer.refreshCntUri(_uri, _dynamicProperties);
	}

	public void refreshPage(Include _iclSubpage, String _uri) {
		composer.refreshPage(_iclSubpage, _uri);
	}

	public void refreshPage(Include _iclSubpage, String _uri, Map<String, Object> _dynamicProperties) {
		composer.refreshPage(_iclSubpage, _uri, _dynamicProperties);
	}
}
