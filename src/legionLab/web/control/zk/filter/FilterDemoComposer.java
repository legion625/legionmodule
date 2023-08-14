package legionLab.web.control.zk.filter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;

import legion.util.DataFO;
import legion.util.LogUtil;
import legion.util.NumberFormatUtil;
import legion.util.filter.FilterOperation;
import legion.util.filter.FilterOperation.FilterCompareOp;
import legion.web.zk.ZkUtil;
import legionLab.web.control.zk.filter.FilterDemoData.FilterDemoDataType;

public class FilterDemoComposer extends SelectorComposer<Component>{

	/**/
	@Wire
	private Grid gridDemo;
	@Wire
	private Textbox txbGridFltrAaa;
	@Wire
	private Combobox cbbGridFltrType;
	@Wire
	private Doublebox dbbGridFltrDdd0, dbbGridFltrDdd1;
	@Wire
	private Checkbox chbGridFltrBbb0, chbGridFltrBbb1;
	
	private GridFltrCtrl<FilterDemoData> gridFltrCtrl;
	
	/**/
	@Wire
	private Listbox lbxDemo;
	@Wire
	private Textbox txbLbxFltrAaa;
	@Wire
	private Combobox cbbLbxFltrType;
	@Wire
	private Doublebox dbbLbxFltrDdd0, dbbLbxFltrDdd1;
	@Wire
	private Checkbox chbLbxFltrBbb0, chbLbxFltrBbb1;
	
	private LbxFltrCtrl<FilterDemoData> lbxFltrCtrl;
	
	
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
		/* Grid */
		RowRenderer<FilterDemoData> rowRenderer = (r, d, i) -> {
			r.appendChild(new Label(d.getAaa()));
			r.appendChild(new Label(DataFO.getStr(d.isBbb())));
			r.appendChild(new Label(NumberFormatUtil.getDecimalString(d.getDdd(), 4)));
			r.appendChild(new Label(d.getType().getName()));
		};
		gridFltrCtrl = GridFltrCtrl.of(gridDemo, rowRenderer);
		//
		Textbox[] txbGridFilters = new Textbox[] {txbGridFltrAaa};
		Combobox[] cbbGridFilters = new Combobox[] {cbbGridFltrType};
		Doublebox[] dbbGridFilters = new Doublebox[] {dbbGridFltrDdd0, dbbGridFltrDdd1};
		Checkbox[] chbGridFilters = new Checkbox[] {chbGridFltrBbb0, chbGridFltrBbb1};
		gridFltrCtrl.initFilter(txbGridFilters, cbbGridFilters, dbbGridFilters, chbGridFilters, this::filterGridData);
		
		//
		ZkUtil.initCbb(cbbGridFltrType, FilterDemoDataType.values(), true);
		
		/* Listbox */
		ListitemRenderer<FilterDemoData> liRenderer = (li, d, i)->{
			li.appendChild(new Listcell(d.getAaa()));
			li.appendChild(new Listcell(DataFO.getStr(d.isBbb())));
			li.appendChild(new Listcell(NumberFormatUtil.getDecimalString(d.getDdd(), 4)));
			li.appendChild(new Listcell(d.getType().getName()));
			
		};
		lbxFltrCtrl = LbxFltrCtrl.of(lbxDemo, liRenderer);
		Textbox[] txbLbxFilters = new Textbox[] {txbLbxFltrAaa};
		Combobox[] cbbLbxFilters = new Combobox[] {cbbLbxFltrType};
		Doublebox[] dbbLbxFilters = new Doublebox[] {dbbLbxFltrDdd0, dbbLbxFltrDdd1};
		Checkbox[] chbLbxFilters = new Checkbox[] {chbLbxFltrBbb0, chbLbxFltrBbb1};
		lbxFltrCtrl.initFilter(txbLbxFilters,cbbLbxFilters,dbbLbxFilters, chbLbxFilters, this::filterLbxData);
		
		// 
		ZkUtil.initCbb(cbbLbxFltrType, FilterDemoDataType.values(), true);
	}
	
	private List<FilterDemoData> filterGridData(List<FilterDemoData> _list) {
		/**/
		String fltrAaa = txbGridFltrAaa.getValue();
		boolean fltrKeepBbb0 = chbGridFltrBbb0.isChecked();
		boolean fltrKeepBbb1 = chbGridFltrBbb1.isChecked();
		Double fltrDddLb = dbbGridFltrDdd0.getValue();
		Double fltrDddUb = dbbGridFltrDdd1.getValue();
		FilterDemoDataType fltrType = cbbGridFltrType.getSelectedItem() == null ? null
				: cbbGridFltrType.getSelectedItem().getValue();

		/* filter */
		FilterOperation<FilterDemoDataFilterParam, ?> fop = new FilterOperation<>();
		// aaa
		if (!DataFO.isEmptyString(fltrAaa))
			fop.addCondition(
					FilterOperation.value(FilterDemoDataFilterParam.AAA, FilterCompareOp.like, "%" + fltrAaa + "%"));
		// bbb
		if (!fltrKeepBbb0)
			fop.addCondition(FilterOperation.value(FilterDemoDataFilterParam.BBB, FilterCompareOp.notEqual, false));
		if (!fltrKeepBbb1)
			fop.addCondition(FilterOperation.value(FilterDemoDataFilterParam.BBB, FilterCompareOp.notEqual, true));
		// ddd
		if (fltrDddLb != null)
			fop.addCondition(
					FilterOperation.value(FilterDemoDataFilterParam.DDD, FilterCompareOp.greaterOrequal, fltrDddLb));
		if (fltrDddUb != null)
			fop.addCondition(
					FilterOperation.value(FilterDemoDataFilterParam.DDD, FilterCompareOp.lessOrequal, fltrDddUb));
		// type
		if (fltrType != null)
			fop.addCondition(FilterOperation.value(FilterDemoDataFilterParam.TYPE, FilterCompareOp.equal, fltrType));

		//
		try {
			List<FilterDemoData> filteredList = fop.filterConditions(_list);
			return filteredList;
		} catch (Throwable e) {
			LogUtil.log(e);
			return new ArrayList<>();
		}
	}
	
	private List<FilterDemoData> filterLbxData(List<FilterDemoData> _list) {
		/**/
		String fltrAaa = txbLbxFltrAaa.getValue();
		boolean fltrKeepBbb0 = chbLbxFltrBbb0.isChecked();
		boolean fltrKeepBbb1 = chbLbxFltrBbb1.isChecked();
		Double fltrDddLb = dbbLbxFltrDdd0.getValue();
		Double fltrDddUb = dbbLbxFltrDdd1.getValue();
		FilterDemoDataType fltrType = cbbLbxFltrType.getSelectedItem() == null ? null
				: cbbLbxFltrType.getSelectedItem().getValue();

		/* filter */
		FilterOperation<FilterDemoDataFilterParam, ?> fop = new FilterOperation<>();
		// aaa
		if (!DataFO.isEmptyString(fltrAaa))
			fop.addCondition(
					FilterOperation.value(FilterDemoDataFilterParam.AAA, FilterCompareOp.like, "%" + fltrAaa + "%"));
		// bbb
		if (!fltrKeepBbb0)
			fop.addCondition(FilterOperation.value(FilterDemoDataFilterParam.BBB, FilterCompareOp.notEqual, false));
		if (!fltrKeepBbb1)
			fop.addCondition(FilterOperation.value(FilterDemoDataFilterParam.BBB, FilterCompareOp.notEqual, true));
		// ddd
		if (fltrDddLb != null)
			fop.addCondition(
					FilterOperation.value(FilterDemoDataFilterParam.DDD, FilterCompareOp.greaterOrequal, fltrDddLb));
		if (fltrDddUb != null)
			fop.addCondition(
					FilterOperation.value(FilterDemoDataFilterParam.DDD, FilterCompareOp.lessOrequal, fltrDddUb));
		// type
		if (fltrType != null)
			fop.addCondition(FilterOperation.value(FilterDemoDataFilterParam.TYPE, FilterCompareOp.equal, fltrType));

		//
		try {
			List<FilterDemoData> filteredList = fop.filterConditions(_list);
			return filteredList;
		} catch (Throwable e) {
			LogUtil.log(e);
			return new ArrayList<>();
		}
	}
	
	// -------------------------------------------------------------------------------
	public void refresh(List<FilterDemoData> _list) {
		gridFltrCtrl.refresh(_list);
		lbxFltrCtrl.refresh(_list);
	}
	
}
