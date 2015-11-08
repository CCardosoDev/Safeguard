package api;

import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

/**
 *
 * @author claudia cardoso e joao silva
 */
public class HeaderHandlerResolver implements HandlerResolver {

    private HeaderHandler headerHandler;

    public HeaderHandlerResolver(HeaderHandler headerHandler) {
        this.headerHandler = headerHandler;
    }

    @Override
    public List<Handler> getHandlerChain(PortInfo portInfo) {
        List<Handler> handlerChain = new ArrayList<Handler>();


        handlerChain.add(headerHandler);

        return handlerChain;
    }
}
