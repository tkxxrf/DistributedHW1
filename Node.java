package project1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

public class Node {

	public static Map<Integer, Pair<OutgoingConnection, IncomingConnection> > connectedNodes;
	public static Map<String, FileNodeState> files;
	public static Map<String, Queue<String>> operations;
	
	private static List<String> ips;
	public static Integer N;
	
	public Node(int id) {
		N = id;
		Map<String, FileNodeState> m1 = new HashMap<String, FileNodeState>();
		files = Collections.synchronizedMap(m1);
		Map<Integer, Pair<OutgoingConnection, IncomingConnection>> m2 = new HashMap<Integer, Pair<OutgoingConnection, IncomingConnection>>();
		connectedNodes = Collections.synchronizedMap(m2);
		Map<String, Queue<String>> m3 = new HashMap<String, Queue<String>>();
		operations = Collections.synchronizedMap(m3);
	}

	private void createConnection(int other) {
		String ip = ips.get(other-1);
		int port = N*100+other;
		OutgoingConnection out = null;
		IncomingConnection in = null;
		try {
			out = new OutgoingConnection(ip, port);
		} catch(IOException e) {
			e.printStackTrace();
		}
		int incomingport = other*100+N;
		try {
			in = new IncomingConnection(incomingport, this, other);
			(new Thread(in)).start();
		} catch(IOException e) {
		   e.printStackTrace();
		} 
		connectedNodes.put(N, Pair.create(out, in));
	}
	
	public void actOn(String filename, String file){
		String op = operations.get(filename).poll();
		if(op == null) return;

		if(op.startsWith("read")){
			System.out.println("reading " + filename);
			System.out.println(file);
			files.get(filename).file = file;
		}
		if(op.startsWith("append")){
			System.out.println("appending to " + filename);
			files.get(filename).file = file + op.substring("append".length() + filename.length() +2);
		}
		if(op.startsWith("delete")){
			files.remove(filename);
			operations.remove(filename);
			synchronized(connectedNodes){
				for(Integer key : connectedNodes.keySet()){
					Pair<OutgoingConnection, IncomingConnection> entry = connectedNodes.get(key);
					entry.first.send("DELETE " + filename);
				}
			}
		}
	}	
	
	private void readTree(String treeFileName, String ipFileName) throws IOException {
		ips = new ArrayList<String>();
		
		String sCurrentLine1;
		BufferedReader br1 = new BufferedReader(new FileReader(ipFileName));
		while ((sCurrentLine1 = br1.readLine()) != null) {
			ips.add(sCurrentLine1);
			System.out.println(sCurrentLine1);
		}
		
		br1.close();
		
		String sCurrentLine2;
		BufferedReader br2 = new BufferedReader(new FileReader(treeFileName));
		while ((sCurrentLine2 = br2.readLine()) != null) {
			sCurrentLine2 = sCurrentLine2.replace("(", "");
			sCurrentLine2 = sCurrentLine2.replace(")", "");
			String[] parts = sCurrentLine2.split(",");
			System.out.println(parts[0] + " " + parts[1]);
			if (Integer.parseInt(parts[0]) == N) {
				int other = Integer.parseInt(parts[1]);
				createConnection(other);
			} else if (Integer.parseInt(parts[1]) == N) {
				int other = Integer.parseInt(parts[0]);
				createConnection(other);
			} else {
				continue;
			}
		}
		
		br2.close();
	}
	   
	public void start() throws IOException {
		Scanner sc = new Scanner(System.in);
		
		while (true) {
			System.out.println("Please Enter a Command");
			String line = sc.nextLine();
			if (line.startsWith("activate")) {
				String[] parts = line.split(" ");
				String file1 = parts[1];
				String file2 = parts[2];
				readTree(file1, file2);
			} else if (line.startsWith("create")) {
				String[] parts = line.split(" ");
				String fileName = parts[1];
				System.out.println("Creating File: " + fileName);
				if (files.get(fileName) != null) {
					System.out.println("Error: File " + fileName + " Already Exists");
					continue;
				} else {
					FileNodeState newFile = new FileNodeState(fileName, N);
					files.put(fileName, newFile);
					Queue<String> queue = new LinkedList<String>();
					operations.put(fileName, queue);
					for(Integer key : connectedNodes.keySet()){
						Pair<OutgoingConnection, IncomingConnection> entry = connectedNodes.get(key);
						entry.first.send("CREATE " + fileName);
					}
				}
			} else if (line.startsWith("delete")) {
				String[] parts = line.split(" ");
				String fileName = parts[1];
				FileNodeState state = files.get(fileName);
				if (files.get(fileName) != null) {
					operations.get(fileName).add(line);
					if(state.holder == N && state.requests.isEmpty()){
						actOn(fileName, "");
						state.asked = false;
					}else{
						state.requests.add(N);
						if(state.asked == false){
							connectedNodes.get(state.holder).first.send("REQUEST " + fileName);
							state.asked = true;
						}
					}
				} else {
					System.out.println("Error: No File Named: " + fileName);
					continue;
				}
			} else if (line.startsWith("read")) {
				String[] parts = line.split(" ");
				String fileName = parts[1];
				FileNodeState state = files.get(fileName);
				if (files.get(fileName) != null) {
					operations.get(fileName).add(line);
					if(state.holder == N && state.requests.isEmpty()){
						actOn(fileName, state.file);
						state.asked = false;
					}else{
						state.requests.add(N);
						if(state.asked == false){
							connectedNodes.get(state.holder).first.send("REQUEST " + fileName);
							state.asked = true;
						}
					}
				} else {
					System.out.println("Error: No File Named: " + fileName);
					continue;
				}
			} else if (line.startsWith("append")) {
				String[] parts = line.split(" ");
				String fileName = parts[1];
				FileNodeState state = files.get(fileName);

				if (files.get(fileName) != null) {
					operations.get(fileName).add(line);
					if(state.holder == N && state.requests.isEmpty()){
						actOn(fileName, state.file);
						state.asked = false;
					}else{
						state.requests.add(N);
						if(state.asked == false){
							connectedNodes.get(state.holder).first.send("REQUEST " + fileName);
							state.asked = true;
						}
					}
				} else {
					System.out.println("Error: No File Named: " + fileName);
					continue;
				}
			} else if (line.equals("close")) {
				System.out.println("Closing Program");
				break;
			} else {
				System.out.println("Unknown Msg: " + line);
			}
		}
		sc.close();
	}
}
