package legion.geneticAlgorithm.staffABTest;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;

import legion.geneticAlgorithm.Gene;
import legion.geneticAlgorithm.GeneticAlgorithm;
import legion.geneticAlgorithm.IterationAbs;
import legion.geneticAlgorithm.PerformanceAbs;

public class MainTest {

	private List<Staff> parseStaffList(File _file){
		assert _file.exists();
		List<Staff> list = new ArrayList<>();
		// 工作表
		try {
			Workbook workbook = WorkbookFactory.create(_file);
			Sheet sheet = workbook.getSheetAt(0);
			
			int rowCnt = sheet.getPhysicalNumberOfRows();
			System.out.println("rowCnt:\t"+rowCnt);
			for (int i = 1; i < rowCnt; i++) { // XXX
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				int staffId = (int) row.getCell(0).getNumericCellValue();
				String name = row.getCell(1).getStringCellValue();
				String sectionCode = row.getCell(2).getStringCellValue();
				String officeNo = row.getCell(3).getStringCellValue();
				int agentStaffId = (int) row.getCell(4).getNumericCellValue();
				Cell cell = row.getCell(5);
				String shuffleBusNo = cell == null ? "" : cell.getStringCellValue();
				
				
				Staff staff = new Staff(staffId, name, sectionCode, officeNo, agentStaffId, shuffleBusNo);
				list.add(staff);
			}
			
			return list;
		} catch (EncryptedDocumentException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<List<Integer>> parseFamilyStaffIdList(File _file) {
		assert _file.exists();
		// 工作表
		try {
			Workbook workbook = WorkbookFactory.create(_file);
			Sheet sheet = workbook.getSheetAt(1);
			
			
			List<List<Integer>> familyStaffIdList = new ArrayList<>();
			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;
				List<Integer> subList = new ArrayList<>();
				int colIndex = 0;
				Cell cell = row.getCell(colIndex);
				while (cell != null) {
					int id = (int) cell.getNumericCellValue();
					subList.add(id);

					// 指向下一個cell
					cell = row.getCell(++colIndex);
				}

				if (subList.size() > 0)
					familyStaffIdList.add(subList);
			}
			return familyStaffIdList;
			
			
		} catch (EncryptedDocumentException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	// -------------------------------------------------------------------------------
	@Test
	public void main() {
		// 讀檔案
		File file = new File("D:\\staff_test_1.xlsx");
		
		// parse出員工列表
		List<Staff> staffList = parseStaffList(file);
		List<List<Integer>> familyStaffIdList = parseFamilyStaffIdList(file);
		
		assertNotNull(staffList);
		assertNotNull(familyStaffIdList);
		
		
		/* 整理資料  */
		Integer[] staffIds = staffList.stream().map(staff -> staff.getStaffId()).collect(Collectors.toList())
				.toArray(new Integer[0]);
		System.out.println("staffList.size(): "+staffList.size());
		int chromosomeIndex = 0;
		Map<Integer, Integer>   staffIdOrderMap = new HashMap<>();
		for (Staff staff : staffList) {
			System.out.println(staff.getStaffId() + "\t" + staff.getName() + "\t" + staff.getSectionCode() + "\t"
					+ staff.getOfficeNo() + "\t" + staff.getAgentStaffId());
			staffIdOrderMap.put(staff.getStaffId(), chromosomeIndex++);
		}
		//
		List<List<Staff>> familyList = new ArrayList<>();
		for (List<Integer> list : familyStaffIdList) {
			List<Staff> familyStaffList = list.stream().map(i -> staffList.get(staffIdOrderMap.get(i)))
					.collect(Collectors.toList());
			familyList.add(familyStaffList);
		}
		System.out.println("familyList.size(): " + familyList.size());
		for (List<Staff> l : familyList) {
			StringBuilder sb = new StringBuilder();
			for (Staff s : l)
				sb.append(s.getStaffId() + "\t");
			System.out.println(sb.toString());
		}
		
		
		// parameters
		int popSize = 200; // population
		int chromosomeLength = staffList.size(); // 員工數
		int iterationLimit = 15;

		Function<Gene, PerformanceAbs> fnGetPerformanceAbs = g -> {
			Map<Integer, String> staffIdShiftMap = decoding(staffIds, g);

			double w1 = 1;
			double w2 = 1;
			double w3 = 3;
			double w4 = 25;
			double w5 = 50;
			
			List<String> commentList = new ArrayList<>();
			
			// 1:單位人數差
			int x1 = computeX1(staffList, staffIdShiftMap, commentList);
			
			// 2:辦公室人數差
			int x2 = computeX2(staffList, staffIdShiftMap, commentList);
			
			// 3:交通車成員
			int x3 = computeX3(staffList, staffIdShiftMap, commentList);
			
			// 4:代理人不能同班別
			int x4 = computeX4(staffList, staffIdShiftMap, commentList);
			
			// 5:家人關係, w5 = 50
			int x5 = computeX5(familyList, staffIdShiftMap, commentList);
			
			// 6:組長和副組長不能同班別，w6 = 50 
//			double x6 = computeX6(staffList);

			PerformanceAbs perfAbs = new PerformanceAbs();
			perfAbs.setObjectiveValue(w1*x1 + w2*x2 + w3*x3 + w4*x4 + w5*x5);
			perfAbs.setCommentList(commentList);
			return perfAbs;
		};
//		
		GeneticAlgorithm ga = new GeneticAlgorithm(popSize, chromosomeLength, fnGetPerformanceAbs);
		
		IterationAbs[] iteAbss = ga.runAlgorithm(iterationLimit);
		System.out.println("iteAbss.length: " + iteAbss.length);
		for (int i = 0; i < iteAbss.length; i++) {
			IterationAbs a = iteAbss[i];
//			System.out.println(i + "\t" + a.getAvgObjValue() + "\t" + a.getGeneBest().getObjectiveValueCache());
			System.out.println("=============Iteration["+i+"]=============");
			System.out.println( "Iteration avg: \t" + a.getAvgObjValue() + "\tIteration best: " + a.getGeneBest().getObjectiveValue());
//			for(int j=0;j<a.getGeneBest().getLength();j++)
//				System.out.print(a.getGeneBest().getChromosome()[j] +"\t");
//			System.out.println();
			
//			System.out.println("------------Comment------------");
//			for (String comment : a.getGeneBest().getPerfAbs().getCommentList())
//				System.out.println(comment);
		}
		
		//
		Gene geneBest = iteAbss[iteAbss.length - 1].getGeneBest();
		System.out.println("------------Comment------------");
		for (String comment : geneBest.getPerfAbs().getCommentList())
			System.out.println(comment);
		
		Map<Integer, String> staffIdShiftMap = decoding(staffIds, geneBest);
		for (Staff staff : staffList) {
			System.out.println(staff.getStaffId() + "\t" + staff.getName() + "\t" + staff.getSectionCode() + "\t"
					+ staff.getOfficeNo() + "\t" + staff.getAgentStaffId() + "\t"
					+ staffIdShiftMap.get(staff.getStaffId()));
			staffIdOrderMap.put(staff.getStaffId(), chromosomeIndex++);
		}
		
	}
	
	private Map<Integer, String> decoding(Integer[] _staffIds, Gene _gene) {
		Map<Integer, String> staffIdShiftMap = new HashMap<>();
		for (int i = 0; i < _gene.getLength(); i++) {
			staffIdShiftMap.put(_staffIds[i], _gene.getChromosome()[i] == 0 ? "A" : "B");
		}
		return staffIdShiftMap;
	}
	
	private String getShift(Staff _staff, Map<Integer, String> _staffIdShiftMap) {
		return _staffIdShiftMap.get(_staff.getStaffId());
	}
	
	/**
	 * X1:單位人數差
	 * @param _staffList
	 * @return
	 */
	private int computeX1(List<Staff> _staffList,Map<Integer, String> _staffIdShiftMap, List<String> _commentList) {
	
		Map<String, List<Staff>> sectionMap = _staffList.stream().collect(Collectors.groupingBy(Staff::getSectionCode));
		int x = 0;
		for (String sectionCode : sectionMap.keySet()) {
			List<Staff> sectionStaffList = sectionMap.get(sectionCode);
			long shift1 = sectionStaffList.stream().filter(staff -> getShift(staff, _staffIdShiftMap) == "A").count();
			long shift2 = sectionStaffList.stream().filter(staff -> getShift(staff, _staffIdShiftMap) == "B").count();
			int diff =(int) Math.abs(shift1 - shift2);
			if (diff > 0) {
				x += diff;
				//
				if (_commentList != null)
					_commentList.add("[INFO]單位[" + sectionCode + "]的AB班人數差距為[" + diff + "]。");
			}
		}
		
		return x;
	}
	
	/**
	 * X2:辦公室人數差
	 * @param _staffList
	 * @return
	 */
	private int computeX2(List<Staff> _staffList,Map<Integer, String> _staffIdShiftMap, List<String> _commentList) {
		Map<String, List<Staff>> officeMap = _staffList.stream().collect(Collectors.groupingBy(Staff::getOfficeNo));
		int x = 0;
		for (String officeNo : officeMap.keySet()) {
			List<Staff> officeStaffList = officeMap.get(officeNo);
			long shift1 = officeStaffList.stream().filter(staff -> getShift(staff, _staffIdShiftMap) == "A").count();
			long shift2 = officeStaffList.stream().filter(staff -> getShift(staff, _staffIdShiftMap) == "B").count();
			int diff =(int) Math.abs(shift1 - shift2);
			if (diff > 0) {
				x += diff;
				//
				if (_commentList != null)
					_commentList.add("[INFO]辦公室[" + officeNo + "]的AB班人數差距為[" + diff + "]。");
			}
			
		}
		return x;
	}
	
	/**
	 * X3:交通車成員必須同班別
	 * @param _staffList
	 * @return
	 */
	private int computeX3(List<Staff> _staffList,Map<Integer, String> _staffIdShiftMap, List<String> _commentList) {
		Map<String, List<Staff>> busMap = _staffList.stream()
				.filter(staff -> staff.getShuffleBusNo() != null && !staff.getShuffleBusNo().isEmpty())
				.collect(Collectors.groupingBy(Staff::getShuffleBusNo));
		int x = 0;
		for (String busNo : busMap.keySet()) {
			List<Staff> busStaffList = busMap.get(busNo);
			long shift1 = busStaffList.stream().filter(staff -> getShift(staff, _staffIdShiftMap) == "A").count();
			long shift2 = busStaffList.stream().filter(staff -> getShift(staff, _staffIdShiftMap) == "B").count();
			int min = (int) Math.min(shift1, shift2);
			if(min>0) {
				x += min;
				//
				if (_commentList != null)
					_commentList.add("[WARN]交通車[" + busNo + "]的A班人數為[" + shift1 + "]，B班人數為["+shift2+"]，有["+min+"]人和其他人未在同一班別。");
			}
			
		}
		return x;
	}
	
	/**
	 * X4:代理人不能同班別
	 * @param _staffList
	 * @return
	 */
	private int computeX4(List<Staff> _staffList, Map<Integer, String> _staffIdShiftMap, List<String> _commentList) {
		Map<Integer, Staff> staffMap = _staffList.stream().collect(Collectors.toMap(s -> s.getStaffId(), s -> s));
		int x = 0;
		for (Staff s : _staffList) {
			Staff agent = staffMap.get(s.getAgentStaffId());
			if (getShift(s, _staffIdShiftMap).equals(getShift(agent, _staffIdShiftMap))) {
				x++;
				//
				if (_commentList != null)
					_commentList.add("[WARN]員工["+s.getStaffId()+"]["+s.getName()+"]和其代理人["+agent.getStaffId()+"]["+agent.getName()+"]班別相同。");
			}
		}
		return x;
	}
	
	/**
	 * X5:家人關係
	 * @param _staffList
	 * @return
	 */
	private int computeX5(List<List<Staff>> _familyList, Map<Integer, String> _staffIdShiftMap, List<String> _commentList) {
		int x = 0;
		for (List<Staff> sList : _familyList) {
			long shift1 = sList.stream().filter(staff -> getShift(staff, _staffIdShiftMap) == "A").count();
			long shift2 = sList.stream().filter(staff -> getShift(staff, _staffIdShiftMap) == "B").count();
			int min = (int) Math.min(shift1, shift2);
			if (min > 0) {
				x += min;
				if (_commentList != null) {
					StringBuilder sb = new StringBuilder();
					sb.append("[ERROR]家族成員");
					for (Staff s : sList) {
						sb.append("[").append(s.getStaffId()).append("][").append(s.getName()).append("]");
					}
					sb.append("未在同一班別。");
				}
			}
		}
		return x;
	}
	
	

	
	// -------------------------------------------------------------------------------
//	private List<Staff> genMockStaffList(int _staffSize) {
//		String[] sectionCodes = new String[] { "5C01", "5C02", "5C03" };
//		String[] officeNos = new String[] {"辦公室1", "辦公室2","辦公室3","辦公室4","辦公室5", "辦公室6", "辦公室7"};
//		String[] shuffleBusNos = new String[] { "交通車1", "交通車2", "交通車3", "交通車4", "交通車5" };
//		Random rand = new Random(System.currentTimeMillis());
//
//		for (int i = 0; i < _staffSize; i++) {
//			int staffId = i + 1;
//			String name = "員工" + (staffId);
//			String sectionCode = sectionCodes[rand.nextInt(3)];
//			int agentStaffId = 0;
//			do {
//				agentStaffId = rand.nextInt(_staffSize) + 1;
//			} while (agentStaffId > 0 && agentStaffId != staffId);
//
//			
//		}
//		
//		
//
//	}
//	
}
