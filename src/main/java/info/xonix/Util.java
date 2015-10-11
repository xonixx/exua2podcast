package info.xonix;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/**
 * User: xonix
 * Date: 11.10.15
 * Time: 18:13
 */
public final class Util {
    public static String receiveUrlText(String url) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new URL(url).openStream(), "UTF-8");
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Unable to fetch: " + url, e);
        } finally {
            if (scanner != null)
                scanner.close();
        }
    }

    private Util() {
    }
}
