package info.xonix;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        log.info("Received xspf URL: " + xspfUrl);

        resp.setCharacterEncoding("UTF-8");

        PrintWriter writer = resp.getWriter();

        if (xspfUrl == null) {
            resp.setContentType("text/html");
            resp.setStatus(400);
            writer.println("Please provide xspf URL!");
            writer.flush();
            return;
        }

        String xspfText = Util.receiveUrlText(xspfUrl);

        resp.setContentType("application/rss+xml");

        writer.write(xspfText);
        writer.flush();
    }
}
