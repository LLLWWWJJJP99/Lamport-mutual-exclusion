package cs6378.conncurrent;

import java.io.IOException;
import java.io.ObjectInputStream;

import cs6378.Message;

public class ClientListener implements Runnable {
	private NClient client;
	private ObjectInputStream br;
	public ClientListener(ObjectInputStream br, NClient client) {
		this.br = br;
		this.client = client;
	}
	
	@Override
	public void run() {
		try {
			Message message = null;
			System.out.println("MyUID " + client.getUID());
			while((message = (Message) br.readObject()) != null) {
				System.out.println(message + " : Client receive line from server");
				client.processMessage(message);
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
