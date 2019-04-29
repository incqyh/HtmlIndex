package query;

import java.util.Set;

import webutil.WebDoc;
import webutil.WebIndex;

public interface Query {
    public Set<WebDoc> matches(WebIndex wind);
}