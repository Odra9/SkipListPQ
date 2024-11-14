public class Entry {
    Entry(int key, String value) {
        this.key=key;
        this.value=value;
    }

	public int getKey() {return key;}
	public String getValue() {return value;}

    public String print() {
        return key + " " + value;
	}

	private int key;
	private String value;
}