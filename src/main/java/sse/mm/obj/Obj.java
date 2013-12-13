package sse.mm.obj;

public class Obj {

	private String parent;
	private String current;
	private String key;
	private String path;

	public Obj() {
		parent = "";
		current = "";
		key = "";
		path = "";
	}

	public Obj(String parent, String current) {
		this.parent = parent;
		this.current = current;
	}

	public Obj(String parent, String current, String key, String path) {
		this.parent = parent;
		this.current = current;
		this.key = key;
		this.path = path;
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
		if (obj instanceof Obj) {
			Obj ob = (Obj) obj;
			return parent.equals(ob.getParent())
					&& current.equals(ob.getCurrent())
					&& key.equals(ob.getKey()) && path.equals(ob.getPath());
		}

		return false;
	}

	@Override
	public String toString() {
		return "{OBJ key=" + key + " path=" + path + " parent=" + parent
				+ " current=" + current + "}";
	}
}
