import Quote.TimeSeriesDaily;

import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.in;
import static java.lang.System.out;

/*
Created on 4/15/2017.
Implementation of genetic algorithm using binary strings to represent solutions

 */

public class BinaryGeneticAlg {
	static final int POPULATION_SIZE = 100;
	static final double MUTATION_RATE = 0.05;
	static final double CROSSOVER_RATE = 0.85;
	static final int MAX_GENERATIONS = 450;
	static final int LOOKBACK_DAYS = 250;
	static final int MAX_SMA_DAYS = 250;
	static ArrayList<TimeSeriesDaily> quotes;

	public static void main(String[] cheese) {
		out.println("Enter your stock ticker: ");
		quotes = DataFetcher.getTimeSeriesDaily(new Scanner(in).nextLine(), OutputSize.full);
		out.println(getSimpleMovingAverage(0, 50));
	}


	/*so this is the fitness function for the genetic algorithm
	Basically what we do here is start with no position in the stock, at the date equal to today - LOOKBACK_DAYS
	then we go through each day until there is a positive moving average crossover (the shorter SMA crosses the larger SMA going up)
	We then hold a long position in the stock until there is a negative moving average crossover, at which point we sell. This does not take into account brokerage fees,
	or possible short positions, which may be implemented in a separate fitness function. Because moving averages have to be recalculated every time, we could only call the getSimpleMovingAverage method once and do
	iterative recalculation on it, however I believe that this would build floating-point arithmetic error and so I have chosen to call it each time.
	 */
	static double profit(Individual ind) {
		boolean invested = false;
		boolean switchPosition = false;

		for (int day = LOOKBACK_DAYS - 1; day > 0; day--) {
			TimeSeriesDaily d1 = quotes.get(day);
			TimeSeriesDaily d2 = quotes.get(day + 1);

		}

		return 0;
	}

	/**
	 * @param date  actually the index in <code>quotes</code> we want to evaluate the SMA at
	 * @param theta number of days to look back for the SMA
	 * @return
	 */
	static double getSimpleMovingAverage(int date, int theta) {
		double ret = 0;
		for (int i = date; i < (date + theta); i++) {
			ret += (quotes.get(i).getClose());
		}
		return ret / (theta);
	}


	private static class Individual {
		String binary;
		double fitness;
	}
}
