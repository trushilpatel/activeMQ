package com.example.activeMQ_DEMO;

import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.jms.annotation.EnableJms;
import org.w3c.dom.Text;

import javax.jms.*;
import javax.naming.InitialContext;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@EnableJms
public class ActiveMqDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActiveMqDemoApplication.class, args);

//        messageProducerConsumerExample();
//        messagePublicSubscriberExample();
//        jms2Way();
//        priorityExample();
//        replyTo();
//        messageIdAndCorrelationId();
//        messageExpiration();
//        accessExpiredMessage();
//        customMessage();
//        byteMessage();
//        objectMessage();
//        messageListener();
//        filteringMessage();
    }

    public static void messagePublicSubscriberExample() {
        try {

            InitialContext initialContext = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession();
            Topic topic = (Topic) initialContext.lookup("topic/myTopic");
            MessageConsumer consumer1 = session.createConsumer(topic);
            MessageConsumer consumer2 = session.createConsumer(topic);


            MessageProducer producer = session.createProducer(topic);
            TextMessage message = session.createTextMessage("Nothing much... but something...");

            producer.send(message);

            connection.start();

            System.out.println(((TextMessage) consumer1.receive()).getText());
            System.out.println(((TextMessage) consumer2.receive()).getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void messageProducerConsumerExample() {
        try {
            InitialContext initialContext = new InitialContext();

            ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");
            MessageProducer messageProducer = session.createProducer(queue);
            TextMessage message = session.createTextMessage("Kem Chho Majama!!!");

            messageProducer.send(message);

            MessageConsumer messageConsumer = session.createConsumer(queue);
            connection.start();
            TextMessage textMessage = (TextMessage) messageConsumer.receive();
            System.out.println(textMessage.getText());
            System.out.println(textMessage.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void replyTo() {
        try {
            InitialContext initialContext = new InitialContext();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");
            Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

            ActiveMQJMSConnectionFactory activeMQJMSConnectionFactory = new ActiveMQJMSConnectionFactory();
            JMSContext jmsContext = activeMQJMSConnectionFactory.createContext();

            TextMessage message = jmsContext.createTextMessage();
            message.setJMSReplyTo(replyQueue);
            message.setText("With Reply To Queue");
            jmsContext.createProducer().send(queue, message);

            TextMessage receiveMessage = (TextMessage) jmsContext.createConsumer(queue).receive();
            System.out.println(receiveMessage.getText());
            System.out.println(receiveMessage.getJMSReplyTo());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void priorityExample() {
        try {
            InitialContext initialContext = new InitialContext();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");

            ActiveMQJMSConnectionFactory connectionFactory = new ActiveMQJMSConnectionFactory();
            JMSContext jmsContext = connectionFactory.createContext();

            jmsContext.createProducer().setPriority(9).send(queue, "Priority 9 message");
            jmsContext.createProducer().setPriority(1).send(queue, "Priority 1 message");
            jmsContext.createProducer().setPriority(5).send(queue, "Priority 5 message");
            JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);
            System.out.println(jmsConsumer.receiveBody(String.class));
            System.out.println(jmsConsumer.receiveBody(String.class));
            System.out.println(jmsConsumer.receiveBody(String.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void jms2Way() {
        try {
            InitialContext initialContext = new InitialContext();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");

            ActiveMQJMSConnectionFactory activeMQJMSConnectionFactory = new ActiveMQJMSConnectionFactory();
            JMSContext jmsContext = activeMQJMSConnectionFactory.createContext();

            jmsContext.createProducer().send(queue, "Hey it's JMS@2 Way...!!!");

            String message = jmsContext.createConsumer(queue).receiveBody(String.class);
            System.out.println(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void messageIdAndCorrelationId() {
        try {
            InitialContext initialContext = new InitialContext();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");

            ActiveMQJMSConnectionFactory activeMQJMSConnectionFactory = new ActiveMQJMSConnectionFactory();
            JMSContext jmsContext = activeMQJMSConnectionFactory.createContext();

            TextMessage textMessage = jmsContext.createTextMessage();
            textMessage.setText(textMessage.getJMSMessageID());

            jmsContext.createProducer().send(queue, textMessage);
            System.out.println(textMessage.getJMSMessageID());

            TextMessage receivedMessage = (TextMessage) jmsContext.createConsumer(queue).receive();
            System.out.println(receivedMessage.getJMSMessageID());
            receivedMessage.setJMSCorrelationID(receivedMessage.getJMSMessageID());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void messageExpiration() {
        try {
            InitialContext initialContext = new InitialContext();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");

            ActiveMQJMSConnectionFactory activeMQJMSConnectionFactory = new ActiveMQJMSConnectionFactory();
            JMSContext jmsContext = activeMQJMSConnectionFactory.createContext();

            TextMessage message = jmsContext.createTextMessage();
            message.setText("Message Expiration");
            message.setJMSExpiration(2000); // in ms
            jmsContext.createProducer().setTimeToLive(2000).send(queue, message);

            Thread.sleep(2100);
            TextMessage receiveMessage = (TextMessage) jmsContext.createConsumer(queue).receive();
            System.out.println(receiveMessage.getText());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void accessExpiredMessage() {
        try {
            InitialContext initialContext = new InitialContext();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");
            Queue expiryQueue = (Queue) initialContext.lookup("queue/ExpiryQueue");

            ActiveMQJMSConnectionFactory activeMQJMSConnectionFactory = new ActiveMQJMSConnectionFactory();
            JMSContext jmsContext = activeMQJMSConnectionFactory.createContext();

            TextMessage message = jmsContext.createTextMessage();
            message.setText("Message Expiration");
            jmsContext.createProducer().setTimeToLive(50).send(queue, message);

            Thread.sleep(100);
//            TextMessage receiveMessage = (TextMessage) jmsContext.createConsumer(queue).receive(1);
//            System.out.println(receiveMessage.getText());

            TextMessage expiryMessage = (TextMessage) jmsContext.createConsumer(expiryQueue).receive();
            System.out.println(expiryMessage.getText());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void customMessage() {
        try {
            InitialContext initialContext = new InitialContext();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");

            ActiveMQJMSConnectionFactory connectionFactory = new ActiveMQJMSConnectionFactory();
            JMSContext jmsContext = connectionFactory.createContext();

            JMSProducer producer = jmsContext.createProducer();
            JMSConsumer consumer = jmsContext.createConsumer(queue);

            TextMessage message = jmsContext.createTextMessage();
            message.setBooleanProperty("valid", true);
            message.setStringProperty("status", "Something...");

            producer.send(queue, message);

            TextMessage receiveMessage = (TextMessage) consumer.receive();
            System.out.println(receiveMessage.getBooleanProperty("valid"));
            System.out.println(receiveMessage.getStringProperty("status"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void byteMessage() {
        try {
            InitialContext initialContext = new InitialContext();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");

            ActiveMQJMSConnectionFactory connectionFactory = new ActiveMQJMSConnectionFactory();
            JMSContext jmsContext = connectionFactory.createContext();

            JMSProducer producer = jmsContext.createProducer();
            JMSConsumer consumer = jmsContext.createConsumer(queue);

            BytesMessage message = jmsContext.createBytesMessage();
            message.writeBoolean(true);
            message.writeUTF("argus");
            producer.send(queue, message);

            BytesMessage receiveMessage = (BytesMessage) consumer.receive();
            System.out.println(receiveMessage.readBoolean());
            System.out.println(receiveMessage.readUTF());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void objectMessage() {
        try {
            InitialContext initialContext = new InitialContext();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");

            ActiveMQJMSConnectionFactory connectionFactory = new ActiveMQJMSConnectionFactory();
            JMSContext jmsContext = connectionFactory.createContext();

            JMSProducer producer = jmsContext.createProducer();
            JMSConsumer consumer = jmsContext.createConsumer(queue);

            ObjectMessage message = jmsContext.createObjectMessage();
            Message m = new Message("Something", "everything...");

            message.setObject(m);
            producer.send(queue, message);

            ObjectMessage receiveMessage = (ObjectMessage) consumer.receive();
            Message ob = (Message) receiveMessage.getObject();
            System.out.println(ob.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void messageListener() {
        try {
            InitialContext initialContext = new InitialContext();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");

            ActiveMQJMSConnectionFactory connectionFactory = new ActiveMQJMSConnectionFactory();
            JMSContext context = connectionFactory.createContext();

            JMSConsumer consumer = context.createConsumer(queue);
            consumer.setMessageListener(new MyListener());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void durableConsumer() {

    }

    public static void sharedConsumer() {

    }

    public static void filteringMessage() {
        try {
            InitialContext initialContext = new InitialContext();
            Queue queue = (Queue) initialContext.lookup("queue/myQueue");

            ActiveMQJMSConnectionFactory connectionFactory = new ActiveMQJMSConnectionFactory();
            JMSContext context = connectionFactory.createContext();

            JMSConsumer consumer = context.createConsumer(queue, "valid LIKE '%T%'");

            String message = consumer.receiveBody(String.class);
            System.out.println(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
