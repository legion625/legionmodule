package legion.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MenuInfo implements MenuEntry {

	// -------------------------------------------------------------------------------
	private String name;
	private boolean empty;
	private List<MenuEntry> items;

	// -------------------------------------------------------------------------------
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isEmpty() {
		return empty;
	}

	@Override
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public List<MenuEntry> getItems() {
		return items;
	}

	public void setItems(List<MenuEntry> items) {
		this.items = items;
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
		result.append("******* Menu *******\n");
		if (items != null && !items.isEmpty()) {
			result.append("+++++ child +++++\n");
			Iterator<MenuEntry> it = items.iterator();
			while (it.hasNext()) {
				MenuEntry entry = it.next();
				result.append(entry.toString());
			}
			result.append("+++++++++++++++++\n");
		}
		return result.toString();
	}

}
