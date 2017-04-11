
import com.google.gson.*;

import java.io.*;
import java.io.FileNotFoundException;
import java.net.URL;
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
            //Testing
/*	    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

	    String inputLine;
	    while ((inputLine = in.readLine()) != null)
		System.out.println(inputLine);
	    in.close(); */
        }

	catch (Throwable ex) {
            ex.printStackTrace();
        }

    }
}
