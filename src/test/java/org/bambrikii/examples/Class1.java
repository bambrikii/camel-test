package org.bambrikii.examples;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;

/**
 * Created by Alexander Arakelyan on 2016.03.27 12:57.
 */
public class Class1 {
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			public void configure() {
				from("test-jms:queue:test.queue").to("file://test");
			}
		});

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
		// Note we can explicit name the component
		context.addComponent("test-jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

		ProducerTemplate template = context.createProducerTemplate();

		context.start();

		for (int i = 0; i < 10; i++) {
			template.sendBody("test-jms:queue:test.queue", "Test Message: " + i);
		}
	}
}
