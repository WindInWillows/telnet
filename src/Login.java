import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Login{
	private JFrame jf = null;
	private JPanel jp1 = new JPanel();
	private JPanel jp2 = new JPanel();
	private JLabel jl1 = new JLabel("被控端IP:");
	private JLabel jl2 = new JLabel("端口号: ");
	private JTextField jtf_ip = new JTextField(8);
	private JTextField jtf_port = new JTextField(8);
	private JButton jb = new JButton("连接");
	public static void main(String[] args) {
		new Login();
	}
	
	public Login(){
		jp1.add(jl1);
		jp1.add(jtf_ip);
		jp2.add(jl2);
		jp2.add(jtf_port);
		jtf_port.setText("8888");
		jp2.add(jb);
		jb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = jtf_ip.getText();
				String port = jtf_port.getText();
				if(ip == ""){}
				else {
					jf.setVisible(false);
					new Controller().start(ip,port);
				}
			}
		});
		jf = new JFrame("远程控制");
		jf.setLayout(new GridLayout(2,1));
		jf.add(jp1);
		jf.add(jp2);
		jf.setSize(200,150);
		jf.setLocationRelativeTo(null);
		jf.setResizable(false);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}

}
