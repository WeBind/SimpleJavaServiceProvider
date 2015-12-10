package example;

import com.rabbitmq.client.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@WebListener
public class CacheListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        try {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();

            channel.queueDeclare(Config.QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages");
            createConsumer(channel, context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        System.out.println("Context initialized");
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.removeAttribute("message");
    }


    public void createConsumer(Channel channel, final ServletContext context) throws IOException {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("received " + message);
                context.setAttribute("message", message);
            }
        };
        channel.basicConsume(Config.QUEUE_NAME, true, consumer);
    }

}
