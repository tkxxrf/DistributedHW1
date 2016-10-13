package project1;

import java.util.*;

public class FileNodeState {
	public int holder;
	public Queue<Integer> requests;
	public boolean asked;
	public String filename;
	public boolean using;
	public String file;
	public FileNodeState(String name, int from){
		filename = name;
		holder = from;
		asked = false;
		requests = new LinkedList<Integer>();
		using = false;
		file = "";
	}
}
