package cs6378.copy2;

import cs6378.Message;

public class LamportClock {
	private int clock = 0;
	private int d = 1;
	public LamportClock(int d) {
		this.d = d;
	}
	public void local_Event() {
		this.clock += d;
	}
	
	public int getClock() {
		return clock;
	}
	public int getD() {
		return d;
	}
	public void msg_event(Message message) {
		this.clock += d;
		if(message.getClock() + d > this.clock) {
			this.clock = message.getClock() + d;
		}
	}
	
	public static void main(String[] args) {
		LamportClock clock = new LamportClock(10);
		clock.clock = 2;
		Message message = new Message(1, "", 1, 1);
		clock.msg_event(message);
		System.out.println(clock.clock);
	}
}
