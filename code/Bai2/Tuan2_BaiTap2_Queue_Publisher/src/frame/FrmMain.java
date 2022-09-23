package frame;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.BasicConfigurator;

public class FrmMain extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrmMain frame = new FrmMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Create the frame.
	 */
	public FrmMain() {
		setTitle("Publisher");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 566, 486);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)),
				"N\u1ED9i dung xem", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(10, 11, 530, 347);
		contentPane.add(panel);
		panel.setLayout(null);

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(10, 15, 510, 327);
		panel.add(textArea);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(269, 15, 2, 2);
		panel.add(scrollPane);

		JButton btnNewButton = new JButton("Send");
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnNewButton.setBounds(465, 369, 75, 67);
		contentPane.add(btnNewButton);

		JTextArea textArea_1 = new JTextArea();
		textArea_1.setBounds(77, 369, 373, 67);
		contentPane.add(textArea_1);

		JLabel lblNewLabel = new JLabel("Enter text");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 396, 57, 14);
		contentPane.add(lblNewLabel);
		
		
		btnNewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					//thiết lập môi trường cho JMS logging
					BasicConfigurator.configure();
					//thiết lập môi trường cho JJNDI
					Properties settings=new Properties();
					settings.setProperty(Context.INITIAL_CONTEXT_FACTORY,
							"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
					settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
					//tạo context
					Context ctx=new InitialContext(settings);
					//lookup JMS connection factory
					Object obj=ctx.lookup("TopicConnectionFactory");
					ConnectionFactory factory=(ConnectionFactory)obj;
					//tạo connection
					Connection con=factory.createConnection("admin","admin");
					//nối đến MOM
					con.start();
					//tạo session
					Session session=con.createSession(
							/*transaction*/false,
							/*ACK*/Session.AUTO_ACKNOWLEDGE
							);
					Destination destination=(Destination)
							ctx.lookup("dynamicTopics/lequan");
					//tạo producer
					MessageProducer producer = session.createProducer(destination);
					//Tạo 1 message
					Message msg=session.createTextMessage(textArea_1.getText().toString());
					//gửi
					producer.send(msg);
					textArea.setText("Sent: " + textArea_1.getText().toString());
					//shutdown connection
					session.close();
					con.close();
					System.out.println("Finished...");
				} catch (Exception e2) {
					System.out.println(e2);
				}
			}
		});
	}
}