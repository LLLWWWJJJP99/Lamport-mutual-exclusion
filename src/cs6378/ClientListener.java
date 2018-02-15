package cs6378;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientListener implements Runnable {
	private NClient client;
	private BufferedReader br;
	public ClientListener(BufferedReader br, NClient client) {
		this.br = br;
		this.client = client;
	}
	
	@Override
	public void run() {
		try {
System.out.println("ClientListener Starts To  Accept Message");
			String line = null;
			while((line = br.readLine()) != null) {
				System.out.println(line + " : Client receive line from server");
			}
		} catch (IOException e) {
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
