package Query;

import java.util.Set;

import WebUtil.WebDoc;
import WebUtil.WebIndex;

public class AtomicQuery implements Query {

    public Set<WebDoc> matches(WebIndex wind) {
        return wind.getMatches(s);
    }

    String s;

    public AtomicQuery(String s)
    {
        this.s = s;
    }

    @Override
    public String toString()
    {
        return s;
    }
}