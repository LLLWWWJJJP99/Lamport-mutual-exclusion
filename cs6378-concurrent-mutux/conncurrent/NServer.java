package cs6378.conncurrent;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.ReversedLinesFileReader;

import cs6378.Message;

public class NServer {
	private int PORT = 30500;
	private static final int ID = 1;
	private static final String FILEPREFIX = ".//files" + ID + "//";


	private void run() {
		try (ServerSocket server = new ServerSocket(PORT)) {
			while (true) {
				Socket socket = server.accept();
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				Message request = (Message) ois.readObject();
				Message reply = new Message(-1, ServerMsg.SUCCESS, -1, -1);
				if (request.getType().equals(ServerMsg.READ)) {
					String fileName = request.getContent();
					File file = new File(FILEPREFIX + fileName);
					String content = readLastLine(file);
					reply.setContent(content);
				} else if (request.getType().equals(ServerMsg.WRITE)) {
					String info = request.getContent();
					String[] strs = info.split(ServerMsg.SEPARATOR);
					File file = new File(FILEPREFIX + strs[0]);
					appendLine(file, strs[1]);
				} else if (request.getType().equals(ServerMsg.ENQUIRY)) {
					List<String> fileNames = enquiryFiles();
					StringBuffer sb = new StringBuffer();
					for (String str : fileNames) {
						sb.append(str + ServerMsg.SEPARATOR);
					}
					reply.setContent(sb.toString());
				} else {
					System.err.println("Server: wrong message type");
				}

				oos.writeObject(reply);
				oos.flush();
				oos.close();
				ois.close();
				socket.close();
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// list all file names from current directory
	private List<String> enquiryFiles() {
		List<String> list = new ArrayList<>();
		File folder = new File(FILEPREFIX);
		File[] files = folder.listFiles();
		for (File file : files) {
			list.add(file.getName());
		}
		return list;
	}

	// read last line from given file
	private String readLastLine(File file) {
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
	private void appendLine(File file, String line) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "rw");
			long len = raf.length();
			raf.seek(len);
			raf.writeBytes("\n" + line);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void test() {
		List<String> list = enquiryFiles();
		System.out.println(list);
		for(int i = 0; i < list.size(); i++) {
			File file = new File(FILEPREFIX + list.get(i)); 
			appendLine(file, "newLine " + i);
		}
		
		for(int i = 0; i < list.size(); i++) {
			File file = new File(FILEPREFIX + list.get(i)); 
			System.out.println(readLastLine(file) + " :last line");
		}
	}
	
	public static void main(String[] args) {
		NServer server = new NServer();
		server.run();
		
		//server.test();
	}
}
