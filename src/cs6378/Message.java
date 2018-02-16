package cs6378;

import java.io.Serializable;

public class Message implements Comparable<Message>, Serializable {
	private final int clock;
	private final String type;
	private final int from;
	private final int to;
	
	public int getClock() {
		return clock;
	}

	public String getType() {
		return type;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public Message(int counter, String type, int from, int to) {
		this.clock = counter;
		this.type = type;
		this.from = from;
		this.to = to;
	}

	public static void main(String[] args) {

	}

	@Override
	public int compareTo(Message o) {
		return this.clock - o.clock;
	}

	@Override
	public String toString() {
		return clock + "," + type + "," + from + "," + to;
	}
}
