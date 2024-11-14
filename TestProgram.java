public class TestProgram {
	public static void main(String[] args) {
		SkipListPQ s = new SkipListPQ(0.5);
		
		System.out.println("Struttura:\n " + s.print() + "\n\nDimensione: " + s.size() + "\nMin: " + s.min().print());
	}
}
