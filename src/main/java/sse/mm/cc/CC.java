package sse.mm.cc;

public class CC {

	private String key;
	private String cc;

	public CC() {
		key = "";
		cc = "";
	}

	public CC(String key, String cc) {
		this.key = key;
		this.cc = cc;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String format(){
		return key + "=" + cc + "\n";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CC) {
			CC c = (CC) obj;
			return key.equals(c.getKey()) && cc.equals(c.getCc());
		}

		return false;
	}

	@Override
	public String toString() {
		return "{CC key=" + key + " cc=" + cc + "}\n";
	}
}
