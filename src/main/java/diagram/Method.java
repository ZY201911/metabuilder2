package diagram;

import java.util.ArrayList;

public class Method {
	private String type = "";
	private String name = "";
	private ArrayList<Attribute> parameters = new ArrayList<>();
	
	public Method(Method m) {
		this.setName(m.getName());
		this.setType(m.getType());
		ArrayList<Attribute> oldattributes = m.getParameters();
		this.setParameters(new ArrayList<Attribute>());
		for(Attribute a : oldattributes) {
			this.getParameters().add(new Attribute(a));
		}
	}
	public String getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public ArrayList<Attribute> getParameters() {
		return parameters;
	}
	
	public void setType(String pType) {
		type = pType;
	}
	public void setName(String pName) {
		name = pName;
	}
	public void setParameters(ArrayList<Attribute> pParameters) {
		parameters = pParameters;
	}
}
