package info.xonix;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
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

        try (PrintWriter writer = resp.getWriter()) {

            if (vk == null) {
                resp.setContentType("text/html");
                resp.setStatus(400);
                writer.println("Please provide vk param!");
                writer.flush();
                return;
            }

//            resp.setContentType("application/rss+xml");
            resp.setContentType("text/plain");

            String vkWallPageText = Util.receiveUrlText("https://vk.com/" + vk,
                    "windows-1251",
                    Collections.singletonMap("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"));

            Document document = Jsoup.parse(vkWallPageText);

            Elements audioDivs = document.select("div.audio[id^=\"audio\"]");
//            Elements elts = document.select("input[type=\"hidden\"][id^=\"audio_info\"]");

            for (Element div : audioDivs) {
                String mp3url = div.select("input[type=\"hidden\"][id^=\"audio_info\"]").first().val();
                String name = div.select("div.title_wrap").first().text().trim();
                writer.println(mp3url);
                writer.println(name);
                writer.println();
            }

            writer.flush();
        }
    }
}
