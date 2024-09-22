package order;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Join extends JFrame implements ActionListener{

	//변수
	JLabel laJoin,laId,laPwd,laAddr,laHp;
	JTextField tfId,tfPwd,tfAddr,tfHp;
	JButton buJoin,buExit;

	Connection con=null;
	Statement stmt;
	PreparedStatement pstmt;
	ResultSet rs;


	//생성자(초기화)
	public Join(){


		laJoin=new JLabel("Join");
		laJoin.setFont(new Font("Dialog",Font.BOLD,30));
		laJoin.setForeground(Color.black);

		laId=new JLabel("ID",JLabel.RIGHT);
		laId.setFont(new Font("Dialog",Font.BOLD,12));
		laId.setForeground(Color.black);

		laPwd=new JLabel("Password",JLabel.RIGHT);
		laPwd.setFont(new Font("Dialog",Font.BOLD,12));
		laPwd.setForeground(Color.black);

		laAddr=new JLabel("Address",JLabel.RIGHT);
		laAddr.setFont(new Font("Dialog",Font.BOLD,12));
		laAddr.setForeground(Color.black);

		laHp=new JLabel("Cellphone(-)",JLabel.RIGHT);
		laHp.setFont(new Font("Dialog",Font.BOLD,12));
		laHp.setForeground(Color.black);

		tfId=new JTextField();
		tfPwd=new JTextField();
		tfAddr=new JTextField();
		tfHp=new JTextField();

		buJoin=new JButton("Join");
		buExit=new JButton("Exit");

		buJoin.addActionListener(this);
		buExit.addActionListener(this);

		//Layout
		getContentPane().setLayout(null);//프로그래머가 직접 배치

		getContentPane().add(laJoin).setBounds(150,30,240,30);
		getContentPane().add(laId).setBounds(80,90,35,20);
		getContentPane().add(laPwd).setBounds(80,120,60,20);
		getContentPane().add(laHp).setBounds(80,150,68,20);
		getContentPane().add(laAddr).setBounds(80,180,55,20);

		getContentPane().add(tfId).setBounds(150,90,150,20);
		getContentPane().add(tfPwd).setBounds(150,120,150,20);
		getContentPane().add(tfHp).setBounds(150,150,150,20);
		getContentPane().add(tfAddr).setBounds(150,180,150,20);

		getContentPane().add(buJoin).setBounds(80,250,100,40);
		getContentPane().add(buExit).setBounds(200,250,100,40);

		getContentPane().setBackground(Color.white);
		setBounds(900,300,400,400);
		//		setVisible(true);

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

		disp();//메소드 호출
	}

	public void disp(){
		try{
			rs=stmt.executeQuery("select * from userinfo");
			while(rs.next()){

				String id=rs.getString("id");
				String pwd=rs.getString("pwd");
				String hp=rs.getString("hp");
				String addr=rs.getString("addr");


			}//while end

			rs.close();

		}catch(SQLException ex){
			System.out.println(ex);
		}//catch end

	}//disp() end---

	public void actionPerformed(ActionEvent e){
		if(e.getSource()==buExit){
			try{
				if(rs!=null){rs.close();}
				if(stmt!=null){stmt.close();}
				if(con!=null){con.close();}
			}catch(Exception ex){}
			setVisible(false);//프로그램 종료

			tfId.setText("");
			tfPwd.setText("");
			tfHp.setText("");
			tfAddr.setText("");

		}//if end

		if(e.getSource()==buJoin){
			String id=tfId.getText().trim();
			String pwd=tfPwd.getText().trim();
			String hp=tfHp.getText().trim();
			String addr=tfAddr.getText().trim();

			try{

				rs=stmt.executeQuery("select id from userinfo");

				while(rs.next()){
					String id2=rs.getString("id"); 
					if(id2.equals(id)){
						JOptionPane.showMessageDialog(this, "사용중인 아이디입니다");
						tfId.requestFocus();
						return;
					}
				}
			}catch(Exception ex3){
				System.out.println(ex3);
			}
			
			if(id.equals("")||id.length()<1){
				JOptionPane.showMessageDialog(this, "ID를 입력해주세요.");
				tfId.requestFocus();
				return;				

			}else if(pwd.equals("")||pwd.length()<1){
				JOptionPane.showMessageDialog(this, "비밀번호를 입력해주세요.");
				tfPwd.requestFocus();
				return;
			}else if(hp.equals("")||hp.length()<1){
				JOptionPane.showMessageDialog(this, "휴대전화 번호를 입력해주세요.");
				tfHp.requestFocus();
				return;
			}else if(addr.equals("")||addr.length()<1){
				JOptionPane.showMessageDialog(this, "주소를 입력해주세요.");
				tfAddr.requestFocus();
				return;
			}



			try{

				String sql="insert into userinfo values(?,?,?,?)";
				pstmt=con.prepareStatement(sql);//생성시 인자 들어감
				//?값 채우기
				pstmt.setString(1, id);
				pstmt.setString(2, pwd);
				pstmt.setString(3, hp);
				pstmt.setString(4, addr);
				pstmt.executeUpdate();//쿼리 실행

				disp();//메소드 호출
				pstmt.close();


				tfId.setText("");
				tfPwd.setText("");
				tfHp.setText("");
				tfAddr.setText("");

				if(e.getSource()==buJoin) {
					JOptionPane.showMessageDialog(this, "회원가입 성공");
					setVisible(false);
					return;
				}

				tfId.requestFocus();//포커스 설정

			}catch(Exception ex2){
				System.out.println(ex2);
			}
		}//if end

	}//actionPerformed end

	//main
	//	public static void main(String args[]){
	//		new Join();
	//	}
}
