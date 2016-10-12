package project1;

import java.util.*;

public class FileNodeState {
	public int holder;
	public Queue<int> requests;
	public boolean asked;
	public String filename;
	public boolean using;
	public String file;
	public FileNodeState(int name, int from){
		filename = name;
		holder = from;
		asked = false;
		requests = new LinkedList<int>();
		using = false;
		file = "";
	}
}
