import Quote.TimeSeriesDaily;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import static java.lang.System.out;

/**
 * Created on 4/16/2017.
 * Tests the BinaryGeneticAlgorithm class.
 */
public class TestGeneticAlgorithm {
	static int TESTS = 10;

	public static void main(String[] cheese) {
		Scanner sc = new Scanner(System.in);
		out.println("Enter your stock ticker: ");
		String s = sc.nextLine();
		out.println("How far back in time do you want to look?");
		int days = Integer.parseInt(sc.nextLine());
		ArrayList<TimeSeriesDaily> quotes = DataFetcher.getTimeSeriesDaily(s, OutputSize.full);
		ArrayList<BinaryGeneticAlg.Individual> results = new ArrayList<>();
		for (int i = 0; i < TESTS; i++) {
			BinaryGeneticAlg alg = new BinaryGeneticAlg(s, quotes, 100, 0.3, 0.65, 10000, days);
			out.println(alg.profit(new BinaryGeneticAlg.Individual(10, 30)));
			BinaryGeneticAlg.Individual ind = alg.simulate();
			results.add(ind);
		}
		Collections.sort(results, (l, r) -> (l.fitness - r.fitness) >= 0 ? -1 : 1);
		out.println("FINAL RESULT: " + results.get(results.size() - 1));
		out.println(results);
	}


}
