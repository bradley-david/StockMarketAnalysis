import Quote.TimeSeriesDaily;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.err;

/*
    Class that fetches and parses JSON from Alpha Vantage for various functions
    api docs at http://www.alphavantage.co/documentation/
 */

public class DataFetcher {
    public static String API_KEY;
    
    //type aliases
    public static final Type MAP_STRING_TO_JSON_ELEMENT = new TypeToken<Map<String, JsonElement>>() {
    }.getType();
    public static final Type MAP_STRING_TO_TIMESERIESDAILY = new TypeToken<Map<String, TimeSeriesDaily>>() {
    }.getType();
    
    //one-time object initialization
    public static Gson gson = new Gson();
    public static SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyy-MM-dd");
    
    static {
        try {
            API_KEY = "&apikey=" + new Scanner(new File("APIKEY.txt")).next();
        } catch (IOException ex) {
            err.println("API Key not found. Exiting program.");
            System.exit(2);
        }
    }
    
    public static final String ALPHA_VANTAGE_SITE = "http://www.alphavantage.co/query?";
    
    
    public static ArrayList<TimeSeriesDaily> getTimeSeriesDaily(String symbol, OutputSize size) {
        String link = ALPHA_VANTAGE_SITE + "function=TIME_SERIES_DAILY" + "&symbol=" + symbol + "&outputsize=" + size + API_KEY;
        try {
            URL url = new URL(link);
            String s = readURL(url);
            //get rid of notation like "1. open" to be just "open"
            s = s.replaceAll("\"\\d\\. ", "\"");
            
            Map<String, JsonElement> map1 = gson.fromJson(s, MAP_STRING_TO_JSON_ELEMENT);
            Map<String, TimeSeriesDaily> map2 = gson.fromJson(map1.get(LookupTable.lookupFunction("TIME_SERIES_DAILY")), MAP_STRING_TO_TIMESERIESDAILY);
            ArrayList<TimeSeriesDaily> list = new ArrayList<>(map2.size());
            
            for (Map.Entry<String, TimeSeriesDaily> e : map2.entrySet()) {
                e.getValue().date = yyyymmdd.parse(e.getKey());
                list.add(e.getValue());
            }
            return list;
            
        } catch (MalformedURLException ex) {
            err.println("Fatal error constructing URL");
            ex.printStackTrace();
            System.exit(3);
        } catch (ParseException ex) {
            err.println("Fatal error Parsing date.");
            ex.printStackTrace();
            System.exit(3);
        }
        return null;
    }
    
    /*Takes a URL and returns a string of all data from the URL*/
    public static String readURL(URL url) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            String newLine;
            
            while ((newLine = reader.readLine()) != null) {
                buffer.append(newLine);
            }
            
            return buffer.toString();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
}
