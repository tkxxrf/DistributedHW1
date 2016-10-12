package project1;

import java.util.*;

public class FileNodeState {
	public String holder;
	public Queue<String> requests;
	public boolean asked;
	public String filename;
	public boolean using;

	public FileNodeState(String name, String from){
		filename = name;
		holder = from;
		asked = false;
		requests = new LinkedList<String>();
		using = false;
	}
}
