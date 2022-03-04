package legion.geneticAlgorithm.staffABTest;

public class Staff {
	private int staffId; // 員工證號
	private String name; // 姓名
	private String sectionCode;
	private String officeNo; // 辦公室
	private int agentStaffId; // 代理人員工證號
	private String shuffleBusNo; // 是否搭交通車
	
//	private int shift; // 班別(1/2)

	Staff(int staffId, String name, String sectionCode, String officeNo, int agentStaffId, String shuffleBusNo) {
		this.staffId = staffId;
		this.name = name;
		this.sectionCode = sectionCode;
		this.officeNo = officeNo;
		this.agentStaffId = agentStaffId;
		this.shuffleBusNo = shuffleBusNo;
	}

	public int getStaffId() {
		return staffId;
	}

	public String getName() {
		return name;
	}

	public String getSectionCode() {
		return sectionCode;
	}

	public String getOfficeNo() {
		return officeNo;
	}

	public int getAgentStaffId() {
		return agentStaffId;
	}

	public String getShuffleBusNo() {
		return shuffleBusNo;
	}

//	public int getShift() {
//		return shift;
//	}
//
//	// -------------------------------------------------------------------------------
//	public void assignShift(int shift) {
//		this.shift = shift;
//	}
	
}
