package legion.web.control.zk.legionmodule;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.impl.XulElement;

import legion.ISystemInfo;
import legion.LegionContext;
import legion.SystemInfoDefault;
import legion.util.DataFO;
import legion.util.LogUtil;
import legion.web.MenuEntry;
import legion.web.MenuInfo;
import legion.web.MenuItemInfo;
import legion.web.MenuRepository;
import legion.web.control.zk.legionmodule.system.SysAttrPageComposer;
import legion.web.zk.ZkNotification;
import legion.web.zk.ZkUtil;

public class MainComposer extends SelectorComposer<Component> {
	private Logger log = LoggerFactory.getLogger(MainComposer.class);
	
	@Wire
	private Label lbSysName;
	
	@Wire
	private Menubar menubar;

	@Wire
	private Include iclMain;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		log.debug("Main2Composer.doAfterCompose");
		try {
			super.doAfterCompose(comp);
			initSys() ;
			initMenubar();
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}
	
	// -------------------------------------------------------------------------------
	@Listen(Events.ON_CLICK + "=#btnHome")
	public void btnHome_clicked() {
		iclMain.setSrc(null);
	}

	// -------------------------------------------------------------------------------
	private void initSys() {
		ISystemInfo systemInfo = LegionContext.getInstance().getSystemInfo();
		String sysName = systemInfo.getName();
		if (!DataFO.isEmptyString(sysName))
			lbSysName.setValue(sysName);
	}
	
	private void initMenubar() {
		log.debug("Main2Composer.test");
		menubar.getChildren().clear();
		
		MenuInfo menuInfo = MenuRepository.getInstance().getMenu(MenuRepository.MAIN_MENU);
		List<MenuEntry> menuItemList =  menuInfo.getItems();
		for(MenuEntry me: menuItemList) {
			parseMenuEntry(menubar, me);
		}
	}
	
	private void parseMenuEntry(XulElement _parentComponent, MenuEntry _menuEntry) {
		MenuItemInfo mi = (MenuItemInfo) _menuEntry;
		int itemSize = mi.getItems() == null ? 0 : mi.getItems().size();
		log.debug("{}\t{}\t{}\t{}", mi.getName(), mi.getText(), mi.getRefId(), itemSize);

		// 子階為0視為最末階，建menuitem。
		if (itemSize == 0) {
			Menuitem menuitem = createMenuitem(mi);
			_parentComponent.appendChild(menuitem);
		}
		// 子階不為0，再建構出一層menu。
		else {
			Menu menu = createMenu(mi);
			_parentComponent.appendChild(menu);
		}
	}
	
	private Menu createMenu(MenuItemInfo _mi) {
		Menu menu = new Menu(_mi.getText());
		menu.setIconSclass(_mi.getIconSclass());
		Menupopup menupopup = new Menupopup();
		

		List<MenuEntry> childrenList = _mi.getItems();
		// 所有子階中，有可能是最末階、也有可能再下階還有，呼叫parseMenuEntry遞迴處理。
		if (childrenList != null)
			for (MenuEntry c : childrenList) {
				parseMenuEntry(menupopup, c);
			}
		menu.appendChild(menupopup);
		for(Component cpn: menupopup.getChildren()) {
			log.debug("{}", cpn.toString());
		}
		return menu;
	}
	
	
	private Menuitem createMenuitem(MenuItemInfo _mi) {
		Menuitem menuitem = new Menuitem(_mi.getText());
		menuitem.setIconSclass(_mi.getIconSclass());
		String navigateUrl = _mi.getNavigateUrl(); 
		if (DataFO.isEmptyString(navigateUrl))
			menuitem.addEventListener(Events.ON_CLICK, evt -> ZkNotification.error("未指定目錄選單內容。"));
		else
			menuitem.addEventListener(Events.ON_CLICK, evt -> iclMain.setSrc(navigateUrl));
		return menuitem;
	}
}
