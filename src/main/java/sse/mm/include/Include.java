package sse.mm.include;

public class Include {

	private String parent;
	private String key;
	private String path;

	public Include() {
		parent = "";
		key = "";
		path = "";
	}

	public Include(String parent, String key, String path) {
		this.parent = parent;
		this.key = key;
		this.path = path;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Include) {
			Include inc = (Include) obj;
			return parent.equals(inc.getParent()) && key.equals(inc.getKey())
					&& path.equals(inc.getPath());
		}

		return false;
	}

	@Override
	public String toString() {
		return "{INC parent=" + parent + " key=" + key + " path=" + path + "}";
	}
}
