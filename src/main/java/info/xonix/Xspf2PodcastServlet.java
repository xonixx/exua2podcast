package info.xonix;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * User: xonix
 * Date: 11.10.15
 * Time: 18:01
 */
public class Xspf2PodcastServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(Xspf2PodcastServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String xspfUrl = req.getParameter("xspf");
        String imgUrl = req.getParameter("img");
        log.info("Received xspf URL: " + xspfUrl);

        resp.setCharacterEncoding("UTF-8");

        try (InputStream xslInputStream = getClass().getClassLoader().getResourceAsStream("xspf2rss.xsl");
             PrintWriter writer = resp.getWriter()) {

            if (xspfUrl == null) {
                resp.setContentType("text/html");
                resp.setStatus(400);
                writer.println("Please provide xspf URL!");
                writer.flush();
                return;
            }

            resp.setContentType("application/rss+xml");

            String xspfText = Util.receiveUrlText(xspfUrl);

            if (imgUrl != null && !"".equals(imgUrl = imgUrl.trim())) {
                // inject imgUrl into xspf for xsl
                xspfText = xspfText.replace("<trackList>", "<img>" + imgUrl + "</img><trackList>");
            }

            writer.print(Util.xsltTransform(xspfText, xslInputStream));
            writer.flush();
        }
    }
}
