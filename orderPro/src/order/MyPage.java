package order;
import java.sql.*;
import java.time.Year;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

public class MyPage extends JFrame implements ActionListener{
	//����
	JLabel laJemok;
	JButton buExit;

	Object data[][]=new Object[0][3];//0�� 5��
	String cols[]={"Id","�޴�","�ֹ���¥"};
	DefaultTableModel model = new DefaultTableModel(data, cols) {
		public boolean isCellEditable(int rows, int cols) {
			return false;
		}
	};
	JTable table=new JTable(model);
	JScrollPane sPane=new JScrollPane(table);//��ũ�ѹ�

	Connection con;
	Statement stmt;
	ResultSet rs;
	
	String idValue = "";

	//������
	public MyPage(String idValue){
		this.idValue = idValue;

		laJemok=new JLabel("�ֹ�����");
		laJemok.setFont(new Font("Dialog",Font.BOLD,20));
		laJemok.setForeground(Color.BLACK);

		
		buExit=new JButton("Exit");

		//�̺�Ʈ���
		buExit.addActionListener(this);

		//layout
		getContentPane().setLayout(null);

		getContentPane().add(laJemok).setBounds(250, 30, 500, 50);
		getContentPane().add(sPane).setBounds(10, 100, 565, 300);
		getContentPane().add(buExit).setBounds(250, 500, 80, 30);
		
		setBounds(200, 200, 600, 600);
//		setVisible(true);

		String URIVER="com.mysql.jdbc.Driver";
		String URL="jdbc:mysql://localhost:3306/mydbc";
		String USER="root";
		String PWD="12345";

		try{
			Class.forName(URIVER);
		}catch(ClassNotFoundException cnf){
			System.out.println("����̹��ε� ���� :"+cnf);
		}//catch


		try{
			con=DriverManager.getConnection(URL,"root","12345");

			stmt=con.createStatement();//Statement ����
		}catch(Exception ex){
			System.out.println("DB���� ����:"+ex);
		}//catch

		disp();
	}//cons
	//����� ���Ǹ޼���
	public void disp(){
		model.setRowCount(0);//���̺� ���� ��� �����
		try{
			rs=stmt.executeQuery("select * from orderlist where id='" + idValue + "'");
			while(rs.next()){

				String id=rs.getString("id");
				String menuname=rs.getString("menuname");
				//String su=rs.getString("su");
				String orderdate=rs.getString("orderdate");

				//String temp[]={nickname,menuname,su,orderdate};
				String temp[]={id,menuname,orderdate};
				model.addRow(temp);

			}//while
			rs.close();

		}catch(SQLException ex){
			System.out.println(ex);
		}//catch
	}//disp() end---

	//�޼���
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==buExit){
			try{
				if(rs!=null){rs.close();}
				if(stmt!=null){stmt.close();}
				if(con!=null){con.close();}
			}catch(Exception ex){}
			
			setVisible(false);
		}//if
	}//
	//����
//	public static void main(String[] args) {
//		new MyPage();
//	}//main
}//class 
