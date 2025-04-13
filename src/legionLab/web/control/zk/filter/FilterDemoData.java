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
	
	private static final String COMMON_CHINESE =
            "的一是在不了有和人這中大為上個國我以要他時來用們生到作地於出就分對成會可主發年動同工也能下過子說產種面而方後多定行學法所民得經之進著等部度家電力裡如水化高自二理起小物現實加量都兩體制機當使點從業本去把性好應開它合還因由其些然前外天政四日那社義事平形相全表間樣與關各重新線內數正心反你明看原麼利比但質氣第向道命此變條沒結解問意建月公無系軍很情者最立代想已通並提直題黨程展果料象員革位常文總次品式活設及管特件長求老頭基資邊流路少圖山統接知將組見計別她手角期根論運農指區強放決西幹做戰先回則任據處隊南給色光門即保治北造百規熱領海口東導壓志世金增爭階思術極交受聯認共權收證改清己美再轉更單風節萬青";

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
				int index = random.nextInt(COMMON_CHINESE.length());
				randomChar = 	COMMON_CHINESE.charAt(index);
//				randomChar = (char) (random.nextInt( 0x9FFF - 0x4E00 + 1) + 0x4E00); // 中文字符的Unicode範圍
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
