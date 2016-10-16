package project1;

import java.net.*;
import java.util.ArrayList;
import java.util.*;
import java.util.Map;
import java.io.*;

public class IncomingConnection implements Runnable {
	private static ServerSocket incoming;
	private Node parent;
	private int connectedTo;

	public IncomingConnection(int inport, Node parent, int to) throws IOException {
		incoming = new ServerSocket(inport);

		this.parent = parent;
      	//serverSocket.setSoTimeout(10000);
		this.connectedTo = to;
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

				if(message[0].equals("TOKEN")){
					System.out.println("recieved " + message[0] + " " + message[1] + " on " + incoming.getLocalPort());
					StringBuilder builder = new StringBuilder();
					for(int i = 2; i < message.length; ++i){
						builder.append(message[i]);
						if(i < message.length-1){
							builder.append(" ");
						}
					}
					String file = builder.toString();
					FileNodeState state = parent.files.get(message[1]);
					synchronized(state){
						state.holder = parent.N;
						Integer ref = state.requests.poll();
						if(ref != null){
							if(ref == parent.N){
								state.holder = parent.N;
								state.asked = false;
								state.using = true;
								parent.actOn(message[1], file);
								state.using = false;
								if(!state.requests.isEmpty()){
									int newref = state.requests.poll();
									parent.connectedNodes.get(newref).first.send("TOKEN " + message[1] + " " +file);
									state.holder = newref;
									state.asked = false;
									
									if(!state.requests.isEmpty()){
										parent.connectedNodes.get(state.holder).first.send("REQUEST " + message[1]);
										state.asked = true;
									}
								}
							}else{
								state.holder = ref;
								parent.connectedNodes.get(ref).first.send("TOKEN " + message[1] + " " +file);
								state.asked = false;
								if(!state.requests.isEmpty()){
									parent.connectedNodes.get(state.holder).first.send("REQUEST " + message[1]);
									state.asked = true;
								}
							}
						}
					}
					parent.files.put(message[1], state);
						System.out.println(state);
				}else if(message[0].equals("CREATE")){
					System.out.println("recieved " + message[0] + " " + message[1] + " on " + incoming.getLocalPort());
					parent.files.put(message[1], new FileNodeState(message[1], this.connectedTo));
					parent.operations.put(message[1], new LinkedList<String>());
					synchronized(parent.connectedNodes){
						for(Integer key : parent.connectedNodes.keySet()){
							Pair<OutgoingConnection, IncomingConnection> entry = parent.connectedNodes.get(key);
							if(key != this.connectedTo){
								entry.first.send("CREATE " + message[1]);
							}
						}
					}
				}else if(message[0].equals("DELETE")){
					System.out.println("recieved " + message[0] + " " + message[1]);
					parent.files.remove(message[1]);
					parent.operations.remove(message[1]);
					synchronized(parent.connectedNodes){
						for(Integer key : parent.connectedNodes.keySet()){
							Pair<OutgoingConnection, IncomingConnection> entry = parent.connectedNodes.get(key);
							if(key != this.connectedTo){
								entry.first.send("DELETE " + message[1]);
							}
						}
					}
				}else if(message[0].equals("REQUEST")){
					System.out.println("recieved " + message[0] + " " + message[1]);
					FileNodeState state = parent.files.get(message[1]);
					synchronized(state){
						if(state.holder == parent.N){
							if(!state.using && state.requests.isEmpty()){
								parent.connectedNodes.get(connectedTo).first.send("TOKEN " + message[1] + " " + state.file);
								state.holder = this.connectedTo;
								state.asked = false;
							}else{
								state.requests.add(this.connectedTo);
							}
						}else{
							state.requests.add(this.connectedTo);
							if(!state.asked){
								parent.connectedNodes.get(state.holder).first.send("REQUEST " + message[1]);
								state.asked = true;
							}
						}
					}
					parent.files.put(message[1], state);
						System.out.println(state);
				}

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
