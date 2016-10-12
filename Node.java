package project1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

	private static Map<Integer, Pair<OutgoingConnection, IncomingConnection> > connectedNodes;
	private static Map<Integer, FileNodeState> files;
	
	private List<String> ips;
	private int N;
	

	private void createConnection(int other) {
		String ip = ips[other-1];
		int port = N*100+other;
		OutgoingConnection out;
		IncomingConnection in;
		try {
			out = new OutgoingConnection(ip, port);
		} catch(IOException e) {
			e.printStackTrace();
		}
		int incomingport = other*100+N;
		try {
			in = new IncomingConnection(incomingport, connectedNodes, files);
			(new Thread(in)).start();
		} catch(IOException e) {
		   e.printStackTrace();
		} 
		connectedNodes.put(N, Pair.create(out, in));
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
				createConnection(other);
			} else if (N.equals(Integer.parseInt(parts[1]))) {
				int other = Integer.parseInt(parts[0]);
				createConnection(other);
			} else {
				continue;
			}
		}
		
		br2.close();
	}
	   
	public static void main(String [] args) {
		if (args.length != 2) return;
	   
		files = new SynchronizedMap<String, FileNodeState>();
		connectedNodes = new SynchronizedMap<String, Connection>();
		
		readTree(args[0], args[1]);
	}
}
