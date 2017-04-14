import java.io.File;
import java.io.FileNotFoundException;
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
        System.out.println("Do you want full or compact output?");
        String outputsize = sc.nextLine();
        System.out.println(DataFetcher.getTimeSeriesDaily(symbol, OutputSize.valueOf(outputsize.toLowerCase())));
    
    
    }
    
    
}
