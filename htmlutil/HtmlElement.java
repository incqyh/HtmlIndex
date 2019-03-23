package htmlutil;

import java.util.ArrayList;
import java.util.HashMap;

public class HtmlElement
{
    String tag = "";
    String value = "";
    String form = "well-formed";
    HashMap<String, String> attribute = new HashMap<String, String>();
    ArrayList<HtmlElement> childElement = new ArrayList<HtmlElement>();

    public void setTag(String tag)
    {
        this.tag = new String(tag);
    }

    public String getTag()
    {
        return tag;
    }

    public void setValue(String value)
    {
        this.value = new String(value);
    }

    public void addValue(String value)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.value);
        sb.append(" ");
        sb.append(value);
        this.value = sb.toString();
    }

    public String getValue()
    {
        return value;
    }

    public void markIll()
    {
        form = "ill-formed";
    }

    public void markPartly()
    {
        if (!form.equals("ill-formed"))
        {
            form = "partly well-formed";
        }
    }

    public String getForm()
    {
        String re = new String(form);
        if (!re.equals("ill-formed"))
        {
            for (HtmlElement he : childElement) {
                String tmp = he.getForm();
                if (tmp.equals("ill-formed"))
                {
                    re = new String(tmp);
                    break;
                }
                if (tmp.equals("partly well-formed"))
                {
                    re = new String(tmp);
                }
            }
        }
        return re;
    }

    public void addAttribute(String name, String value)
    {
        attribute.put(name, value);
    }

    public void addChild(HtmlElement he)
    {
        childElement.add(he);
    }

    public String getContent()
    {
        StringBuilder re = new StringBuilder();
        re.append(this.value);
        for (HtmlElement item : childElement) {
            re.append(" ");
            re.append(item.getContent());
        }

        return re.toString();
    }

    public ArrayList<HtmlElement> getElementByTagName(String tag)
    {
        return getElementByTagName(tag, 1<<31);
    }

    public ArrayList<HtmlElement> getElementByTagName(String tag, int depth)
    {
        ArrayList<HtmlElement> re = new ArrayList<HtmlElement>();
        if (depth == 0) return re;

        if (this.tag.equals(tag))
        {
            re.add(this);
        }
        for (HtmlElement item : childElement) {
            re.addAll(item.getElementByTagName(tag, depth - 1));
        }

        return re;
    }

    public ArrayList<HtmlElement> getElementByAttribute(String name, String value)
    {
        return getElementByAttribute(name, value, 1<<31);
    }

    public ArrayList<HtmlElement> getElementByAttribute(String name, String value, int depth)
    {
        ArrayList<HtmlElement> re = new ArrayList<HtmlElement>();
        if (depth == 0) return re;

        if (attribute.get(name) != null && attribute.get(name).equals(value))
        {
            re.add(this);
        }
        for (HtmlElement item : childElement) {
            re.addAll(item.getElementByAttribute(name, value, depth - 1));
        }

        return re;
    }

    public String getAttributeValue(String name)
    {
        return attribute.get(name);
    }
}