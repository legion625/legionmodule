package legionLab.web.control.zk.filter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;

import legion.util.DataFO;
import legion.util.LogUtil;
import legion.util.filter.FilterOperation;
import legion.util.filter.FilterOperation.FilterCompareOp;

public class FilterDemoComposer extends SelectorComposer<Component>{

	@Wire
	private Grid gridDemo;
	@Wire
	private Textbox txbFltrAaa, txbFltrBbb, txbFltrCcc, txbFltrDdd;
	
	private GridFltrCtrl<FilterDemoData> gridFltrCtrl;
	
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			init();
			
			// mock data 
			List<FilterDemoData> dataList = FilterDemoData.getMockData();
			// refresh
			refresh(dataList);
			
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}
	
	private void init() {
		RowRenderer<FilterDemoData> renderer = (r, d, i) -> {
			r.appendChild(new Label(d.getAaa()));
			r.appendChild(new Label(d.getBbb()));
			r.appendChild(new Label(d.getCcc()));
			r.appendChild(new Label(d.getDdd()));
		};
		gridFltrCtrl = GridFltrCtrl.of(gridDemo, renderer);
		//
		Textbox[] txbs = new Textbox[] {txbFltrAaa, txbFltrBbb, txbFltrCcc, txbFltrDdd};
		gridFltrCtrl.initFilter(txbs, this::filter);

	}
	
	private List<FilterDemoData> filter(List<FilterDemoData> _list){
		/**/
		String fltrAaa = txbFltrAaa.getValue();
		String fltrBbb = txbFltrBbb.getValue();
		String fltrCcc = txbFltrCcc.getValue();
		String fltrDdd = txbFltrDdd.getValue();
		
		/* filter*/
		FilterOperation<FilterDemoDataFilterParam, ?> fop = new FilterOperation<>();
		if(!DataFO.isEmptyString(fltrAaa))
			fop.addCondition(FilterOperation.value(FilterDemoDataFilterParam.AAA, FilterCompareOp.like,"%"+ fltrAaa+"%"));
		if(!DataFO.isEmptyString(fltrBbb))
			fop.addCondition(FilterOperation.value(FilterDemoDataFilterParam.BBB, FilterCompareOp.like,"%"+ fltrBbb+"%"));
		if(!DataFO.isEmptyString(fltrCcc))
			fop.addCondition(FilterOperation.value(FilterDemoDataFilterParam.CCC, FilterCompareOp.like,"%"+ fltrCcc+"%"));
		if(!DataFO.isEmptyString(fltrDdd))
			fop.addCondition(FilterOperation.value(FilterDemoDataFilterParam.DDD, FilterCompareOp.like,"%"+ fltrDdd+"%"));
		
		//
		try {
			List<FilterDemoData> filteredList = fop.filterConditions(_list);
			return filteredList;
		} catch (Throwable e) {
			LogUtil.log(e);
			return new ArrayList<>();
		}
	}
	
	public void refresh(List<FilterDemoData> _list) {
		gridFltrCtrl.refresh(_list);
		
	}
	
	
	
	
	
	
	
}
