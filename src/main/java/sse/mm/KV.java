package sse.mm;

public class KV {

	private String kind;
	private String key;
	private String path;

	public KV() {
		kind = "";
		key = "";
		path = "";
	}

	public KV(String kind) {
		this.kind = kind;
		this.key = "";
		this.path = "";
	}

	public KV(String kind, String key, String path) {
		this.kind = kind;
		this.key = key;
		this.path = path;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String format(){
		return key + "=" + path + "\n";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof KV) {
			KV kv = (KV) obj;
			return kind.equals(kv.getKind()) && key.equals(kv.getKey())
					&& path.equals(kv.getPath());

		}

		return false;
	}

	@Override
	public String toString() {
		return "{" + key + " key=" + key + " path=" + path + "}\n";
	}
}
