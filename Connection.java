package project1;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.*;

public class Connection implements Runnable {
	private static ServerSocket incoming;
	private static ServerSocket outgoing;
	private Map<String, FileNodeState> files;
	private Map<String, Connection> connectedNodes;
	private Node parent;
	private String connectedTo;

	public Connection(String node, String in, String out, Map<String, FileNodeState> files, Map<String, Connection> connectedNodes, Node parent) throws IOException {
		int inport = Integer.parseInt(in);
		incoming = new ServerSocket(port);

		int outport = Integer.parseInt(out);
		outgoing = new ServerSocket(outport);

      	//serverSocket.setSoTimeout(10000);
		this.files = files;
		this.connectedNodes = connectedNodes;
		this.parent = parent;
		this.connectedTo = node;
	}

	public synchronized send(){
		
	}
	
	public void run() {
		while(true) {
			try {
				System.out.println("Waiting for client on port " + 
						incoming.getLocalPort() + "...");
				Socket server = incoming.accept();
             
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(server.getInputStream());
             
				String[] message = in.readUTF().split(" ");

				if(mesage[0].equals("TOKEN")){
					StringBuilder builder = new StringBuilder();
					for(int i = 2; i < message.length; ++i){
						builder.append(message[i]);
						if(i < message.length-1){
							builder.append(" ");
						}
					}
					String file = builder.toString();
					FileNodeState state = files.get(message[1]);
					synchronized(state){
						if((String ref = state.requests.poll()) != null){
							if(ref == parent.name){
								state.holder = parent.name;
								state.asked = false;
								state.using = true;
								parent.actOn(message[1], file);
								state.using = false;
							}else{
								connectedNodes.get(ref).send("TOKEN " + message[1] + " " +file);
								state.holder = ref;
								if(!state.requests.empty()){
									connectedNodes.get(state.holder).send("REQUEST " + message[1]);
								}
							}
						}
					}

				}else if(mesage[0].equals("CREATE")){
					files.put(message[1], new FileNodeState(message[1], message[2]));
				}else if(mesage[0].equals("DELETE")){
					files.remove(message[1]);
				}else if(mesage[0].equals("REQUEST")){
					FileNodeState state = files.get(message[1]);
					synchronized(state){
						if(state.holder == parent.name){
							if(!state.using){
								this.send("REQUEST " + message[1]);
								state.holder = this.connectedTo;
								state.asked = false;
							}else{
								state.requests.add(this.connectedTo);
							}
						}else{
							state.requests.add(this.connectedTo);
							if(!state.asked){
								connectedNodes.get(state.holder).send("REQUEST " + message[1]);
								state.asked = true;
							}
						}

				}

				server.close();
			}catch(SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			}catch(IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
