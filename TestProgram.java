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
            int countLevel = 1;
		    while(t.above!=null) {
                countLevel++;
                t=t.above;
            }
		    String str = e.toString() + " " + countLevel;
            return str; 
        }
		
		public MyEntry e;
		public Node next, prev, above, below;
	}

    public SkipListPQ(double alpha) {
        this.alpha = alpha;
        this.rand = new Random();
        this.size = 0; 
        this.h = 0;

        s = new Node(NegInf, sentinel);
        s.next = new Node(PosInf, sentinel);
        s.next.prev = s;
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

		return t.e;  
    }

    public int insert(int newKey, String newValue) {    
        int l = generateEll(alpha, newKey);

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

        //SkipSearch()
        int steps = 1;
        Node t = s;
        for(int i=h-l;i>=0;i--) {
            t = t.below;
        }
        Node prevLoop = null;
        do {
            steps++;

            while (t.next.e.getKey() <= newKey) {
                t = t.next;
                steps++;
            }

            Node oldNext = t.next;
            Node newNode = new Node(newKey, newValue);
            t.next = newNode;

            newNode.next = oldNext;
            newNode.prev = t;
            oldNext.prev = newNode;

            newNode.above = prevLoop;
            if(prevLoop!=null) prevLoop.below = newNode;

            prevLoop = newNode;
            
            t = t.below;
        } while (t!=null);
        
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

        MyEntry e = t.e;

        remove(t);

        //remove useless levels
        t = s;
        while(t.below!=null && t.below.next.e.getValue()!=sentinel) {
            t=t.below;
            h--;
        }
        s = t;
        if(s.above!=null) {
            remove(s.above.next);
            remove(s.above);
            s.above = null;
        }
        //right sentinel
        //remove(t.above.next);
        //left sentinel
        //remove(t.above);
        t.above = null;
        t.next.above=null;
        
        size--;
        return e;
    }

    private void remove(Node n) {
        if(n==null) return;
        /*
        //if(n.prev!=null && n.next!=null) {
            n.prev.next = n.next;
            n.next.prev = n.prev;
        //}
        n.next = null;
        n.prev = null;
        remove(n.above);
        n.above=null;
        n.below=null;*/
        if(n.e.getValue()!=sentinel) {
            n.prev.next = n.next;
            n.next.prev = n.prev;
        } else {
            n.prev = null;
            n.next = null;
            n.below = null;
        }

        remove(n.above);
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
        //return (str.length() > 0) ? str.substring(0, str.length()-2) : "";
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
			            //System.out.println("Min: " + SLPQ.min().getValue());
                        min = SLPQ.min();
                        System.out.println(min.getKey() + " " + min.getValue());
                        break;
                    case 1:
			            //System.out.println("Rimosso: " + SLPQ.removeMin().getValue()); 
                        min = SLPQ.removeMin();
                        System.out.println(min.getKey() + " " + min.getValue());
                        break;
                    case 2:
                        int key = Integer.parseInt(line[1]);
                        String value = line[2];
			            insSteps += SLPQ.insert(key, value); 
                        insN++;
                        System.out.println("Inserito: ("+key+", "+value+")");
                        break;
                    case 3:
			            SLPQ.print(); 
                        break;
                    default:
                        System.out.println("Invalid operation code");
                        return;
                }
            }

            System.out.println(alpha + " " + SLPQ.size() + " " + insN + " " + insSteps/insN);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
