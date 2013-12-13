package sse.mm.directory;

import java.io.File;

public class Directory {

	private static final String SSE = "/sse/";
	private static final String USER_HOME = "user.dir";
	private String parent;
	private String current;

	public Directory(){
		parent = "";
		current = "";
	}
	
	public Directory(String parent, String current) {
		this.parent = parent;
		this.current = current;
	}
	
	public boolean setPCDirectoriesFrom(String makeFile,String rootMake){
		
		if(makeFile == null || makeFile.equals("")){
			System.err.println("makeFile is invalid.");
			if(!guessPCDirectories()){
				return false;
			}
			
			return true;
		}
		
		File f = new File(makeFile);
		if(!f.exists()){
			//System.err.println("makeFile doesn't exist at: " + makeFile);
			if(!guessPCDirectories()){
				
				//System.err.println("about to check rootMake at: " + rootMake);
				int st = rootMake.indexOf(SSE);
				if(st == -1){
					return false;
				}
				
				parent = rootMake.substring(0,st);
				current = rootMake.substring(st + 1,rootMake.lastIndexOf("/"));
			}
			
			return true;
		}
		
		int strt = f.getAbsolutePath().indexOf(SSE);
		if(strt == -1){
			File ff = new File(rootMake);
			strt = ff.getAbsolutePath().indexOf(SSE);
			parent = ff.getAbsolutePath().substring(0, strt);
			current = ff.getAbsolutePath().substring(strt + 1,ff.getAbsolutePath().lastIndexOf("/"));
		}else{
			parent = f.getAbsolutePath().substring(0, strt);
			current = f.getAbsolutePath().substring(strt + 1,f.getAbsolutePath().lastIndexOf("/"));
		}
		
		return true;
	}
	
	public boolean guessPCDirectories(){
		
		File cd = new File(System.getProperty(USER_HOME));
		if(!cd.exists()){
			for(File f: cd.listFiles()){
				if(f.isDirectory()){
					if(f.getName().equals(SSE)){
						parent = cd.getAbsolutePath();
						return true;
					}else if(f.getName().contains(SSE)){
						parent = f.getAbsolutePath();
						return true;
					}
				}
			}
		}
		//System.err.println("could not guess parent and current directories at '" + System.getProperty(USER_HOME) + "'");
		return false;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public String formatParent(){
		return "PARENT_DIR=" + parent + "\n";
	}
	
	public String formatCurrent(){
		return "CURRENT_DIR=" + current + "\n";
	}
	
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Directory) {
			Directory dir = (Directory) obj;
			return parent.equals(dir.getParent())
					&& current.equals(dir.getCurrent());
		}

		return false;
	}

	@Override
	public String toString() {
		return "{DIR parent directory=" + parent + " current=" + current + "}\n";
	}
}
