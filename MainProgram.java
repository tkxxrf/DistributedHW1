package project1;

import java.io.IOException;

public class MainProgram {
	static Node N;
	
	public static void main(String [] args) throws IOException {
		if (args.length != 1) return;
		Integer id = Integer.parseInt(args[0]);
		N = new Node(id);
		N.start();
	}
}
