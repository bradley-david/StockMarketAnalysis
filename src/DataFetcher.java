import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import static java.lang.System.err;

/*
    Class that fetches and parses JSON from Alpha Vantage for various functions
    
 */

public class DataFetcher {
    public static String API_KEY;
    
    static {
        try {
            API_KEY = "&apikey=" + new Scanner(new File("APIKEY.txt")).next();
        } catch (IOException ex) {
            err.println("API Key not found. Exiting program.");
            System.exit(2);
        }
    }
    
    public static final String ALPHA_VANTAGE_SITE = "http://www.alphavantage.co/query?";

//    public static ArrayList<TimeSeriesDaily> getTimeSeriesDaily() {
//
//    }
    
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
