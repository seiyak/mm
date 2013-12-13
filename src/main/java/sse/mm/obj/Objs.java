package sse.mm.obj;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Objs {
	private final Map<String, Obj> objs;

	public Objs() {
		objs = new HashMap<String, Obj>();
	}

	public void add(String parent, String current, String key, String path) {
		objs.put(key, new Obj(parent, current, key, path));
	}
	
	public void add(Obj obj){
		objs.put(obj.getKey(), obj);
	}

	public boolean search(String key) {
		if (objs.get(key) == null) {
			return false;
		}

		return true;
	}
	
	public Obj getObj(String key){
		return objs.get(key);
	}

	public boolean checkAllObjs(String parent,String current){
		
		boolean checked = true;
		Iterator<Entry<String,Obj>> itr = objs.entrySet().iterator();
		String sub = "";
		int index = -1;
		while(itr.hasNext()){
			Obj o = itr.next().getValue();
			index = o.getPath().lastIndexOf("/");
			sub = o.getPath().substring(0,index);
			if(!sub.equals(parent + "/" + current)){
				System.err.println(o.getKey() + " doesn't start with '" + parent + "/" + current + ".");
				StringBuilder stb = new StringBuilder(o.getPath());
				stb = stb.replace(0, index, parent + "/" + current);
				
				System.err.println("change to '" + o.getPath() + "' to '" + stb.toString() + "'.");
				o.setPath(stb.toString());
				checked = false;
			}
		}
		
		return checked;
	}
	
	public String format(){
		String str = "";
		Iterator<Entry<String, Obj>> itr = objs.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, Obj> entry = itr.next();
			str += entry.getValue().getKey() + "=" + entry.getValue().getPath() + "\n";
		}

		return str;
	}
	
	@Override
	public String toString() {
		String str = "";
		Iterator<Entry<String, Obj>> itr = objs.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, Obj> entry = itr.next();
			str += entry.getValue() + "\n";
		}

		return str;
	}
}
