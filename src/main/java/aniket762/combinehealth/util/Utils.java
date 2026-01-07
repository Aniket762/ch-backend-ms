package aniket762.combinehealth.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Utils {

    public static String readFromUrl(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line).append(" ");
        }
        in.close();

        // remove html tag and normalize spaces
        String textOnly = sb.toString().replaceAll("<[^>]*>", " ");
        textOnly = textOnly.replaceAll("\\s+", " ").trim();
        return textOnly;
    }

    public static String normalize(String text) {
        if (text == null) return "";
        return text.replaceAll("\\s+", " ").trim();
    }

    public static String cleanHtml(String html) {
        if (html == null) return "";

        html = html.replaceAll("(?is)<script.*?>.*?</script>", " ");
        html = html.replaceAll("(?is)<style.*?>.*?</style>", " ");

        html = html.replaceAll("<[^>]+>", " ");

        html = html.replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">");

        html = html.replaceAll("(?i)function\\s*\\(", " ")
                .replaceAll("(?i)var\\s+", " ")
                .replaceAll("(?i)window\\.", " ");

        html = html.replaceAll("\\s+", " ").trim();

        return html;
    }
}
