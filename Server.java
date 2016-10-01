package project1;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

public class Server extends Thread {
	private Socket client;
	private List<String> tokens;
	private List<Boolean> tokensInUse;
	private Map<String, List<String>> files;
   
	public Server(String argument, List<String> tokens, List<Boolean> tokensInUse, Map<String, List<String>> files) throws IOException {
		this.tokens = tokens;
		this.tokensInUse = tokensInUse;
		this.files = files;
		
		String[] arguments = argument.split(":");
		String server = arguments[0];
		int port = Integer.parseInt(arguments[1]);
		try {
			client = new Socket(server, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			System.out.println("Just connected to " + client.getRemoteSocketAddress());
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);

			out.writeUTF("Hello from " + client.getLocalSocketAddress());
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);

			System.out.println("Server says " + in.readUTF());
			client.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	   
   /*
   public static void main(String [] args) {
	   if (args.length%2 != 0) return;
	   for (int i=0; i<args.length; i+=2){
		   if (args[i] == "-p") {
			   int port = Integer.parseInt(args[i+1]);
			   try {
				   Thread t = new Server(port);
				   t.start();
			   } catch(IOException e) {
				   e.printStackTrace();
			   } 
		   } else if (args[i] == "-s") {

		   }
	   }
   }
   */
}

