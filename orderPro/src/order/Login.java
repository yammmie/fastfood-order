package order;
import java.sql.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Login extends JFrame implements ActionListener{

	//변수
	JLabel laLog,laId,laPwd;
	JTextField tfId,tfPwd;
	JButton buLogin,buJoin;
	Image im;
	String idValue="";
	String pwdValue="";

	Connection con=null;
	Statement stmt;
	PreparedStatement pstmt;
	ResultSet rs;

	Join join;
	Order order;
	Admin admin;

	//생성자
	
	
	public Login(){
		join=new Join();
		admin = new Admin();
		
		laLog=new JLabel("Login");
		laLog.setFont(new Font("Dialog",Font.BOLD,30));
		laLog.setForeground(Color.black);

		laId=new JLabel("ID",JLabel.RIGHT);
		laId.setFont(new Font("Dialog",Font.BOLD,12));
		laId.setForeground(Color.black);

		laPwd=new JLabel("Password",JLabel.RIGHT);
		laPwd.setFont(new Font("Dialog",Font.BOLD,12));
		laPwd.setForeground(Color.black);

		tfId=new JTextField();
		tfPwd=new JTextField();
	

		getContentPane().setBackground(Color.white);
		buLogin=new JButton("Login");
		buJoin=new JButton("Join");


		//이벤트 등록

		buLogin.addActionListener(this);
		buJoin.addActionListener(this);

		join.buJoin.addActionListener(this);

		//Layout
		getContentPane().setLayout(null);//프로그래머가 직접 배치

		getContentPane().add(laLog).setBounds(150,30,240,30);

		getContentPane().add(laId).setBounds(80,120,35,20);
		getContentPane().add(laPwd).setBounds(80,150,60,20);

		getContentPane().add(tfId).setBounds(150,120,150,20);
		getContentPane().add(tfPwd).setBounds(150,150,150,20);

		getContentPane().add(buLogin).setBounds(80,250,100,40);
		getContentPane().add(buJoin).setBounds(200,250,100,40);

		
		setBounds(400,300,400,400);
		setVisible(true);

	
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//프로그램 종료

		try{
			Class.forName("com.mysql.jdbc.Driver");
		}catch(ClassNotFoundException cnf){
			System.out.println("드라이버로딩 실패:"+cnf);
		}

		try{
			String url="jdbc:mysql://localhost:3306/mydbc";
			con=DriverManager.getConnection(url,"root","12345");

			stmt=con.createStatement();//Statement 생성
		}catch(Exception ex){
			System.out.println("DB연결 실패:"+ex);
		}

		//disp();//메소드 호출

	}//cons end
	public void disp(){
		try{

			rs=stmt.executeQuery("select id,pwd from userinfo");

			while(rs.next()){

				String id=rs.getString("id");
				String pwd=rs.getString("pwd");

			}//while end

			rs.close();

		}catch(SQLException ex){
			System.out.println(ex);
		}//catch end

	}//disp() end---

	public void actionPerformed(ActionEvent e){

		if(e.getSource()==buLogin){
			try{
				rs=stmt.executeQuery("select id, pwd from userinfo");

				String idgt=tfId.getText().trim();
				String pwdgt=tfPwd.getText().trim();

				while(rs.next()){

					idValue=rs.getString("id");
					pwdValue=rs.getString("pwd");

					if(idValue.equals(idgt))
						break;

				}//while end

				if(idValue.equals(idgt)){
					if(pwdValue.equals(pwdgt)) {
						JOptionPane.showMessageDialog(this, "로그인 성공");
						setVisible(false);
						
						if(idValue.equals("admin")) // 관리자 계정일 경우
							admin.setVisible(true);
						else { // 일반 계정일 경우
							order = new Order(idValue);
							order.setVisible(true);
						}

						return;
					}else if(!pwdValue.equals(pwdgt)){
						JOptionPane.showMessageDialog(this, "비밀번호를 확인하세요");
						tfPwd.setText("");
						tfPwd.requestFocus();
						
						return;
					}
				}else if(!idValue.equals(idgt)){
					JOptionPane.showMessageDialog(this, "아이디를 확인하세요");
					tfId.setText("");
					tfPwd.setText("");
					tfId.requestFocus();
					
					return;
				}

				rs.close();
			}catch(SQLException ex){
				System.out.println(ex);
			}//catch

		}//if

		if(e.getSource()==buJoin){
			join.setVisible(true);			
		}//if
	}//action
	//main
//	public static void main(String[] args){
//		new Login();
//	}//main
}//class
