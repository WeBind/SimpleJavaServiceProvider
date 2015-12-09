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
    public String sayHello(String name) {
        MessageContext msgCtx = svcCtx.getMessageContext();
        ServletContext ctx = (ServletContext)
                msgCtx.get(MessageContext.SERVLET_CONTEXT);


        return ctx.getAttribute("conf") + " " + name + ".";
    }

    @WebMethod()
    public String updateConf(String name) {
        MessageContext msgCtx = svcCtx.getMessageContext();
        ServletContext ctx = (ServletContext)
                msgCtx.get(MessageContext.SERVLET_CONTEXT);
        ctx.setAttribute("conf", name);
        return "OK";
    }
}
