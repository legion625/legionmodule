package legion.web;

public interface MenuEntry {
	void addItem(MenuEntry _node);

	boolean isEmpty();

	void setEmpty(boolean empty);
}
