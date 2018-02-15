package cs6378.copy;

public class Message implements Comparable<Message> {
	private final int counter;
	private final String type;
	private final int from;
	private final int to;
	
	public Message(int counter, String type, int from, int to) {
		this.counter = counter;
		this.type = type;
		this.from = from;
		this.to = to;
	}

	public static void main(String[] args) {

	}

	@Override
	public int compareTo(Message o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
