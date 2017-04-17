import Quote.TimeSeriesDaily;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.System.out;

/*
Created on 4/15/2017.
Implementation of genetic algorithm using binary strings to represent solutions

 */

public class BinaryGeneticAlg {
	int POPULATION_SIZE = 100;
	double MUTATION_RATE = 0.05;
	double CROSSOVER_RATE = 0.85;
	int MAX_GENERATIONS = 10000;
	int LOOKBACK_DAYS = 250;
	String stock;

	ArrayList<TimeSeriesDaily> quotes;


	public static void main(String[] cheese) {
		Scanner sc = new Scanner(System.in);
		out.println("Enter your stock ticker: ");
		String s = sc.nextLine();
		out.println("How far back in time do you want to look?");
		int days = Integer.parseInt(sc.nextLine());
		ArrayList<TimeSeriesDaily> quotes = DataFetcher.getTimeSeriesDaily(s, OutputSize.full);
		BinaryGeneticAlg alg = new BinaryGeneticAlg(s, quotes, 100, 0.01, 0.85, 1000, days);
		out.println(alg.profit(new BinaryGeneticAlg.Individual(10, 30)));
		out.println(quotes.get(alg.LOOKBACK_DAYS - 1));
		alg.simulate();
	}


	public BinaryGeneticAlg(String s, ArrayList<TimeSeriesDaily> list) {
		stock = s;
		quotes = list;
	}

	public BinaryGeneticAlg(String s, ArrayList<TimeSeriesDaily> list, int populationSize, double mutationRate, double crossoverRate, int maxGenerations, int lookbackDays) {
		this(s, list);
		POPULATION_SIZE = populationSize;
		MUTATION_RATE = mutationRate;
		CROSSOVER_RATE = crossoverRate;
		MAX_GENERATIONS = maxGenerations;
		LOOKBACK_DAYS = lookbackDays;

	}

	public Individual simulate() {

		if (quotes.size() == 0) { //hasn't been initialized, throw an exception
			throw new RuntimeException("Unable to find stock quotes!");
		}

		ArrayList<Individual> pool = new ArrayList<>(POPULATION_SIZE);

		//generate initial population
		for (int i = 0; i < POPULATION_SIZE; i++) {
			int smallerSMALength = randInt(1, Individual.MAX_SMA_LENGTH - 1);
			int largerSMALength = randInt(smallerSMALength + 1, Individual.MAX_SMA_LENGTH);
			pool.add(new Individual(smallerSMALength, largerSMALength));
		}

		//actually start genetic algorithm
		for (int gen = 0; gen < MAX_GENERATIONS; gen++) {
			double totalFitness = 0; //this is used for roulette wheel sampling from the gene pool
			//check profit from every individual in the gene pool
			for (Individual i : pool) {
				i.fitness = profit(i);
				totalFitness += i.fitness;
			}
			//make the next generation
			ArrayList<Individual> tempPool = new ArrayList<>(POPULATION_SIZE);
			int cPop = 0;
			while (cPop < POPULATION_SIZE) {
				cPop += 2;

//
//				Individual offspring1 = rouletteSample(totalFitness, pool);    //sample from current population
//				Individual offspring2 = rouletteSample(totalFitness, pool);
//
				Individual offspring1 = maxSample(pool, 1);    //sample from current population
				Individual offspring2 = maxSample(pool, 2);

				crossover(offspring1, offspring2, CROSSOVER_RATE);    //cross over their chromosomes
				mutate(offspring1, MUTATION_RATE);        //mutate each one
				mutate(offspring2, MUTATION_RATE);
				tempPool.add(offspring1);    //add them to the temp gene pool
				tempPool.add(offspring2);
			}

			if (pool.size() != tempPool.size()) {
				throw new RuntimeException("Gene pool sizes are different");
			}
			pool.clear();    //delete the previous generation
			pool.addAll(tempPool); //add the current generation
		}
		//so now we have run the genetic algorithm MAX_GENERATIONS times
		//we find the solution with the max fitness
		for (Individual i : pool) {
			i.fitness = profit(i);
		}
		Individual best = pool.parallelStream().max((l, r) -> (l.fitness - r.fitness) >= 0 ? -1 : 1).orElse(null);
		//and print it
		out.println(
				"After a genetic algorithm search with "
						+ MAX_GENERATIONS + " generations, the following solution gives the best investment performance over the past " + LOOKBACK_DAYS
						+ " days on stock " + stock
						+ ": " + best
						+ "\nIt would return a profit of $" + String.format("%.2f", best.fitness) + " per share with the crossing of moving averages technique."
		);
		return best;

	}

	/*so this is the fitness function for the genetic algorithm
	Basically what we do here is start with no position in the stock, at the date equal to today - LOOKBACK_DAYS
	then we go through each day until there is a positive moving average crossover (the shorter SMA crosses the larger SMA going up)
	We then hold a long position in the stock until there is a negative moving average crossover, at which point we sell. This does not take into account brokerage fees,
	or possible short positions, which may be implemented in a separate fitness function. Because moving averages have to be recalculated every time, we could only call the getSimpleMovingAverage method once and do
	iterative recalculation on it, however I believe that this would build floating-point arithmetic error and so I have chosen to call it each time.
	This is nothing like the profit function described in Kapoor et al, but that made no sense to me so I did this instead.
	 */
	double profit(Individual ind) {
		//the crossover and mutation operations sometimes create values of theta1 greater than theta2, which is of course nonsense. this takes care of this,
		if (ind.getTheta1() >= ind.getTheta2()) {
			return 0;
		}
		boolean invested = false;
		int startOfBuyPeriod = 0;
		double returns = 0;

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
	double getSimpleMovingAverage(int date, int theta) {
		double ret = 0;
		for (int i = date; i < (date + theta); i++) {
			ret += (quotes.get(i).getClose());
		}
		return ret / (theta);
	}

	/*
	class that represents an individual possible solution in the genetic algorithm model. Encodes theta1 and theta2 as 8bit integers (for maximum SMA length of 512 days) concatenated together in the string called "binary" to allow crossover and mutation operations
	Theta1 and theta2 respectively represent the lengths of the short and long simple moving averages.
	 */

	public static class Individual {
		static final int MAX_SMA_LENGTH = 512;
		static final int CHROMOSOME_LENGTH = 16;
		String binary;
		double fitness;

		public Individual(int theta1, int theta2) {
			if (theta1 > 512 || theta2 > 512 || theta1 >= theta2) {
				throw new IllegalArgumentException();
			}
			binary = String.format("%8s", Integer.toBinaryString(theta1)).replace(' ', '0')
					+ String.format("%8s", Integer.toBinaryString(theta2)).replace(' ', '0');
		}

		public int getTheta1() {
			return Integer.parseInt(binary.substring(0, 8), 2);
		}

		public int getTheta2() {
			return Integer.parseInt(binary.substring(8, 16), 2);

		}

		@Override
		public String toString() {
			return "\nSmall SMA length: " + getTheta1() + " Large SMA length: " + getTheta2() + " Profit: " + String.format("%.2f", fitness);
		}
	}

	public static int randInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public static Individual rouletteSample(double totalFitness, ArrayList<Individual> population) {
		double slice = Math.random() * totalFitness;
		double currentFitness = 0;
		for (int i = 0; i < population.size(); i++) {
			currentFitness += population.get(i).fitness;
			if (currentFitness >= slice) {
				return population.get(i);
			}
		}
		return population.parallelStream().max((l, r) -> (l.fitness - r.fitness) >= 0 ? -1 : 1).orElse(null);
	}

	public static Individual maxSample(ArrayList<Individual> population, int rank) {
		Collections.sort(population, (l, r) -> (l.fitness - r.fitness) >= 0 ? -1 : 1);
		return population.get(population.size() - rank);
	}


	public static void crossover(Individual i1, Individual i2, double crossoverRate) {
		if (Math.random() < crossoverRate) {
			int crossoverPoint = (int) (Math.random() * Individual.CHROMOSOME_LENGTH);
			String t1 = i1.binary.substring(0, crossoverPoint) + i2.binary.substring(crossoverPoint, Individual.CHROMOSOME_LENGTH);
			String t2 = i2.binary.substring(0, crossoverPoint) + i1.binary.substring(crossoverPoint, Individual.CHROMOSOME_LENGTH);
			String t3 = i1.binary;
			String t4 = i2.binary;
			i1.binary = t1;
			i2.binary = t2;

			//the crossover and mutation operations sometimes create values of theta1 greater than theta2, which is of course nonsense. this takes care of that
			if (i1.getTheta1() >= i1.getTheta2()) {
				i1.binary = t3;
			}
			if (i2.getTheta1() >= i2.getTheta2()) {
				i2.binary = t3;
			}
		}
	}

	public static void mutate(Individual ind, double mutationRate) {
		String temp = ind.binary;
		char[] s = ind.binary.toCharArray();
		for (int i = 0; i < s.length; i++) {
			if (Math.random() < mutationRate) {
				if (s[i] == '0') {
					s[i] = '1';
				} else {
					s[i] = '0';
				}
			}
		}
		ind.binary = new String(s);

		//the crossover and mutation operations sometimes create values of theta1 greater than theta2, which is of course nonsense. this takes care of this,
		if (ind.getTheta1() >= ind.getTheta2()) {
			ind.binary = temp;
		}
	}

}
