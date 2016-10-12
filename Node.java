package project1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
	//private List<Node> connectedNodes;
	
	private List<String> ips;
	private int N;
	
	private List<Thread> ports;
	private List<Thread> servers;
	private List<String> tokens;
	private List<Boolean> tokensInUse;
	private Map<String, List<String>> files;
	
	/*
	public Node() throws IOException {
		//connectedNodes = new ArrayList<Node>();
	   
		//serverSocket = new ServerSocket(port);
		//serverSocket.setSoTimeout(10000);
	}
	*/
	
	private void createServer (int other) {
		String ip = ips[other-1];
		int port = N*100+other;
		try {
			Thread t = new Server(ip, port, tokens, tokensInUse, files);
			servers.add(t);
			t.start();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createClient(int other) {
		int port = other*100+N;
		try {
			Thread t = new Client(port, tokens, tokensInUse, files);
			ports.add(t);
			t.start();
		} catch(IOException e) {
		   e.printStackTrace();
		} 
	}
	
	private void readTree(String treeFileName, String ipFileName) {
		String sCurrentLine1;
		br1 = new BufferedReader(new FileReader(treeFileName));
		while ((sCurrentLine1 = br1.readLine()) != null) {
			ips.add(sCurrentLine1);
			System.out.println(sCurrentLine1);
		}
		
		br1.close();
		
		String sCurrentLine2;
		br2 = new BufferedReader(new FileReader(treeFileName));
		while ((sCurrentLine2 = br2.readLine()) != null) {
			sCurrentLine2.replace("(", "");
			sCurrentLine2.replace(")", "");
			String[] parts = sCurrentLine2.split(",");
			System.out.println(parts[0] + " " + parts[1]);
			if (N.equals(Integer.parseInt(parts[0]))) {
				int other = Integer.parseInt(parts[1]);
				createServer(other);
				createClient(other);
			} else if (N.equals(Integer.parseInt(parts[1]))) {
				int other = Integer.parseInt(parts[0]);
				createServer(other);
				createClient(other);
			} else {
				continue;
			}
		}
		
		br2.close();
	}
	   
	public static void main(String [] args) {
		if (args.length != 2) return;
	   
		ports = new ArrayList<Thread>();
		servers = new ArrayList<Thread>();
	   
		tokens = new ArrayList<String>();
		tokensInUse = new ArrayList<Boolean>();
	   
		files = new HashMap<String, List<String> >();
		
		readTree(args[0], args[1]);
	   
		/*
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
		*/
	}
   
	/*
   	public void connect(Node n) {
   		connectedNodes.add(n);
   		n.connect(this);
   	}
   	*/
}
