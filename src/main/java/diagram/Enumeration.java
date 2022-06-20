package diagram;

import java.util.ArrayList;

public class Enumeration extends DataType {
	private String literalsString = "";
	private ArrayList<String> literals = new ArrayList<>();
	
	@Override
	public void buildProperties() {
		super.buildProperties();
		putProperty("literals", new Property(""));
	}
	
	public String getLiteralsString() {
		return literalsString;
	}
	public ArrayList<String> getLiterals() {
		return literals;
	}
	public void setLiteralsString(String pLiterals) {
		literalsString = pLiterals;
	}
	public void setLiterals(ArrayList<String> pLiterals) {
		literals = pLiterals;
	}
}