package legionLab.web.control.zk.filter;

import java.util.List;
import java.util.function.Function;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;

public class GridFltrCtrl<T> extends FltrCtrl<T> {
	private Grid grid;

	private GridFltrCtrl(Grid grid, RowRenderer<T> renderer) {
		this.grid = grid;
		this.grid.setRowRenderer(renderer);
	}

	public static <T> GridFltrCtrl<T> of(Grid grid, RowRenderer<T> renderer) {
		return new GridFltrCtrl<>(grid, renderer);
	}

	@Override
	protected void setModel(List<T> filteredList) {
		ListModelList<T> model = new ListModelList<>(filteredList);
		grid.setModel(model);
	}

}
