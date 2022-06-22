package diagram;

import java.util.ArrayList;

public class BClass extends Classifier {
	private boolean isAbstract = false;
	private boolean isInterface = false;
	private String attributesString = "";
	private ArrayList<Attribute> attributes = new ArrayList<>();
	private String methodsString = "";
	private ArrayList<Method> methods = new ArrayList<>();
	private BClass superClass = null;
	private ArrayList<BClass> superInterfaces = new ArrayList<>();
	private ArrayList<BClass> memberClasses = new ArrayList<>();
	
	@Override
	public void buildProperties() {
		super.buildProperties();
		putProperty("attributes", new Property(""));
		putProperty("methods", new Property(""));
	}
	
	BClass(boolean pAbstract, boolean pInterface) {
		isAbstract = pAbstract;
		isInterface = pInterface;
	}
	
	BClass(Element e) {
		this.isAbstract = ((BClass)e).getIsAbstract();
		this.isInterface = ((BClass)e).getIsInterface();
	}

	public boolean getIsAbstract() {
		return isAbstract;
	}
	public boolean getIsInterface() {
		return isInterface;
	}
	public String getAttributesString() {
		setAttributes(getProperties().get("attributes").getValue());
		return attributesString;
	}
	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}
	public String getMethodsString() {
		setMethods(getProperties().get("methods").getValue());
		return methodsString;
	}
	public ArrayList<Method> getMethods() {
		return methods;
	}
	public BClass getSuperClass() {
		return superClass;
	}
	public ArrayList<BClass> getSuperInterfaces() {
		return superInterfaces;
	}
	public ArrayList<BClass> getMemberClasses() {
		return memberClasses;
	}
	
	public void setIsAbstract(boolean pIsAbstract) {
		isAbstract = pIsAbstract;
	}
	public void setIsInterface(boolean pIsInterface) {
		isInterface = pIsInterface;
	}
	public void setAttributes(String pAttributes) {
		attributesString = pAttributes;
	}
	public void setAttributes(ArrayList<Attribute> pAttributes) {
		attributes = pAttributes;
	}
	public void setMethods(String pMethods) {
		methodsString = pMethods;
	}
	public void setMethods(ArrayList<Method> pMethods) {
		methods = pMethods;
	}
	public void setSuperClass(BClass pSuperClass) {
		superClass = pSuperClass;
	}
	public void setSuperInterfaces(ArrayList<BClass> pSuperInterfaces) {
		superInterfaces = pSuperInterfaces;
	}
	public void setMemberClasses(ArrayList<BClass> pMemberClasses) {
		memberClasses = pMemberClasses;
	}
	
	@Override
	public BClass clone() {
		BClass clone = (BClass)super.clone();
		
		clone.setAttributes(new String(clone.getAttributesString()));
		
		clone.setMethods(new String(clone.getMethodsString()));
		
		ArrayList<Attribute> oldattributes = clone.getAttributes();
		clone.setAttributes(new ArrayList<Attribute>());
		for(Attribute a : oldattributes) {
			clone.getAttributes().add(new Attribute(a));
		}
		
		ArrayList<Method> oldmethods = clone.getMethods();
		clone.setMethods(new ArrayList<Method>());
		for(Method m : oldmethods) {
			clone.getMethods().add(new Method(m));
		}
		
		return clone;
	}
}
