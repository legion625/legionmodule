package legionLab.web.control.zk;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;
import org.zkoss.zul.Menubar;

import legion.util.LogUtil;
import legion.web.MenuEntry;
import legion.web.MenuInfo;
import legion.web.MenuItemInfo;
import legion.web.MenuRepository;

public class Main2Composer extends SelectorComposer<Component> {
	private Logger log = LoggerFactory.getLogger(Main2Composer.class);
	
	@Wire
	private Menubar menubar;

	@Wire
	private Include iclMain;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		log.debug("Main2Composer.doAfterCompose");
		System.out.println("Main2Composer.doAfterCompose");
		try {
			super.doAfterCompose(comp);
			test();
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}
	
	public void test() {
		log.debug("Main2Composer.test");
		System.out.println("Main2Composer.test");
		MenuInfo menuInfo = MenuRepository.getInstance().getMenu(MenuRepository.MAIN_MENU);
		List<MenuEntry> menuItemList =  menuInfo.getItems();
		for(MenuEntry me: menuItemList) {
			MenuItemInfo mi = (MenuItemInfo) me;
			log.error("{}\t{}\t{}", mi.getName(), mi.getText(), mi.getRefId());
		}
		
		
	}
}
