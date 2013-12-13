package sse.mm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sse.mm.cc.CC;
import sse.mm.cflag.CFlags;
import sse.mm.command.Command;
import sse.mm.command.Commands;
import sse.mm.directory.Directory;
import sse.mm.include.Include;
import sse.mm.include.Includes;
import sse.mm.obj.Obj;
import sse.mm.obj.Objs;

public class Make {

	private Directory directory;
	private CC cc;
	private CFlags cflags;
	private Includes includes;
	private Objs objects;
	private KV exec;
	private Commands allCommand;
	private Commands commands;
	private Commands execCommand;
	private Commands testCommand;
	private Commands cleanCommand;
	private static final String SSE_TEST = "/sse/test";
	private static final String SSE = "/sse";
	private static final String MKFILE = "/Makefile";
	private static final String OBJ_SUFFIX = "_OBJ";

	public Make() {
		directory = new Directory();
		cflags = new CFlags();
		includes = new Includes();
		objects = new Objs();
		exec = new KV("EXEC");
		allCommand = new Commands();
		commands = new Commands();
		execCommand = new Commands();
		testCommand = new Commands();
		cleanCommand = new Commands();
	}

	public Make(Directory directory) {
		this.directory = directory;
	}

	public Directory getDirectory() {
		return directory;
	}

	public void setDirectory(String makeFile,String rootMake) {
		if (!directory.setPCDirectoriesFrom(makeFile,rootMake)) {
			System.err
					.println("could not find parent and current directory in '"
							+ makeFile + "'");
		}
	}

	public void setCC(String key, String cc) {
		this.cc = new CC(key, cc);
	}

	public void addCFlags(String[] cflags) {
		if (cflags == null) {
			return;
		}

		for (String cflag : cflags) {
			this.cflags.addCFlag(cflag);
		}
	}

	public void addInclude(String parent, String key, String path) {
		includes.add(parent, key, path);
	}
	
	public boolean addInclude(Include include){
		if(include != null){
			includes.add(include);
			return true;
		}
		
		return false;
	}
	
	public Include getInclude(String key){
		return includes.getInclude(key);
	}
	
	public boolean searchInclude(String key){
		return includes.search(key);
	}

	public void addObj(String parent, String current, String key, String path) {
		objects.add(parent, current, key, path);
	}
	
	public void addObj(Obj obj){
		objects.add(obj);
	}

	public void setExec(String key, String path) {
		exec.setKey(key);
		exec.setPath(path);
	}

	public void setAllCommand(String key, String incPath, String path,
			List<String> params) {
		allCommand.add(key, incPath, path, params);
	}

	public void setExecCommand(String key, String incPath, String path,
			List<String> params) {
		execCommand.add(key, incPath, path, params);
	}
	
	public void appendExecCommandPath(String path){
		
		Iterator<Entry<String,Command>> itr = execCommand.getCItr();
		execCommand.appendPath(itr.next().getKey(), path);
	}
	
	public void appendExecCommandInclude(String path){
		
		Iterator<Entry<String,Command>> itr = execCommand.getCItr();
		Entry<String,Command> ent = itr.next();
		if(!ent.getValue().findInclude(path)){
			execCommand.appendInclude(ent.getKey(), path);
		}
	}

	public void addCommand(String key, String incPath, String path,
			List<String> params) {
		commands.add(key, incPath, path, params);
	}
	
	public void addCommand(Command command){
		commands.add(command);
	}

	public Command getCommand(String key) {
		return commands.getCommand(key);
	}

	public void setTestCommand(String key, String incPath, String path,
			List<String> params) {
		testCommand.add(key, incPath, path, params);
	}
	
	public void appendTestCommandPath(String path){
		Iterator<Entry<String,Command>> itr = testCommand.getCItr();
		testCommand.appendPath(itr.next().getKey(), path);
	}
	
	public void appendTestCommandInclude(String path){
		Iterator<Entry<String,Command>> itr = testCommand.getCItr();
		Entry<String,Command> ent = itr.next();
		if(!ent.getValue().findInclude(path)){
			testCommand.appendInclude(ent.getKey(), path);
		}
	}
	
	public boolean searchTestCommandInclude(String path){
		Iterator<Entry<String,Command>> itr = testCommand.getCItr();
		return itr.next().getValue().findInclude(path);
	}

	public void setCleanCommand(String key, String incPath, String path,
			List<String> params) {
		cleanCommand.add(key, incPath, path, params);
	}

	public Map<String, String> getModMks() {

		Map<String, String> mods = new HashMap<String, String>();
		Iterator<Entry<String, Command>> itr = commands.getCItr();
		String inc = "", tmp = "";
		int strt = -1;
		while (itr.hasNext()) {
			Entry<String, Command> ent = itr.next();
			String[] sp = ent.getValue().getPath().split("/");
			inc = sp[0].trim();
			if(sp.length == 2){
				tmp = includes.get(inc.substring(2, inc.indexOf(")")));
			}else if(sp.length > 2){
				tmp = includes.get(inc.substring(2, inc.indexOf(")")));
				for(int i = 0; i < sp.length;i++){
					if(sp[i].trim().startsWith("$(") || sp[i].endsWith(".c")){
						continue;
					}
					if(i == (sp.length - 1)){
						tmp += sp[i];
					}else{
						tmp += "/" + sp[i];
					}
				}
			}else{
				System.err.println(ent.getValue().getKey() + " has some invalid path: " + ent.getValue().getPath());
			}
			if (tmp.equals("")) {
				continue;
			}

			strt = tmp.lastIndexOf(SSE);
			StringBuilder stb = new StringBuilder(tmp);
			if(!tmp.substring(0,strt).equals(directory.getParent())){
				stb = stb.replace(0, strt, directory.getParent());
			}
			stb = stb.replace(strt, strt + SSE.length(), SSE_TEST);
			mods.put(ent.getKey(), stb.toString() + MKFILE);
		}

		return mods;
	}

	public boolean searchObj(String key){
		return objects.search(key);
	}
	
	public Obj getObj(String key){
		return objects.getObj(key);
	}
	
	public Map<String,String> checkAllIncs(String makeFile,Command command){
		
		Map<String,String> map = new HashMap<String,String>();
		for(String s: command.getIncludes()){
			if(!includes.search(s)){
				
				StringBuilder stb = new StringBuilder(s);
				map.put(s,stb.replace(s.lastIndexOf("_"),s.length(),OBJ_SUFFIX).toString());
				System.err.println(s + " is not included as INC at '" + makeFile + "'.");
			}
		}
		
		return map;
	}
	
	public boolean checkAllObjs(){
		return objects.checkAllObjs(directory.getParent(),directory.getCurrent());
	}
	
	public boolean searchCommand(String key){
		
		return commands.findCommand(key);
	}
	
	public String format(){
		String str = "";
		str += cc.format();
		str += cflags.format();
		str += directory.formatParent();
		str += includes.format();
		str += directory.formatCurrent();
		str += objects.format();
		str += exec.format();
		str += allCommand.format();
		str += execCommand.format();
		str += commands.format();
		str += testCommand.format();
		str += cleanCommand.format();

		return str;
	}
	
	@Override
	public String toString() {
		String str = "";
		str += directory.toString();
		str += cc.toString();
		str += cflags.toString();
		str += includes.toString();
		str += objects.toString();
		str += exec.toString();
		str += allCommand.toString();
		str += execCommand.toString();
		str += commands.toString();
		str += testCommand.toString();
		str += cleanCommand.toString();

		return str;
	}
}
