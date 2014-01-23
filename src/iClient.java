import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.io.*;

import javax.swing.*;

//请参考iServer的注释
@SuppressWarnings("serial")
class CFrame extends JFrame{
	
	JTextField 	port	=new JTextField(5);
	JTextField	ip		=new JTextField(9);
	JTextField 	sendBox	=new JTextField(21);
	
	TextArea 	textBox	=new TextArea(25,49);
	
	JButton 	connect	=new JButton("连接");
	JButton 	disc	=new JButton("断开");
	JButton 	send	=new JButton("发送(Enter)");
	
	ServerSocket server=null;
	Socket client=null;
	SListener sListener=new SListener();  
	KeyListener keyListener=new KeyListener();
	
	CFrame(String str){
		super(str);
		build();
		
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void build() {
		this.setLocation(20,20);
		this.setSize(365,500);
		this.setResizable(false);
		
		JPanel North=new JPanel();
		port.setText("10086");
		ip.setText("127.0.0.1");
		disc.setEnabled( false );
		connect.addActionListener(sListener);
		disc.addActionListener(sListener);
		North.add( new JLabel("地址：") );
		North.add( ip );
		North.add( new JLabel(":") );
		North.add( port );
		North.add( connect );
		North.add( disc );
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
	
	private void sendMsg(){
		try{
			PrintWriter co = new PrintWriter(client.getOutputStream());
			String str = sendBox.getText();
			if(str != "") {
				SimpleDateFormat df=new SimpleDateFormat("HH:mm:ss");
				textBox.append("用户端　"+df.format(new Date())+":\n" + str + "\n");
				co.write("用户端　"+df.format(new Date())+":\n"+ str + "\n");
				co.flush();
			}
			sendBox.setText("");
		}
		catch(Exception ec){
			textBox.append("发送失败，请重试\n");
		}
	}
	
	class SListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			Object o=e.getSource();
			if( o==connect ){
				try{
					connect.setEnabled(false);
					
					int i=Integer.parseInt(port.getText());
					client=new Socket(ip.getText(),i);
					ClientThread cT=new ClientThread();
					cT.start();
					
					disc.setEnabled(true);
				}
				catch(Exception ec){
					textBox.append("无法连接到服务器，请重试\n");
					connect.setEnabled(true);
					disc.setEnabled(false);
				}

			}
			else if( o==disc ){
				try {
					disc.setEnabled(false);
					PrintWriter co = new PrintWriter(client.getOutputStream());
					co.write("用户端已经断开了连接！\n");
					co.flush();
					client.close();
					connect.setEnabled(true);
					
				} catch (IOException e1) {
					textBox.append("断开连接时出现了问题，请重试\n");
					connect.setEnabled(false);
					disc.setEnabled(true);
				}
			}
			else if( o==send ){
				sendMsg();
			}
			
		}
		
	}
	
	class KeyListener extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			if( KeyEvent.VK_ENTER ==e.getKeyCode() )
				sendMsg();
		}
	}
	
	
	class ClientThread extends Thread{
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


public class iClient {
	public static void main(String[] args){
		new CFrame("客户端");
	}
}
