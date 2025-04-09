package legion.system.type;

import legion.kernel.IdxEnum;

public enum SysAttrType implements IdxEnum {
	UNDEFINED(0, "未定義"), SYS(1, "系統"), //
	;

	private int idx;
	private String desp;

	private SysAttrType(int idx, String desp) {
		this.idx = idx;
		this.desp = desp;
	}

	@Override
	public int getIdx() {
		return idx;
	}

	@Override
	public String getDesp() {
		return desp;
	}
	
	// -------------------------------------------------------------------------------
	public static SysAttrType get(int _idx) {
		for(SysAttrType t:values())
			if(t.idx== _idx)
				return t;
		return UNDEFINED;
	}
}
