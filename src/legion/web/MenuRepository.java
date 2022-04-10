package legion.web;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.util.DataFO;

public class MenuRepository {
	protected static Logger log = LoggerFactory.getLogger(MenuRepository.class);
	
	public final static String REPOSITORY_KEY = "APPLICATION_REPOSITORY_KEY";
	public final static String MAIN_MENU = "MAIN_MENU";
	
	
	
	// -------------------------------------------------------------------------------
	private Map<String, MenuInfo> menus;
	private Map<String, MenuItemInfo> menuRefIds;
	
	// -------------------------------------------------------------------------------
	private final static MenuRepository INSTANCE = new MenuRepository();

	private MenuRepository() {
		menus = new HashMap<>();
		menuRefIds = new HashMap<>();
	}
	public final static MenuRepository getInstance() {
		return INSTANCE;
	}

	// -------------------------------------------------------------------------------
	public void addMenu(String menuName, MenuInfo menu) {
		menus.put(menuName, menu);
	}

	public MenuInfo getMenu(String menuName) {
		return menus.get(menuName);
	}

	public boolean containsMenuItemRefid(String _menuRefId) {
		return menuRefIds.containsKey(_menuRefId);
	}

	public MenuItemInfo getMenuItem(String _menuRefId) {
		return menuRefIds.get(_menuRefId);
	}

	// -------------------------------------------------------------------------------
	public void initMainMenu(InputStream _menuStream) {
		MenuInfo menu = xml2MainMenu(_menuStream);
		addMenu(MenuRepository.MAIN_MENU, menu);

		List<MenuEntry> items = menu.getItems();
		if (items != null && !items.isEmpty())
			for (MenuEntry item : items)
				if (item instanceof MenuItemInfo)
					registerMenuItemRefId((MenuItemInfo) item);
		
		log.debug("menu.toString(): {}", menu.toString());
	}
	
	protected static synchronized MenuInfo xml2MainMenu(InputStream _input) {
		SAXBuilder saxBuilder = null;
		Document doc = null;
		Element menuRoot = null;
		MenuInfo menu = null;
		try {
			saxBuilder = new SAXBuilder();
			doc = saxBuilder.build(_input);
			menuRoot = doc.getRootElement();
			if (menuRoot != null) {
				menu = new MenuInfo();
				parseItems(menuRoot, menu);
			}
		} catch (JDOMException e) {
			log.error("initial XML parsing fail. {}", e.getMessage());
			return null;
		} catch (Exception e) {
			log.error("initial XML parsing fail. {}", e.getMessage());
			return null;
		} finally {
			saxBuilder = null;
			doc = null;
		}
		return menu;
	}
	
	/** 轉換MenuItem tag成相對的Menu物件 */
	private static void parseItems(Element _parentEle, MenuEntry _parent)
			throws JDOMException, UnsupportedEncodingException, Exception {
		List<Element> items = _parentEle.getChildren("MenuItem");
		if (items != null && !items.isEmpty()) {
			Iterator<Element> itemsIt = items.iterator();
			while (itemsIt.hasNext()) {
				Element itemEle = itemsIt.next();

				MenuItemInfo item = new MenuItemInfo(itemEle.getAttributeValue("refId"), "",
						itemEle.getAttributeValue("text"), itemEle.getAttributeValue("localeLabelKey"));
				if (itemEle.getAttributeValue("navigateUrl") != null)
					item.setNavigateUrl(itemEle.getAttributeValue("navigateUrl"));
				if (itemEle.getAttributeValue("target") != null)
					item.setTarget(itemEle.getAttributeValue("target"));
				if (itemEle.getAttributeValue("condition") != null)
					item.setCondition(itemEle.getAttributeValue("condition"));
				if (itemEle.getAttributeValue("handlerClass") != null)
					item.setHandlerClass(itemEle.getAttributeValue("handlerClass"));
				if (itemEle.getAttributeValue("handlerMethod") != null)
					item.setHandlerMethod(itemEle.getAttributeValue("handlerMethod"));
				if (itemEle.getAttributeValue("handlerData") != null)
					item.setHandlerData(itemEle.getAttributeValue("handlerData"));
				if (itemEle.getAttributeValue("invalidHidden") != null
						&& "false".equalsIgnoreCase(itemEle.getAttributeValue("invalidHidden")))
					item.setInvalidHidden(false);
				else
					item.setInvalidHidden(true);

				// absHoverImage
				if (itemEle.getAttributeValue("absHoverImage") != null)
					item.setAbsHoverImage(itemEle.getAttributeValue("absHoverImage"));

				// absImage
				if (itemEle.getAttributeValue("absImage") != null)
					item.setAbsImage(itemEle.getAttributeValue("absImage"));

				// iconSclass
				if (itemEle.getAttributeValue("iconSclass") != null)
					item.setIconSclass(itemEle.getAttributeValue("iconSclass"));

				// remark
				String remark = itemEle.getChildTextTrim("Remark");
				if (!DataFO.isEmptyString(remark))
					item.setRemark(remark);

				//
				item.setParent(_parent);
				_parent.addItem(item);
				parseItems(itemEle, item);
			}
			_parent.setEmpty(false);
		} else
			_parent.setEmpty(true);
	}
	
	protected void registerMenuItemRefId(MenuItemInfo _menuItem) {
		if (!DataFO.isEmptyString(_menuItem.getRefId())) {
			// 有標註refId->進行註冊
			if (menuRefIds.containsKey(_menuItem.getRefId())) {
				log.warn("Menu refId: {} 已存在", _menuItem.getRefId());
				return;
			}
			menuRefIds.put(_menuItem.getRefId(), _menuItem);
		}
		// 子選單
		List<MenuEntry> items = _menuItem.getItems();
		if (items != null && !items.isEmpty())
			for (MenuEntry item : items)
				if (item instanceof MenuItemInfo)
					registerMenuItemRefId((MenuItemInfo) item);
	}

}
