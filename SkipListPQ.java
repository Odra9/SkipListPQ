import java.util.Random;

public class SkipListPQ {
	private static final int NegInf = Integer.MIN_VALUE, PosInf = Integer.MAX_VALUE;
	private static final String sentinel = "sentinel";

	private class Node {
		Node(int key, String value) {
			e = new Entry(key, value);
			//next=null; prev=null; above=null; below=null;
		}

		public int getKey() {return e.getKey();}
		public String getValue() {return e.getValue();}

		public String print() {return e.print();}
		
		public Entry e;
		public Node next, prev, above, below;
		//private int level;
	}

	SkipListPQ(double a) {
		this.a = a;
		this.h = 0;
		this.size = 2;

		//start node
		s = new Node(NegInf, sentinel);
		s.next = new Node(PosInf, sentinel);
		s.next.prev = s;
	}

	public int size() {
		return size;
	}

	public Node minNode() {
		Node minNode = s;
		while(minNode.below!=null) {
			minNode = minNode.below;
		}

		if (minNode.next.getValue()!=sentinel) minNode=minNode.next;

		return minNode;
	}

	public Entry min() {
		return minNode().e;
	}
	
	public int insert(int newKey, String newValue) {
		int l = generateEll(0.5f, newKey);
		//Node ins = new Node(newKey, newValue);

		//push s up
		while (l>=h) {
			Node newS = new Node(NegInf, sentinel);
			s.above = newS;
			newS.below = s;
			newS.next = new Node(PosInf, sentinel);
			newS.next.prev = newS;
			s.next.above = newS.next;
			newS.next.below = s.next;
			s = s.above;
			h++;
			size+=2;
		}

		Node p = s;
		//push p down
		for(int i=h-l-1;i>0;i--) {
			p = p.below;
		}
		
		int steps = 0;
		Node insAbove = null;
		do {
			steps++;
			p = p.below;
			while(p.next.getKey() <= newKey) {
				p = p.next;
				steps++;
			}

			Node oldNext = p.next;
			//insert between p and oldNext
			p.next = new Node(newKey, newValue);
			p.next.next = oldNext;
			oldNext.prev = p.next;
			p.next.above = insAbove;
			if(insAbove != null)
				insAbove.below = p.next;
			insAbove = p.next;
			size++;
		} while(p.below!=null && p.getKey()!=newKey);

		return steps;
	}

	private int generateEll(float α, int key) {
		//Random rand = new Random(key);
		Random rand = new Random();
		float f = rand.nextFloat();
		int level = 0;

		while (f<a) {
			level++;
			f = rand.nextFloat();
		}

		return level;
	}

	//returns (int steps, Node found)
	/*private Object[] skipSearch(int key) {
		Node p = s;
		int steps = 0;
		do
			steps++;
			while(p.next.getKey() < key) {
				p = p.next;
				steps++;
			}
		} while(p.below!=null && p.getKey()==key);

		if (p.getKey()!=key) p=null;
		
		return new Object[]{steps, p};
	}*/

	public Entry removeMin() {
		Node min = minNode();
		Entry eMin = min.e;
		remove(min);

		return eMin;
	}

	private void remove(Node n) {
		if(n.getValue()==sentinel)
			return;
		
		//sposto il puntatore in cima alla "pila"
		while(n.above!=null) {n=n.above;}

		n.prev.next = n.next;
		n.next.prev = n.prev;
		n.above = null;
		n.below.above=null;
		remove(n.below);
		n.below = null;
		n.e = null;
	}

	public String print() {
		Node t = s;
		while(t.below!=null){t=t.below;}
		String str = "";
		do {
			int countLevel = 1;
			Node p = t;
			while(p.above!=null) {
				p = p.above;
				countLevel++;
			}
			str += t.print() + " " + countLevel + "\n";
			t = t.next;
		}	while(t!=null);

		return str.substring(0,str.length()-1);
	}

	private double a;
	private int h, size;
	private Node s;
}