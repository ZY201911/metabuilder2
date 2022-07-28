package diagram;

import java.util.ArrayList;

public class Property {
	private String value = "";
	
	public Property(String value) {
		setValue(value);
	}
	
	public Property(Property p) {
		setValue(p.getValue());
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public ArrayList<Attribute> splitAsAttributes() {
		ArrayList<Attribute> resultArrayList = new ArrayList<>();
		String[] strArr = getValue().replace("\n", "").split(";");
		for(int i = 0; i < strArr.length; i++) {
			String[] tmpStrArr = strArr[i].split(":");
			Attribute aAttribute = null;
			if(tmpStrArr.length <= 1) {
				aAttribute = new Attribute(tmpStrArr[0], "");
			}
			else if(tmpStrArr.length == 2) {
				aAttribute = new Attribute(tmpStrArr[0], tmpStrArr[1]);
			}
			else {
				String secondString = "";
				for(int j = 1; j < tmpStrArr.length; j++) {
					secondString += tmpStrArr[j];
				}
				aAttribute = new Attribute(tmpStrArr[0], secondString);
			}
			resultArrayList.add(aAttribute);
		}
		return resultArrayList;
	}
	
	public ArrayList<Method> splitAsMethods() {
		ArrayList<Method> resultArrayList = new ArrayList<>();
		String[] strArr = getValue().replace("\n", "").split(";");
		for(int i = 0; i < strArr.length; i++) {
			String[] tmpStrArr = strArr[i].split("\\)");
			String methodType = "";
			if(tmpStrArr.length == 2) {
				methodType = tmpStrArr[1].replace(":", "");
			}
			else if(tmpStrArr.length >= 2) {
				for(int j = 1; j < tmpStrArr.length; j++) {
					methodType += tmpStrArr[j];
				}
			}
			String[] methodNameAndParameters = tmpStrArr[0].split("\\(");
			String methodName = methodNameAndParameters[0];
			ArrayList<Attribute> methodParameters = new ArrayList<>();
			if(methodNameAndParameters.length >= 2) {
				methodParameters = splitAsAttributesByComma(methodNameAndParameters[1]);
			}
			Method aMethod = new Method(methodType, methodName, methodParameters);
			resultArrayList.add(aMethod);
		}
		return resultArrayList;
	}
	
	private ArrayList<Attribute> splitAsAttributesByComma(String value) {
		ArrayList<Attribute> resultArrayList = new ArrayList<>();
		String[] strArr = value.replace("\n", "").split(",");
		for(int i = 0; i < strArr.length; i++) {
			String[] tmpStrArr = strArr[i].split(":");
			Attribute aAttribute = null;
			if(tmpStrArr.length <= 1) {
				aAttribute = new Attribute(tmpStrArr[0], "");
			}
			else if(tmpStrArr.length == 2) {
				aAttribute = new Attribute(tmpStrArr[0], tmpStrArr[1]);
			}
			else {
				String secondString = "";
				for(int j = 1; j < tmpStrArr.length; j++) {
					secondString += tmpStrArr[j];
				}
				aAttribute = new Attribute(tmpStrArr[0], secondString);
			}
			resultArrayList.add(aAttribute);
		}
		return resultArrayList;
	}
	
	public ArrayList<String> splitAsLiterals() {
		ArrayList<String> resultArrayList = new ArrayList<>();
		String[] strArr = getValue().replace("\n", "").split(";");
		for(int i = 0; i < strArr.length; i++) {
			resultArrayList.add(strArr[i]);
		}
		return resultArrayList;
	}
}
