package project1;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
			System.out.println("Just connected to " + client.getRemoteSocketAddress());
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);

			out.writeUTF(message);
			client.close();
		} catch(IOException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}