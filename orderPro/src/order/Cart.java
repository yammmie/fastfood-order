package order;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.table.*; // DefaultTableModel

// 장바구니
public class Cart extends JFrame implements ActionListener {
	// 전역변수
	JLabel lbTitle;
	JButton btBuy, btDelete, btExit;

	Object data[][] = new Object[0][2]; // 0행 2열
	String cols[] = { "메뉴 이름", "가격" };
	DefaultTableModel model = new DefaultTableModel(data, cols) {
		public boolean isCellEditable(int rows, int cols) {
			return false;
		}
	}; // addRow();
	JTable table = new JTable(model);
	JScrollPane sPane = new JScrollPane(table); // 스크롤바
	
	Connection con;
	PreparedStatement pstmt;
	Statement stmt;
	ResultSet rs;
	
	int row = -1; // 행 번호 저장
	int num = -1; // cart db의 num 저장
	String menuValue = ""; // 메뉴명 저장
	String idValue = ""; // 아이디 저장
	int priceValue = 0; // 가격 저장
	
	// 생성자
	public Cart(String idValue) {
		this.idValue = idValue;
		
		lbTitle = new JLabel("장바구니");
		lbTitle.setFont(new Font("Dialog", Font.BOLD, 20));
		
		btBuy = new JButton("모두 구매");
		btDelete = new JButton("삭제");
		btExit = new JButton("종료");
		
		// 이벤트 등록
		btBuy.addActionListener(this);
		btDelete.addActionListener(this);
		btExit.addActionListener(this);
		table.addMouseListener(new MyMouse());
		
		// Layout
		getContentPane().setLayout(null);
		
		getContentPane().add(lbTitle).setBounds(150, 20, 250, 30);
		
		getContentPane().add(btBuy).setBounds(50, 400, 90, 35);
		getContentPane().add(btDelete).setBounds(150, 400, 90, 35);
		getContentPane().add(btExit).setBounds(250, 400, 90, 35);
		getContentPane().add(sPane).setBounds(10, 70, 365, 300);
		
		setBounds(400, 200, 400, 500);
//		setVisible(false);
//		setVisible(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 프로그램 종료

		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			String URL = "jdbc:mysql://localhost:3306/mydbc";
			con = DriverManager.getConnection(URL, "root", "12345");
			
			stmt = con.createStatement(); // Statement 생성
		} catch(Exception e) {
			System.out.println("DB 연결 실패 : " + e);
		}
		
		cart_disp();
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
		if(e.getSource() == btBuy) { // 모두 구매
			int num = 0;
			int price = 0;
			
			try {
				rs = stmt.executeQuery("select price from cart where id='" + idValue + "'");
				
				while(rs.next()) {
					num ++;
					
					price += rs.getInt("price"); // price 변수에 가격 누적 저장
				}
				
				if(num == 0)  // 장바구니에 메뉴 X
					JOptionPane.showMessageDialog(this, "장바구니에 담은 메뉴가 없습니다");
				
				if(num > 0) { // 장바구니에 메뉴가 있을 때
					int answer = JOptionPane.showConfirmDialog(this, "총 가격은 " + price + " 원입니다\n구매하시겠습니까", "주문 확인", JOptionPane.YES_NO_OPTION);
					
					switch(answer) {
					case JOptionPane.YES_OPTION: // 구매
						buy();						
						break;
					case JOptionPane.NO_OPTION:
						break;
					}
				}
				
				cart_disp();
			} catch(Exception e2) {
				System.out.println("모두 구매 DB 연결 실패 : " + e2);
			}
			
			row = -1;
		}
		
		if(e.getSource() == btDelete) { // 삭제
			if(row < 0) { // 행 선택 X				
				JOptionPane.showMessageDialog(this, "삭제할 메뉴를 선택하세요");
				
				return;
			}
			
			try {
				rs = stmt.executeQuery("select num from cart where id = '" + idValue + "' limit " + row + ", 1");
				
				while(rs.next()) {
					num = rs.getInt("num");
				}
				
				if(menuValue != "")
					stmt.executeUpdate("delete from cart where num='" + num + "'");
				
				cart_disp();
			} catch(Exception e2) {
				System.out.println("삭제 예외 : " + e2);
			}
			
			row = -1;
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
			
			setVisible(false);
		}
	}
	
	// 사용자 정의 메소드
	public void cart_disp() {
		model.setRowCount(0); // 테이블 내용 모두 지움
		
		try {
			rs = stmt.executeQuery("select * from cart where id='" + idValue + "'");
			
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
	
	public void buy() {
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
		
		String id = idValue.trim();
		String menuname = menuValue.trim();
		String date = sdf.format(today);
		String stMenu = "";
		
		try {
			rs = stmt.executeQuery("select menuname from cart where id='" + idValue + "'");
			
			while(rs.next()) {
				menuname = rs.getString("menuname");
				
				stMenu += menuname + ":";
			}
			
			rs.close();
		} catch(Exception e) {
			System.out.println("buy() select 예외 : " + e);
		}
		
		String temp[] = stMenu.split(":");
		
		try {
			String sql = "insert into orderlist(id, menuname, orderdate) values(?, ?, ?)";
			pstmt = con.prepareStatement(sql);
			
			
			for(int i=0; i<temp.length; i++) {
				pstmt.setString(1, id);
				pstmt.setString(2, temp[i]);
				pstmt.setString(3, date);
				
				pstmt.executeUpdate();
			    pstmt.clearParameters();
			}
			
			stmt.executeUpdate("delete from cart where id='" + idValue + "'");
			
			cart_disp();
			
			menuValue = "";
			row = -1;
			
			pstmt.close();
			rs.close();
		} catch(Exception e) {
			System.out.println("buy() insert 예외 : " + e);
		}
	}
//	
//	public static void main(String[] args) {
//		new Cart();
//	}
}