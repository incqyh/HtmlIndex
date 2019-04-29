package query;

import java.util.Set;

import webutil.WebDoc;
import webutil.WebIndex;

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