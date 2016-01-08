package example;

import com.google.gson.Gson;
import com.rabbitmq.client.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

@WebListener
public class CacheListener implements ServletContextListener {
    //When the context is initialized = when the ServiceProvider is deployed.
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        //Retrieve the SP number from a file that was generated when deploying the app.
        //And set it as the current NUMBER.
        String[] ids = retrieveIdsFromContext(context);
        context.setAttribute(Config.NUMBER, ids[0]);

        //Now, connect to RabbitMQ using QUEUE_NAME as a queue
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();
            channel.exchangeDeclare(ids[1],"direct");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, ids[1], ids[0]);
            channel.queueBind(queueName, ids[1], ids[2]);

            System.out.println(" [*] Waiting for messages");
            //Create the consumer associated to the queue
            createConsumer(channel, context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        System.out.println("Context initialized");
        System.out.println("My number is: " + ids[0]);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.removeAttribute(Config.CONFIG_DELAY);
        context.removeAttribute(Config.CONFIG_MSG_SIZE);
        context.removeAttribute(Config.NUMBER);
    }


    public String[] retrieveIdsFromContext(ServletContext context) {
        String[] ids = new String[3];
//        id, exchangeId, broadcastId;
        try {
            InputStream inputStream = context.getResourceAsStream("/number.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            ids[0] = reader.readLine();
            ids[1]= reader.readLine();
            ids[2]= reader.readLine();
        } catch (Exception e) {
            System.out.println("No number found");
            e.printStackTrace();
        }
        return ids;


    }

    public void createConsumer(Channel channel, final ServletContext context) throws IOException {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received message " + message);
                Gson g = new Gson();
                ConfigMessage mess = g.fromJson(message, ConfigMessage.class);
                context.setAttribute(Config.CONFIG_DELAY, mess.delay);
                context.setAttribute(Config.CONFIG_MSG_SIZE, mess.messageSize);
            }
        };
        channel.basicConsume(Config.QUEUE_NAME, true, consumer);
    }

}
