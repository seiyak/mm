package sse.mm.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Commands {

	private final Map<String, Command> commands;

	public Commands() {
		commands = new HashMap<String, Command>();
	}

	public void add(String key, String incPath, String path, List<String> params) {
		commands.put(key, new Command(key, incPath, path, params));
	}
	
	public void add(Command command){
		commands.put(command.getKey(), command);
	}
	
	public void appendInclude(String key,String path){
		Command c = commands.get(key);
		if(c != null){
			int index =  c.getLastIIndex();
			if(index > 0){
				c.addParam(path, index);
			}
		}
	}
	
	public void appendPath(String key,String path){
		
		Command c = commands.get(key);
		if(c != null){
			c.appendPath(path);
		}
	}

	public boolean findCommand(String key) {
		if (commands.get(key) == null) {
			return false;
		}

		return true;
	}

	public void removeCommand(String key){
		commands.remove(key);
	}
	
	public Command getCommand(String key){
		return commands.get(key);
	}
	
	public Iterator<Entry<String,Command>> getCItr(){
		return commands.entrySet().iterator();
	}
	
	public String format(){
		String str = "";
		Iterator<Entry<String, Command>> itr = commands.entrySet().iterator();
		while (itr.hasNext()) {
			str += itr.next().getValue().format();
		}

		return str;
	}
	
	@Override
	public String toString() {
		String str = "";
		Iterator<Entry<String, Command>> itr = commands.entrySet().iterator();
		while (itr.hasNext()) {
			str += itr.next().getValue() + "\n";
		}

		return str;
	}
}
