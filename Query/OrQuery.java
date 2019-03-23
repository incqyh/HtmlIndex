package Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import WebUtil.WebDoc;
import WebUtil.WebIndex;

public class OrQuery implements Query {

    public Set<WebDoc> matches(WebIndex wind) {
        Set<WebDoc> re = new HashSet<WebDoc>();
        for (Query q : queries) {
            Set<WebDoc> s = q.matches(wind);
            re.addAll(s);
        }
        return re;
    }

    ArrayList<Query> queries;

    public OrQuery(ArrayList<Query> qs)
    {
        queries = qs;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("or(");
        for (Query q : queries) {
            sb.append(q.toString());
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(')');
        return sb.toString();
    }
}