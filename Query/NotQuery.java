package Query;

import java.util.HashSet;
import java.util.Set;

import WebUtil.WebDoc;
import WebUtil.WebIndex;

public class NotQuery implements Query {

    public Set<WebDoc> matches(WebIndex wind) {
        Set<WebDoc> s = q.matches(wind);
        Set<WebDoc> re = new HashSet<WebDoc>();
        for (WebDoc wd : wind.getAllDocuments()){
            if (!s.contains(wd))
            {
                re.add(wd);
            }
        }
        return re;
    }

    Query q;

    public NotQuery(Query q)
    {
        this.q = q;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("not(");
        sb.append(q.toString());
        sb.append(')');
        return sb.toString();
    }
}