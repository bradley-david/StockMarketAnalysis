
import com.google.gson.*;
import com.google.gson.reflect.*;

import java.io.*;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ParseQuotes {
    static final String SITE = "http://www.alphavantage.co/query?";
    
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        Scanner apiscanner = new Scanner(new File("APIKEY.txt"));
        String API_KEY = apiscanner.nextLine();
        System.out.println("Enter your stock symbol: ");
        String symbol = sc.nextLine();
        String function = "TIME_SERIES_DAILY";
        String outputsize = "compact";
        
        
        try {
            URL url = new URL(SITE + "function=" + function + "&symbol=" + symbol + "&outputsize=" + outputsize + "&apikey=" + API_KEY);
            String s = readURL(url);
            //get rid of notation like "1. open" to be just "open"
            s = s.replaceAll("\"\\d\\. ", "\"");
            System.out.println(s);
            Gson gson = new Gson();
            
            Type type1 = new TypeToken<Map<String, JsonElement>>() {
            }.getType();
            Map<String, JsonElement> map1 = gson.fromJson(s, type1);
            Type type2 = new TypeToken<Map<String, TimeSeriesDaily>>() {
            }.getType();
            Map<String, TimeSeriesDaily> map2 = gson.fromJson(map1.get(LookupTable.lookupFunction(function)), type2);
            List<TimeSeriesDaily> list = new ArrayList<>(map2.size());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            for (Map.Entry<String, TimeSeriesDaily> e : map2.entrySet()) {
                e.getValue().date = sdf.parse(e.getKey());
                list.add(e.getValue());
            }
            
            System.out.println(list);
            
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        
    }
    
    /*Takes a URL and returns a string of all data from the URL*/
    public static String readURL(URL url) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
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
