/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package example;

import com.rabbitmq.client.Channel;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import junit.framework.TestCase;

/**
 *
 * @author Adirael
 */
public class CacheListenerTest extends TestCase {
    
    public CacheListenerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of contextInitialized method, of class CacheListener.
     *
    public void testContextInitialized() {
        System.out.println("contextInitialized");
        ServletContextEvent sce = null;
        CacheListener instance = new CacheListener();
        instance.contextInitialized(sce);
    }

    /**
     * Test of contextDestroyed method, of class CacheListener.
     *
    public void testContextDestroyed() {
        System.out.println("contextDestroyed");
        ServletContextEvent sce = null;
        CacheListener instance = new CacheListener();
        instance.contextDestroyed(sce);
    }

    /**
     * Test of retrieveIdsFromContext method, of class CacheListener.
     */
    public void testRetrieveIdsFromContext() {
        System.out.println("retrieveIdsFromContext");
       // ServletContext context = null;
        //CacheListener instance = new CacheListener();
       // String[] expResult = null;
        //String[] result = instance.retrieveIdsFromContext(context);
        System.out.println("Test not implemented.");
        assertEquals(1, 1);
    }

    /**
     * Test of createConsumer method, of class CacheListener.
     *
    public void testCreateConsumer() throws Exception {
        System.out.println("createConsumer");
        Channel channel = null;
        String queueName = "";
        ServletContext context = null;
        CacheListener instance = new CacheListener();
        instance.createConsumer(channel, queueName, context);
    }
//*/
}
