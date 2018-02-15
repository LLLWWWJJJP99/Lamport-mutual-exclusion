package cs6378.copy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NClient {
	private ServerSocket myServer;
	private HashMap<Integer, BufferedReader> receivers;
	private HashMap<Integer, PrintStream> senders;
	private HashMap<Integer, Socket> outcomingNeighbors;
	private HashMap<Integer, Socket> incomingNeighbors;
	private List<Message> messageList;
	private static final int numOfNeighbors = 1;
	private int UID = 2;

	public NClient(int uid) {
		messageList = Collections.synchronizedList(new ArrayList<Message>() {
			public synchronized boolean add(Message message) {
				boolean ret = super.add(message);
				Collections.sort(messageList);
				return ret;
			}
		});
		outcomingNeighbors = new HashMap<>();
		incomingNeighbors = new HashMap<>();

		receivers = new HashMap<>();
		senders = new HashMap<>();
		this.UID = uid;
	}

	public ServerSocket getMyServer() {
		return myServer;
	}

	public HashMap<Integer, BufferedReader> getReceivers() {
		return receivers;
	}

	public HashMap<Integer, PrintStream> getSenders() {
		return senders;
	}

	public HashMap<Integer, Socket> getOutcomingNeighbors() {
		return outcomingNeighbors;
	}

	public HashMap<Integer, Socket> getIncomingNeighbors() {
		return incomingNeighbors;
	}

	public List<Message> getMessageList() {
		return messageList;
	}

	public int getUID() {
		return UID;
	}

	public static void main(String[] args) {
		
	}

	public void init(int[] ids, int[] ports, String[] ips) {
		for (int i = 0; i < ids.length; i++) {
			if (UID == ids[i]) {
				try {
					System.out.println(ports[i]);
					myServer = new ServerSocket(ports[i]);
					System.out.println("finish Setting up myserver");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				final int j = i;
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean done = false;
						while (!done) {
							try {
								Socket socket = new Socket(ips[j], ports[j]);
								PrintStream ps = new PrintStream(socket.getOutputStream());
								senders.put(ids[j], ps);
								outcomingNeighbors.put(ids[j], socket);
								done = true;
							} catch (IOException e) {
								try {
									Thread.sleep(100);
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
		System.out.println("finish Setting up outcoming connections");
		for (int i = 0; i < ids.length - 1; i++) {
			try {
				System.out.println(myServer.getInetAddress().getHostAddress() + " ");
				Socket socket = myServer.accept();
				incomingNeighbors.put(ids[i], socket);
				receivers.put(ids[i], new BufferedReader(new InputStreamReader(socket.getInputStream())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Network setup is finished");
		listenToNeighbors();
		while (outcomingNeighbors.size() < numOfNeighbors || incomingNeighbors.size() < numOfNeighbors) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Program starts to works");
	}

	private void listenToNeighbors() {
		for (Map.Entry<Integer, BufferedReader> entry : receivers.entrySet()) {
			BufferedReader br = entry.getValue();
			new Thread(new ClientListener(br, this)).start();
		}
	}

	public void test() {
		cleanUp();
		broadcast(null);
	}

	public void startWork() {
		cleanUp();

	}

	private void broadcast(Message message) {
		for (Map.Entry<Integer, PrintStream> entry : senders.entrySet()) {
			PrintStream ps = entry.getValue();
			ps.println("Hello from " + this.UID + " to " + entry.getKey());
		}
		System.out.println("MyUID: " + this.UID);
	}

	private void privateMessage() {

	}

	private void cleanUp() {
		this.messageList.clear();
	}

	public void generate() {

	}
}
