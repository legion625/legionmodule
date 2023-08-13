package legionLab.web.control.zk.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FilterDemoData {
	private String aaa;
	private String bbb;
	private String ccc;
	private String ddd;
	public FilterDemoData(String aaa, String bbb, String ccc, String ddd) {
		this.aaa = aaa;
		this.bbb = bbb;
		this.ccc = ccc;
		this.ddd = ddd;
	}
	public String getAaa() {
		return aaa;
	}
	public String getBbb() {
		return bbb;
	}
	public String getCcc() {
		return ccc;
	}
	public String getDdd() {
		return ddd;
	}
	
	// -------------------------------------------------------------------------------
	public static List<FilterDemoData> getMockData(){
		List<FilterDemoData> list = new ArrayList<>();
		for(int i=0;i<100;i++)
			list.add(
					new FilterDemoData(
							"AAA" +generateRandomString(2),
							"BBB" +generateRandomString(3),
							"CCC" +generateRandomString(4),
							"DDD" +generateRandomString(5)
							
							)
					);
		return list ; 
	} 
	

//	public class RandomStringGenerator {
//	    public static void main(String[] args) {
//	        int length = 20; // 要生成的字串長度
//	        String randomString = generateRandomString(length);
//	        System.out.println("隨機字串: " + randomString);
//	    }

	    public static String generateRandomString(int length) {
//	        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789汉字漢字"; // 包含英文字母、數字和中文字符
//	        Random random = new Random();
//	        StringBuilder sb = new StringBuilder(length);
//
//	        for (int i = 0; i < length; i++) {
//	            int randomIndex = random.nextInt(characters.length());
//	            char randomChar = characters.charAt(randomIndex);
//	            sb.append(randomChar);
//	        }
//
//	        return sb.toString();
	    	
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
//	}

}
