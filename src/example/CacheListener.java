package example;

import com.google.gson.Gson;
import com.rabbitmq.client.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.*;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class CacheListener implements ServletContextListener {
    Logger lg = java.util.logging.Logger.getLogger("OMG");
    //When the context is initialized = when the ServiceProvider is deployed.
    public void contextInitialized(ServletContextEvent sce) {
        lg.log(Level.INFO, "started");

        ServletContext context = sce.getServletContext();
        //Retrieve the SP number from a file that was generated when deploying the app.
        //And set it as the current NUMBER.
        String[] ids = retrieveIdsFromContext(context);
        String id = ids[0],
                exchange = ids[1],
                bcast = ids[2],
                callback = ids[3];
        context.setAttribute(Config.NUMBER, ids[0]);
        context.setAttribute(Config.CALLBACK, ids[3]);
        //Now, connect to RabbitMQ using QUEUE_NAME as a queue
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();
            channel.exchangeDeclare(exchange,"direct");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchange, id);
            channel.queueBind(queueName, exchange, id);

            lg.log(Level.INFO, id + " [*] Waiting for messages");
            //Create the consumer associated to the queue
            createConsumer(channel, queueName, context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        lg.log(Level.INFO,id + "Context initialized");
        lg.log(Level.INFO, id + "My number is: " + id + " - " + exchange + " - " + bcast);
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
            ids[3] = reader.readLine();
        } catch (Exception e) {
            System.out.println("No number found");
            e.printStackTrace();
        }
        return ids;


    }

    public void createConsumer(Channel channel, String queueName, final ServletContext context) throws IOException {
        java.util.logging.Logger.getLogger("OMG").log(Level.INFO, "Consumer created");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = null;
                TypedMessage t = new TypedMessage("omg");
                try {
                    message = new String(body, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                java.util.logging.Logger.getLogger("OMG").log(Level.INFO, "Received message " + message);
                Gson g = new Gson();
                TypedMessage mess = g.fromJson(message, TypedMessage.class);
                if(mess.type.equals("config")) {
                    ConfigMessage conf = g.fromJson(message, ConfigMessage.class);
                    context.setAttribute(Config.CONFIG_DELAY, ""+conf.responseTime );
                    context.setAttribute(Config.CONFIG_MSG_SIZE, ""+conf.responseLength );
                } else {
                    //Go
                }
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
