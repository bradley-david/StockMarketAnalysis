import java.util.HashMap;


public class LookupTable {
    //maps between function names as in URL and fancy function names returned in JSON
    private static HashMap<String, String> functionLookupTable = new HashMap<>();
    
    static {
        functionLookupTable.put("TIME_SERIES_DAILY", "Time Series (Daily)");
    }
    
    public static String lookupFunction(String s) {
        return functionLookupTable.get(s);
    }
    
}
