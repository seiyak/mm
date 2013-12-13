package sse.mm.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sse.mm.Make;
import sse.mm.command.Command;
import sse.mm.include.Include;
import sse.mm.obj.Obj;
import sse.mm.writer.MakeWriter;

public class MakeParser {

	private final static String INC = "INC";
	private final static String OBJ = "OBJ";
	private final static String CC = "CC";
	private final static String CFLAGS = "CFLAGS";
	private final static String EXEC = "EXEC";
	private final static String EXEC_VAR = "$(" + EXEC + ")";
	private final static String ALL = "all";
	private final static String CLEAN = "clean";
	private final static String PARENT_DIR = "PARENT_DIR";
	private final static String PARENT_DIR_VAR = "$(PARENT_DIR)";
	private final static String CURRENT_DIR = "CURRENT_DIR";
	private final static String CURRENT_DIR_VAR = "$(CURRENT_DIR)";
	private final static String LOG_CMD = "log.o";
	private final String makeFile;
	private Make cMake;

	public MakeParser(String makeFile) {
		this.makeFile = makeFile;
		cMake = new Make();
	}
	
	public void printCMake() {
		parse();
		System.out.println("\n" + cMake);
	}

	public void modify() {
		boolean done = parse();
		boolean donec = compare();
		if(done && donec){
			System.out.println("\nall the commands seem consistent with the origin comands at '" + makeFile + "'.");
			System.out.println(cMake.format());
		}else if(done && !donec){
			MakeWriter mwriter = new MakeWriter(makeFile,cMake);
			mwriter.write();
		}
	}

	/**
	 * assume that origin Makefile is always updated and correct, so
	 * cMake needs to be modified accordingly.
	 * 
	 * @param cMake
	 */
	private boolean compare() {

		Iterator<Entry<String, String>> itr = cMake.getModMks().entrySet()
				.iterator();
		MakeParser psr;
		boolean cns = true;
		Command lcmd = null;
		Obj lobj = null;
		Include linc = null;
		while (itr.hasNext()) {
			Entry<String, String> ent = itr.next();
			psr = new MakeParser(ent.getValue());
			psr.parse();

			if(lcmd == null && lobj == null && linc == null && !cMake.searchCommand(LOG_CMD) && psr.getCMake().searchCommand(LOG_CMD)){
				lcmd = psr.getCMake().getCommand(LOG_CMD);
				lobj = psr.getCMake().getObj("LOG_OBJ");
				if((linc = cMake.getInclude("SSE_INC")) == null){
					linc = psr.getCMake().getInclude("SSE_INC");
				}
			}
			
			Command ocmd = psr.getCMake().getCommand(ent.getKey());
			Command ccmd = cMake.getCommand(ent.getKey());
			//checks origin Makefile
			Map<String,String> allIncs = cMake.checkAllIncs(makeFile,ocmd);
			if(ocmd.equals(ccmd) && allIncs.isEmpty()){
				System.out.println(ocmd.getKey() + " seems consistent");
			}else{
				cns = false;
				System.err.println(ocmd.getKey() + " doesn't seem consistent on '" + makeFile + "'.");
				System.err.println("ocmd: " + ocmd.format());
				System.err.println("ccmd: " + ccmd.format());
				replace(ocmd,ccmd,allIncs,psr.getCMake());
			}
		}
		
		adjustCMake(lobj,linc,lcmd);
		return cns & cMake.checkAllObjs();
	}
	
	private void adjustCMake(Obj obj,Include inc,Command command){
		if(command != null && obj != null && inc != null){
			if(cMake.getInclude("SSE_INC") == null && cMake.getObj("LOG_OBJ") == null){
				cMake.addObj(obj);
				cMake.addInclude(inc);
				cMake.addCommand(command);
			}else if(cMake.getInclude("SSE_INC") != null && cMake.getObj("LOG_OBJ") == null){
				cMake.addObj(obj);
				cMake.addCommand(command);
			}else if(cMake.getInclude("SSE_INC") == null && cMake.getObj("LOG_OBJ") != null){
				cMake.addInclude(inc);
			}
			
			String ph = obj.getPath().substring(obj.getPath().lastIndexOf("/") + 1);
			cMake.appendTestCommandInclude("SSE_INC");
			cMake.appendExecCommandPath(ph);
			cMake.appendExecCommandInclude("SSE_INC");
		}
	}

	private void replace(Command ocmd,Command ccmd,Map<String,String> incDiffMap,Make oMake){
		ccmd.clear();
		ccmd.setPath(ocmd.getPath());
		for(String s: ocmd.getParams()){
			ccmd.addParam(s);
		}

		Obj obj;
		Iterator<Entry<String,String>> itr = incDiffMap.entrySet().iterator();
		while(itr.hasNext()){
			Entry<String,String> entry = itr.next();
			if(!cMake.searchObj(entry.getValue()) && !oMake.searchObj(entry.getValue())){
				//s might be SSE_OBJ converted from SSE_INC or
				cMake.addInclude(oMake.getInclude(entry.getKey()));
				cMake.appendTestCommandInclude(entry.getKey());
				cMake.appendExecCommandInclude(entry.getKey());
				continue;
			}
			if(!cMake.searchObj(entry.getValue()) && !cMake.searchInclude(entry.getKey())){
				cMake.addInclude(oMake.getInclude(entry.getKey()));
				obj = oMake.getObj(entry.getValue());
				String ph = obj.getPath().substring(obj.getPath().lastIndexOf("/") + 1);
				obj.setPath(cMake.getDirectory().getParent() + "/" + cMake.getDirectory().getCurrent() + "/" + ph);
				cMake.addObj(obj);
				cMake.addCommand(oMake.getCommand(ph));
				cMake.appendTestCommandPath(ph);
				cMake.appendTestCommandInclude(entry.getKey());
				cMake.appendExecCommandPath(ph);
				cMake.appendExecCommandInclude(entry.getKey());
			}else if(cMake.searchObj(entry.getValue()) && !cMake.searchInclude(entry.getKey())){
				cMake.addInclude(oMake.getInclude(entry.getKey()));
			}else if(!cMake.searchObj(entry.getValue()) && cMake.searchInclude(entry.getKey())){
				obj = oMake.getObj(entry.getValue());
				String ph = obj.getPath().substring(obj.getPath().lastIndexOf("/") + 1);
				obj.setPath(cMake.getDirectory().getParent() + "/" + cMake.getDirectory().getCurrent() + "/" + ph);
				cMake.addObj(obj);
				cMake.addCommand(oMake.getCommand(ph));
				cMake.appendTestCommandPath(ph);
				cMake.appendTestCommandInclude(entry.getKey());
				cMake.appendExecCommandPath(ph);
				cMake.appendExecCommandInclude(entry.getKey());
			}
		}
	}	
	
	public boolean parse() {

		if (makeFile == null || makeFile.equals("")) {
			System.err.println("makeFile is invalid.");
			return false;
		}

		BufferedReader br = null;
		try {
			File f = new File(this.makeFile);
			if (!f.exists()) {
				System.err.println("makeFile does not exist at: " + makeFile);
				return false;
			}

			System.out.println("about to parse Makefile at: " + makeFile);
			br = new BufferedReader(new FileReader(new File(makeFile)));
			String str = "", parent = "", current = "", key = "", incPath = "", path = "";
			List<String> params = new LinkedList<String>();
			boolean block = false, comd = false;
			int k = -1;
			while ((str = br.readLine()) != null) {
				String[] tmp = str.split("=");
				if (!block && isSame(tmp, str)) {
					block = true;
				}

				if (!block) {
					// include and object blocks
					if (tmp[0].equals(CC)) {
						cMake.setCC(tmp[0], tmp[1]);
					} else if (tmp[0].equals(CFLAGS)) {
						String[] cfs = tmp[1].split(" ");
						if (!isSame(cfs, tmp[1])) {
							cMake.addCFlags(cfs);
						}
					} else if (tmp[0].equals(PARENT_DIR)) {
						//if(!new File(tmp[1]).exists()){
						//	cMake.setDirectory(tmp[1],makeFile);
						//	parent = cMake.getDirectory().getParent();
						//}else{
							cMake.setDirectory(tmp[1],makeFile);
							//parent = tmp[1];
							parent = cMake.getDirectory().getParent();
						//}
						cMake.getDirectory().setParent(parent);
					} else if (tmp[0].equals(CURRENT_DIR)) {
						current = tmp[1];
						cMake.getDirectory().setCurrent(current);
					} else if (tmp[0].contains(INC)) {
						cMake.addInclude(parent, tmp[0],
								convertParentVariable(tmp[1], parent));
					} else if (tmp[0].contains(OBJ)) {
						cMake.addObj(
								parent,
								current,
								tmp[0],
								convertCurrentVariable(
										convertParentVariable(tmp[1], parent),
										current));
					} else if (tmp[0].equals(EXEC)) {
						cMake.setExec(tmp[0], tmp[1]);
					}
				} else {
					// *.o commands and others include ':' block
					if (str.contains(":")) {
						tmp = str.split(":");
						if (!isSame(tmp, str)) {
							if (tmp[0].equals(ALL)) {
								// finds all: command
								key = tmp[0];
								incPath = "\"\"";
								path = tmp[1];
								comd = false;
								cMake.setAllCommand(key, incPath, path, params);
								k = -1;
							} else if (tmp[0].equals(EXEC_VAR)) {
								// finds $(EXEC) command
								key = tmp[0];
								incPath = tmp[1].split("//")[0];
								path = tmp[1];
								comd = true;
								k = 1;
							} else if (!tmp[0].contains("test")
									&& tmp[0].endsWith(".o")) {
								// finds *.o command
								key = tmp[0];
								incPath = tmp[1].split("//")[0];
								path = tmp[1];
								comd = true;
								k = 2;
							} else if (tmp[0].contains("test")
									&& tmp[0].endsWith(".o")) {
								// finds XXX_test.o command
								key = tmp[0];
								incPath = tmp[1].split("//")[0];
								path = tmp[1];
								comd = true;
								k = 3;
							} else if (tmp[0].equals(CLEAN)) {
								// finds clean command
								key = tmp[0];
								incPath = "\"\"";
								path = "\"\"";
								comd = true;
								k = 4;
							}
						}
					} else {
						if (str.startsWith("\t")) {
							if (comd) {
								// finds $(CC) ...
								tmp = str.split("\t");
								tmp = tmp[1].split(" ");
								for (String s : tmp) {
									params.add(s);
								}

								if (k == 1) {
									cMake.setExecCommand(key, incPath, path,
											params);
								} else if (k == 2) {
									cMake.addCommand(key, incPath, path, params);
								} else if (k == 3) {
									cMake.setTestCommand(key, incPath, path,
											params);
								} else if (k == 4) {
									cMake.setCleanCommand(key, incPath, path,
											params);
								}
								k = -1;
								params.clear();
							}
						} else {
							comd = false;
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
		}
		
		return true;
	}

	private boolean isSame(String[] tmp, String str) {
		if (tmp.length == 1 && tmp[0].equals(str)) {
			// System.out
			// .println("'=' is not found. It's done with include and object blocks!!");
			return true;
		}

		return false;
	}

	private String convertParentVariable(String path, String parent) {
		return convertVariable("parent", path, parent);
	}

	private String convertCurrentVariable(String path, String current) {
		return convertVariable("current", path, current);
	}

	private String convertVariable(String key, String path, String replace) {

		StringBuilder stb = new StringBuilder(path);
		int strt = 0, end = 0;
		//String rep = "";
		if (key.equals("parent")) {
			strt = path.indexOf(PARENT_DIR_VAR);
			//rep = PARENT_DIR_VAR;
			end = strt + PARENT_DIR_VAR.length();
		} else if (key.equals("current")) {
			strt = path.indexOf(CURRENT_DIR_VAR);
			//rep = CURRENT_DIR_VAR;
			end = strt + CURRENT_DIR_VAR.length();
		}

		try {
			stb = stb.replace(strt, end, replace);
		} catch (StringIndexOutOfBoundsException ex) {
			//System.err.println("could not find replacements for '" + rep
			//		+ "' in " + path + " on " + makeFile);
			//System.err.println("aborted");
			//System.exit(0);
			return path;
		}

		return stb.toString();
	}

	public Make getCMake() {
		return cMake;
	}
}
