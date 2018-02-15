package cs6378.copy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NServer {
	private ServerSocket serverSocket = null;
	private static int PORT = 30000;
	private static String IP = "127.0.0.1";
	private void init() {
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(IP, PORT));
			while(true) {
				Socket server = serverSocket.accept();
				new Thread(new ServerThread(server)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			closeResource();
		}
	}
	
	private void closeResource() {
		try {
			if(serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] agrs) {
		/*File file = new File(".//test.txt");
		System.out.println(readLastLine(file));
		appendLine(file, "Hello, World");*/
		NServer server = new NServer();
		server.init();
	}
}
