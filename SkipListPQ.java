public class SkipListPQ {
	private static final int NegInf = Integer.MIN_VALUE, PosInf = Integer.MAX_VALUE;
	private static final String sentinel = "sentinel";

	private class Node {
		Node(int key, String value) {
			e = new Entry(key, value);
			next=null; prev=null; above=null; below=null;
		}

		public int getKey() {return e.getKey();}
		public String getValue() {return e.getValue();}

		public String print() {
			return e.print();
		}
		
		public Entry e;
		public Node next, prev, above, below;
		//private int level;
	}

	SkipListPQ(double a) {
		this.a = a;
		this.h = 0;

		//start node
		s = new Node(NegInf, sentinel);
		s.next = new Node(PosInf, sentinel);
		s.next.prev = s;
	}

	public int size() {
		int N = 1;
		Node t = s;
		while(t.next!=null) {
			N++;
			t = t.next;
		}
		return N;
	}

	public Node minNode() {
		Node minNode = s;
		Node t = s.next;
		while(t!=null) {
			if(t.getKey()<minNode.getKey())
				minNode=t;

			t=t.next;
		}
		return minNode;
	}

	public Entry min() {
		return minNode().e;
	}
	
	public void insert(int newKey, int newValue) {

	}

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
		String str = "";
		do {
			str += t.print() + "\n";
			t = t.next;
		}	while(t!=null);

		return str.substring(0,str.length()-1);
	}

	private double a;
	private int h;
	private Node s;
}