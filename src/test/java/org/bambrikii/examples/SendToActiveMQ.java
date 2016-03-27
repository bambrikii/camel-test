package org.bambrikii.examples;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Created by Alexander Arakelyan on 2016.03.27 13:06.
 */
public class SendToActiveMQ {
	public static void main(String[] args) throws Exception {
		CamelContext camelContext = new DefaultCamelContext();
//		camelContext.addComponent("activemq", activeMQComponent("vm://localhost?broker.persistent=false"));

//		JmsComponent component = new JmsComponent();
//		component.setConnectionFactory(new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false"));
//		camelContext.addComponent("activemq", component);

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");
		camelContext.addComponent("activemq", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

		camelContext.addRoutes(new RouteBuilder() {
			public void configure() {
				from("file://test").to("activemq:queue:test.queue");
			}
		});
		camelContext.addRoutes(new RouteBuilder() {
			public void configure() {
				from("activemq:queue:test.queue").to("file://test2");
			}
		});

		camelContext.start();

		ProducerTemplate template = camelContext.createProducerTemplate();
		for (int i = 0; i < 5; i++) {
			template.sendBody("file://test", "Test Message: " + i);
		}
		Thread.sleep(100000);
		while (System.in.read() < 0) {
			Thread.sleep(1);
		}
		camelContext.stop();
	}

	public class MyListener implements MessageListener {
		public void onMessage(Message jmsMessage) {
			// ...
		}
	}
}
