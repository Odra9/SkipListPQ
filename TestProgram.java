import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//Class my entry
class MyEntry {
    private Integer key;
    private String value;
    public MyEntry(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public MyEntry(MyEntry e) {
        this.key = e.key;
        this.value = e.value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + " " + value;
    }
}

//Class SkipListPQ
class SkipListPQ {
    private static final int NegInf = Integer.MIN_VALUE, PosInf = Integer.MAX_VALUE;
    private static final String sentinel = "sentinel";

    private int size, h;
    private double alpha;
    private Random rand;
    private Node s;

    private class Node {
		Node(int key, String value) {
			e = new MyEntry(key, value);
		}

        @Override
        public String toString() {
            Node t = this;
            int levelT = 0;
		    while(t.above!=null) {
                t=t.above;
                levelT++;
            }
		    String str = e.toString() + " " + (levelT+1);
            return str; 
        }
		
		public MyEntry e;
		public Node next, prev, above, below;
	}

    public SkipListPQ(double alpha) {
        this.alpha = alpha;
        this.rand = new Random();
        this.size = 0; 
        this.h = 1;

        //level = 1
        s = new Node(NegInf, sentinel);
        s.next = new Node(PosInf, sentinel);
        s.next.prev = s;
        //level = 0
        s.below = new Node(NegInf, sentinel);
        s.below.next = new Node(PosInf, sentinel);
        s.below.next.prev = s.below;
        s.below.above = s;
        s.next.below = s.below.next;
        s.below.next.above = s.next;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size<=0;
    }

    public MyEntry min() {
        if(isEmpty()) return null;

	    Node t = s;
		while(t.below!=null) {
			t = t.below;
		}
		t=t.next;

        MyEntry min = new MyEntry(t.e);

        System.out.println(min.getKey() + " " + min.getValue());

		return min;  
    }

    public int insert(int newKey, String newValue) {    
        int l = generateEll(alpha, newKey);

        int oldH = h;

        //increase height
        while(l>=h) {
            Node newS = new Node(NegInf, sentinel);
			s.above = newS;
			newS.below = s;
			newS.next = new Node(PosInf, sentinel);
			newS.next.prev = newS;
			s.next.above = newS.next;
			newS.next.below = s.next;
			s = s.above;
			h++;
        }

        //SkipInsert()
        int steps = 1;
        Node t = s;
        int currH = this.h;
        Node prevLoop = null;
        while (t.below!=null) {
            t = t.below;
            if (currH <= oldH) steps++;     //new levels should not be counted
            currH--;
            while (t.next.e.getKey() <= newKey) {
                t = t.next;
                steps++;
            }

            if(currH<=l) {
                Node oldNext = t.next;
                Node newNode = new Node(newKey, newValue);
                
                t.next = newNode;
                newNode.next = oldNext;
                newNode.prev = t;
                oldNext.prev = newNode;

                newNode.above = prevLoop;
                if(prevLoop!=null) prevLoop.below = newNode;

                prevLoop = newNode;
            }
        }
        
        size++;
        return steps;
    }

    private int generateEll(double alpha_ , int key) {
        int level = 0;
        if (alpha_ >= 0. && alpha_< 1) {
          while (rand.nextDouble() < alpha_) {
              level += 1;
          }
        }
        else{
          while (key != 0 && key % 2 == 0){
            key = key / 2;
            level += 1;
          }
        }
        return level;
    }

    public MyEntry removeMin() {
	    if(isEmpty()) return null;
        
        Node t = s;
		while(t.below!=null) {
			t = t.below;
		}
		t=t.next;

        MyEntry min = new MyEntry(t.e);

        remove(t);

        //remove excess empty levels
        t = s;
        while(t.below!=null && t.below.next.e.getValue()==sentinel) {
            t=t.below;
            h--;
        }
        s = t;
        if(s.above!=null) {
            remove(s.above.next);
            remove(s.above);
            s.above = null;
            s.next.above = null;
        }
        
        System.gc(); //suggests to the system to free the memory allocated by now removed nodes

        size--;
        return min;
    }

    /* remove makes use of java garbage collector
       to remove a node we simply delete all reference pointers to it
       making it unreachable to the rest of the program, then once finished we call the garbage collector in removeMin()
    */
    private void remove(Node n) {
        if(n==null) return;

        if(n.e.getValue()!=sentinel) {
            n.prev.next = n.next;
            n.next.prev = n.prev;
        }

        n.prev = null;
        n.next = null;
        n.below = null;
        remove(n.above);
        n.above = null;
    }

    @Override
    public String toString() {
	    Node t = s;
        String str = "";
        while(t.below!=null) {
            t = t.below;
        }
        t = t.next;

        while (t.e.getValue()!=sentinel) {
            str += t.toString() + ", ";     
            t=t.next;
        }

        return str;
    } 

    public void print() {
        System.out.println(toString());
    }
}

//TestProgram

public class TestProgram {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TestProgram <file_path>");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String[] firstLine = br.readLine().split(" ");
            int N = Integer.parseInt(firstLine[0]);
            double alpha = Double.parseDouble(firstLine[1]);
            System.out.println(N + " " + alpha);

            SkipListPQ SLPQ = new SkipListPQ(alpha);

            int insN = 0, insSteps = 0;
            MyEntry min;
            for (int i = 0; i < N; i++) {
                String[] line = br.readLine().split(" ");
                int operation = Integer.parseInt(line[0]);

                switch (operation) {
                    case 0:
                        min = SLPQ.min();
                        break;
                    case 1:
                        min = SLPQ.removeMin();
                        break;
                    case 2:
                        int key = Integer.parseInt(line[1]);
                        String value = line[2];
			            insSteps += SLPQ.insert(key, value); 
                        insN++;
                        break;
                    case 3:
			            SLPQ.print(); 
                        break;
                    default:
                        System.out.println("Invalid operation code");
                        return;
                }
            }

            System.out.println(alpha + " " + SLPQ.size() + " " + insN + " " + (double)insSteps/insN);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}