package query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import webutil.WebDoc;
import webutil.WebIndex;

public class AndQuery implements Query {

    public Set<WebDoc> matches(WebIndex wind) {
        Set<WebDoc> re = null;
        for (Query q : queries) {
            Set<WebDoc> s = q.matches(wind);
            if (re == null)
            {
                re = s;
            }
            else
            {
                re.retainAll(s);
            }
        }
        return re != null? re : new HashSet<WebDoc>();
    }

    ArrayList<Query> queries;

    public AndQuery(ArrayList<Query> qs)
    {
        queries = qs;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("and(");
        for (Query q : queries) {
            sb.append(q.toString());
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(')');
        return sb.toString();
    }
}