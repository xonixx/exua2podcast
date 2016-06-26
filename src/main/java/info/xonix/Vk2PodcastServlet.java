package info.xonix;

import nu.xom.Attribute;
import nu.xom.Serializer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * User: xonix
 */
public class Vk2PodcastServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(Vk2PodcastServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String vk = req.getParameter("vk");
        log.info("Received vk: " + vk);

        resp.setCharacterEncoding("UTF-8");

        try (ServletOutputStream outputStream = resp.getOutputStream()) {

            if (vk == null) {
                resp.setContentType("text/html");
                resp.setStatus(400);
                PrintWriter writer = new PrintWriter(outputStream);
                writer.println("Please provide vk param!");
                writer.flush();
                return;
            }

            resp.setContentType("application/rss+xml");
//            resp.setContentType("text/plain");

            String vkWallUrl = "https://vk.com/" + vk;
            String vkWallPageText = Util.receiveUrlText(vkWallUrl,
                    "windows-1251",
                    Collections.singletonMap("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"));

            Document document = Jsoup.parse(vkWallPageText);

            Elements audioDivs = document.select("div.audio[id^=\"audio\"]");
//            Elements elts = document.select("input[type=\"hidden\"][id^=\"audio_info\"]");

            nu.xom.Element rss = new nu.xom.Element("rss");
            rss.addNamespaceDeclaration("itunes", "http://www.itunes.com/dtds/podcast-1.0.dtd");
            rss.addAttribute(new Attribute("version", "2.0"));

            nu.xom.Element channel = new nu.xom.Element("channel");
            rss.appendChild(channel);

            nu.xom.Element title = new nu.xom.Element("title");
            title.appendChild("TODO");
            channel.appendChild(title);

            nu.xom.Element link = new nu.xom.Element("link");
            link.appendChild(vkWallUrl);
            channel.appendChild(link);

            nu.xom.Element img = new nu.xom.Element("itunes:image", "http://www.itunes.com/dtds/podcast-1.0.dtd");
            img.addAttribute(new Attribute("href", "TODO"));
//            img.addNamespaceDeclaration("itunes", "http://www.itunes.com/dtds/podcast-1.0.dtd");
            channel.appendChild(img);

            for (Element div : audioDivs) {
                String mp3url = div.select("input[type=\"hidden\"][id^=\"audio_info\"]").first().val();
                String name = div.select("div.title_wrap").first().text().trim();

                nu.xom.Element item = new nu.xom.Element("item");
                nu.xom.Element itemTitle = new nu.xom.Element("title");
                itemTitle.appendChild(name);
                item.appendChild(itemTitle);

                nu.xom.Element enclosure = new nu.xom.Element("enclosure");
                enclosure.addAttribute(new Attribute("type", "audio/mpeg"));
                enclosure.addAttribute(new Attribute("url", mp3url));
                item.appendChild(enclosure);

                channel.appendChild(item);

            }


            Serializer serializer = new Serializer(outputStream, "UTF-8");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.write(new nu.xom.Document(rss));

            outputStream.flush();
        }
    }
}
