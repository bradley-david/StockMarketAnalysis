public class Specie {
	public int theta1; //short num days to look back
	public int theta2; //long num days to look back

	public double fitness;

	public Specie(int t1, int t2) {
		theta1 = t1;
		theta2 = t2;
	}
}
