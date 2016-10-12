package project1;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
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

				if(mesage[0].equals("TOKEN")){
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
						if((int ref = state.requests.poll()) != null){
							if(ref == parent.N){
								state.holder = parent.N;
								state.asked = false;
								state.using = true;
								parent.actOn(message[1], file);
								state.using = false;
							}else{
								parent.connectedNodes.get(ref).first.send("TOKEN " + message[1] + " " +file);
								state.holder = ref;
								if(!state.requests.empty()){
									parent.connectedNodes.get(state.holder).first.send("REQUEST " + message[1]);
								}
							}
						}
					}

				}else if(mesage[0].equals("CREATE")){
					parent.files.put(message[1], new FileNodeState(message[1], this.connectedTo));
					synchronized(parent.connectedNodes){
					for(Map.Entry<Integer, Pair<OutgoingConnection, IncomingConnection>> entry : parent.connectedNodes.entrySet()){
						if(entry.getKey() != this.connectedTo){
							entry.first.send("CREATE " + message[1]);
						}
					}
					}
				}else if(mesage[0].equals("DELETE")){
					parent.files.remove(message[1]);
					synchronized(parent.connectedNodes){
					for(Map.Entry<Integer, Pair<OutgoingConnection, IncomingConnection>> entry : parent.connectedNodes.entrySet()){
						if(entry.getKey() != this.connectedTo){
							entry.first.send("DELETE " + message[1]);
						}
					}
					}
				}else if(mesage[0].equals("REQUEST")){
					FileNodeState state = parent.files.get(message[1]);
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
								parent.connectedNodes.get(state.holder).first.send("REQUEST " + message[1]);
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
