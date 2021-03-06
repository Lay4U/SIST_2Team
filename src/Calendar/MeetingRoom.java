package Calendar;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import HSJ.*;
/**
 * @author 방수빈
 * 
 *         회의실 예약 구현 이용자는 회의실 예약, 예약 취소, 예약 현황 확인을 할 수 있다.
 * @param roomNumber 회의실 방 번호
 * @param user       현재 접속 중인 유저의 정보
 * @param mc         MyCalendar 클래스
 * @param DATA4      회의실 예약 정보가 저장된 경로
 * @param year       회의실 예약 년도
 * @param month      회의실 예약 달
 * @param day        회의실 예약 일
 * @param lastDay    달 일수
 * @param day        of week 해당 월의 1일의 요일
 * 
 *
 */
public class MeetingRoom {
	private final String DATA4;
	private Calendar date;
	private int year;
	private int month;
	private int day;
	private int lastDay = 0;
	private int day_of_week = 0;
	private User user;
	private String[] roomNumber = { "501", "502", "503", "504", "505", "506", "601", "602", "603", "701", "702", "703",
			"704", "815", "816", "817", "819", "820", "821" };
	LinkedList<String[]> listRoom = new LinkedList<>();

	public MeetingRoom(User user) {
		this.user = user;
		DATA4 = "data\\schedule\\meetingRoom.txt";
		date = Calendar.getInstance();
		this.year = date.get(Calendar.YEAR);
		this.month = date.get(Calendar.MONTH) + 1;
		this.day = date.get(Calendar.DATE);
		load();
	}
	/**
	 * 회의실 예약 관리 각 항목으로 넘어가도록 int값을 입력받아 각 메소드로 이동한다.
	 */
	public void MeetingRoomScreen() {
		cls();

		System.out.println("                ▣ 회의실 예약 관리 목록 ▣");
		System.out.println("====================================================");
		System.out.println("|| 1. 회의실  || 2. 회의실 예약  || 3. 회의실 예약 ||");
		System.out.println("||      예약  ||           취소  ||      현황 확인 ||");
		System.out.println("====================================================");
		System.out.println("목차로 돌아가려면 0번을 누르세요.\n");
		int num = Integer.parseInt(Util.get("카테고리(번호)를 선택하세요"));

		cls();

		if (num == 0) {
			return;
		} else if (num == 1) {
			createRoomReservation(roomNumber);
		} else if (num == 2) {
			deleteRoom();
		} else if (num == 3) {
			readRoom();
		} else {
			System.out.println("잘못된 번호를 입력하셨습니다.");
		}

	}// MeetingRoomScreen()

	/**
	 * 출력화면에서 화면이 넘어간 것처럼 표현하기 위해 빈줄 100줄 출력
	 */
	private void cls() {
		for (int i = 0; i < 100; i++) {
			System.out.println();
		}
	}// cls()

	private void load() {
		try {
			String line = "";
			BufferedReader readMeetingRoom = new BufferedReader(new FileReader(DATA4));
			while ((line = readMeetingRoom.readLine()) != null) {
				String[] temp = line.split(",");
				listRoom.add(temp);
			}
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	public int[] showCanlendar(ArrayList<int[]> t) {
//	      System.out.println(Arrays.toString(t.get(0)));
//	      System.out.println(this.year);
//	      System.out.println(this.month);
//	      System.out.println(this.day);
		// 마지막일?
		while (true) {
			lastDay = getLastDay(year, month);
			// 해당 월의 1일의 요일?
			day_of_week = getDayOfWeek(year, month); // 4
			// 달력 출력하기
			System.out.println();
			System.out.println("===================================================");
			System.out.printf("                     %d년 %d월\n", year, month);
			System.out.println("===================================================");
			System.out.println("[일]\t[월]\t[화]\t[수]\t[목]\t[금]\t[토]");
			// 1일의 요일을 맞추기 위해서..
			for (int i = 0; i < day_of_week; i++) {
				System.out.print("\t");
			}
//	                     System.out.println(ANSI_RED + "This text is red!" + ANSI_RESET);
			// 날짜 출력
			boolean isStar = false;
			for (int i = 1; i <= lastDay; i++) {
				for (int j = 0; j < t.size(); j++) {
					if (t.get(j)[0] == this.year && t.get(j)[1] == this.month && t.get(j)[2] == i) {
						System.out.printf("%3d*\t", i);
						isStar = true;
						break;
					} else {
						isStar = false;
					}
				}
				if (!isStar) {
					System.out.printf("%3d\t", i);
//	               System.out.println(t.get(0));
				}
//	               if ((day_of_week + i - 1) % 7 == 0) {
				int a = i % 7;
				int b = 7 - day_of_week;
				b = 7 - day_of_week == 7 ? 0 : b;
				if (i % 7 == b) {
					System.out.println();
				}
			}
			System.out.println();
			System.out.println();
			String s = Util.get("월 이동(a or d) 끝내기(q) 날짜선택(요일입력)");
			if (s.equals("q")) {
				int[] t2 = { year, month, day };
				return t2;
			} else if (s.equals("a")) {
				if (month - 1 != 0)
					month--;
				else {
					month = 12;
					year--;
				}
			} else if (s.equals("d")) {
				if (month + 1 != 12)
					month++;
				else {
					month = 1;
					year++;
				}
			} else {
				int[] t2 = { year, month, Util.toInt(s) };
				return t2;
			}
		}
	}

//	public void output() {
//
//		this.date = Calendar.getInstance();
//		year = date.get(Calendar.YEAR);
//		month = date.get(Calendar.MONTH);
//		month++;
//
//		// System.out.println(day_of_week);
//
//		showCanlendar(year, month);
//
//	}// output
	private void showCanlendar(int year, int month) {
		// 마지막일?
		while (true) {
			lastDay = getLastDay(year, month);

			// 해당 월의 1일의 요일?
			day_of_week = getDayOfWeek(year, month); // 4
			// 달력 출력하기
			System.out.println();
			System.out.println("===================================================");
			System.out.printf("                     %d년 %d월\n", year, month);
			System.out.println("===================================================");
			System.out.println("[일]\t[월]\t[화]\t[수]\t[목]\t[금]\t[토]");

			// 1일의 요일을 맞추기 위해서..
			for (int i = 0; i < day_of_week; i++) {
				System.out.print("\t");
			}

			// 날짜 출력
			for (int i = 1; i <= lastDay; i++) {
				if (i == 9999)
//	               System.out.println(ANSI_RED + "This text is red!" + ANSI_RESET);
					System.out.printf("%3d*\t", i);
				else
					System.out.printf("%3d\t", i);

				int a = i % 7;
				int b = 7 - day_of_week;

				b = 7 - day_of_week == 7 ? 0 : b;
				if (i % 7 == b) {
//	         if ((day_of_week + i - 1) % 7 == 0) {
					System.out.println();
				}
			}

			System.out.println();
			System.out.println();
			String s = Util.get("월 이동(a or d) 일정/예약 날짜 입력(q)");
			if (s.equals("q"))
				break;
//	         KeyEvent event = new KeyEvent();
//	         if(Event.getKeycode() == KeyEvent.VK_LEFT)
			if (s.equals("a")) {
				if (month - 1 != 0)
					month--;
				else {
					month = 12;
					year--;
				}
			}
			if (s.equals("d")) {
				if (month + 1 != 12)
					month++;
				else {
					month = 1;
					year++;
				}
			}

		}
	}

	public int[] showCanlendar(int year, int month, int day) {
		// 마지막일?
		while (true) {
			lastDay = getLastDay(year, month);

			// 해당 월의 1일의 요일?
			day_of_week = getDayOfWeek(year, month); // 4
			// 달력 출력하기
			System.out.println();
			System.out.println("===================================================");
			System.out.printf("                     %d년 %d월\n", year, month);
			System.out.println("===================================================");
			System.out.println("[일]\t[월]\t[화]\t[수]\t[목]\t[금]\t[토]");

			// 1일의 요일을 맞추기 위해서..
			for (int i = 0; i < day_of_week; i++) {
				System.out.print("\t");
			}

			// 날짜 출력
			for (int i = 1; i <= lastDay; i++) {
				if (i == day)
//	                     System.out.println(ANSI_RED + "This text is red!" + ANSI_RESET);
					System.out.printf("%3d*\t", i);
				else
					System.out.printf("%3d\t", i);

				int a = i % 7;
				int b = 7 - day_of_week;

				b = 7 - day_of_week == 7 ? 0 : b;
				if (i % 7 == b) {
//	               if ((day_of_week + i - 1) % 7 == 0) {
					System.out.println();
				}
			}

			System.out.println();
			System.out.println();
			String s = Util.get("월 이동(a or d) 끝내기(q) 날짜선택(요일입력)");
			if (s.equals("q"))
				break;
//	               KeyEvent event = new KeyEvent();
//	               if(Event.getKeycode() == KeyEvent.VK_LEFT)
			else if (s.equals("a")) {
				if (month - 1 != 0)
					month--;
				else {
					month = 12;
					year--;
				}
			} else if (s.equals("d")) {
				if (month + 1 != 12)
					month++;
				else {
					month = 1;
					year++;
				}
			} else {
				int[] t = { year, month, Util.toInt(s) };
				return t;
			}

		}
		return null;

	}

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
 * meetingRoom.txt을 읽어온 후 예약 내용을 저장한다.
 * @param roomNumber 방 번호
 */
	public void createRoomReservation(String[] roomNumber) {
		System.out.println("회의실 예약을 등록합니다. 달력을 확인해주세요");
		showCanlendar(this.year, this.month);
		String s = Util.get("날짜를 입력해주세요(yyyy-mm-dd): ");
		String[] temp = s.split("-");
		Calendar newTask = Calendar.getInstance();
		newTask.set(Util.toInt(temp[0]), Util.toInt(temp[1]), Util.toInt(temp[2]));
		String content = Util.get("예약 목적을 입력해주세요");
		// temp0 -> 2021 temp1 ->05 temp2 ->31
		String ymd = newTask.get(Calendar.YEAR) + "-" + newTask.get(Calendar.MONTH) + "-"
				+ newTask.get(Calendar.DAY_OF_MONTH);
		// 홍길동,2021-5-4,t,testaaa
		for (int i = 0; i < roomNumber.length; i++) {
			System.out.printf("[%d] %s\n", i + 1, roomNumber[i]);
		}
		int choiceRoom = Util.toInt(Util.get("방 번호를 선택해주세요"));
		String selectedRoom = roomNumber[choiceRoom - 1];
		String position = null;
		String depart = null;

		try {
			BufferedReader readRoom = new BufferedReader(new FileReader(DATA4));
			String line = "";
			while ((line = readRoom.readLine()) != null) {
				String[] t = line.split(",");
				if (selectedRoom == t[3] && ymd == t[4]) {
					Util.puase("이미 예약된 방과 같은 날짜 입니다. 다시 선택해주세요");
					return;
				}
			}
			BufferedReader read = new BufferedReader(new FileReader("data\\HR.txt"));
			long seed = System.currentTimeMillis();
			Random rand = new Random(seed);
			rand.setSeed(seed);

			while ((line = read.readLine()) != null) {
				String[] t = line.split(",");
				if (t[0].equals(this.user.getName())) {
					position = t[1];
					depart = t[2];
					String[] sl = { this.user.getName(), position, depart, selectedRoom, ymd, content };
					listRoom.add(sl);
					break;
				}
			}
			FileWriter fw = new FileWriter(DATA4, true);
			fw.write(this.user.getName() + ",");
			fw.write(position + ",");
			fw.write(depart + ",");
			fw.write(selectedRoom + ",");
			fw.write(ymd + ",");
			fw.write(content + "\n");
			fw.close();

		} catch (IOException e) {
			System.out.println(e);
		}
		System.out.println("회의실 예약이 완료됐습니다.");
		System.out.println();
		int num2 = Integer.parseInt(Util.get("▶ 목차로 돌아가려면 0번을 누르세요."));
		if(num2 == 0) {
			cls();
			MeetingRoomScreen();
		}

	}
/**
 * listRoom의 index를 찾아서 그 index의 내용을 삭제한다.
 * meetingRoom.txt를 다시 저장한다.
 */
	public void deleteRoom() {
		// 홍길동,차장,인사,505,2021-5-1,sdf
		ArrayList<int[]> t = new ArrayList<>();
		for (int i = 0; i < listRoom.size(); i++) {
			if (listRoom.get(i)[0].equals(this.user.getName())) {
				String[] temp = listRoom.get(i)[4].split("-");
				int year = Util.toInt(temp[0]);
				int month = Util.toInt(temp[1]);
				int day = Util.toInt(temp[2]);
				int[] g = { year, month, day };
				t.add(g);

			}
		} // exit for
		System.out.println("삭제할 일정을 선택해주세요");
		int[] c = showCanlendar(t);
		for (int i = 0; i < listRoom.size(); i++) {
			if (listRoom.get(i)[0].equals(this.user.getName())) {
				String[] temp = listRoom.get(i)[4].split("-");
				int year = Util.toInt(temp[0]);
				int month = Util.toInt(temp[1]);
				int day = Util.toInt(temp[2]);
				if (c[0] == year && c[1] == month && c[2] == day) {
					System.out.println();
					System.out.println(listRoom.get(i)[3] + " 회의실 예약을 취소했습니다.");
					listRoom.remove(i);
				}
			}

		}

		// Filewrite로 list 모두 쓰기
		FileWriter fw = null;
		try {
			fw = new FileWriter(DATA4);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (int i = 0; i < listRoom.size(); i++) {
			try {
				fw.write(String.format("%s,%s,%s,%s,%s,%s\n", listRoom.get(i)[0], listRoom.get(i)[1],
						listRoom.get(i)[2], listRoom.get(i)[3], listRoom.get(i)[4], listRoom.get(i)[5]));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
		int num2 = Integer.parseInt(Util.get("▶ 목차로 돌아가려면 0번을 누르세요."));
		if(num2 == 0) {
			cls();
			MeetingRoomScreen();
		}

	}// deleteVacation()
/**
 * listRoom의 index를 찾은 후 그 index의 내용을 읽어온다. 
 */
	public void readRoom() {
//홍길동,차장,인사,821,2021-5-18,회의
		ArrayList<int[]> t = new ArrayList<>();
		for (int i = 0; i < listRoom.size(); i++) {
			if (listRoom.get(i)[0].equals(this.user.getName())) {
				String[] temp = listRoom.get(i)[4].split("-");
				int year = Util.toInt(temp[0]);
				int month = Util.toInt(temp[1]);
				int day = Util.toInt(temp[2]);
				int[] g = { year, month, day };
				t.add(g);
			}
		}
		System.out.println("일정을 선택해주세요");
		int[] c = showCanlendar(t);
		for (int i = 0; i < listRoom.size(); i++) {
			if (listRoom.get(i)[0].equals(this.user.getName())) {
				String[] temp = listRoom.get(i)[4].split("-");
				int year = Util.toInt(temp[0]);
				int month = Util.toInt(temp[1]); 
				int day = Util.toInt(temp[2]);
				if (c[0] == year && c[1] == month && c[2] == day) {
					System.out.println();
					System.out.println("예약 일정을 출력합니다");
					System.out.println();
					System.out.println("이름 : " + listRoom.get(i)[0]);
					System.out.println("직급 : " + listRoom.get(i)[1]);
					System.out.println("부서 : " + listRoom.get(i)[2]);
					System.out.println("회의실 번호 : " + listRoom.get(i)[3]);
					System.out.println("예약 날짜 : " + listRoom.get(i)[4]);
					System.out.println("내용 : " + listRoom.get(i)[5]);
					System.out.println();
				}
			}
		}
		System.out.println("남은 일정이 없습니다.");
		System.out.println();
		int num2 = Integer.parseInt(Util.get("▶ 목차로 돌아가려면 0번을 누르세요."));
		if(num2 == 0) {
			cls();
			MeetingRoomScreen();
		}

	}
}
