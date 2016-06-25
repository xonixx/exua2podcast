package info.xonix;

import net.sf.saxon.TransformerFactoryImpl;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Scanner;

/**
 * User: xonix
 * Date: 11.10.15
 * Time: 18:13
 */
public final class Util {
    public static String receiveUrlText(String url) {
        return receiveUrlText(url, null);
    }
    public static String receiveUrlText(String url, Map<String,String> headers) {
        Scanner scanner = null;
        try {
            URLConnection connection = new URL(url).openConnection();
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            InputStream source = connection.getInputStream();
            scanner = new Scanner(source, "UTF-8");
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Unable to fetch: " + url, e);
        } finally {
            if (scanner != null)
                scanner.close();
        }
    }

    public static String xsltTransform(String xml, InputStream xslInputStream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        xsltTransform(xml, xslInputStream, baos);
        try {
            return baos.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void xsltTransform(String xml, InputStream xslInputStream, OutputStream outputStream) {
        try {
            /**
             * We use Saxon, because built-in Xalan is buggy in GAE:
             * javax.xml.transform.TransformerConfigurationException: Translet class loaded, but unable to create translet instance.
             */
//            TransformerFactory factory = TransformerFactory.newInstance();
            TransformerFactory factory = new TransformerFactoryImpl();

            Source xslt = new StreamSource(xslInputStream);
            Transformer transformer = factory.newTransformer(xslt);

            Source text = new StreamSource(new StringReader(xml));
            transformer.transform(text, new StreamResult(outputStream));
        } catch (TransformerException e) {
            throw new RuntimeException("Unable to do XSLT", e);
        }
    }

    private Util() {
    }
}
