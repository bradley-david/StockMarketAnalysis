import Quote.TimeSeriesDaily;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.in;
import static java.lang.System.out;

/*
Created on 4/15/2017.
Implementation of genetic algorithm using binary strings to represent solutions

 */

public class BinaryGeneticAlg {
	static int POPULATION_SIZE = 100;
	static double MUTATION_RATE = 0.05;
	static double CROSSOVER_RATE = 0.85;
	static int MAX_GENERATIONS = 450;
	static int LOOKBACK_DAYS = 10;
	static int MAX_SMA_DAYS = 250;
	static final DecimalFormat SIXTEEN_ZEROS = new DecimalFormat("0000000000000000");
	static ArrayList<TimeSeriesDaily> quotes;


	public static void main(String[] cheese) {
		out.println("Enter your stock ticker: ");
		quotes = DataFetcher.getTimeSeriesDaily(new Scanner(in).nextLine(), OutputSize.full);
		LOOKBACK_DAYS = 30;
		Individual ind = new Individual(10, 20);
		out.println(profit(ind));

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
		int startOfBuyPeriod = 0;
		double returns = 0;
		out.println(quotes.get(LOOKBACK_DAYS - 1));
		for (int day = LOOKBACK_DAYS - 1; day >= 0; day--) {
			double smallSMA = getSimpleMovingAverage(day, ind.getTheta1());
			double largeSMA = getSimpleMovingAverage(day, ind.getTheta2());
			//signifies positive crossover - not currently holding stock, so must have previously had short SMA below long SMA 
			if (smallSMA > largeSMA && !invested) {
				invested = true; // buy the stock
				startOfBuyPeriod = day;

			} else if (smallSMA <= largeSMA && invested) {    // signifies negative SMA crossover - the shorter SMA is now lower than the long-run SMA, meaning the stock is trending downward
				invested = false; //sell the stock
				returns += quotes.get(day).getClose() - quotes.get(startOfBuyPeriod).getClose();
			}
		}

		//if we are invested today (at the end of the period we are analyzing), sell that stock
		if (invested) {
			returns += quotes.get(0).getClose() - quotes.get(startOfBuyPeriod).getClose();
		}
		return returns;
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

	/*
	class that represents an individual possible solution in the genetic algorithm model. Encodes theta1 and theta2 as 16bit integers concatenated together in the string called "binary" to allow crossover and mutation operations
	Theta1 and theta2 respectively represent the lengths of the short and long simple moving averages.
	 */

	private static class Individual {

		String binary;
		double fitness;

		public Individual(int theta1, int theta2) {
			if (theta1 > Short.MAX_VALUE * 2 - 1 || theta2 > Short.MAX_VALUE * 2 - 1) {
				throw new IllegalArgumentException();
			}
			binary = String.format("%16s", Integer.toBinaryString(theta1)).replace(' ', '0')
					+ String.format("%16s", Integer.toBinaryString(theta2)).replace(' ', '0');
		}

		int getTheta1() {
			return Integer.parseInt(binary.substring(0, 16), 2);
		}

		int getTheta2() {
			return Integer.parseInt(binary.substring(16, 32), 2);

		}


	}
}
