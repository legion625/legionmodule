package legion.system;

import legion.DataServiceFactory;
import legion.data.SystemDataService;
import legion.datasource.manager.DSManager;

public class SysAttr extends SysObjectModel{
	// -------------------------------------------------------------------------------
	private final static String TYPE_INDEX = "0"; // TODO public 有用到再開放
	private final static String TYPE_INDEX_NAME = "系統屬性別"; // TODO public 有用到再開放
	
	private String itemType = ""; // 屬性類別
	private String itemValue = ""; // 屬性名稱
	private String itemKey = ""; // 屬性索引

	// -------------------------------------------------------------------------------
	private SysAttr() {
		super();
	}
	
	protected static SysAttr newInstance() {
		SysAttr attr = new SysAttr();
		if (!attr.generateUid()) {
			log.error("attr.generateUid return false.");
			return null;
		}
		return attr;
	}
	
	protected static SysAttr getInstance(String _uid, long _objCreateTime, long _objUpdateTime) {
		SysAttr attr = new SysAttr();
		attr.setUid(_uid);
		attr.setObjectCreateTime(_objCreateTime);
		attr.setObjectUpdateTime(_objUpdateTime);
		return attr;
	}
	
	protected static SysAttr load(String _uid) {
		return DataServiceFactory.getInstance().getService(SystemDataService.class).loadSysAttrByUid(_uid);
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getItemValue() {
		return itemValue;
	}

	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}

	public String getItemKey() {
		return itemKey;
	}

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}
	
	
	// -------------------------------------------------------------------------------
	@Override
	protected boolean save() {
		return DataServiceFactory.getInstance().getService(SystemDataService.class).saveSysAttr(this);
	}

	@Override
	protected boolean delete() {
		// 進行交易處理
		if (!DSManager.getInstance().beginTransaction()) {
			log.error("beginTransaction return false.");
			return false;
		}

		try {
			// 更新所有相關資訊
			// TODO

			// 刪除本身
			if (!DataServiceFactory.getInstance().getService(SystemDataService.class).deleteSysAttr(this)) {
				log.error("deleteSysAttr return false.");
				DSManager.getInstance().failTransaction();
				return false;
			}

			DSManager.getInstance().endTransaction();
			return true;
		} catch (Throwable e) {
			// rollback transaction it there is one.
			DSManager.getInstance().failTransaction();
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean equals(Object _obj) {
		if (!(_obj instanceof SysAttr))
			return false;
		SysAttr obj = (SysAttr) _obj;
		if (this.getItemKey().equalsIgnoreCase(obj.getItemKey()) && this.getItemKey() == obj.getItemKey())
			return true;
		else
			return false;
	}
	

}
