package webutil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
// import javax.net.ssl.HttpsURLConnection;

public class Common {
    public static String readToString(File file) throws IOException {
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(file);
        in.read(filecontent);
        in.close();
        return new String(filecontent);
    }

    static public String sendGet(String url) throws IOException {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        // int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    static ArrayList<String> split(String raw)
    {
        ArrayList<String> re = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for (char c : raw.toLowerCase().toCharArray()) {
            if (c >= 'a' && c <= 'z')
            {
                sb.append(c);
            }
            else
            {
                if (sb.length() != 0)
                {
                    re.add(sb.toString());
                    sb.delete(0, sb.length());
                }
            }
        }
        if (sb.length() != 0)
        {
            re.add(sb.toString());
        }
        return re;
    }
}