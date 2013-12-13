package sse.main;

import java.io.File;
import java.nio.file.Paths;

import sse.mm.parser.MakeParser;

public class Main {

	public static void main(String[] args){
		File f = new File(Paths.get("").toAbsolutePath().toString());
		if(f.isDirectory()){
			MakeParser parser = null;
			for(File ff: f.listFiles()){
				if(ff.getName().equals("Makefile")){
					parser = new MakeParser(ff.getAbsolutePath());
					break;
				}
			}
			
			if(parser != null){
				parser.modify();
			}else{
				System.err.println("no 'Makefile' was found at '" + f.getAbsolutePath() + "'.");
			}
		}
	}
}
