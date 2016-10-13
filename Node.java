package project1;

import java.io.IOException;
import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

	public static Map<Integer, Pair<OutgoingConnection, IncomingConnection> > connectedNodes;
	public static Map<String, FileNodeState> files;
	public static Map<String, Queue<String>> operations;
	
	private static List<String> ips;
	public static int N;
	

	private static void createConnection(int other) {
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
			in = new IncomingConnection(incomingport, N, other);
			(new Thread(in)).start();
		} catch(IOException e) {
		   e.printStackTrace();
		} 
		connectedNodes.put(N, Pair.create(out, in));
	}
	
	public static void actOn(String filename){
		String op = operations.get(filename).poll();
		if(!op) return;

		if(op.startsWith("read")){
			System.out.println("reading " + filename);
			String file = files.get(filename).file;
			System.out.println(file);
		}
		if(op.startsWith("append")){
			System.out.println("appending to " + filename);
			String file = files.get(filename).file;
			files.get(filename).file = file + op.substring(String("append").length + filename.length +2);
		}
		if(op.startsWith("delete")){
			files.remove(filename);
			operations.remove(filename);
			synchronized(connectedNodes){
				for(Map.Entry<Integer, Pair<OutgoingConnection, IncomingConnection>> entry : connectedNodes.entrySet()){
					entry.first.send("DELETE " + filename);
				}
			}
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
		if (args.length != 1) return;
	   
		files = new SynchronizedMap<String, FileNodeState>();
		connectedNodes = new SynchronizedMap<String, Connection>();
		operations = new SynchronizedMap<String, Queue<String>>();

		this.N = Integer.parseInt(args[0]);
		
		c = system.console();
		
		while (true) {
			String line = c.readline();
			if (line.startsWith("activate")) {
				String[] parts = line.split(" ");
				String file1 = parts[1];
				String file2 = parts[2];
				readTree(file1, file2);
			} else if (line.startsWith("create")) {
				String[] parts = line.split(" ");
				String fileName = parts[1];
				if (files.get(fileName) != null) {
					System.out.println("Error: File " + fileName + " Already Exists");
					continue;
				} else {
					FileNodeState newFile = new FileNodeState(fileName, N);
					files.put(fileName, newFile);
					Queue<String> queue = new LinkedList<String>();
					operations.put(fileName, queue);
					for(Map.Entry<Integer, Pair<OutgoingConnection, IncomingConnection>> entry : connectedNodes.entrySet()){
						entry.first.send("CREATE " + fileName);
					}
				}
			} else if (line.startsWith("delete")) {
				String[] parts = line.split(" ");
				String fileName = parts[1];
				if (files.get(fileName) != null) {
					operations.get(fileName).put("delete");
					actOn(fileName);
				} else {
					System.out.println("Error: No File Named: " + fileName);
					continue;
				}
			} else if (line.startsWith("read")) {
				String[] parts = line.split(" ");
				String fileName = parts[1];
				if (files.get(fileName) != null) {
					FileNodeState file = files.get(fileName);
					int holder = file.holder;
					connectedNodes.get(holder).first.send("REQUEST " + fileName);
					operations.get(fileName).put("read");
					actOn(fileName);
				} else {
					System.out.println("Error: No File Named: " + fileName);
					continue;
				}
			} else if (line.startsWith("append")) {
				String[] parts = line.split(" ");
				String fileName = parts[1];
				if (files.get(fileName) != null) {
					FileNodeState file = files.get(fileName);
					int holder = file.holder;
					connectedNodes.get(holder).first.send("REQUEST " + fileName);
					StringBuilder builder = new StringBuilder();
					for(int i = 2; i < parts.length; ++i){
						builder.append(parts[i]);
						if(i < parts.length-1){
							builder.append(" ");
						}
					}
					String file = builder.toString();
					operations.get(fileName).put("append " + file);
					actOn(fileName);
				} else {
					System.out.println("Error: No File Named: " + fileName);
					continue;
				}
			} else if (line.equals("close")) {
				break;
			} else {
				System.out.println("Unknown Msg: " + line);
			}
		}
	}
}
