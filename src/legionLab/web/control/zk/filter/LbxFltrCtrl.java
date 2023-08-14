package legionLab.web.control.zk.filter;

import java.util.List;

import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListitemRenderer;

public class LbxFltrCtrl<T> extends FltrCtrl<T> {
	private Listbox lbx;

	private LbxFltrCtrl(Listbox lbx, ListitemRenderer<T> renderer) {
		this.lbx = lbx;
		this.lbx.setItemRenderer(renderer);
	}

	public static <T> LbxFltrCtrl<T>  of(Listbox lbx, ListitemRenderer<T> renderer) {
		return new LbxFltrCtrl<>(lbx, renderer);
	}

	@Override
	protected void setModel(List<T> filteredList) {
		ListModelList<T> model = new ListModelList<>(filteredList);
		lbx.setModel(model);
	}

}
