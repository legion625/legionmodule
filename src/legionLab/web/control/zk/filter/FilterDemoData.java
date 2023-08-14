package legionLab.web.control.zk.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import legion.type.IdxEnum;

public class FilterDemoData {
	private String aaa;
	private boolean bbb;
	private double ddd;
	private FilterDemoDataType type;
	
	public FilterDemoData(String aaa, boolean bbb, double ddd, FilterDemoDataType type) {
		this.aaa = aaa;
		this.bbb = bbb;
		this.ddd = ddd;
		this.type = type;
	}
	
	public String getAaa() {
		return aaa;
	}
	
	public boolean isBbb() {
		return bbb;
	}

	public double getDdd() {
		return ddd;
	}

	public FilterDemoDataType getType() {
		return type;
	}
	
	// -------------------------------------------------------------------------------
	public enum FilterDemoDataType implements IdxEnum {
		UNDEFINED(0, "未定義"), //
		T1(1, "類型1"), T2(2, "類型2"), T3(3, "類型3"), //
		;

		private int idx;
		private String name;

		private FilterDemoDataType(int idx, String name) {
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
		
		public static FilterDemoDataType get(int _idx) {
			for (FilterDemoDataType t : values())
				if (_idx == t.idx)
					return t;
			return UNDEFINED;
		}

	}
	
	// -------------------------------------------------------------------------------
	public static List<FilterDemoData> getMockData(){
		List<FilterDemoData> list = new ArrayList<>();
		for (int i = 0; i < 100; i++)
			list.add(new FilterDemoData( //
					"AAA" + generateRandomString(2), //
					getRandomBoolean(), //
					getRandomDouble(), //
					getRandomType() //
			));
		return list;
	}


	public static String generateRandomString(int length) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int randomType = random.nextInt(3); // 0: 英文, 1: 數字, 2: 中文
			char randomChar;

			if (randomType == 0) {
				randomChar = (char) (random.nextInt(26) + 'a'); // 英文小寫字母
			} else if (randomType == 1) {
				randomChar = (char) (random.nextInt(10) + '0'); // 數字
			} else {
				randomChar = (char) (random.nextInt(20902 - 19968 + 1) + 19968); // 中文字符的Unicode範圍
			}

			sb.append(randomChar);
		}

		return sb.toString();
	}

	public static boolean getRandomBoolean() {
		Random random = new Random();
		return random.nextBoolean();
	}
	
	public static double getRandomDouble() {
		Random random = new Random();
		return random.nextDouble();
	}
	
	public static FilterDemoDataType getRandomType() {
		Random random = new Random();
		return FilterDemoDataType.get(random.nextInt(3) + 1);
	}

}
