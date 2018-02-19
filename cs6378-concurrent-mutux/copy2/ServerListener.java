package cs6378.copy2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.ReversedLinesFileReader;

public class ServerListener implements Runnable {
	private Socket client;
	private BufferedReader br;
	private PrintStream ps;
	private File file = new File(".//test.txt");

	public ServerListener(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			ps = new PrintStream(client.getOutputStream());
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line + " : Server Receives");
				System.out.println(" : ServerThread return read line");
				//ps.println(readLastLine(file));
			}
		} catch (IOException e) {
			e.printStackTrace();
			closeResource();
		}
	}

	private void closeResource() {
		System.out.println("Cleaning out Server Side Resource");
		try {
			if (br != null) {
				br.close();
			}

			if (ps != null) {
				ps.close();
			}

			if (client != null) {
				client.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
