package legionLab.web.control.zk.filter;

import java.util.List;
import java.util.function.Function;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Textbox;

public abstract class FltrCtrl<T> {
	//
	private Function<List<T>, List<T>> fnFilter;

	//
	private List<T> list;

	public void initFilter(Textbox[] txbs, Combobox[] cbbs, Doublebox[] dbbs, Checkbox[] chbs,
			Function<List<T>, List<T>> fnFilter) {
		if (txbs != null)
			for (Textbox txb : txbs) {
				txb.addEventListener(Events.ON_CHANGE, evt -> refresh());
				txb.addEventListener(Events.ON_OK, evt -> refresh());
			}

		if (cbbs != null)
			for (Combobox cbb : cbbs) {
				cbb.addEventListener(Events.ON_SELECT, evt -> refresh());
				cbb.addEventListener(Events.ON_CHANGE, evt -> refresh());
				cbb.addEventListener(Events.ON_OK, evt -> refresh());
			}
		if (dbbs != null)
			for (Doublebox dbb : dbbs) {
				dbb.addEventListener(Events.ON_CHANGE, evt -> refresh());
				dbb.addEventListener(Events.ON_OK, evt -> refresh());
			}
		if (chbs != null)
			for (Checkbox chb : chbs) {
				chb.addEventListener(Events.ON_CHECK, evt -> refresh());
			}

		//
		this.fnFilter = fnFilter;
	}

	public void refresh(List<T> list) {
		this.list = list;
		refresh();
	}

	private void refresh() {
		List<T> filteredList = fnFilter == null ? list : fnFilter.apply(list);
		setModel(filteredList);
	}

	protected abstract void setModel(List<T> filteredList);
}
