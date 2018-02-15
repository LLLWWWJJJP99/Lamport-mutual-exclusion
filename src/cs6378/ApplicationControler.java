package cs6378;

import cs6378.copy.NClient;

public class ApplicationControler {

	public static void main(String[] args) {
		NClient client = new NClient(2);
		// int[] nids = new int[] {1, 2, 3, 4, 5};
		// int[] ports = new int[] {30000, 30001, 30002, 30003, 30004};
		// String[] ips = new String[] {"127.0.0.1", "127.0.0.1", "127.0.0.1",
		// "127.0.0.1", "127.0.0.1"};
		int[] nids = new int[] { 1, 2 };
		int[] ports = new int[] { 30000, 30001 };
		String[] ips = new String[] { "127.0.0.1", "127.0.0.1" };
		client.init(nids, ports, ips);
		client.test();
		
		
	}

}
