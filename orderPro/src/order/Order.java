package order;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.table.*; // DefaultTableModel

// 주문 페이지
public class Order extends JFrame implements ActionListener {
	// 전역 변수
	JLabel lbTitle;
	JButton btMp, btBuy, btAdd, btCart, btExit;
	
	Object data[][] = new Object[0][2]; // 0행 2열
	String cols[] = { "메뉴 이름", "가격" };
	DefaultTableModel model = new DefaultTableModel(data, cols){
		public boolean isCellEditable(int rows, int cols) {
			return false;
		}
	}; // addRow();
	JTable table = new JTable(model);
	JScrollPane sPane = new JScrollPane(table); // 스크롤바
	
	Connection con;
	Statement stmt;
	PreparedStatement pstmt;
	ResultSet rs;
	
	MyPage myPage;
	Cart cart;
	
	int cnt; // 장바구니에서 넣을 때 행 구분
	int row = -1; // 행 번호 저장
	String menuValue = ""; // 메뉴명 저장
	String idValue = ""; // 아이디 저장
	int priceValue = 0; // 가격 저장
	
	// 생성자
	public Order(String idValue) {
		this.idValue = idValue;
		
		lbTitle = new JLabel("주문 페이지");
		lbTitle.setFont(new Font("Dialog", Font.BOLD, 20));
		
		btMp = new JButton("마이 페이지");
		btMp.setFont(new Font("Dialog", Font.BOLD, 12));
		
		btBuy = new JButton("구매");
		btAdd = new JButton("담기");
		btCart = new JButton("이동");
		btExit = new JButton("종료");
		
		// 이벤트 등록
		btMp.addActionListener(this);
		btBuy.addActionListener(this);
		btAdd.addActionListener(this);
		btCart.addActionListener(this);
		btExit.addActionListener(this);
		
		table.addMouseListener(new MyMouse());
		
		// Layout
		getContentPane().setLayout(null);
		
		getContentPane().add(lbTitle).setBounds(120, 20, 250, 30);
		getContentPane().add(btMp).setBounds(260, 20, 110, 35);
		
		getContentPane().add(btBuy).setBounds(20, 400, 80, 35);
		getContentPane().add(btAdd).setBounds(105, 400, 80, 35);
		getContentPane().add(btCart).setBounds(190, 400, 80, 35);
		getContentPane().add(btExit).setBounds(275, 400, 80, 35);
		getContentPane().add(sPane).setBounds(10, 70, 365, 300);
		
		setBounds(400, 200, 400, 500);
//		setVisible(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 프로그램 종료
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			System.out.println("드라이버 로딩 성공");
		} catch(ClassNotFoundException cnf) {
			System.out.println("드라이버 로딩 실패 : " + cnf);
		}

		try {
			String URL = "jdbc:mysql://localhost:3306/mydbc";
			con = DriverManager.getConnection(URL, "root", "12345");
			
			System.out.println("DB 연결 성공");
			
			stmt = con.createStatement(); // Statement 생성
		} catch(Exception e) {
			System.out.println("DB 연결 실패 : " + e);
		}
		
		menu_disp();
		check_cnt();
	}
	
	// inner class
	class MyMouse extends MouseAdapter {
		public void mousePressed(MouseEvent me) {
			row = table.getSelectedRow(); // 행 선택
			menuValue = (String)model.getValueAt(row, 0); // 메뉴명 얻기
			String sPriceValue = (String)model.getValueAt(row, 1); // 가격 얻기
			priceValue = Integer.parseInt(sPriceValue);
		}
	}
	
	// 메소드 오버라이딩
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btMp) { // 마이 페이지
			myPage = new MyPage(idValue);
			
			myPage.setVisible(true);
		}
		
		if(e.getSource() == btBuy) { // 구매
			if(row < 0) { // 행 선택 X
				JOptionPane.showMessageDialog(this, "메뉴를 선택하세요");
				
				return;
			}
			
			// orderlist에 추가
			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
			
			String id = idValue.trim();
			String menuname = menuValue.trim();
			String date = sdf.format(today);
			
			try {
				String sql = "insert into orderlist(id, menuname, orderdate) values(?, ?, ?)";
				pstmt = con.prepareStatement(sql);
				
				pstmt.setString(1, id);
				pstmt.setString(2, menuname);
				pstmt.setString(3, date);
				
				pstmt.executeUpdate();
				
				menu_disp();
				
				JOptionPane.showMessageDialog(this, menuname + "\n구매 완료했습니다");
				
				pstmt.close();
			} catch(Exception e2) {
				System.out.println("구매 예외 : " + e2);
			}
			
			row = -1;
		}
		
		if(e.getSource() == btAdd) { // 담기
			if(row < 0) { // 행 선택 X
				JOptionPane.showMessageDialog(this, "메뉴를 선택하세요");
				
				return;
			}
			
			// cart에 추가
			String id = idValue.trim();
			String menuname = menuValue.trim();
			int price = priceValue;
			
			check_cnt();
			cnt ++;
			
			try {
				String sql = "insert into cart(num, id, menuname, price) values(?, ?, ?, ?)";
				pstmt = con.prepareStatement(sql);
				
				pstmt.setInt(1, cnt);
				pstmt.setString(2, id);
				pstmt.setString(3, menuname);
				pstmt.setInt(4, price);
				
				pstmt.executeUpdate();
				
				menu_disp();
				
				JOptionPane.showMessageDialog(this, menuname + "\n장바구니에 추가했습니다");
				
				pstmt.close();
			} catch(Exception e2) {
				System.out.println("구매 예외 : " + e2);
			}
			
			row = -1;
		}
		
		if(e.getSource() == btCart) { // 장바구니 이동
			cart = new Cart(idValue);		
			
			cart.setVisible(true);
		}
		
		if(e.getSource() == btExit) { // 닫기
			try {
				if(rs != null)
					rs.close();
				
				if(stmt != null)
					stmt.close();
				
				if(con != null)
					con.close();
			} catch(Exception e2) {}
			
			System.exit(0);
		}
	}
	
	// 사용자 정의 메소드
	public void menu_disp() {
		model.setRowCount(0); // 테이블 내용 모두 지움
		
		try {
			rs = stmt.executeQuery("select * from menu");
			
			while(rs.next()) {
				String menuname = rs.getString("menuname"); // 메뉴명
				int price = rs.getInt("price"); // 가격
				
				String sPrice = Integer.toString(price);
				
				String temp[] = { menuname, sPrice };
				model.addRow(temp);
			}
			
			rs.close();
		} catch(SQLException e) {
			System.out.println(e);
		}
	}
	
	public void check_cnt() {
		try {
			rs = stmt.executeQuery("select * from cart");
			
			while(rs.next()) {
				cnt = rs.getInt("num");
			}
		} catch(Exception e) {
			System.out.println("cnt 예외");
		}
	}

	public static void main(String[] args) {		
//		new Order("mm");		
	}
}