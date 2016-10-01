package project1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainProgram {
	   
  	public MainProgram() throws IOException {
      
  	}
  	
  	private void connect(List<Node> nodes, String fileName) {
  		String line;
  		try {
  			FileReader fileReader = new FileReader(fileName);
  			
  			BufferedReader bufferedReader = new BufferedReader(fileReader);
  			while ((line = bufferedReader.readLine()) != null) {
  				String[] parts = line.split(" ");
  				
  			}
  			bufferedReader.close();
  		} catch(IOException e) {}
  	}
	   
  	public static void main(String [] args) {
	   int N = Integer.parseInt(args[0]);
	   String fileName = args[1];
	   for (int i=0; i<N; i++) {
		   try {
			   Node n = new Node();
		   } catch (IOException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
	   }
  	}
}
