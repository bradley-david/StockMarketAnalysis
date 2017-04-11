
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;

public class ParseQuotes {
    static final String SITE = "http://www.alphavantage.co/query?";
    
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        Scanner apiscanner = new Scanner(new File(System.getProperty("user.dir").substring(0, 1) + ":\\Other API Keys" + "\\alphavantage.txt"));
        String API_KEY = apiscanner.nextLine();
        System.out.println("Enter your stock symbol: ");
        String symbol = sc.nextLine();
        String function = "TIME_SERIES_DAILY";
        String outputsize = "compact";
        
        
        try {
            URL url = new URL(SITE + "function=" + function + "&symbol=" + symbol + "&outputsize=" + outputsize + "&apikey=" + API_KEY);
            Scanner jsonScanner = new Scanner(url.openStream());
            String ret = jsonScanner.useDelimiter("\\\\ZZZZZ").next();
            //    System.out.println(ret);
            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(ret).getAsJsonObject();
            JsonObject series = root.get("Time Series (Daily)").getAsJsonObject();
            String ser = series.toString();
            ser = "[" + ser.substring(1, ser.length() - 1) + "]";
            out.println(ser);
            
            out.println(ser);
            series = parser.parse(ser).getAsJsonObject();
            for (JsonElement element : series.getAsJsonArray()) {
                out.println(element);
            }
            List<TimeSeriesDaily> list = new ArrayList();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
