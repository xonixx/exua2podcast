package info.xonix;

import com.google.gson.Gson;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Serializer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: xonix
 */
public class Any2PodcastServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(Any2PodcastServlet.class.getName());

    private static Map<String, Map> podcastsMap = new ConcurrentHashMap<>();

    public static final Gson gson = new Gson();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Headers", "accept, content-type");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        Map json = gson.fromJson(new InputStreamReader(req.getInputStream(), "UTF-8"), Map.class);

        log.warning("uid: " + uid + ", json: " + json);

        podcastsMap.put(uid, json);

        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Headers", "accept, content-type");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletOutputStream outputStream = resp.getOutputStream();

        try {
            String uid = req.getParameter("uid");

            resp.setCharacterEncoding("UTF-8");

            Map podcastJson = podcastsMap.get(uid);

            if (podcastJson == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print("Not found: " + uid);
                printStream.flush();
                return;
            }

            String title = (String) podcastJson.get("title");
            String img = (String) podcastJson.get("img");
            String link = (String) podcastJson.get("link");
            Collection<Map> items = (Collection<Map>) podcastJson.get("items");

            resp.setContentType("application/rss+xml");

            Element rss = new Element("rss");
            rss.addNamespaceDeclaration("itunes", "http://www.itunes.com/dtds/podcast-1.0.dtd");
            rss.addAttribute(new Attribute("version", "2.0"));

            Element channel = new Element("channel");
            rss.appendChild(channel);

            if (title != null) {
                Element titleE = new Element("title");
                titleE.appendChild(title.trim());
                channel.appendChild(titleE);
            }

            if (link != null) {
                Element linkE = new Element("link");
                linkE.appendChild(link);
                channel.appendChild(linkE);
            }

            if (img != null) {
                Element imgE = new Element("itunes:image", "http://www.itunes.com/dtds/podcast-1.0.dtd");
                imgE.addAttribute(new Attribute("href", img));
                channel.appendChild(imgE);
            }

            for (Map item : items) {
                String name = (String) item.get("name");
                String url = (String) item.get("url");

                Element itemE = new Element("item");
                Element itemTitle = new Element("title");

                // tmp for troubleshoot
                name += " (" + url + ")";

                itemTitle.appendChild(name);
                itemE.appendChild(itemTitle);

                Element enclosure = new Element("enclosure");
                enclosure.addAttribute(new Attribute("type", "audio/mpeg"));
                enclosure.addAttribute(new Attribute("url", url));
                itemE.appendChild(enclosure);

                channel.appendChild(itemE);
            }

            Serializer serializer = new Serializer(outputStream, "UTF-8");
            serializer.setIndent(4);
            serializer.setMaxLength(0);
            serializer.write(new nu.xom.Document(rss));

            outputStream.flush();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("text/html");
            PrintStream printStream = new PrintStream(outputStream);
            printStream.println("<H1>Error :(</H1>");
            printStream.print(e.getMessage());
            printStream.flush();
        }
    }
}
