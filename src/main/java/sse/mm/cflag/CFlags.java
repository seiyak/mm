package sse.mm.cflag;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class CFlags {

	private final String key = "CFLAGS";
	private final Set<String> cflags;

	public CFlags() {
		cflags = new LinkedHashSet<String>();
	}

	public void addCFlag(String cflag) {
		cflags.add(cflag);
	}

	public String format(){
		return key + "=" + toStringCFlags() +"\n";
	}
	
	@Override
	public String toString() {
		return "{CFLAGS key=" + key + " cflags=" + toStringCFlags() + "}\n";
	}

	private String toStringCFlags() {

		String str = "";
		Iterator<String> itr = cflags.iterator();
		while (itr.hasNext()) {
			str += itr.next() + " ";
		}

		return str;
	}
}
