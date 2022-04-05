package legion.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MenuItemInfo implements MenuEntry {
	// -------------------------------------------------------------------------------
	private String refId = "";
	private String name = "";
	private String text = "";
	private String localeLabelKey = "";
	private String navigateUrl = "";
	private String target = "";
	private String condition = "";
	private String handlerClass = "";
	private String handlerMethod = "";
	private String handlerData = "";
	private boolean invalidHidden = true;

	private MenuEntry parent;
	private boolean empty;
	private List<MenuEntry> items;
	
	private String absHoverImage = "";
	private String absImage = "";
	private String iconSclass = "";
	private String remark = "";

	// -------------------------------------------------------------------------------
	public MenuItemInfo(String refId, String name, String text, String localeLabelKey) {
		super();
		this.refId = refId;
		this.name = name;
		this.text = text;
		this.localeLabelKey = localeLabelKey;
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLocaleLabelKey() {
		return localeLabelKey;
	}

	public void setLocaleLabelKey(String localeLabelKey) {
		this.localeLabelKey = localeLabelKey;
	}

	public String getNavigateUrl() {
		return navigateUrl;
	}

	public void setNavigateUrl(String navigateUrl) {
		this.navigateUrl = navigateUrl;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getHandlerClass() {
		return handlerClass;
	}

	public void setHandlerClass(String handlerClass) {
		this.handlerClass = handlerClass;
	}

	public String getHandlerMethod() {
		return handlerMethod;
	}

	public void setHandlerMethod(String handlerMethod) {
		this.handlerMethod = handlerMethod;
	}

	public String getHandlerData() {
		return handlerData;
	}

	public void setHandlerData(String handlerData) {
		this.handlerData = handlerData;
	}

	public boolean isInvalidHidden() {
		return invalidHidden;
	}

	public void setInvalidHidden(boolean invalidHidden) {
		this.invalidHidden = invalidHidden;
	}

	public MenuEntry getParent() {
		return parent;
	}

	public void setParent(MenuEntry parent) {
		this.parent = parent;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public List<MenuEntry> getItems() {
		return items;
	}

	public void setItems(List<MenuEntry> items) {
		this.items = items;
	}

	public String getAbsHoverImage() {
		return absHoverImage;
	}

	public void setAbsHoverImage(String absHoverImage) {
		this.absHoverImage = absHoverImage;
	}

	public String getAbsImage() {
		return absImage;
	}

	public void setAbsImage(String absImage) {
		this.absImage = absImage;
	}

	public String getIconSclass() {
		return iconSclass;
	}

	public void setIconSclass(String iconSclass) {
		this.iconSclass = iconSclass;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	// -------------------------------------------------------------------------------
	@Override
	public void addItem(MenuEntry _node) {
		if (items == null)
			items = new ArrayList<>();
		items.add(_node);
	}
	
	public Iterator<MenuEntry> iterator() {
		if (items == null)
			return null;
		return items.iterator();
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("******* MenuItem *******\n");
		result.append("text = " + text + ", localeLabelKey = " + localeLabelKey + ", empty = " + empty
				+ ", condition = " + condition + ", url = " + navigateUrl + " \n");
		if (items != null && !items.isEmpty()) {
			result.append("    +++++ child +++++\n");
			Iterator<MenuEntry> it = items.iterator();
			while (it.hasNext()) {
				MenuEntry entry = it.next();
				result.append(entry.toString());
			}
			result.append("    +++++++++++++++++\n");
		}
		return result.toString();
	}

}
