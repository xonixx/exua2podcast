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
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

/**
 * User: xonix
 */
public class Any2PodcastServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(Any2PodcastServlet.class.getName());

    public static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String data = req.getParameter("q");

        try (ServletOutputStream outputStream = resp.getOutputStream()) {

            if (data == null || "".equals(data = data.trim())) {
                resp.setContentType("text/html");
                resp.setStatus(400);
                PrintWriter writer = new PrintWriter(outputStream);
                writer.println("Please provide data!");
                writer.flush();
                return;
            }

            Map podcastJson = gson.fromJson(data, Map.class);
            String title = (String) podcastJson.get("title");
            String img = (String) podcastJson.get("img");
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

//            Element linkE = new Element("link");
//            linkE.appendChild("TBD");
//            channel.appendChild(linkE);

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
            serializer.setMaxLength(64);
            serializer.write(new nu.xom.Document(rss));

            outputStream.flush();
        }

        resp.setCharacterEncoding("UTF-8");
    }
}
