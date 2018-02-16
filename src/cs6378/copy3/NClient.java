package cs6378.copy3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import cs6378.Message;

public class NClient {
	private ServerSocket myServer;
	private Map<Integer, ObjectInputStream> receivers;
	private Map<Integer, ObjectOutputStream> senders;
	private Map<Integer, Socket> outcomingNeighbors;
	private Map<Integer, Socket> incomingNeighbors;
	private Map<ObjectOutputStream, Integer> neighbors;
	private static int numOfNeighbors;
	private LamportClock clock;

	public LamportClock getClock() {
		return clock;
	}

	private LamportMutux mutux;

	private final int UID = 4;

	public NClient() {
		clock = new LamportClock(1);
		mutux = new LamportMutux(this);

		outcomingNeighbors = Collections.synchronizedMap(new HashMap<Integer, Socket>());
		incomingNeighbors = Collections.synchronizedMap(new HashMap<Integer, Socket>());

		neighbors = Collections.synchronizedMap(new HashMap<ObjectOutputStream, Integer>());
		receivers = Collections.synchronizedMap(new HashMap<Integer, ObjectInputStream>());
		senders = Collections.synchronizedMap(new HashMap<Integer, ObjectOutputStream>());
	}

	public ServerSocket getMyServer() {
		return myServer;
	}

	public Map<Integer, ObjectInputStream> getReceivers() {
		return receivers;
	}

	public Map<Integer, ObjectOutputStream> getSenders() {
		return senders;
	}

	public Map<Integer, Socket> getOutcomingNeighbors() {
		return outcomingNeighbors;
	}

	public Map<Integer, Socket> getIncomingNeighbors() {
		return incomingNeighbors;
	}

	public int getUID() {
		return UID;
	}

	public static void main(String[] args) {
		NClient client = new NClient();
		int[] nids = new int[] { 1, 2, 3, 4, 5 };
		int[] ports = new int[] { 30000, 30001, 30002, 30003, 30004 };
		String[] ips = new String[] { "127.0.0.1", "127.0.0.1", "127.0.0.1", "127.0.0.1", "127.0.0.1" };
		// int[] nids = new int[] { 1, 2, 3 };
		// int[] ports = new int[] { 30000, 30001, 30002 };
		// String[] ips = new String[] { "127.0.0.1", "127.0.0.1", "127.0.0.1" };

		client.init(nids, ports, ips);
		client.test();
	}

	private void init(int[] ids, int[] ports, String[] ips) {
		numOfNeighbors = ids.length - 1;
		int myPort = 0;
		for (int i = 0; i < ids.length; i++) {
			if (UID == ids[i]) {
				myPort = ports[i];
			} else {

				final int j = i;
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean done = false;
						while (!done) {
							try {
								Socket socket = new Socket(ips[j], ports[j]);
								ObjectOutputStream ps = new ObjectOutputStream(socket.getOutputStream());
								senders.put(ids[j], ps);
								neighbors.put(ps, ids[j]);
								outcomingNeighbors.put(ids[j], socket);
								done = true;
							} catch (IOException e) {
								try {
									Thread.sleep(50);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
								System.out.println("Waitting For Other Clients To Be Started");
								done = false;
							}
						}
					}
				}).start();
			}
		}

		try {
			myServer = new ServerSocket(myPort);
			System.out.println("finish Setting up myserver");
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("finish Setting up outcoming connections");
		for (int i = 0; i < ids.length; i++) {
			try {
				if (ids[i] != UID) {
					Socket socket = myServer.accept();
					incomingNeighbors.put(ids[i], socket);
					receivers.put(ids[i], new ObjectInputStream(socket.getInputStream()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Network setup is finished");
		listenToNeighbors();
		while (outcomingNeighbors.size() < numOfNeighbors || incomingNeighbors.size() < numOfNeighbors) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Program starts to works");
	}

	private void listenToNeighbors() {
		for (Map.Entry<Integer, ObjectInputStream> entry : receivers.entrySet()) {
			ObjectInputStream br = entry.getValue();
			new Thread(new ClientListener(br, this)).start();
		}
	}

	private synchronized void test() {
		mutux.cleanUp();
		broadcast(LamportMsg.REQUEST);
	}

	public void startWork() {
		mutux.cleanUp();

	}

	public synchronized void broadcast(String type) {
		if (type.equals(LamportMsg.REPLY)) {
			System.err.println("error: cannnot broadcast reply message");
			return;
		}
		this.clock.local_Event();
		if (type.equals(LamportMsg.REQUEST)) {
			mutux.queueRequest();
		}
		System.out.println("senders" + senders);
		for (Map.Entry<Integer, ObjectOutputStream> entry : senders.entrySet()) {
			try {
				ObjectOutputStream ps = entry.getValue();
				System.out.println(this.clock + "||" + neighbors + "**" + this.UID);
				Message message = new Message(this.clock.getClock(), type, this.UID, neighbors.get(ps));
				System.out.println("message sent: " + message);
				ps.writeObject(message);
				ps.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void processMessage(Message message) {
		this.clock.msg_event(message);
		if (message.getType().equals(LamportMsg.RELEASE)) {
			mutux.releaseMessage(message);
		} else if (message.getType().equals(LamportMsg.REPLY)) {
			mutux.replyMessage(message);
		} else if (message.getType().equals(LamportMsg.REQUEST)) {
			Message reply = mutux.requestMessage(message);
			privateMessage(reply);
		} else {
			System.err.println("error: You receive wrong type of message");
		}
	}

	public synchronized void privateMessage(Message message) {
		if (!message.getType().equals(LamportMsg.REPLY)) {
			System.err.println("error: only reply message could be sent privatly");
			return;
		}
		this.clock.local_Event();
		try {
			ObjectOutputStream oos = senders.get(message.getTo());
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generate() {

	}
}
