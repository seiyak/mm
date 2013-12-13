package sse.mm.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Command {

	private static final String INC = "-I";
	private static final String INC_PREFIX = INC + "$(";
	private String key;
	private String incPath;
	private String path;
	private final List<String> params;
	private final Map<String,Boolean> paramMap;
	private final Map<String, Boolean> includes;
	private final Map<String, String> asIsIncludes;

	public Command() {
		params = new LinkedList<String>();
		paramMap = new HashMap<String,Boolean>();
		includes = new HashMap<String, Boolean>();
		asIsIncludes = new HashMap<String, String>();
	}

	public Command(String key, String incPath, String path) {
		this.key = key;
		this.incPath = incPath;
		this.setPath(path);
		params = new LinkedList<String>();
		paramMap = new HashMap<String,Boolean>();
		includes = new HashMap<String, Boolean>();
		asIsIncludes = new HashMap<String, String>();
	}

	public Command(String key, String incPath, String path, List<String> params) {
		this.key = key;
		this.incPath = incPath;
		this.path = path;
		this.params = new LinkedList<String>();
		paramMap = new HashMap<String,Boolean>();
		includes = new HashMap<String, Boolean>();
		asIsIncludes = new HashMap<String, String>();
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				addParam(params.get(i));
			}
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getIncPath() {
		return incPath;
	}

	public void setIncPath(String incPath) {
		this.incPath = incPath;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void appendPath(String path){
		
		if(path.endsWith("test.o")){
			return;
		}
		
		String[] tmp = this.path.split(" ");
		boolean f = false;
		for(String s: tmp){
			if(s.equals(path)){
				f = true;
				break;
			}
		}
		
		if(!f){
			this.path += " " + path;
		}
	}
	
	public List<String> getParams(){
		return params;
	}
	
	public void clear(){
		params.clear();
		paramMap.clear();
		includes.clear();
		asIsIncludes.clear();
	}
	
	public void addParam(String param) {
		adParam(param);
		params.add(param);
		paramMap.put(param, true);
	}
	
	public void addParam(String param,int index){
		adParam(param);
		params.add(index, INC_PREFIX + param+ ")");
		paramMap.put(INC_PREFIX + param+ ")", true);
	}
	
	private void adParam(String param){
		if (param.startsWith(INC) && !incPath.equals(param)) {
			if (!param.contains("/")) {
				includes.put(param.substring(4, param.length() - 1), true);
				asIsIncludes.put(param.substring(4, param.length() - 1),param);
			} else {
				includes.put(param, true);
				asIsIncludes.put(param, INC_PREFIX + param + ")");
			}
		}
	}

	public boolean findInclude(String incl) {
		if (includes.get(incl) == null) {
			return false;
		}

		return true;
	}
	
	public boolean findParam(String param){
		if(paramMap.get(param) == null){
			return false;
		}
		
		return true;
	}
	
	public String[] getIncludes(){
		
		String[] inc = new String[includes.size()];
		Iterator<Entry<String,Boolean>> itr = includes.entrySet().iterator();
		int index = 0;
		while(itr.hasNext()){
			inc[index] = itr.next().getKey();
			index++;
		}
		
		return inc;
	}

	public int getLastIIndex(){
		
		int indx = 0,index = 0;
		for(String s: params){
			if(s.startsWith(INC)){
				index = indx;
			}
			
			indx++;
		}
		return index + 1;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Command) {
			Command cm = (Command) obj;
			return key.equals(cm.getKey()) && incPath.equals(cm.getIncPath())
					&& path.equals(cm.getPath()) && equalsIncludes(cm) && equalsParams(cm);

		}

		return false;
	}
	
	private boolean equalsIncludes(Command cm){
		
		String[] cmInds = cm.getIncludes();
		
		if(includes.size() != cmInds.length){
			return false;
		}
		
		for(String s: cmInds){
			if(!findInclude(s)){
				return false;
			}
		}
		
		return true;
	}
	
	private boolean equalsParams(Command cm){

		List<String> pms = cm.getParams();
		if(paramMap.size() != pms.size()){
			return false;
		}
		
		for(String s: pms){
			if(!findParam(s)){
				return false;
			}
		}
		return true;
	}
	
	public String format(){
		
		String str = "";
		if(key.equals("all")){
			str += key + ":" + path + "\n\n";
		}else if(key.equals("clean")){
			str += key + ":\n\t" + toStringParams(false) + "\n\n";
		}else{
			str += key + ":" + path + "\n\t" + toStringParams(false) + "\n\n";
		}
		
		return str;
	}

	@Override
	public String toString() {
		return "{COM key=" + key + " path=" + path + "\n\t" + toStringParams(true)
				+ "\n\tincludes=" + toStringIncludes() + "\n}";
	}

	private String toStringParams(boolean prefix) {

		String str = "";
		if(prefix){
			str += "params=";
		}
		for (int i = 0; i < params.size(); i++) {
			if (i < params.size() - 1) {
				str += params.get(i) + " ";
			} else {
				str += params.get(i);
			}
		}

		return str;
	}

	private String toStringIncludes() {

		Iterator<Entry<String, Boolean>> itr = includes.entrySet().iterator();
		String str = "";
		while (itr.hasNext()) {
			str += itr.next().getKey() + " ";
		}

		return str;
	}
}
