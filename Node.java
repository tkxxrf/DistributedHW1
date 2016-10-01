package project1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
	List<Node> connectedNodes;
	   
	public Node() throws IOException {
		connectedNodes = new ArrayList<Node>();
	   
		//serverSocket = new ServerSocket(port);
		//serverSocket.setSoTimeout(10000);
	}
	   
	public static void main(String [] args) {
		if (args.length%2 != 0) return;
	   
		List<Thread> ports = new ArrayList<Thread>();
		List<Thread> servers = new ArrayList<Thread>();
	   
		List<String> tokens = new ArrayList<String>();
		List<Boolean> tokensInUse = new ArrayList<Boolean>();
	   
		Map<String, List<String>> files = new HashMap<String, List<String> >();
	   
		for (int i=0; i<args.length; i+=2) {
			if (args[i].equals("-s")) {
				try {
					Thread t = new Server(args[i+1], tokens, tokensInUse, files);
					servers.add(t);
					t.start();
				} catch(IOException e) {
					e.printStackTrace();
				} 
			} else if (args[i].equals("-p")) {
				try {
					Thread t = new Client(args[i+1], tokens, tokensInUse, files);
					ports.add(t);
					t.start();
				} catch(IOException e) {
				   e.printStackTrace();
				} 
			}
		}
	}
   
   	public void connect(Node n) {
   		connectedNodes.add(n);
   		n.connect(this);
   	}
}
