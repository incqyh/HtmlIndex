package ui;

import java.io.IOException;
import java.util.ArrayList;

import query.Query;
import query.QueryBuilder;
import query.WrongQueryException;
import webutil.WebIndex;
import webutil.WebDoc;

class Control {
    WebIndex wi;

    ArrayList<String> history;
    int presentIndex;

    static Control control = new Control();

    static public Control GetInstance() {
        return control;
    }

    Control() {
        history = new ArrayList<String>();
        wi = new WebIndex();
    }

    public void init() {

    }

    public ArrayList<String> getHistory()
    {
        return history;
    }

    public ArrayList<String> searchAction(String query) throws WrongQueryException
    {
        ArrayList<String> re = new ArrayList<String>();
        Query q = QueryBuilder.parse(query);
        for (WebDoc wd : q.matches(wi)) {
            re.add(wd.toString());
        }
        return re;
    }

    public String backAction()
    {
        if (presentIndex > 0)
        {
            presentIndex -= 1;
        }
        return history.get(presentIndex);
    }

    public String forwardAction()
    {
        if (presentIndex < history.size() - 1)
        {
            presentIndex += 1;
        }
        return history.get(presentIndex);
    }

    public void goAction(String url) throws IOException
    {
        for (String s : history) {
            if (s.equals(url))
                return;
        }

        WebDoc wd = new WebDoc(url);
        System.out.println(wd.toString());
        wi.add(wd);
        history.add(url);
        presentIndex = history.size() - 1;
    }
}