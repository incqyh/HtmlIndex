package webutil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import htmlutil.HtmlElement;
import htmlutil.HtmlParser;

public class WebDoc {
    String url;
    String rawHtml;
    String content;
    HashSet<String> keywords = new HashSet<String>();
    HashMap<String, Integer> contentWords = new HashMap<String, Integer>();
    String htmlState;

    public WebDoc(String url) throws IOException {
        this.url = url.trim();
        if (this.url.startsWith("file")) {
            getFile(this.url.substring(5));
        } else if (this.url.startsWith("http")) {
            getWeb(this.url);
        } else {
            throw (new IOException("Unrecognized url type!"));
        }

        init();
    }

    void getFile(String url) throws IOException
    {
        File file = new File(url);
        rawHtml = Common.readToString(file);
    }
    void getWeb(String url) throws IOException
    {
        rawHtml = Common.sendGet(url);
    }
    void init()
    {
        HtmlElement he = HtmlParser.parse(rawHtml.toLowerCase());
        htmlState = he.getForm();

        for (HtmlElement h : he.getElementByAttribute("name", "keywords")) {
            String keywordstr = h.getAttributeValue("content");
            if (keywordstr != null && keywordstr.length() != 0) {
                for (String s : Common.split(keywordstr)) {
                    keywords.add(s);
                }
            }
        }

        this.content = he.getContent().toLowerCase();
        for (String s : Common.split(this.content)) {
            if (contentWords.containsKey(s)) {
                contentWords.put(s, contentWords.get(s) + 1);
            } else {
                contentWords.put(s, 1);
            }
        }
    }

    public String getFullContent()
    {
        return rawHtml;
    }
    public HashSet<String> getKeywords()
    {
        return keywords;
    }

    public int search(String wd)
    {
        // String tmp = wd.trim();
        // for (int i : contentWords.get(Common.Split(tmp).get(0))){
        //     if (rawHtml.subSequence(i, i + tmp.length()).equals(tmp))
        //     {
        //         return i;
        //     }
        // }
        CharSequence tmp = wd;
        if (content.contains(tmp))
        {
            return 1;
        }
        return -1;
    }

    @Override
    public String toString() {
        if (!contentWords.isEmpty())
        {
            String max = null;
            String min = null;
            for (String s : contentWords.keySet()) {
                if (max == null) max = s;
                if (min == null) min = s;
                if (max.compareTo(s) < 0) max = s;
                if (min.compareTo(s) > 0) min = s;
            }
            return String.format("%s %d (%s-%s) %d %s", 
            url, contentWords.size(), min,
            max, keywords.size(), htmlState);
        }
        else
        {
            return String.format("%s %d %d %s", 
            url, contentWords.size(), keywords.size(), htmlState);
        }
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}