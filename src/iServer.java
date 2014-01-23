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
	
	JButton		rand	=new JButton("随机端口");
	JButton 	start	=new JButton("启动");
	JButton 	end		=new JButton("终止");
	JButton 	send	=new JButton("发送(Enter)");
	
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

	//用于基础窗体的构建和事件的绑定
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
		North.add( new JLabel("端口：") );
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
	
	//发送信息专用函数
	private void sendMsg(){
		try{
			PrintWriter so = new PrintWriter(client.getOutputStream());
			String str = sendBox.getText();
			if(str != "") {
				//含发送时间后缀
				SimpleDateFormat df=new SimpleDateFormat("HH:mm:ss");
				textBox.append("服务器　"+df.format(new Date())+":\n" + str + "\n");
				so.write("服务器　"+df.format(new Date())+":\n" + str + "\n");
				so.flush();
			}
			//发送成功则清空发送框的内容
			sendBox.setText("");
		}
		catch(Exception ec){
			textBox.append("发送失败，请重试\n");
		}
	}
	
	//各类事件监听
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
					start.setText("已启动");
					end.setEnabled(true);
				}
				catch(Exception ec){
					textBox.append("服务器程序故障，请关闭后重试\n");
					start.setText("启动");
					start.setEnabled(true);
					end.setEnabled(false);
				}

			}
			else if( o==end ){
				try {
					end.setEnabled(false);
					//发送断开的消息
					PrintWriter co = new PrintWriter(client.getOutputStream());
					co.write("服务器端已经中止了连接！\n");
					co.flush();
					//关闭客户端和服务器
					client.close();
					server.close();
					//按钮重布局
					start.setText("启动");
					start.setEnabled(true);
				} 
				catch (IOException e1) {
					textBox.append("断开连接时出现了问题，请重试\n");
					start.setText("已启动");
					start.setEnabled(false);
					end.setEnabled(true);
				}
			}
			else if( o==rand ){
				int i=(int) (Math.random()*65535);	//随机端口号
				port.setText( i+"" );
			}
			else if( o==send ){
				sendMsg();
			}
			
		}
		
	}
	
	//键盘监听
	class KeyListener extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			//编辑发送框时按下回车键可直接发送消息
			if( KeyEvent.VK_ENTER ==e.getKeyCode() )
				sendMsg();
		}
	}
	
	//接收消息线程
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
		new SFrame("服务器端");
	}
}
