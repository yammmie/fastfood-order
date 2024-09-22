package order;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.table.*; // DefaultTableModel

// ��ٱ���
public class Cart extends JFrame implements ActionListener {
	// ��������
	JLabel lbTitle;
	JButton btBuy, btDelete, btExit;

	Object data[][] = new Object[0][2]; // 0�� 2��
	String cols[] = { "�޴� �̸�", "����" };
	DefaultTableModel model = new DefaultTableModel(data, cols) {
		public boolean isCellEditable(int rows, int cols) {
			return false;
		}
	}; // addRow();
	JTable table = new JTable(model);
	JScrollPane sPane = new JScrollPane(table); // ��ũ�ѹ�
	
	Connection con;
	PreparedStatement pstmt;
	Statement stmt;
	ResultSet rs;
	
	int row = -1; // �� ��ȣ ����
	int num = -1; // cart db�� num ����
	String menuValue = ""; // �޴��� ����
	String idValue = ""; // ���̵� ����
	int priceValue = 0; // ���� ����
	
	// ������
	public Cart(String idValue) {
		this.idValue = idValue;
		
		lbTitle = new JLabel("��ٱ���");
		lbTitle.setFont(new Font("Dialog", Font.BOLD, 20));
		
		btBuy = new JButton("��� ����");
		btDelete = new JButton("����");
		btExit = new JButton("����");
		
		// �̺�Ʈ ���
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
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ���α׷� ����

		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			String URL = "jdbc:mysql://localhost:3306/mydbc";
			con = DriverManager.getConnection(URL, "root", "12345");
			
			stmt = con.createStatement(); // Statement ����
		} catch(Exception e) {
			System.out.println("DB ���� ���� : " + e);
		}
		
		cart_disp();
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
		if(e.getSource() == btBuy) { // ��� ����
			int num = 0;
			int price = 0;
			
			try {
				rs = stmt.executeQuery("select price from cart where id='" + idValue + "'");
				
				while(rs.next()) {
					num ++;
					
					price += rs.getInt("price"); // price ������ ���� ���� ����
				}
				
				if(num == 0)  // ��ٱ��Ͽ� �޴� X
					JOptionPane.showMessageDialog(this, "��ٱ��Ͽ� ���� �޴��� �����ϴ�");
				
				if(num > 0) { // ��ٱ��Ͽ� �޴��� ���� ��
					int answer = JOptionPane.showConfirmDialog(this, "�� ������ " + price + " ���Դϴ�\n�����Ͻðڽ��ϱ�", "�ֹ� Ȯ��", JOptionPane.YES_NO_OPTION);
					
					switch(answer) {
					case JOptionPane.YES_OPTION: // ����
						buy();						
						break;
					case JOptionPane.NO_OPTION:
						break;
					}
				}
				
				cart_disp();
			} catch(Exception e2) {
				System.out.println("��� ���� DB ���� ���� : " + e2);
			}
			
			row = -1;
		}
		
		if(e.getSource() == btDelete) { // ����
			if(row < 0) { // �� ���� X				
				JOptionPane.showMessageDialog(this, "������ �޴��� �����ϼ���");
				
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
				System.out.println("���� ���� : " + e2);
			}
			
			row = -1;
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
			
			setVisible(false);
		}
	}
	
	// ����� ���� �޼ҵ�
	public void cart_disp() {
		model.setRowCount(0); // ���̺� ���� ��� ����
		
		try {
			rs = stmt.executeQuery("select * from cart where id='" + idValue + "'");
			
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
			System.out.println("buy() select ���� : " + e);
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
			System.out.println("buy() insert ���� : " + e);
		}
	}
//	
//	public static void main(String[] args) {
//		new Cart();
//	}
}