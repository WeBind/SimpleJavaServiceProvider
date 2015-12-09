package example;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

@WebService()
public class HelloWorldImpl {
    @Resource
    private WebServiceContext svcCtx;
    @WebMethod()
    public byte[] sendMessage() throws InterruptedException {
        ServletContext ctx = retrieveSC();
        long time = System.currentTimeMillis();

        int delay = Integer.parseInt(ctx.getAttribute("delay").toString());
        int messageSize = Integer.parseInt(ctx.getAttribute("messageSize").toString());

        byte[] mess = generateMessage(messageSize);

        long time2 = System.currentTimeMillis();

        Thread.sleep(delay - (time2 - time));

        return mess;
    }



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
