package sse.mm.include;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Includes {

	private final Map<String, Include> includes;

	public Includes() {
		includes = new HashMap<String, Include>();
	}

	public void add(String parent, String key, String path) {
		includes.put(key, new Include(parent, key, path));
	}
	
	public void add(Include include){
		System.out.println("about to add include: " + include);
		includes.put(include.getKey(),include);
	}

	public boolean search(String key) {
		if (includes.get(key) == null) {
			return false;
		}

		return true;
	}

	public String get(String key) {
		Include inc = null;
		if ((inc = includes.get(key)) == null) {
			return "";
		}

		return inc.getPath();
	}
	
	public Include getInclude(String key){
		return includes.get(key);
	}

	public String format(){
		String str = "";
		Iterator<Entry<String, Include>> itr = includes.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, Include> entry = itr.next();
			str += entry.getValue().getKey() + "=" + entry.getValue().getPath() + "\n";
		}
		
		return str;
	}
	
	@Override
	public String toString() {
		String str = "";
		Iterator<Entry<String, Include>> itr = includes.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, Include> entry = itr.next();
			str += entry.getValue() + "\n";
		}

		return str;
	}
}
