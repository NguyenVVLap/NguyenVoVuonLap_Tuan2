package com.example.ActiveMQ;

import java.util.Date;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.BasicConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.ActiveMQ.data.Person;
import com.example.ActiveMQ.helper.XMLConvert;
@SpringBootApplication
public class ActiveMqApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActiveMqApplication.class, args);
		
		try {
			//config environment for JMS
			BasicConfigurator.configure();
			//config environment for JNDI
			Properties settings=new Properties();
			settings.setProperty(Context.INITIAL_CONTEXT_FACTORY,
					"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
			//create context
			Context ctx=new InitialContext(settings);
			//lookup JMS connection factory
			ConnectionFactory factory=
					(ConnectionFactory)ctx.lookup("ConnectionFactory");
			//lookup destination. (If not exist-->ActiveMQ create once)
			Destination destination=
					(Destination) ctx.lookup("dynamicQueues/thanthidet");
			//get connection using credential
			Connection con=factory.createConnection("admin","admin");
			//connect to MOM
			con.start();
			//create session
			Session session=con.createSession(
					/*transaction*/false,
					/*ACK*/Session.AUTO_ACKNOWLEDGE
					);
			//create producer
			MessageProducer producer = session.createProducer(destination);
			//create text message
			Message msg=session.createTextMessage("hello mesage from Lap");
			producer.send(msg);
			Person p=new Person(1001, "Th??n Th??? ?????t", new Date());
			String xml = new XMLConvert<Person>(p).object2XML(p);
			msg=session.createTextMessage(xml);
			producer.send(msg);
			//shutdown connection
			session.close();con.close();
			System.out.println("Finished...");
		} catch (Exception e) {
			System.out.println(e);
		}
		
		
		
		
		
		try {
			//thi???t l???p m??i tr?????ng cho JMS
			BasicConfigurator.configure();
			//thi???t l???p m??i tr?????ng cho JJNDI
			Properties settings=new Properties();
			settings.setProperty(Context.INITIAL_CONTEXT_FACTORY,
					"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
			//t???o context
			Context ctx=new InitialContext(settings);
			//lookup JMS connection factory
			Object obj=ctx.lookup("ConnectionFactory");
			ConnectionFactory factory=(ConnectionFactory)obj;
			//lookup destination
			Destination destination
			=(Destination) ctx.lookup("dynamicQueues/thanthidet");
			//t???o connection
			Connection con=factory.createConnection("admin","admin");
			//n???i ?????n MOM
			con.start();
			//t???o session
			Session session=con.createSession(
					/*transaction*/false,
					/*ACK*/Session.CLIENT_ACKNOWLEDGE
					);
			//t???o consumer
			MessageConsumer receiver = session.createConsumer(destination);
			//blocked-method for receiving message - sync
			//receiver.receive();
			//Cho receiver l???ng nghe tr??n queue, ch???ng c?? message th?? notify - async
			System.out.println("T?? was listened on queue...");
			receiver.setMessageListener(new MessageListener() {
				@Override
				//c?? message ?????n queue, ph????ng th???c n??y ???????c th???c thi
				public void onMessage(Message msg) {//msg l?? message nh???n ???????c
					try {
						if(msg instanceof TextMessage){
							TextMessage tm=(TextMessage)msg;
							String txt=tm.getText();
							System.out.println("Nh???n ???????c "+txt);
							msg.acknowledge();//g???i t??n hi???u ack
						}
						else if(msg instanceof ObjectMessage){
							ObjectMessage om=(ObjectMessage)msg;
							System.out.println(om);
						}
						//others message type....
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			System.out.println(e);
		}
	};
		
		
	
}
