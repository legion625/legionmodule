package legion.system;

import legion.DataServiceFactory;
import legion.data.SystemDataService;
import legion.system.type.SysAttrType;

public class SysAttr extends SysObjectModel {

	// -------------------------------------------------------------------------------
//	private final static String TYPE_INDEX = "0"; // TODO public 有用到再開放
//	private final static String TYPE_INDEX_NAME = "系統屬性別"; // TODO public 有用到再開放

	private SysAttrType type = SysAttrType.UNDEFINED; // 屬性類別
	private String key = ""; // 屬性索引
	private String value = ""; // 屬性名稱

	// -------------------------------------------------------------------------------
	private SysAttr(SysAttrType type) {
		this.type = type;
	}

	protected static SysAttr newInstance(SysAttrType _type) {
		SysAttr sa = new SysAttr(_type);
		sa = sa.configNewInstance();
		return sa;
	}

	public static SysAttr getInstance(String _uid, SysAttrType _type, long _objectCreateTime,
			long _objectUpdateTime) {
		SysAttr sa = new SysAttr(_type);
		sa.configGetInstance(_uid, _objectCreateTime, _objectUpdateTime);
		return sa;
	}

//	protected static SysAttr load(String _uid) {
//		return DataServiceFactory.getInstance().getService(SystemDataService.class).loadSysAttr(_uid);
//	}

	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
	public SysAttrType getType() {
		return type;
	}

	public void setType(SysAttrType type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	// -------------------------------------------------------------------------------
	public int getTypeIdx() {
		return getType() == null ? 0 : getType().getIdx();
	}

	// -------------------------------------------------------------------------------
	@Override
	protected boolean save() {
		return DataServiceFactory.getInstance().getService(SystemDataService.class).saveSysAttr(this);
	}

	@Override
	protected boolean delete() {
		return DataServiceFactory.getInstance().getService(SystemDataService.class).deleteSysAttr(getUid());
//		// 進行交易處理
//		if (!DSManager.getInstance().beginTransaction()) {
//			log.error("beginTransaction return false.");
//			return false;
//		}
//
//		try {
//			// 更新所有相關資訊
//			// TODO
//
//			// 刪除本身
//			if (!DataServiceFactory.getInstance().getService(SystemDataService.class).deleteSysAttr(this)) {
//				log.error("deleteSysAttr return false.");
//				DSManager.getInstance().failTransaction();
//				return false;
//			}
//
//			DSManager.getInstance().endTransaction();
//			return true;
//		} catch (Throwable e) {
//			// rollback transaction it there is one.
//			DSManager.getInstance().failTransaction();
//			log.error(e.getMessage());
//			e.printStackTrace();
//			return false;
//		}
	}

	// -------------------------------------------------------------------------------
	public static SysAttr create() {
		SysAttr sa = newInstance(SysAttrType.SYS);
		if (sa.save())
			return sa;
		return null;
	}

}
