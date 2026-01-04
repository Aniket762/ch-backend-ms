package aniket762.combinehealth.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Utils {
    public static String readFromUrl(String urlString) throws IOException{
        URL url = new URL(urlString);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        StringBuilder sb = new StringBuilder();
        String inputLine;

        while((inputLine = in.readLine()) != null) {
            sb.append(inputLine).append(" ");
        }
        in.close();
        return sb.toString().trim();
    }
}
