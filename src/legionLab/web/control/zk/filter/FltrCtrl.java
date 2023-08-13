package legionLab.web.control.zk.filter;

import java.util.List;
import java.util.function.Function;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;

public abstract class FltrCtrl<T> {
	//
	private Function<List<T>, List<T>> fnFilter;

	//
	private List<T> list;
	
	public void initFilter(Textbox[] txbs, Function<List<T>, List<T>> fnFilter) {
		for (Textbox txb : txbs) {
			txb.addEventListener(Events.ON_CHANGE, evt -> refresh());
			txb.addEventListener(Events.ON_OK, evt -> refresh());
		}
		//
		this.fnFilter = fnFilter;
	}

	public void refresh(List<T> list) {
		this.list = list;
		refresh();
	}
	
	private void refresh() {
		List<T> filteredList =fnFilter==null?list: fnFilter.apply(list);
		setModel(filteredList);
	}
	
	protected abstract void setModel(List<T> filteredList);
}
