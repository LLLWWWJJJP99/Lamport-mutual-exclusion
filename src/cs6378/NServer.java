package cs6378;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NServer {
	private ServerSocket serverSocket = null;
	private int PORT = 30500;
	private String IP = "127.0.0.1";

	private void init() {
		while (true) {
			try {
				serverSocket = new ServerSocket();
				serverSocket.bind(new InetSocketAddress(IP, PORT));
				Socket server = serverSocket.accept();
				new Thread(new ServerListener(server)).start();
			} catch (IOException e) {
				e.printStackTrace();
				closeResource();
			}
		}
	}

	private void closeResource() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
	}
}
