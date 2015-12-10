package example;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

@WebService()
public class SoapProvider {
    @Resource
    private WebServiceContext svcCtx;

    /**
     * Run the service, according to the configuration
     */
    @WebMethod()
    public String doStuff() throws InterruptedException {
        System.out.println("Doing stuff");
        ServletContext ctx = retrieveSC();
        byte[] mess;
        long time2,
            time = System.currentTimeMillis();

        int delay = Integer.parseInt(
                ctx.getAttribute("delay") != null ? ctx.getAttribute("delay").toString() : "0"),
            messageSize = Integer.parseInt(
                ctx.getAttribute("messageSize") != null ? ctx.getAttribute("messageSize").toString() : "10");

        mess = generateMessage(messageSize);

        time2 = System.currentTimeMillis();

        Thread.sleep(delay - (time2 - time));

        return ""+ctx.getAttribute("message");//mess;
    }



    /**
     * Configures the WebService
     * @param messageSize   size of the answer message
     * @param delay         delay before the service responds
     */
    @WebMethod()
    public String configure(int messageSize, int delay) {
        ServletContext ctx = retrieveSC();
        ctx.setAttribute("delay", delay);
        ctx.setAttribute("messageSize", messageSize);

        return "OK";
    }




    private byte[] generateMessage(int messageSize) {
        byte[] mess = new byte[messageSize];
        for(int i=0;i < messageSize;i++) {
            mess[i] =(byte) ('a' +(i%26));
        }
        return mess;
    }

    private ServletContext retrieveSC() {
        MessageContext msgCtx = svcCtx.getMessageContext();
        return (ServletContext)
                msgCtx.get(MessageContext.SERVLET_CONTEXT);
    }
}
