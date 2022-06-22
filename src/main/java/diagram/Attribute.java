package diagram;

public class Attribute {
	private String type = "";
	private String name = "";
	
	
	public Attribute(Attribute a) {
		this.setName(a.getName());
		this.setType(a.getType());
	}
	public String getType() {
		return type;
	}
	public void setType(String pType) {
		type = pType;
	}
	public String getName() {
		return name;
	}
	public void setName(String pName) {
		name = pName;
	}
}
