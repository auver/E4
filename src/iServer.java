import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import javax.swing.*;

@SuppressWarnings("serial")
class SFrame extends JFrame{
	
	JTextField 	port	=new JTextField(5);
	JTextField 	sendBox	=new JTextField(21);
	
	TextArea 	textBox	=new TextArea(25,49);
	
	JButton		rand	=new JButton("����˿�");
	JButton 	start	=new JButton("����");
	JButton 	end		=new JButton("��ֹ");
	JButton 	send	=new JButton("����(Enter)");
	
	ServerSocket server=null;
	Socket client=null;
	SListener sListener=new SListener(); 
	KeyListener keyListener=new KeyListener();
	
	SFrame(String str){
		super(str);
		build();
		
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	//���ڻ�������Ĺ������¼��İ�
	private void build() {
		this.setLocation(410,20);
		this.setSize(365,500);
		this.setResizable(false);
		
		JPanel North=new JPanel();
		port.setText("10086");
		end.setEnabled( false );
		start.addActionListener(sListener);
		end.addActionListener(sListener);
		rand.addActionListener(sListener);
		North.add( new JLabel("�˿ڣ�") );
		North.add( port );
		North.add( rand );
		North.add( start );
		North.add( end );
		this.add(North,BorderLayout.NORTH);
		
		JPanel Center=new JPanel();
		textBox.setEditable(false);
		textBox.setBackground(Color.white);
		Center.add( textBox );
		this.add(Center,BorderLayout.CENTER);
		
		JPanel South=new JPanel();
		send.addActionListener(sListener);
		sendBox.addKeyListener(keyListener);
		South.add( sendBox );
		South.add( send );	
		this.add(South,BorderLayout.SOUTH);
		

	}
	
	//������Ϣר�ú���
	private void sendMsg(){
		try{
			PrintWriter so = new PrintWriter(client.getOutputStream());
			String str = sendBox.getText();
			if(str != "") {
				//������ʱ���׺
				SimpleDateFormat df=new SimpleDateFormat("HH:mm:ss");
				textBox.append("��������"+df.format(new Date())+":\n" + str + "\n");
				so.write("��������"+df.format(new Date())+":\n" + str + "\n");
				so.flush();
			}
			//���ͳɹ�����շ��Ϳ������
			sendBox.setText("");
		}
		catch(Exception ec){
			textBox.append("����ʧ�ܣ�������\n");
		}
	}
	
	//�����¼�����
	class SListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			Object o=e.getSource();
			if( o==start ){
				try{
					start.setEnabled(false);
					int i=Integer.parseInt(port.getText());
					server=new ServerSocket(i);
					client=server.accept();
					ServerThread sT=new ServerThread();
					sT.start();
					start.setText("������");
					end.setEnabled(true);
				}
				catch(Exception ec){
					textBox.append("������������ϣ���رպ�����\n");
					start.setText("����");
					start.setEnabled(true);
					end.setEnabled(false);
				}

			}
			else if( o==end ){
				try {
					end.setEnabled(false);
					//���ͶϿ�����Ϣ
					PrintWriter co = new PrintWriter(client.getOutputStream());
					co.write("���������Ѿ���ֹ�����ӣ�\n");
					co.flush();
					//�رտͻ��˺ͷ�����
					client.close();
					server.close();
					//��ť�ز���
					start.setText("����");
					start.setEnabled(true);
				} 
				catch (IOException e1) {
					textBox.append("�Ͽ�����ʱ���������⣬������\n");
					start.setText("������");
					start.setEnabled(false);
					end.setEnabled(true);
				}
			}
			else if( o==rand ){
				int i=(int) (Math.random()*65535);	//����˿ں�
				port.setText( i+"" );
			}
			else if( o==send ){
				sendMsg();
			}
			
		}
		
	}
	
	//���̼���
	class KeyListener extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			//�༭���Ϳ�ʱ���»س�����ֱ�ӷ�����Ϣ
			if( KeyEvent.VK_ENTER ==e.getKeyCode() )
				sendMsg();
		}
	}
	
	//������Ϣ�߳�
	class ServerThread extends Thread{
		public void run(){
			try{
				BufferedReader bi = new BufferedReader(new InputStreamReader(client.getInputStream()) );
				String s=bi.readLine();
				while( s!=null ){
					textBox.append(s+"\n");
					s=bi.readLine();
				}
			}
			catch(Exception ec){
			}

		}
	}
	
}


public class iServer {
	public static void main(String[] args){
		new SFrame("��������");
	}
}
