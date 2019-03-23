package Query;

import java.util.Set;

import WebUtil.WebDoc;
import WebUtil.WebIndex;

public interface Query {
    public Set<WebDoc> matches(WebIndex wind);
}