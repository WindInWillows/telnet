import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Controller {
	private Socket client = null;
	private JFrame jf = new JFrame();
	private JLabel jl = new JLabel();
	private boolean connected = false;
	private DataOutputStream dos = null;
	private ObjectOutputStream oos = null;
	
	public void setJFrame(){
		jf.setSize(Toolkit.getDefaultToolkit().getScreenSize());
//		jf.setSize(400, 300);
		jf.setTitle("Disconnected!");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
	}
	
	public void start(String ip, String port){
		setJFrame();
		try {
			client = new Socket(ip,Integer.parseInt(port));
//			client = new Socket("127.0.0.1",8888);
			connected = true;
			dos = new DataOutputStream(client.getOutputStream());
			oos = new ObjectOutputStream(client.getOutputStream());
			jf.setTitle("Controling!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		jf.add(jl);
		jf.addMouseListener(new MouseEven());
		jf.addKeyListener(new KeyEven());
		jf.addMouseWheelListener(new Wheel());
		jf.addMouseMotionListener(new MouseMove());
		new Thread(new showImage()).start();
	}

	class showImage implements Runnable{
		@Override
		public void run() {
			DataInputStream dis;
			try {
				dis = new DataInputStream(client.getInputStream());
				while(connected){
					try {
						int len = dis.readInt();
						byte[] image = new byte[len];
						dis.readFully(image);
						ByteArrayInputStream bais = new ByteArrayInputStream(image);
						ImageIcon icon = new ImageIcon(ImageIO.read(bais));
						jl.setIcon(icon);
						jl.repaint();
						Thread.sleep(50);
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
						connected = false;
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				connected = false;
			}
		}
	}
	
	private class KeyEven implements KeyListener{
		public void sendKey(Socket c,KeyEvent e){
			try {
				oos.writeObject(e);
				oos.flush();
			} catch (IOException ee) {
				ee.printStackTrace();
			}
		}
		@Override
		public void keyPressed(KeyEvent e) {
			sendKey(client, e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			sendKey(client,e);			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			sendKey(client,e);			
		}
	}
	
	public void sendMouse(MouseEvent me){
		try {
			oos.writeObject(me);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class MouseEven implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			sendMouse(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			sendMouse(e);
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			sendMouse(arg0);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			sendMouse(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			sendMouse(e);
		}
	}

	private class Wheel implements MouseWheelListener{
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			try {
				oos.writeObject(e);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}

	private class MouseMove implements MouseMotionListener{

		@Override
		public void mouseDragged(MouseEvent e) {
			sendMouse(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			sendMouse(e);
		}
		
	}
}

