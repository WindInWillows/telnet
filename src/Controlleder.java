import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;

public class Controlleder{
	private ServerSocket ss = null;
	private DataOutputStream dos = null;
	private ObjectInputStream ois = null;
	private boolean connected = false; 
	private Socket client = null;
	public static void main(String[] args) {
		try {
			new Controlleder();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Controlleder() throws IOException {
		ss = new ServerSocket(8888);
		client = ss.accept();
		connected = true;
		ois = new ObjectInputStream(client.getInputStream());
		dos = new DataOutputStream(client.getOutputStream());
		new Thread(new Screencapture()).start();
		try {
			new Thread(new actionResponse()).start();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	class Screencapture implements Runnable{
		Robot robot = null;
		Rectangle rec;
		public Screencapture() throws IOException {
			try {
				this.robot = new Robot();
				Toolkit tk = Toolkit.getDefaultToolkit();
				Dimension ds = tk.getScreenSize();
				rec = new Rectangle(0,0,(int)ds.getWidth(),(int)ds.getHeight());
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			for(int i=0;i<1000;i++){
				BufferedImage image = robot.createScreenCapture(rec);
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(image, "jpeg", baos);
					byte[] b = baos.toByteArray();
					dos.writeInt(b.length);
					dos.write(b);
					dos.flush();
				} catch (IOException ee) {
					connected = false;
					System.exit(0);
					ee.printStackTrace();
				}
			}
		}
	}
	
	class actionResponse implements Runnable{
		private Robot r = null;
		
		public actionResponse() throws AWTException, IOException {
			r = new Robot();
		}
		@Override
		public void run() {
			while(connected){
				try {
					InputEvent input = (InputEvent)ois.readObject();
					handleEvent(input);
				} catch (ClassNotFoundException e) {
					connected = false;
				} catch (IOException e) {
					connected = false;
				}
			}
		}
		private void handleEvent(InputEvent input) {
			MouseEvent mous = null;
			KeyEvent key = null;
			MouseWheelEvent wheel = null;
			int mousebuttonmask = -100;
			
			switch (input.getID()) {
			case MouseEvent.MOUSE_MOVED:
				mous = (MouseEvent)input;
				r.mouseMove((int)mous.getX(), (int)mous.getY());
				break;
			case MouseEvent.MOUSE_CLICKED:
				mous = (MouseEvent)input;
				mousebuttonmask = getMouseClick(mous.getButton());
				r.mousePress(mousebuttonmask);
				break;
			case MouseEvent.MOUSE_RELEASED:
				mous = (MouseEvent)input;
				mousebuttonmask = getMouseClick(mous.getButton());
				r.mouseRelease(mousebuttonmask);
				break;
			case MouseEvent.MOUSE_WHEEL:
				wheel = (MouseWheelEvent)input;
				r.mouseWheel(wheel.getWheelRotation());
				break;
			case MouseEvent.MOUSE_PRESSED:
				mous = (MouseEvent)input;
				mousebuttonmask = getMouseClick(mous.getButton());
				r.mousePress(mousebuttonmask);
				break;
			case MouseEvent.MOUSE_DRAGGED:
				mous = (MouseEvent)input;
				r.mouseMove((int)(mous.getX()), (int)(mous.getY()));
				break;
			case KeyEvent.KEY_PRESSED:
				key = (KeyEvent)input;
				r.keyPress(key.getKeyCode());
				break;
			case KeyEvent.KEY_RELEASED:
				key = (KeyEvent)input;
				r.keyRelease(key.getKeyCode());
				break;
			default:
				System.out.println("unknow event:"+input.getID());
				break;
			}
		}
		private int getMouseClick(int button) {
			switch(button){
			case MouseEvent.BUTTON1:
				return InputEvent.BUTTON1_MASK;
			case MouseEvent.BUTTON2:
				return InputEvent.BUTTON2_MASK;
			case MouseEvent.BUTTON3:
				return InputEvent.BUTTON3_MASK;
			default :
				return -1;
			}
		}
	}
}
