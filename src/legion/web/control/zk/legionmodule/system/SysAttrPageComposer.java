package legion.web.control.zk.legionmodule.system;

import java.util.List;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.RowRenderer;

import legion.system.SysAttr;
import legion.util.LogUtil;

public class SysAttrPageComposer extends SelectorComposer<Component> {
	public final static String URI = "/legionmodule/system/sysAttrPage.zul";

	// -------------------------------------------------------------------------------
	@Wire
	private Grid gridSysAttr;
	
	// -------------------------------------------------------------------------------
	private List<SysAttr> sysAttrList;
	
	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}
	
	// -------------------------------------------------------------------------------
	private void init() {
		RowRenderer<SysAttr> renderer = (row, sa, i) -> {
			row.appendChild(new Label(sa.getType().getDesp()));
			row.appendChild(new Label(sa.getKey()));
			row.appendChild(new Label(sa.getValue()));
		};
		gridSysAttr.setRowRenderer(renderer);
	}
	
	// -------------------------------------------------------------------------------
	public void setSysAttrList(List<SysAttr> sysAttrList) {
		this.sysAttrList = sysAttrList;

		ListModelList<SysAttr> model = sysAttrList == null ? new ListModelList<>() : new ListModelList<>(sysAttrList);
		gridSysAttr.setModel(model);
	}
	
	@Listen(Events.ON_CLICK + "=#btnAddSysAttr")
	public void btnAddSysAttr_clicked() {
		ListModelList<SysAttr> model = (ListModelList) gridSysAttr.getModel();
		
	}
	

}
