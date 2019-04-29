package query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class QueryBuilder {
    static HashMap<String, Integer> Operator = new HashMap<String, Integer>(){
        private static final long serialVersionUID = 4L;

        {
            put("(", 0);
            put(")", 0);
            put("and", 3);
            put("or", 1);
            put("not", 7);
        }
    }; 

    public static Query parse(String q) throws WrongQueryException
    {
        String formatQ = q.trim().toLowerCase().replaceAll(" +", "").replaceAll("\t+", "");
        if (formatQ.contains("()"))
        {
            throw (new WrongQueryException(q));
        }

        if (q == null || q.length() == 0)
        {
            throw (new WrongQueryException(""));
        }

        if (!q.contains(","))
        {
            return parseDal(q);
        }
            // .replaceAll(" *and *", "and")
            // .replaceAll(" *or *", "or")
            // .replaceAll(" *not *", "not");
        return parsePn(formatQ);
    }

    static Query parsePn(String q) throws WrongQueryException
    {
        Query query = null;
        if (q.startsWith("and("))
        {
            ArrayList<Query> queries = new ArrayList<Query>();
            int bi = 4;
            int ei = bi;
            int cnt = 1;
            for (; ei < q.length(); ++ei)
            {
                if (q.charAt(ei) == '(') cnt += 1;
                if (q.charAt(ei) == ')') cnt -= 1;
                if (cnt == 1 && q.charAt(ei) == ',')
                {
                    queries.add(parsePn(q.substring(bi, ei)));
                    bi = ei + 1;
                    ei = bi;
                }
            }
            if (cnt != 0 || q.charAt(ei - 1) != ')')
            {
                throw (new WrongQueryException(q));
            }
            queries.add(parsePn(q.substring(bi, ei - 1)));

            query = new AndQuery(queries);
        }
        else if (q.startsWith("or("))
        {
            ArrayList<Query> queries = new ArrayList<Query>();
            int bi = 3;
            int ei = bi;
            int cnt = 1;
            for (; ei < q.length(); ++ei)
            {
                if (q.charAt(ei) == '(') cnt += 1;
                if (q.charAt(ei) == ')') cnt -= 1;
                if (cnt == 1 && q.charAt(ei) == ',')
                {
                    queries.add(parsePn(q.substring(bi, ei)));
                    bi = ei + 1;
                    ei = bi;
                }
            }
            if (cnt != 0 || q.charAt(ei - 1) != ')')
            {
                throw (new WrongQueryException(q));
            }
            queries.add(parsePn(q.substring(bi, ei - 1)));

            query = new OrQuery(queries);
        }
        else if (q.startsWith("not("))
        {
            int bi = 4;
            int ei = bi;
            int cnt = 1;
            for (; ei < q.length(); ++ei)
            {
                if (q.charAt(ei) == '(') cnt += 1;
                if (q.charAt(ei) == ')') cnt -= 1;
            }
            if (cnt != 0 || q.charAt(ei - 1) != ')')
            {
                throw (new WrongQueryException(q));
            }
            query = new NotQuery(parsePn(q.substring(bi, ei - 1)));
        }
        else
        {
            for (char c : q.toCharArray())
            {
                if (!Character.isLetter(c))
                {
                    throw (new WrongQueryException(q));
                }
            }
            query = new AtomicQuery(q);
        }
        return query;
    }

    static Query parseDal(String dal) throws WrongQueryException
    {
        try
        {
            return rpn2Query(dal2Rpn(dal));
        }
        catch(Exception e)
        {
            throw (new WrongQueryException(dal));
        }
    }

    public static String parseInfixForm(String dal) throws WrongQueryException {
        try
        {
            return rpn2Query(dal2Rpn(dal)).toString();
        }
        catch (Exception e)
        {
            throw new WrongQueryException(dal);
        }
    }

    // Direct Algebraic Logic
    // Reverse Polish Notation
    static ArrayList<String> dal2Rpn(String dal) throws WrongQueryException 
    {
        Stack<String> operator = new Stack<String>();
        ArrayList<String> rpn = new ArrayList<>();
        String tmp = dal.replace("(", " ( ").replace(")", " ) ").toLowerCase();
        for (String s : tmp.split(" +")) {
            if (!Operator.containsKey(s))
            {
                rpn.add(s);
            }
            else if (s.equals("("))
            {
                operator.push(s);
            }
            else if (s.equals(")"))
            {
                while (!operator.empty() && !operator.peek().equals("(") )
                {
                    rpn.add(operator.pop());
                }
                if (operator.empty())
                {
                    throw(new WrongQueryException(dal));
                }
                operator.pop();
            }
            else
            {
                while (!operator.empty() && Operator.get(operator.peek()) > Operator.get(s))
                {
                    rpn.add(operator.pop());
                }
                operator.push(s);
            }
        }
        while (!operator.empty())
        {
            if (operator.peek().equals("("))
            {
                throw(new WrongQueryException(dal));
            }
            rpn.add(operator.pop());
        }
        return rpn;
    }

    private static Query rpn2Query(ArrayList<String> rpn) throws Exception
    {
        Stack<Query> operand = new Stack<Query>();
        for (String op : rpn) {
            if (Operator.containsKey(op))
            {
                if (op.equals("and"))
                {
                    ArrayList<Query> queries = new ArrayList<Query>();
                    queries.add(operand.pop());
                    queries.add(operand.pop());
                    operand.push(new AndQuery(queries));
                }
                else if (op.equals("or"))
                {
                    ArrayList<Query> queries = new ArrayList<Query>();
                    queries.add(operand.pop());
                    queries.add(operand.pop());
                    operand.push(new OrQuery(queries));
                }
                else if (op.equals("not"))
                {
                    operand.push(new NotQuery(operand.pop()));
                }
            }
            else
            {
                operand.push(new AtomicQuery(op));
            }
        }
        if (operand.size() != 1)
        {
            throw(new Exception());
        }
        return operand.pop();
    }
}