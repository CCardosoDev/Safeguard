package api;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 *
 * @author claudia cardoso e joao silva
 */
public class HeaderHandler implements SOAPHandler<SOAPMessageContext> {

    private String userName = null;
    private String password = null;
    private String accessKey = null;

    public HeaderHandler(String userName, String password, String accessKey) {
        this.userName = userName;
        this.password = password;
        this.accessKey = accessKey;
    }

    public HeaderHandler(String accessKey) {
        this.accessKey = accessKey;
    }

    @Override
    public Set<QName> getHeaders() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext c) {
        Boolean outboundProperty = (Boolean) c.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outboundProperty.booleanValue()) {
            //SOAPMessage message = c.getMessage();
            try {

                SOAPEnvelope envelope = c.getMessage().getSOAPPart().getEnvelope();
                envelope.setPrefix("soapenv");
                envelope.removeNamespaceDeclaration("S");
                envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
                envelope.addNamespaceDeclaration("def", "http://services.sapo.pt/definitions");
                envelope.addNamespaceDeclaration("market", "http://services.sapo.pt/Metadata/Market");
                SOAPHeader header = envelope.getHeader();
                if (header == null) {
                    header = envelope.addHeader();
                }

                if (userName != null && password != null) {
                    SOAPElement ESBredentials = header.addChildElement("ESBCredentials", "def");
                    ESBredentials.addChildElement("ESBUsername", "def").addTextNode(userName);
                    ESBredentials.addChildElement("ESBPassword", "def").addTextNode(password);
                }

                header.addChildElement("ESBAccessKey", "market").addTextNode(accessKey);


                /*message.writeTo(System.out);
                 System.out.println("");*/

            } catch (SOAPException ex) {
                ex.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext c) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return true;
    }

    @Override
    public void close(MessageContext mc) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
