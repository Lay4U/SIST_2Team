import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

public class Attendance {
	private User user;
	private final String DATA;
	private final String DATA2;

	private ArrayList<Calendar> chulgeun; // 출근 시간
	private ArrayList<Calendar> toegeun; // 퇴근 시간
	private Calendar overTime; // 연장 근무
	private String workTime;
	private String attendancePath;
	private String dayWorkingTimePath;
	private ArrayList<String> memberName;
	private int lastDay = 0;
	private int day_of_week = 0;
	private long weakWorkTime;
	private long positionWorkTime;
	private static Calendar c;
	private static Scanner scan;

	public Attendance(User user) {
		DATA = "data/Contact.txt"; // Window
		// DATA = "data/Contact.txt"; // Mac
		DATA2 = "data/attendance/working.txt";

		weakWorkTime = 0;
		positionWorkTime = 0;
		c = Calendar.getInstance();
		chulgeun = new ArrayList<Calendar>();
		toegeun = new ArrayList<Calendar>();
		overTime = Calendar.getInstance();
		this.user = user;
		attendancePath = "";
		scan = new Scanner(System.in);
		memberName = new ArrayList<String>();

	}

	private static String menu() {

		System.out.println("===================");
		System.out.println("1. 출근시간 입력");
		System.out.println("2. 퇴근시간 입력");
		System.out.println("3. 원하는 날짜의 근무시간 조회");
		System.out.println("4. 종료");
		System.out.println("===================");
		System.out.print("선택: ");

		String sel = scan.nextLine();

		return sel;

	}

	public void attendanceScreen() throws IOException {

		System.out.println("[근태관리]");

		boolean loop = true;

		String sel = menu();

		if (sel.equals("1")) {
		} else if (sel.equals("2")) {

		} else if (sel.equals("3")) {
			searchWorkingTime();
		} else if (sel.equals("4")) {

		} else {
			loop = true;
		}

	}



	/**
	 * 5월 출퇴근 시간이 나와있는 더미 데이터에서 필요한 값을 ArrayList 에 저장하는 메소드
	 * 로그인한 본인의 근무시간 확인 
	 * @author 2조
	 * @throws IOException
	 */
	public void readWorkingTime() throws IOException {

		BufferedReader read = new BufferedReader(new FileReader(DATA2));
		String line = "";

		while ((line = read.readLine()) != null) {
			String[] temp = line.split(",");
			if (temp[0].equals(this.user.getName())) {
				String in = temp[1];
				String out = temp[2];
				String[] inDate = in.split(" ");
				String[] inDate2 = inDate[0].split("-"); // 년월일
				String[] inDate3 = inDate[1].split(":");// 시분초
				int inYear = Integer.parseInt(inDate2[0]);
				int inMonth = Integer.parseInt(inDate2[1]);
				inMonth--;
				int inDay = Integer.parseInt(inDate2[2]);

				int inHour = Integer.parseInt(inDate3[0]);
				int inMin = Integer.parseInt(inDate3[1]);

				String[] outDate = out.split(" ");
				String[] outDate2 = outDate[0].split("-"); // 년월일
				String[] outDate3 = outDate[1].split(":");// 시분초
				int outYear = Integer.parseInt(outDate2[0]);
				int outMonth = Integer.parseInt(outDate2[1]);
				outMonth--;
				int outDay = Integer.parseInt(outDate2[2]);

				int outHour = Integer.parseInt(outDate3[0]);
				int outMin = Integer.parseInt(outDate3[1]);

				Calendar c1 = Calendar.getInstance();
				c1.set(inYear, inMonth, inDay, inHour, inMin, 00);
				Calendar c2 = Calendar.getInstance();
				c2.set(outYear, outMonth, outDay, outHour, outMin, 00);

				this.chulgeun.add(c1);
				this.toegeun.add(c2);
			}
		} // while

	}// readWorkingTime

	/**
	 * ArrayList에 저장된 Calendar 값을 이용해 일별 근무시간 구하는 메소드
	 * 토, 일요일을 제외한 평일만 나타내주기 
	 * @throws IOException
	 * 
	 */
	public void dayWorkingTime() throws IOException {

		lastDay = getLastDay(this.chulgeun.get(this.chulgeun.size() - 1).get(Calendar.YEAR),
				this.chulgeun.get(this.chulgeun.size() - 1).get(Calendar.MONTH));
		int sum = 0;
		int i = 0;
		int cnt = 1;
		dayWorkingTimePath = "data/attendance/dayWorkingTimePath.txt";
		FileWriter fw = new FileWriter(dayWorkingTimePath);
		
		for (i = 0; i < this.chulgeun.size(); i++) {
			if ((this.chulgeun.get(i).get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
					&& (chulgeun.get(i).get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)) {
				sum = (int) ((this.toegeun.get(i).getTimeInMillis() - this.chulgeun.get(i).getTimeInMillis()) / 1000
						/ 60);
				cnt++;

				String dayWorkingTime = String.format("%d", sum - 60);
				fw.write(this.user.getName());
				fw.write(",");
				fw.write(String.format("%s-%s-%s", this.chulgeun.get(i).get(Calendar.YEAR),
						this.chulgeun.get(i).get(Calendar.MONTH) + 1, this.chulgeun.get(i).get(Calendar.DATE)));
				fw.write(",");
				fw.write(dayWorkingTime);
				fw.write("\r\n");
			}
		} // i for
		fw.close();

	}// DayWorkingTime

	/**
	 * 원하는 년,월을 입력하여 해당 근무 시간을 조회하는 메소드 
	 */
	public void searchWorkingTime() {

		try {
			BufferedReader read = new BufferedReader(new FileReader(dayWorkingTimePath));
			System.out.print("근무 시간을 확인하고 싶은 년도와 월을 입력해주세요.(YYYY-M): ");
			String yearMonth = scan.nextLine();

			String[] temp1 = yearMonth.split("-"); // 입력받은 2021-5 쪼개기

//			ArrayList<Integer> list = new ArrayList<Integer>();

			String line = "";
			// String date = "2021-05-02";
			String str = "";
			int sum = 0;
			int workingMin = 0;

			while ((line = read.readLine()) != null) {
				//System.out.println(line);
				String temp[] = line.split(",");
				workingMin = Integer.parseInt(temp[2]);
//				System.out.println(workingMin);
				String check = temp[0];
				String check2 = this.user.getName();
				if (temp[0].equals(this.user.getName())) {
					String workingDay = temp[1];
					String[] yearMonthDay = temp[1].split("-"); // 2021-5-3 쪼개기
					if (yearMonthDay[0].equals(temp1[0]) && yearMonthDay[1].equals(temp1[1])) {
						
						sum += workingMin;
					}
				}
			} // while
			System.out.printf("%s님의 %s년 %s월 근무시간은 %d입니다.\n ", this.user.getName(), temp1[0], temp1[1],sum);
		} catch (Exception e) {
			System.out.println(e);
		}
	}// searchWorkingTime

	public static int getDayOfWeek(int year, int month) {

		int totalDays = 0;

		for (int i = 1; i < year; i++) { // 전년도까지

			if (isLeafYear(i)) {
				totalDays += 366;
			} else {
				totalDays += 365;
			}

		}

		// 2021.1.1 ~ 2021.3.31
		for (int i = 1; i < month; i++) { // 전월까지
			totalDays += getLastDay(year, i);
		}
		// 2021.4.1 ~ 2021.4.1
		totalDays++;
		return totalDays % 7;
	}

	public static int getLastDay(int year, int month) {

		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			return 31; // return문: 메소드를 종료하는 역할(break 유사) + 값 반환
		case 4:
		case 6:
		case 9:
		case 11:
			return 30;
		case 2:
			return isLeafYear(year) ? 29 : 28;
		}
		return 0;
	}// getLastDay

	public static boolean isLeafYear(int year) {

		if (year % 4 == 0) {
			if (year % 100 == 0) {
				if (year % 400 == 0) {
					return true; // 윤년
				} else {
					return false; // 평년
				}
			} else {
				return true; // 윤년
			}
		} else {
			return false; // 평년
		}

	}
	
	/**
	 * contact.txt 명단에 있는 인원들마다 각각 근무시간을 생성하기 위한 더미 메소드
	 */
	public void dummy() {
		//ialbutt0,qdf5bG,주반택,nbresland0@sist2.co.kr,010-9965-6848,과장,인사
		String path = "data/contact.txt";
		String name = "";
		try {
			BufferedReader read = new BufferedReader(new FileReader(path));
			
			String line = "";
			ArrayList<String> list = new ArrayList<String>();
			Random r = new Random();
			attendancePath = "data/attendance/memberWorkingTimeDummy.txt";
			FileWriter fw = new FileWriter(attendancePath,true);
			String dummy = "";

			while ((line = read.readLine()) != null) {
				String[] temp = line.split(",");

				list.add(temp[2]);
			} // while
			
			for (int i = 0; i < list.size(); i++) {
				name = list.get(i);
				for (int j = 1; j <= 31; j++) {

//					System.out.printf("%s,2021-05-%02d 08:%02d:00,2021-05-%02d 18:%2d:00\n", name, j,
//							r.nextInt(40) + 20, j, r.nextInt(15) + 15);
					dummy = String.format("%s,2021-05-%02d 08:%02d:00,2021-05-%02d 18:%2d:00\n", name, j,
							r.nextInt(40) + 20, j, r.nextInt(15) + 15);

					fw.write(dummy);
				}
			} // for i

			fw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}//dummy
	
	
	/**
	 * 5월 출퇴근 시간이 나와있는 더미 데이터에서 필요한 값을 ArrayList 에 저장하는 메소드
	 * 관리자의 입장에서 모든 직원의 근무시간 확인 
	 * @author 2조
	 * @throws IOException
	 */
	public void readWorkingTime2() throws IOException {
		attendancePath = "data/attendance/memberWorkingTimeDummy.txt";
		BufferedReader read = new BufferedReader(new FileReader(attendancePath));
		String line = "";

		while ((line = read.readLine()) != null) {
			String[] temp = line.split(",");
				this.memberName.add(temp[0]);
				String in = temp[1];
				String out = temp[2];
				String[] inDate = in.split(" ");
				String[] inDate2 = inDate[0].split("-"); // 년월일
				String[] inDate3 = inDate[1].split(":");// 시분초
				int inYear = Integer.parseInt(inDate2[0]);
				int inMonth = Integer.parseInt(inDate2[1]);
				inMonth--;
				int inDay = Integer.parseInt(inDate2[2]);

				int inHour = Integer.parseInt(inDate3[0]);
				int inMin = Integer.parseInt(inDate3[1]);

				String[] outDate = out.split(" ");
				String[] outDate2 = outDate[0].split("-"); // 년월일
				String[] outDate3 = outDate[1].split(":");// 시분초
				int outYear = Integer.parseInt(outDate2[0]);
				int outMonth = Integer.parseInt(outDate2[1]);
				outMonth--;
				int outDay = Integer.parseInt(outDate2[2]);

				int outHour = Integer.parseInt(outDate3[0]);
				int outMin = Integer.parseInt(outDate3[1]);

				Calendar c1 = Calendar.getInstance();
				c1.set(inYear, inMonth, inDay, inHour, inMin, 00);
				Calendar c2 = Calendar.getInstance();
				c2.set(outYear, outMonth, outDay, outHour, outMin, 00);

				this.chulgeun.add(c1);
				this.toegeun.add(c2);
			
		} // while

	}// readWorkingTime
	/**
	 * 멤버별 근무시간 더미 데이터 이용해 월~금 근무 일자 추출 + 근무 시간을 새로운 더미로 만드는 메소드 
	 * ex) 홍길동,2021-5-3,521 
	 * @throws IOException
	 */
	public void dayWorkingTime2() throws IOException {

		lastDay = getLastDay(this.chulgeun.get(this.chulgeun.size() - 1).get(Calendar.YEAR),
				this.chulgeun.get(this.chulgeun.size() - 1).get(Calendar.MONTH));
		int sum = 0;
		int i = 0;
		int cnt = 1;
		//dayWorkingTimePath = "data/attendance/dayWorkingTimePath.txt";
		String testPath = "data/attendance/memberTime.txt";
		FileWriter fw = new FileWriter(testPath);
		
		for (i = 0; i < this.chulgeun.size(); i++) {
			if ((this.chulgeun.get(i).get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
					&& (chulgeun.get(i).get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)) {
				sum = (int) ((this.toegeun.get(i).getTimeInMillis() - this.chulgeun.get(i).getTimeInMillis()) / 1000
						/ 60);
				cnt++;

				String dayWorkingTime = String.format("%d", sum - 60);
				fw.write(this.memberName.get(i));
				fw.write(",");
				fw.write(String.format("%s-%s-%s", this.chulgeun.get(i).get(Calendar.YEAR),
						this.chulgeun.get(i).get(Calendar.MONTH) + 1, this.chulgeun.get(i).get(Calendar.DATE)));
				fw.write(",");
				fw.write(dayWorkingTime);
				fw.write("\r\n");
			}
		} // i for
		fw.close();

	}// DayWorkingTime
	
	
	/**
	 * 멤버 이름 , 년, 월 검색으로 해당 년,월의 근무시간 출력 
	 */
	
	public void adminSearchWorkingTime() {
		String testPath = "data/attendance/memberTime.txt";
		try {
			BufferedReader read = new BufferedReader(new FileReader(testPath));
			System.out.print("근무 시간을 확인하고 싶은 직원의 이름 입력: ");
			String memName = scan.nextLine();
			System.out.print("근무 시간을 확인하고 싶은 년도와 월을 입력해주세요.(YYYY-M): ");
			String yearMonth = scan.nextLine();

			String[] temp1 = yearMonth.split("-"); // 입력받은 2021-5 쪼개기
			
//			ArrayList<Integer> list = new ArrayList<Integer>();

			String line = "";
			// String date = "2021-05-02";
			String str = "";
			int sum = 0;
			int workingMin = 0;

			while ((line = read.readLine()) != null) {
				//System.out.println(line);
				String temp[] = line.split(",");
				workingMin = Integer.parseInt(temp[2]);
//				System.out.println(workingMin);
				
				if (temp[0].equals(memName)) {
					String workingDay = temp[1];
					String[] yearMonthDay = temp[1].split("-"); // 2021-5-3 쪼개기
					if (yearMonthDay[0].equals(temp1[0]) && yearMonthDay[1].equals(temp1[1])) {
						
						sum += workingMin;
					}
				} 
			} // while
			System.out.printf("%s님의 %s년 %s월 근무시간은 %d시간입니다.\n ", memName, temp1[0], temp1[1],sum/60);
			
			
			
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}// searchWorkingTime
	
	
		// public void Time() {
//		Random rnd = new Random();
//		int a = rnd.nextInt();
//
//		try {
//			BufferedReader read = new BufferedReader(new FileReader(DATA));
//			String line = "";
//
//			while ((line = read.readLine()) != null) {
//				String[] temp = line.split(",");
//				if (temp[2].equals(this.user.getName())) {
//					this.chulgeun.set(c.get(Calendar.YEAR), 3, 30, 9, 00, 00);
//					this.toegeun.set(c.get(Calendar.YEAR), 3, 30, rnd.nextInt(2) + 18, rnd.nextInt(10) + 10, 00);
//				}
//			} // while
//
//			/**
//			 * 이름,출근시간,퇴근시간 으로 해당 파일에 저장.
//			 */
//			attendancePath = "data/attendance/working.txt";
//			FileWriter fw = new FileWriter(attendancePath, true);
//			String chulgeun = String.format("%tF %tT", this.chulgeun, this.chulgeun);
//			String toegeun = String.format("%tF %tT", this.toegeun, this.toegeun);
//			fw.write(this.user.getName());
//			fw.write(",");
//			fw.write(chulgeun);
//			fw.write(",");
//			fw.write(toegeun);
//			fw.write("\r\n");
//			fw.close();
//
//			dayWorkingTime();
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//
//	}
	
	

}






