package order;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class Admin extends JFrame implements ActionListener{
	//����
	JLabel laJemok, laName, laPrice;
	JTextField tfName,tfPrice;
	JButton buInsert,buDelete, buUpdate,buExit;
	
	Object data[][]=new Object[0][2];//0�� 2��
	String cols[]= {"�̸�","����"};
	DefaultTableModel model=new DefaultTableModel(data,cols);//addRow()
	JTable table=new JTable(model);
	JScrollPane sPane=new JScrollPane(table);
	
	Connection con=null;
	Statement stmt;
	PreparedStatement pstmt;
	ResultSet rs;
	
	OrderUpdate orderUpdate;
	
	//������: �ʱ�ȭ�۾�
	
	public Admin() {
		
		getContentPane().setBackground(new Color(50,200,100));
		
		orderUpdate=new OrderUpdate();
		
		laJemok=new JLabel("������ ������");
		laJemok.setFont(new Font("Dialog",Font.BOLD,23));
		laJemok.setForeground(Color.blue);
		
		laName=new JLabel("�޴�");
		laName.setFont(new Font("Serif",Font.PLAIN,15));
		laName.setForeground(Color.blue);
		
		laPrice=new JLabel("����");
		laPrice.setFont(new Font("Serif",Font.PLAIN,15));
		laPrice.setForeground(Color.blue);
		
		tfName=new JTextField();
		tfPrice=new JTextField();
		
		buInsert=new JButton("�߰�");
		buInsert.setBackground(new Color(255,128,0));
		buDelete=new JButton("����");
		buDelete.setBackground(new Color(255,128,0));
		buUpdate=new JButton("����");
		buUpdate.setBackground(new Color(255,128,0));
		buExit=new JButton("�ݱ�");
		buExit.setBackground(new Color(255,128,0));
		
		buInsert.addActionListener(this);
		buDelete.addActionListener(this);
		buUpdate.addActionListener(this);
		buExit.addActionListener(this);
		
		table.addMouseListener(new MyMouse());
		
		orderUpdate.buUpdate.addActionListener(this);//***�̺�Ʈ���
		
		//Layout
		getContentPane().setLayout(null);
		
		getContentPane().add(laJemok).setBounds(230,20,250,30);
		
		getContentPane().add(laName).setBounds(150,85,50,20);
		getContentPane().add(laPrice).setBounds(150,115,50,20);

		getContentPane().add(tfName).setBounds(240,85,150,20);
		getContentPane().add(tfPrice).setBounds(240,115,150,20);
		
		getContentPane().add(buInsert).setBounds(120,170,80,30);
		getContentPane().add(buDelete).setBounds(210,170,80,30);
		getContentPane().add(buUpdate).setBounds(300,170,80,30);
		getContentPane().add(buExit).setBounds(390,170,80,30);
	
		getContentPane().add(sPane).setBounds(10,230,570,200);
		
		setBounds(550,200,600,600);
//		setVisible(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}catch(ClassNotFoundException cnf) {
			System.out.println("����̹��ε� ����:"+cnf);
		}
		
		try {
			String url="jdbc:mysql://localhost:3306/mydbc";
			con=DriverManager.getConnection(url, "root", "12345");
			
			stmt=con.createStatement();//Statement ����
		}catch(Exception ex) {
			System.out.println("DB���� ����:"+ex);
		}
		
		disp();
				
	}//cons end------------
	
	public void disp() {
		model.setRowCount(0);
		try {
			rs=stmt.executeQuery("select * from menu");
			while(rs.next()) {
				
				String menuname=rs.getString("menuname");
				String price=rs.getString("price");
		
				String temp[]= {menuname,price};
				model.addRow(temp);
			}//while end
			
			rs.close();
		}catch(SQLException ex) {
			System.out.println(ex);
		}//catch end
	}//disp() end-----
	
	//��������
	int row=-1;//���ȣ ����
	String value="";//�� ����
	
	//inner class
	class MyMouse extends MouseAdapter{
		public void mousePressed(MouseEvent me) {
			row=table.getSelectedRow();//�༱��
			value=(String)model.getValueAt(row, 1);//id���
			
		}//mousePressed() end--
	}//MyMosue end--
	
	//method
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==buExit) {
			try {
				if(rs!=null) {rs.close();}
				if(stmt!=null) {stmt.close();}
				if(con!=null) {con.close();}
			}catch(Exception ex) {}
				System.exit(0);		
		}//if end
		
		if(e.getSource()==buInsert) {
			String menuname=tfName.getText().trim();
			String price=tfPrice.getText().trim();
			
			if(menuname.equals("")||menuname.length()<1) {
				JOptionPane.showMessageDialog(this,"�޴��� �ʼ� �Է��Դϴ�");
				tfName.requestFocus();//��Ŀ�� ����
				return;
			}//if
			
			if(price.equals("")||price.length()<1) {
				JOptionPane.showMessageDialog(this, "������ �ʼ� �Է��Դϴ�");
				tfPrice.requestFocus();
				return;
			}//if

			try {
				
				String sql="insert into menu values(?,?)";
				pstmt=con.prepareStatement(sql);//������ ���� ����
				//?�� ä���
				pstmt.setString(1, menuname);
				pstmt.setString(2, price);
		
				pstmt.executeUpdate();//���� ����
				
				disp();//�޼��� ȣ��
				pstmt.close();
				
				tfName.setText("");
				tfPrice.setText("");
			
				tfName.requestFocus();//��Ŀ������
				
			}catch(Exception ex2) {
				System.out.println(ex2);
			}
						
			
		}//if end
		
		//����
		if(e.getSource()==buDelete) {
			if(row<0) {
				JOptionPane.showMessageDialog(this, "������ ���� ���� �����Ͻÿ�");
				return;
			}//if
			
			try {
				if(value!="") {//����
					stmt.executeUpdate("delete from menu where price='"+value+"'");
				}//if
				
				disp();//�޼��� ȣ��
				value="";
				row=-1;
				
			}catch(SQLException ex5) {
				System.out.println("������ ���� :"+ex5);
			}
			
			
		}//if end
		
		//�� ����(ȭ��ǥ��)
		if(e.getSource()==buUpdate) {
			if(row<0) {
				JOptionPane.showMessageDialog(null, "������ ���� ���� �Ͻÿ�");
				return;
			}
			String imMenu=(String)model.getValueAt(row,0);
			String imPrice=(String)model.getValueAt(row, 1);
			
			orderUpdate.tfName.setText(imMenu);
			orderUpdate.tfPrice.setText(imPrice);
		
			orderUpdate.setVisible(true);
			
		}//if end
		
		//�ۼ��� (����)
		if(e.getSource()==orderUpdate.buUpdate) {
			String imMenu=orderUpdate.tfName.getText().trim();
			String imPrice=orderUpdate.tfPrice.getText().trim();
			
			try {
				String sql="update menu set price=? where menuname=?";
				pstmt=con.prepareStatement(sql);//������ ���� ����
				
				//?�� ä���
				pstmt.setString(1, imPrice);
				pstmt.setString(2, imMenu);
				
				pstmt.executeUpdate();//���� ����
				
				disp();//�޼��� ȣ��
				
				row=-1;
				value="";
				orderUpdate.setVisible(false);
				
			}catch(SQLException ex7) {
				System.out.println("�� ���� ����"+ex7);
			}
			
		}//if
		
		
	}//actionPerformed end
	
	
	//main
//	public static void main (String args[]){
//		new Admin();
//	}//main end
	
}//class end
