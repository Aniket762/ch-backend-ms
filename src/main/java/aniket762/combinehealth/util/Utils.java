package aniket762.combinehealth.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Utils {

    // ✅ read URL and strip HTML tags for prototype
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
        return text.toLowerCase().replaceAll("[^a-z0-9\\. ]", " ").replaceAll("\\s+", " ").trim();
    }
}
