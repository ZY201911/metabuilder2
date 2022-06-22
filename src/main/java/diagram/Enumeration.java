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
	
	@Override
	public Enumeration clone() {
		Enumeration clone = (Enumeration)super.clone();
		clone.setLiteralsString(new String(clone.getLiteralsString()));
		ArrayList<String> oldliterals = clone.getLiterals();
		clone.setLiterals(new ArrayList<String>());
		for(String s : oldliterals) {
			clone.getLiterals().add(new String(s));
		}
		return clone;
	}
}
