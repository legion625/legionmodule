package legion.system.type;

import legion.type.IdxEnum;

public enum SysAttrType implements IdxEnum {
	UNDEFINED(0, "未定義"), SYS(1, "系統"), //
	;

	private int idx;
	private String name;

	private SysAttrType(int idx, String name) {
		this.idx = idx;
		this.name = name;
	}

	@Override
	public int getIdx() {
		return idx;
	}

	@Override
	public String getName() {
		return name;
	}

	// -------------------------------------------------------------------------------
	public static SysAttrType get(int _idx) {
		for (SysAttrType t : values())
			if (t.idx == _idx)
				return t;
		return UNDEFINED;
	}

}
