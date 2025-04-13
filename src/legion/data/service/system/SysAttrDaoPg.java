package legion.data.service.system;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.event.Level;

import legion.data.service.PgDao;
import legion.system.SysAttr;
import legion.system.type.SysAttrType;
import legion.util.LogUtil;

public class SysAttrDaoPg extends PgDao {

	protected SysAttrDaoPg(String source) {
		super(source);
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------SysAttr------------------------------------
	private final static String TB_SYS_ATTR = "sys_attr";
	private final static String COL_SYS_ATTR_TYPE_IDX = "type_idx";
	private final static String COL_SYS_ATTR_KEY = "attr_key";
	private final static String COL_SYS_ATTR_VALUE = "attr_value";

	boolean saveSysAttr(SysAttr _sysAttr) {
		DbColumn<SysAttr>[] cols = new DbColumn[] {
				DbColumn.of(COL_SYS_ATTR_TYPE_IDX, ColType.INT, SysAttr::getTypeIdx),
				DbColumn.of(COL_SYS_ATTR_KEY, ColType.STRING, SysAttr::getKey),
				DbColumn.of(COL_SYS_ATTR_VALUE, ColType.STRING, SysAttr::getValue), };
		return saveObject(TB_SYS_ATTR, cols, _sysAttr);
	}

	boolean deleteSysAttr(String _uid) {
		return deleteObject(TB_SYS_ATTR, _uid);
	}

	private SysAttr parseSysAttr(ResultSet _rs) {
		try {
			SysAttrType type = SysAttrType.get(_rs.getInt(COL_SYS_ATTR_TYPE_IDX));
			SysAttr sa = SysAttr.getInstance(parseUid(_rs), type, parseObjectCreateTime(_rs), parseObjectUpdateTime(_rs));
			/* pack attributes */
			sa.setKey(_rs.getString(COL_SYS_ATTR_KEY));
			sa.setValue(_rs.getString(COL_SYS_ATTR_VALUE));
			return sa;
		} catch (SQLException e) {
			LogUtil.log(log, e, Level.ERROR);
			return null;
		}
	}

	SysAttr loadSysAttr(String _uid) {
		return loadObject(TB_SYS_ATTR, _uid, this::parseSysAttr);
	}

	List<SysAttr> loadSysAttrList() {
		return loadObjectList(TB_SYS_ATTR, this::parseSysAttr);
	}

}
