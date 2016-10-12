package project1;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.*;

public class Client extends Thread {
	private static ServerSocket serverSocket;
	private List<String> tokens;
	private List<Boolean> tokensInUse;
	private Map<String, List<String>> files;

	public Client(int port, List<String> tokens, List<Boolean> tokensInUse, Map<String, List<String>> files) throws IOException {
		//int port = Integer.parseInt(argument);
		serverSocket = new ServerSocket(port);
      	//serverSocket.setSoTimeout(10000);
		this.tokens = tokens;
		this.tokensInUse = tokensInUse;
		this.files = files;
	}
	
	public void run() {
		while(true) {
			try {
				System.out.println("Waiting for client on port " + 
						serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
             
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(server.getInputStream());
             
				System.out.println(in.readUTF());
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress()
						+ "\nGoodbye!");
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
	
	private void getToken() {
		Socket server;
		try {
			server = serverSocket.accept();
			DataInputStream in = new DataInputStream(server.getInputStream());
			String fileName = in.readUTF();
			int length = in.readInt();
			List<String> fileContent = new ArrayList<String>();
			for (int i=0; i<length; i++) {
				fileContent.add(in.readUTF());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
