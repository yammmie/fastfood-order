package order;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class Admin extends JFrame implements ActionListener{
	//변수
	JLabel laJemok, laName, laPrice;
	JTextField tfName,tfPrice;
	JButton buInsert,buDelete, buUpdate,buExit;
	
	Object data[][]=new Object[0][2];//0행 2열
	String cols[]= {"이름","가격"};
	DefaultTableModel model=new DefaultTableModel(data,cols);//addRow()
	JTable table=new JTable(model);
	JScrollPane sPane=new JScrollPane(table);
	
	Connection con=null;
	Statement stmt;
	PreparedStatement pstmt;
	ResultSet rs;
	
	OrderUpdate orderUpdate;
	
	//생성자: 초기화작업
	
	public Admin() {
		
		getContentPane().setBackground(new Color(50,200,100));
		
		orderUpdate=new OrderUpdate();
		
		laJemok=new JLabel("관리자 페이지");
		laJemok.setFont(new Font("Dialog",Font.BOLD,23));
		laJemok.setForeground(Color.blue);
		
		laName=new JLabel("메뉴");
		laName.setFont(new Font("Serif",Font.PLAIN,15));
		laName.setForeground(Color.blue);
		
		laPrice=new JLabel("가격");
		laPrice.setFont(new Font("Serif",Font.PLAIN,15));
		laPrice.setForeground(Color.blue);
		
		tfName=new JTextField();
		tfPrice=new JTextField();
		
		buInsert=new JButton("추가");
		buInsert.setBackground(new Color(255,128,0));
		buDelete=new JButton("삭제");
		buDelete.setBackground(new Color(255,128,0));
		buUpdate=new JButton("변경");
		buUpdate.setBackground(new Color(255,128,0));
		buExit=new JButton("닫기");
		buExit.setBackground(new Color(255,128,0));
		
		buInsert.addActionListener(this);
		buDelete.addActionListener(this);
		buUpdate.addActionListener(this);
		buExit.addActionListener(this);
		
		table.addMouseListener(new MyMouse());
		
		orderUpdate.buUpdate.addActionListener(this);//***이벤트등록
		
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
			System.out.println("드라이버로딩 실패:"+cnf);
		}
		
		try {
			String url="jdbc:mysql://localhost:3306/mydbc";
			con=DriverManager.getConnection(url, "root", "12345");
			
			stmt=con.createStatement();//Statement 생성
		}catch(Exception ex) {
			System.out.println("DB연결 실패:"+ex);
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
	
	//전역변수
	int row=-1;//행번호 저장
	String value="";//값 저장
	
	//inner class
	class MyMouse extends MouseAdapter{
		public void mousePressed(MouseEvent me) {
			row=table.getSelectedRow();//행선택
			value=(String)model.getValueAt(row, 1);//id얻기
			
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
				JOptionPane.showMessageDialog(this,"메뉴는 필수 입력입니다");
				tfName.requestFocus();//포커스 설정
				return;
			}//if
			
			if(price.equals("")||price.length()<1) {
				JOptionPane.showMessageDialog(this, "가격은 필수 입력입니다");
				tfPrice.requestFocus();
				return;
			}//if

			try {
				
				String sql="insert into menu values(?,?)";
				pstmt=con.prepareStatement(sql);//생성시 인자 들어간다
				//?값 채운다
				pstmt.setString(1, menuname);
				pstmt.setString(2, price);
		
				pstmt.executeUpdate();//쿼리 실행
				
				disp();//메서드 호출
				pstmt.close();
				
				tfName.setText("");
				tfPrice.setText("");
			
				tfName.requestFocus();//포커스설정
				
			}catch(Exception ex2) {
				System.out.println(ex2);
			}
						
			
		}//if end
		
		//삭제
		if(e.getSource()==buDelete) {
			if(row<0) {
				JOptionPane.showMessageDialog(this, "삭제할 행을 먼저 선택하시오");
				return;
			}//if
			
			try {
				if(value!="") {//삭제
					stmt.executeUpdate("delete from menu where price='"+value+"'");
				}//if
				
				disp();//메서드 호출
				value="";
				row=-1;
				
			}catch(SQLException ex5) {
				System.out.println("삭제중 예외 :"+ex5);
			}
			
			
		}//if end
		
		//글 수정(화면표시)
		if(e.getSource()==buUpdate) {
			if(row<0) {
				JOptionPane.showMessageDialog(null, "수정할 행을 선택 하시오");
				return;
			}
			String imMenu=(String)model.getValueAt(row,0);
			String imPrice=(String)model.getValueAt(row, 1);
			
			orderUpdate.tfName.setText(imMenu);
			orderUpdate.tfPrice.setText(imPrice);
		
			orderUpdate.setVisible(true);
			
		}//if end
		
		//글수정 (수정)
		if(e.getSource()==orderUpdate.buUpdate) {
			String imMenu=orderUpdate.tfName.getText().trim();
			String imPrice=orderUpdate.tfPrice.getText().trim();
			
			try {
				String sql="update menu set price=? where menuname=?";
				pstmt=con.prepareStatement(sql);//생성자 인자 들어간다
				
				//?값 채우고
				pstmt.setString(1, imPrice);
				pstmt.setString(2, imMenu);
				
				pstmt.executeUpdate();//쿼리 수행
				
				disp();//메서드 호출
				
				row=-1;
				value="";
				orderUpdate.setVisible(false);
				
			}catch(SQLException ex7) {
				System.out.println("글 수정 예외"+ex7);
			}
			
		}//if
		
		
	}//actionPerformed end
	
	
	//main
//	public static void main (String args[]){
//		new Admin();
//	}//main end
	
}//class end
