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
    public byte[] doStuff() throws InterruptedException {
        ServletContext ctx = retrieveSC();
        System.out.println("Doing stuff - delay:" + ctx.getAttribute(Config.CONFIG_DELAY));
        byte[] mess;
        long time2,
            time = System.currentTimeMillis();

        int delay = Integer.parseInt(
                ctx.getAttribute(Config.CONFIG_DELAY) != null ? ctx.getAttribute(Config.CONFIG_DELAY).toString() : "0"),
            messageSize = Integer.parseInt(
                ctx.getAttribute(Config.CONFIG_MSG_SIZE) != null ? ctx.getAttribute(Config.CONFIG_MSG_SIZE).toString() : "10");

        mess = generateMessage(messageSize);

        time2 = System.currentTimeMillis();

        Thread.sleep(delay - (time2 - time));

        return mess;
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
        mess[mess.length-1]='\0';
        return mess;
    }

    private ServletContext retrieveSC() {
        MessageContext msgCtx = svcCtx.getMessageContext();
        return (ServletContext)
                msgCtx.get(MessageContext.SERVLET_CONTEXT);
    }
}
