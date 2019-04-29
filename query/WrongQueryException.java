package query;

public class WrongQueryException extends Exception
{
    private static final long serialVersionUID = 1L;

    public WrongQueryException(String query) {
        super(String.format("The search pattern is wrong: %s", query));
    }
}