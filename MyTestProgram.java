public class TestProgram {
	public static void main(String[] args) {
		SkipListPQ s = new SkipListPQ(0.5);
		
		//System.out.println("Struttura:\n " + s.print() + "\n\nDimensione: " + s.size() + "\nMin: " + s.min().print() + "\n");

		s.insert(0, "a");
		s.insert(1, "b");

		//System.out.println("Struttura:\n " + s.print() + "\n\nDimensione: " + s.size() + "\n");

		s.insert(3, "c");
		s.insert(2, "d");
		s.insert(0, "e");

		System.out.println("Struttura:\n " + s.print() + "\n\nDimensione: " + s.size() + "\n");
	}
}
