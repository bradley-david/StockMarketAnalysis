import Quote.TimeSeriesDaily;

import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.in;
import static java.lang.System.out;

/**
 * Created on 4/16/2017.
 * Tests the BinaryGeneticAlgorithm class.
 */
public class TestGeneticAlgorithm {
	static int TESTS = 1000;

	public static void main(String[] cheese) {
		out.println("Enter your stock ticker: ");
		String s = new Scanner(in).nextLine();
		ArrayList<TimeSeriesDaily> quotes = DataFetcher.getTimeSeriesDaily(s, OutputSize.full);
		for (int i = 0; i < TESTS; i++) {
			BinaryGeneticAlg alg = new BinaryGeneticAlg(s, quotes, 100, 0.05, 0.85, 10000, 30);
			out.println(alg.profit(new BinaryGeneticAlg.Individual(10, 30)));
			alg.simulate();
		}
	}


}
