package order;
import java.awt.*;
import javax.swing.*;

public class OrderUpdate extends JFrame{
	//��������
		JLabel laJemok, laName, laPrice;
		JTextField tfName,tfPrice;
		JButton buUpdate;

		
		//������
		public OrderUpdate() {
			//����
			getContentPane().setBackground(new Color(100,200,150));
			
			laJemok=new JLabel("����");
			laJemok.setFont(new Font("Dialog",Font.BOLD,20));
			laJemok.setForeground(Color.blue);
			
			laName=new JLabel("�޴�",JLabel.RIGHT);
			laName.setFont(new Font("Serif",Font.BOLD,15));
			laName.setForeground(Color.blue);
			
			laPrice=new JLabel("����",JLabel.RIGHT);
			laPrice.setFont(new Font("Serif",Font.BOLD,15));
			laPrice.setForeground(Color.blue);
						
			tfName=new JTextField();
			tfPrice=new JTextField();
		
			//
			buUpdate=new JButton("����");
			
			tfName.setEditable(false);
	
			
			//Layout
			getContentPane().setLayout(null);//���α׷��Ӱ� ���� ��ġ
			
			getContentPane().add(laJemok).setBounds(180,20,250,30);
			
			getContentPane().add(laName).setBounds(60,90,50,20);
			getContentPane().add(laPrice).setBounds(60,120,50,20);
			
			//
			getContentPane().add(tfName).setBounds(130,90,150,20);
			getContentPane().add(tfPrice).setBounds(130,120,150,20);
								
			getContentPane().add(buUpdate).setBounds(150,180,80,30);
	
			
			setBounds(650,300,400,400);
					
		}//cons end--
}
