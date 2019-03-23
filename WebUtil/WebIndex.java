package WebUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class WebIndex {
    HashMap<String, HashMap<WebDoc, Integer>> contentIndex = new HashMap<String, HashMap<WebDoc, Integer>>();
    HashMap<String, HashSet<WebDoc>> keywordIndex = new HashMap<String, HashSet<WebDoc>>();

    HashSet<WebDoc> htmlCollection = new HashSet<WebDoc>();

    public WebIndex()
    {

    }

    public void add(WebDoc doc)
    {
        htmlCollection.add(doc);
        for (String s : doc.contentWords.keySet()){
            if (!contentIndex.containsKey(s))
            {
                contentIndex.put(s, new HashMap<WebDoc, Integer>());
            }
            contentIndex.get(s).put(doc, doc.contentWords.get(s));
        }
        if (!doc.keywords.isEmpty())
        {
            for (String s : doc.keywords) {
                if (!keywordIndex.containsKey(s))
                {
                    keywordIndex.put(s, new HashSet<WebDoc>());
                }
                keywordIndex.get(s).add(doc);
            }
        }
    }

    public HashSet<WebDoc> getAllDocuments()
    {
        return htmlCollection;
    }

    public ArrayList<String> getDocStr()
    {
        ArrayList<String> re = new ArrayList<String>();
        for (WebDoc wd : htmlCollection) {
            re.add(wd.toString());
        }
        return re;
    }

    public Set<WebDoc> getMatches(String s)
    {
        HashSet<WebDoc> re = new HashSet<WebDoc>();
        ArrayList<String> sw = Common.split(s);

        HashMap<WebDoc, Integer> point = new HashMap<WebDoc, Integer>();

        for (String w : sw) {
            if (keywordIndex.containsKey(w))
            {
                for (WebDoc wd : keywordIndex.get(w)) {
                    if (point.keySet().contains(wd))
                    {
                        point.put(wd, point.get(wd) + 10);
                    }
                    else
                    {
                        point.put(wd, 10);
                    }
                }
            }
        }
        
        for (String w : sw) {
            if (contentIndex.containsKey(w))
            {
                for (WebDoc wd : contentIndex.get(w).keySet()) {
                    if (point.keySet().contains(wd))
                    {
                        point.put(wd, point.get(wd) + contentIndex.get(w).get(wd));
                    }
                    else
                    {
                        point.put(wd, contentIndex.get(w).get(wd));
                    }
                }
            }
            else
            {
                return re;
            }
        }

        TreeMap<Integer, WebDoc> tm = new TreeMap<Integer, WebDoc>(new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                return i1 < i2 ? 1 : -1;
            }
        });

        for (WebDoc wd : point.keySet()) {
            tm.put(point.get(wd), wd);
        }

        for (WebDoc wd: tm.values()) {
            if (point.get(wd) == 0) break;
            if (wd.search(s.toLowerCase()) >= 0)
            {
                re.add(wd);
            }
        }

        return re;
    }

    @Override
    public String toString()
    {
        return String.format("WebIndex over keywords contains %d words from %d documents", 
        contentIndex.size(), htmlCollection.size());
    }
}