package frame;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.BasicConfigurator;

public class FrmMain extends JFrame {

	private JPanel contentPane;
	private static JTextArea textArea;

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
		try {
			//thiết lập môi trường cho JMS
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
					/*ACK*/Session.CLIENT_ACKNOWLEDGE
					);
			//tạo consumer
			Destination destination=(Destination) ctx.lookup("dynamicTopics/lequan");
			MessageConsumer receiver = session.createConsumer(destination);
			//receiver.receive();//blocked method
			//Cho receiver lắng nghe trên queue, chừng có message thì notify
			receiver.setMessageListener(new MessageListener() {
				@Override
				//có message đến queue, phương thức này được thực thi
				public void onMessage(Message msg) {//msg là message nhận được
					try {
						if(msg instanceof TextMessage){
							TextMessage tm=(TextMessage)msg;
							String txt=tm.getText();
							textArea.setText(txt);
							msg.acknowledge();//gửi tín hiệu ack
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public FrmMain() {
		setTitle("Subcriber");
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

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(10, 15, 510, 327);
		panel.add(textArea);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(269, 15, 2, 2);
		panel.add(scrollPane);
		
		
		
	}
}