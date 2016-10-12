package project1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

	private static HashMap<String, Connection> connectedNodes;
	private static HashMap<String, FileNodeState> files;
	   
	public static void main(String [] args) {
		if (args.length%2 != 0) return;
	   	   
		files = new SynchronizedMap<String, FileNodeState>();
		connectedNodes = new SynchronizedMap<String, Connection>();

		//
		//for node in connections:
		// Connection c = new Connection(args);
		//	new Thread(c).start();

	}
}
