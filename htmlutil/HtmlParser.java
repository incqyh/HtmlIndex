package htmlutil;

import java.util.HashSet;
import java.util.LinkedList;

public class HtmlParser
{
    enum ParserState
    {
        Data,
        TagOpen,
        TagOpenName,
        TagClose,
        TagCloseName,
        PropertyNameOpen,
        PropertyName,
        PropertyValueOpen,
        PropertyValueInDoubleQuotation,
        PropertyValueInSingleQuotation,
        Exclamation,
        Comment,
        DocType,
        CodeTag,
    }

    static HashSet<String> specialTag = new HashSet<String>(){
        private static final long serialVersionUID = 1L;
        {
            add("meta");
            add("hr");
            add("br");
            add("link");
            add("p");
        }
    };

    static HashSet<Character> legelNameCharacter = new HashSet<Character>(){
        private static final long serialVersionUID = 2L;
        {
            add(':');
            add('-');
            add('_');
        }
    };

    static HashSet<String> codeTag = new HashSet<String>(){
        private static final long serialVersionUID = 3L;
        {
            add("style");
            add("script");
        }
    };

    static public HtmlElement parse(String raw)
    {
        StringBuilder buf1 = new StringBuilder();
        StringBuilder buf2 = new StringBuilder();
        String propertyName = "";
        String propertyValue = "";

        LinkedList<HtmlElement> tmpElements = new LinkedList<HtmlElement>();
        HtmlElement tmpRoot = new HtmlElement();
        HtmlElement tmpChild = new HtmlElement();

        ParserState state = ParserState.Data;

        buf2.append("<tmp>");
        buf2.append(raw);
        buf2.append("</tmp>");

        for (char c : buf2.toString().toCharArray()){
            switch(state)
            {
                case Data:
                    if (c == '<')
                    {
                        state = ParserState.TagOpen;
                        tmpRoot.addValue(buf1.toString());
                        buf1.delete(0, buf1.length());
                    }
                    else
                    {
                        buf1.append(c);
                    }
                    break;
                case TagOpen:
                    if (Character.isLetter(c))
                    {
                        buf1.append(c);
                        state = ParserState.TagOpenName;
                    }
                    else if (c == '/')
                    {
                        state = ParserState.TagClose;
                    }
                    else if (c == '!')
                    {
                        state = ParserState.Exclamation;
                    }
                    else if (!Character.isWhitespace(c))
                    {
                        tmpChild.markIll();
                    }
                    break;
                case TagOpenName:
                    if (Character.isWhitespace(c))
                    {
                        if (codeTag.contains(buf1.toString()))
                        {
                            state = ParserState.CodeTag;
                        }
                        else
                        {
                            tmpChild.setTag(buf1.toString());
                            buf1.delete(0, buf1.length());
                            state = ParserState.PropertyNameOpen;
                        }
                    }
                    else if (c == '>')
                    {
                        tmpChild.setTag(buf1.toString());
                        buf1.delete(0, buf1.length());

                        if (specialTag.contains(tmpChild.getTag()))
                        {
                            tmpRoot.addChild(tmpChild);
                            tmpChild = new HtmlElement();
                        }
                        else 
                        {
                            tmpRoot.addChild(tmpChild);
                            tmpElements.push(tmpRoot);
                            tmpRoot = tmpChild;
                            tmpChild = new HtmlElement();
                        }
                        state = ParserState.Data;
                    }
                    else if (c == '/')
                    {
                        tmpChild.setTag(buf1.toString());

                        tmpRoot.addChild(tmpChild);
                        tmpElements.push(tmpRoot);
                        tmpRoot = tmpChild;
                        tmpChild = new HtmlElement();

                        state = ParserState.TagCloseName;
                    }
                    else if (Character.isLetter(c) || Character.isDigit(c))
                    {
                        buf1.append(c);
                    }
                    else
                    {
                        tmpChild.markIll();
                    }
                    break;
                case TagClose:
                    if (Character.isLetter(c))
                    {
                        buf1.append(c);
                        state = ParserState.TagCloseName;
                    }
                    else if (!Character.isWhitespace(c))
                    {
                        tmpChild.markIll();
                    }
                    break;
                case TagCloseName:
                    if (Character.isWhitespace(c))
                    {
                    }
                    else if (c == '>')
                    {
                        String tag = buf1.toString().trim();
                        buf1.delete(0, buf1.length());
                        if (tag.equals(tmpRoot.getTag()))
                        {
                            // tmpChild = tmpRoot;
                            tmpChild = new HtmlElement();
                            tmpRoot = tmpElements.pop();
                            state = ParserState.Data;
                        }
                        else if (specialTag.contains(tag))
                        {
                            state = ParserState.Data;
                        }
                        else
                        {
                            tmpRoot.markPartly();

                            boolean flag = false;
                            for (HtmlElement he : tmpElements)
                            {
                                if (he.tag.equals(tag))
                                {
                                    flag = true;
                                    break;
                                }
                            }

                            if (flag)
                            {
                                while (!tag.equals(tmpRoot.getTag()) && !tmpElements.isEmpty())
                                {
                                    tmpRoot.markPartly();
                                    tmpRoot = tmpElements.pop();
                                }
                                tmpChild = new HtmlElement();
                                tmpRoot = tmpElements.pop();
                                state = ParserState.Data;
                            }
                        }
                    }
                    else if (Character.isLetter(c) || Character.isDigit(c))
                    {
                        buf1.append(c);
                    }
                    else
                    {
                        tmpChild.markIll();
                    }
                    break;
                case Exclamation:
                    buf1.append(c);
                    if (buf1.toString().equals("--"))
                    {
                        state = ParserState.Comment;
                    }
                    else if (buf1.toString().trim().equals("doctype"))
                    {
                        state = ParserState.DocType;
                    }
                    else if (c == '>')
                    {
                        buf1.delete(0, buf1.length());
                        tmpChild.markIll();
                    }
                    break;
                case PropertyNameOpen:
                    if (Character.isLetter(c))
                    {
                        buf1.append(c);
                        state = ParserState.PropertyName;
                    }
                    else if (c == '>')
                    {
                        if (specialTag.contains(tmpChild.getTag()))
                        {
                            tmpRoot.addChild(tmpChild);
                            tmpChild = new HtmlElement();
                        }
                        else 
                        {
                            tmpRoot.addChild(tmpChild);
                            tmpElements.push(tmpRoot);
                            tmpRoot = tmpChild;
                            tmpChild = new HtmlElement();
                        }
                        buf1.delete(0, buf1.length());
                        state = ParserState.Data;
                    }
                    else if (c == '/')
                    {
                        if (!specialTag.contains(tmpChild.getTag()))
                        {
                            tmpRoot.addChild(tmpChild);
                            tmpElements.push(tmpRoot);
                            tmpRoot = tmpChild;
                            tmpChild = new HtmlElement();

                            buf1.append(tmpRoot.getTag());
                            state = ParserState.TagCloseName;
                        }
                    }
                    else if (!Character.isWhitespace(c))
                    {
                        tmpChild.markIll();
                    }
                    break;
                case PropertyName:
                    if (c == '=')
                    {
                        propertyName = buf1.toString();
                        buf1.delete(0, buf1.length());
                        state = ParserState.PropertyValueOpen;
                    }
                    else if (Character.isLetter(c) || Character.isDigit(c)
                        || legelNameCharacter.contains(c))
                    {
                        buf1.append(c);
                    }
                    else if (!Character.isWhitespace(c))
                    {
                        tmpChild.markIll();
                    }
                    break;
                case PropertyValueOpen:
                    if (c == '\'')
                    {
                        state = ParserState.PropertyValueInSingleQuotation;
                    }
                    else if (c == '\"')
                    {
                        state = ParserState.PropertyValueInDoubleQuotation;
                    }
                    else if (!Character.isWhitespace(c))
                    {
                        tmpChild.markIll();
                    }
                    break;
                case PropertyValueInDoubleQuotation:
                    if (c == '\"')
                    {
                        propertyValue = buf1.toString();
                        buf1.delete(0, buf1.length());
                        tmpChild.addAttribute(propertyName, propertyValue);
                        state = ParserState.PropertyNameOpen;
                    }
                    else
                    {
                        buf1.append(c);
                    }
                    break;
                case PropertyValueInSingleQuotation:
                    if (c == '\'')
                    {
                        propertyValue = buf1.toString();
                        buf1.delete(0, buf1.length());
                        tmpChild.addAttribute(propertyName, propertyValue);
                        state = ParserState.PropertyNameOpen;
                    }
                    else
                    {
                        buf1.append(c);
                    }
                    break;
                case Comment:
                    buf1.append(c);
                    if (buf1.toString().endsWith("-->"))
                    {
                        state = ParserState.Data;
                        buf1.delete(0, buf1.length());
                    }
                    break;
                case DocType:
                    if (c == '>')
                    {
                        buf1.delete(0, buf1.length());
                        state = ParserState.Data;
                    }
                    break;
                case CodeTag:
                    buf2.append(c);
                    if (buf2.toString().endsWith(String.format("</%s>", buf1.toString())))
                    {
                        state = ParserState.Data;
                        buf1.delete(0, buf1.length());
                        buf2.delete(0, buf1.length());
                    }
                    break;
            }
        }

        while (!tmpElements.isEmpty())
        {
            tmpRoot = tmpElements.pop();
            tmpRoot.markIll();
        }
        return tmpRoot.childElement.get(0);
    }
}