import java.util.ArrayList;;
import Quote.*;

public class GeneticAlg {

	static boolean diag = true;
	static String SYMBOL = "AAPL";
	static OutputSize outputSize = OutputSize.full;
	static int popSize = 20;
	static int daysBack = 500;
	static int period = 10;
	static ArrayList<TimeSeriesDaily> stocks;

	public static void main (String[] args) {
		//Get Stock Prices
		print("Fetching Stock Prices...");
		stocks = DataFetcher.getTimeSeriesDaily(SYMBOL, outputSize);

		//Initialize list of Species
		print("Initialzing population...");
		ArrayList<Specie> population = initPop(popSize, daysBack);

		//Evaluate Fitness
		print("Evaluating fitness...");
		for (Specie s : population)
			s.fitness = getFitness(s);
		
		for (Specie s : population)
			System.out.println(s);
	}
	public static double getMovingAverage(int theta) {
		double sum = 0;
		for (int i = 0; i < theta; i++) {
			sum += stocks.get(period + i).getClose();	
		}

		return sum / theta;
	}

	public static double getFitness(Specie s) {
		int t1 = s.theta1;
		int t2 = s.theta2;
		
		int buyIndicator = getMovingAverage(t1) > getMovingAverage(t2) ? 1 : -1;
		double fitness = 1.0;

		for (int day = 1; day < period; day++) {
			fitness *= 1 + buyIndicator * ((stocks.get(day).getClose() - stocks.get(day - 1).getClose()) / stocks.get(day - 1).getClose());		
		}
		
		return fitness;		
	}

	public static ArrayList<Specie> initPop(int popSize, int daysBack) {
		ArrayList<Specie> returnlist = new ArrayList<Specie>();
		for (int i = 0; i < popSize; i++) {
			int theta1 = (int) (Math.random() * (daysBack - 1));
			int theta2 = (int) (Math.random() * (daysBack - theta1) + theta1);

			returnlist.add(new Specie(theta1, theta2));
		}

		return returnlist;
	}

	public static void print(String s) {
		if (diag)
			System.out.println(s);
	}
}


