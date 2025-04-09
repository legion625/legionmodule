package legion.web.control.zk.legionmodule.system;

import java.util.List;
import java.util.Optional;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

import com.amazonaws.services.rds.model.Option;

import legion.BusinessServiceFactory;
import legion.system.SysAttr;
import legion.system.SystemService;
import legion.util.DataFO;
import legion.util.LogUtil;
import legion.web.zk.ZkUtil;

public class SysAttrPageComposer extends SelectorComposer<Component> {
	public final static String URI = "/legionmodule/system/sysAttrPage.zul";

	// -------------------------------------------------------------------------------
	@Wire
	private Grid gridSysAttr;
	
	// -------------------------------------------------------------------------------
	private SystemService sysService = BusinessServiceFactory.getInstance().getService(SystemService.class);
	
	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			init();
			
			List<SysAttr> sysAttrList = sysService.loadSysAttrList();
			refreshSysAttrList(sysAttrList);
			
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}
	
	// -------------------------------------------------------------------------------
	private void init() {
		RowRenderer<SysAttr> renderer = (row, sa, i) -> {
			// 
			Toolbarbutton btnDelete = new Toolbarbutton("Delete");
			btnDelete.setIconSclass("fa fa-minus");
			btnDelete.addEventListener(Events.ON_CLICK, el -> {
				ListModelList<SysAttr> model =(ListModelList) gridSysAttr.getListModel();
				if(sysService.deleteSysAttr(sa.getUid())) {
					ZkUtil.showNotificationInfo("刪除系統屬性成功。");
					model.remove(sa);
				}else {
					ZkUtil.showNotificationError();
				}
			});
			row.appendChild(btnDelete);
			//
			row.appendChild(new Label(sa.getType().getDesp()));
			// key
			Textbox txbKey = new Textbox(sa.getKey());
			txbKey.addEventListener(Events.ON_CHANGE, evt -> {
				String key = DataFO.orElse(txbKey.getValue(), "");
				sa.setKey(key);
				if (sysService.saveSysAttr(sa))
					ZkUtil.showNotificationInfo("更新系統屬性成功。");
				else
					ZkUtil.showNotificationError();
			});
			row.appendChild(txbKey);
			// value
			Textbox txbValue = new Textbox(sa.getValue());
			txbValue.addEventListener(Events.ON_CHANGE, evt -> {
				String value = DataFO.orElse(txbValue.getValue(), "");
				sa.setValue(value);
				if (sysService.saveSysAttr(sa))
					ZkUtil.showNotificationInfo("更新系統屬性成功。");
				else
					ZkUtil.showNotificationError();
			});
			row.appendChild(txbValue);
		};
		gridSysAttr.setRowRenderer(renderer);
	}
	
	// -------------------------------------------------------------------------------
	public void refreshSysAttrList(List<SysAttr> sysAttrList) {
		ListModelList<SysAttr> model = sysAttrList == null ? new ListModelList<>() : new ListModelList<>(sysAttrList);
		gridSysAttr.setModel(model);
	}
	
	@Listen(Events.ON_CLICK + "=#btnAddSysAttr")
	public void btnAddSysAttr_clicked() {
		ListModelList<SysAttr> model = (ListModelList) gridSysAttr.getModel();

		SysAttr newSysAttr = sysService.createSysAttr();
		if (newSysAttr != null) {
			model.add(newSysAttr);
			ZkUtil.showNotificationInfo("新增系統屬性成功。");
			return;
		} else {
			ZkUtil.showNotificationError();
		}
	}
	

}
