package project1;

import java.net.*;
import java.io.*;

public class OutgoingConnection {
	private String server;
	private int port;
   
	public OutgoingConnection(String server, int port) throws IOException {
		this.server = server;
		this.port = port;
	}

	public synchronized void send(String message) {
		try {

			Socket client = new Socket(server, port);
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			System.out.println("sending " + message + " to " + server + ":" + port);
			out.writeUTF(message);
			client.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
