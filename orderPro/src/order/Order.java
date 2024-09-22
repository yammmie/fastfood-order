package order;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.table.*; // DefaultTableModel

// �ֹ� ������
public class Order extends JFrame implements ActionListener {
	// ���� ����
	JLabel lbTitle;
	JButton btMp, btBuy, btAdd, btCart, btExit;
	
	Object data[][] = new Object[0][2]; // 0�� 2��
	String cols[] = { "�޴� �̸�", "����" };
	DefaultTableModel model = new DefaultTableModel(data, cols){
		public boolean isCellEditable(int rows, int cols) {
			return false;
		}
	}; // addRow();
	JTable table = new JTable(model);
	JScrollPane sPane = new JScrollPane(table); // ��ũ�ѹ�
	
	Connection con;
	Statement stmt;
	PreparedStatement pstmt;
	ResultSet rs;
	
	MyPage myPage;
	Cart cart;
	
	int cnt; // ��ٱ��Ͽ��� ���� �� �� ����
	int row = -1; // �� ��ȣ ����
	String menuValue = ""; // �޴��� ����
	String idValue = ""; // ���̵� ����
	int priceValue = 0; // ���� ����
	
	// ������
	public Order(String idValue) {
		this.idValue = idValue;
		
		lbTitle = new JLabel("�ֹ� ������");
		lbTitle.setFont(new Font("Dialog", Font.BOLD, 20));
		
		btMp = new JButton("���� ������");
		btMp.setFont(new Font("Dialog", Font.BOLD, 12));
		
		btBuy = new JButton("����");
		btAdd = new JButton("���");
		btCart = new JButton("�̵�");
		btExit = new JButton("����");
		
		// �̺�Ʈ ���
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
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ���α׷� ����
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			System.out.println("����̹� �ε� ����");
		} catch(ClassNotFoundException cnf) {
			System.out.println("����̹� �ε� ���� : " + cnf);
		}

		try {
			String URL = "jdbc:mysql://localhost:3306/mydbc";
			con = DriverManager.getConnection(URL, "root", "12345");
			
			System.out.println("DB ���� ����");
			
			stmt = con.createStatement(); // Statement ����
		} catch(Exception e) {
			System.out.println("DB ���� ���� : " + e);
		}
		
		menu_disp();
		check_cnt();
	}
	
	// inner class
	class MyMouse extends MouseAdapter {
		public void mousePressed(MouseEvent me) {
			row = table.getSelectedRow(); // �� ����
			menuValue = (String)model.getValueAt(row, 0); // �޴��� ���
			String sPriceValue = (String)model.getValueAt(row, 1); // ���� ���
			priceValue = Integer.parseInt(sPriceValue);
		}
	}
	
	// �޼ҵ� �������̵�
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btMp) { // ���� ������
			myPage = new MyPage(idValue);
			
			myPage.setVisible(true);
		}
		
		if(e.getSource() == btBuy) { // ����
			if(row < 0) { // �� ���� X
				JOptionPane.showMessageDialog(this, "�޴��� �����ϼ���");
				
				return;
			}
			
			// orderlist�� �߰�
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
				
				JOptionPane.showMessageDialog(this, menuname + "\n���� �Ϸ��߽��ϴ�");
				
				pstmt.close();
			} catch(Exception e2) {
				System.out.println("���� ���� : " + e2);
			}
			
			row = -1;
		}
		
		if(e.getSource() == btAdd) { // ���
			if(row < 0) { // �� ���� X
				JOptionPane.showMessageDialog(this, "�޴��� �����ϼ���");
				
				return;
			}
			
			// cart�� �߰�
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
				
				JOptionPane.showMessageDialog(this, menuname + "\n��ٱ��Ͽ� �߰��߽��ϴ�");
				
				pstmt.close();
			} catch(Exception e2) {
				System.out.println("���� ���� : " + e2);
			}
			
			row = -1;
		}
		
		if(e.getSource() == btCart) { // ��ٱ��� �̵�
			cart = new Cart(idValue);		
			
			cart.setVisible(true);
		}
		
		if(e.getSource() == btExit) { // �ݱ�
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
	
	// ����� ���� �޼ҵ�
	public void menu_disp() {
		model.setRowCount(0); // ���̺� ���� ��� ����
		
		try {
			rs = stmt.executeQuery("select * from menu");
			
			while(rs.next()) {
				String menuname = rs.getString("menuname"); // �޴���
				int price = rs.getInt("price"); // ����
				
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
			System.out.println("cnt ����");
		}
	}

	public static void main(String[] args) {		
//		new Order("mm");		
	}
}