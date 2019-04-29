import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import query.Query;
import query.QueryBuilder;
import query.WrongQueryException;
import webutil.WebDoc;
import webutil.WebIndex;
import ui.View;

class WebTest
{
    static WebIndex testCreateWebIndex(String filename)
    {
        WebIndex wi = new WebIndex();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));  
            String buf = null;
            while ((buf = reader.readLine()) != null) {
                try{
                    buf = buf.trim();
                    if (buf.startsWith("file") || buf.startsWith("http"))
                    {
                        WebDoc wd = new WebDoc(buf);
                        System.out.println(wd.toString());
                        wi.add(wd);
                    }
                } catch (Exception e) {
                    System.out.println(String.format("Wrong url: %s", buf));
                    System.out.println(e.getMessage());
                    
                    e.printStackTrace();             
                    // StackTraceElement stackTraceElement= e.getStackTrace()[0]; 
                    // System.out.println("File="+stackTraceElement.getFileName()); 
                    // System.out.println("Line="+stackTraceElement.getLineNumber()); 
                    // System.out.println("Method="+stackTraceElement.getMethodName()); 
                }
            }
            reader.close();  

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return wi;
    }

    static ArrayList<Query> testCreateQueries(String filename)
    {
        ArrayList<Query> queries = new ArrayList<Query>();

        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));  
            String buf = null;
            while ((buf = reader.readLine()) != null) {
                try{
                    buf = buf.trim();
                    Query query = QueryBuilder.parse(buf);
                    System.out.println(query.toString());
                    queries.add(query);
                } catch (WrongQueryException e) {
                    // System.out.println(String.format("Wrong query: %s", buf));
                    System.out.println(e.getMessage());
                    
                    // e.printStackTrace();
                }
            }
            reader.close();  

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return queries;
    }

    public static void main(String args[]) {
        new View();
    }

    public static void main2(String[] args)
    {
        String urlFilename = "data/tests.txt";
        String queryFilename = "data/sampleQueryFileStage2.txt";
        // if (args.length != 2)
        // {
        //     System.out.println("Please input the file name!");
        //     return;
        // }
        // String urlFilename = args[0];
        // String queryFilename = args[0];
        System.out.println(String.format("Url file name: %s", urlFilename));
        System.out.println(String.format("Query file name: %s", queryFilename));

        System.out.println();
        System.out.println("Test create webindex with urls provided in the file:");
        WebIndex wi = testCreateWebIndex(urlFilename);
        System.out.println();
        System.out.println(wi.toString());
        System.out.println();
        System.out.println("Search string 'This is the local testing', and results are:");
        for (WebDoc wd : wi.getMatches("This is the local testing")) {
            System.out.println(wd.toString());
        } 

        System.out.println();
        System.out.println("Test create queries in the file:");
        ArrayList<Query> queries = testCreateQueries(queryFilename);
        for (Query query : queries) {
            System.out.println();
            System.out.println(String.format("Search result for: %s", query.toString()));
            for (WebDoc wd : query.matches(wi)) {
                System.out.println(wd.toString());
            }
        }
        System.exit(0);
    }
}