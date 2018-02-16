package cs6378.copy4;

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
			while((line = br.readLine()) != null) {
				System.out.println(line +" : Server Receives");
				System.out.println(" : ServerThread return read line");
				ps.println(readLastLine(file));
			}
		} catch (IOException e) {
			e.printStackTrace();
			closeResource();
		}
	}
	
	private void closeResource() {
		System.out.println("Cleaning out Server Side Resource");
		try {
			if(br != null) {
				br.close();
			}
			
			if(ps != null) {
				ps.close();
			}
			
			if(client != null) {
				client.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//list all file names from current directory
	public List<String> enquiryFiles() {
		List<String> list = new ArrayList<>();
		File folder = new File(".//files");
		File[] files = folder.listFiles();
		for (File file : files) {
			list.add(file.getName());
		}
		return list;
	}
		
	//read last line from given file
	public String readLastLine(File file) {
		StringBuilder sb = new StringBuilder();
		ReversedLinesFileReader reader = null;
		try {
			reader = new ReversedLinesFileReader(file, Charset.defaultCharset());
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.length() > 0) {
					sb.append(line);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}
		
	// append a line to specific file
	public void appendLine(File file, String line) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "rw");
			long len = raf.length();
			raf.seek(len);
			raf.writeBytes("\n" + line);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
