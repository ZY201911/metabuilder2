package file;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import diagram.Association;
import diagram.BClass;
import diagram.Composition;
import diagram.DataType;
import diagram.Diagram;
import diagram.Enumeration;
import diagram.Generalization;
import diagram.NamedElement;
import diagram.Package;
import diagram.Property;
import diagram.Relationship;
import geom.Point;

public final class XmlDecoder {
	
	static Document document = null;
	static HashMap<Integer, NamedElement> namedElementMap = new HashMap<>();
	
	private XmlDecoder() {}
	
	/**
	 * @param pDiagram A XML object that encodes the diagram.
	 * @return The decoded diagram.
	 * @throws DeserializationException If it's not possible to decode the object into a valid diagram.
	 */
	public static Diagram decode(File pFile)
	{
		assert pFile != null;
		
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read(pFile);
			Element root = document.getRootElement();
		    List<Element> eleList = root.elements();
		    
		    Diagram diagram = new Diagram();
		    
		    for(Element element : eleList) {
		    	decodeNamedElement(element, diagram);
		    }
		    
		    List<Node> allList = document.selectNodes("descendant::*");
		    for(Node aNode : allList) {
		    	decodeRelationship((Element)aNode, diagram);
		    }
		    
		    return diagram;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static void decodeNamedElement(Element curElement, Diagram pDiagram) {
		if(curElement.getName().equals("packagedElement")) {
			String xmiType = curElement.attribute("type").getValue();
			if(xmiType.equals("BClass")) {
				BClass bClass = new BClass(false, false);
				Attribute tmpAttribute = curElement.attribute("isAbstract");
				if(tmpAttribute != null && tmpAttribute.getValue() == "true") {
					bClass.setIsAbstract(true);
				}
				tmpAttribute = curElement.attribute("isInterface");
				if(tmpAttribute != null && tmpAttribute.getValue() == "true") {
					bClass.setIsInterface(true);
				}
				tmpAttribute = curElement.attribute("x");
				Attribute tmpAttribute2 = curElement.attribute("y");
				if(tmpAttribute != null && tmpAttribute2 != null) {
					int x = Integer.parseInt(tmpAttribute.getValue());
					int y = Integer.parseInt(tmpAttribute2.getValue());
					bClass.setPosition(new Point(x, y));
				}
				else {
					bClass.setPosition(new Point(0, 0));
				}
				attr2Properties(bClass, curElement);
				tmpAttribute = curElement.attribute("id");
				if(tmpAttribute != null) {
					namedElementMap.put(Integer.parseInt(tmpAttribute.getValue()), bClass);
				}
				pDiagram.addNamedElement(bClass);
			}
			else if(xmiType.equals("Package")) {
				Package aPackage = new Package();
				Attribute tmpAttribute = curElement.attribute("x");
				Attribute tmpAttribute2 = curElement.attribute("y");
				if(tmpAttribute != null && tmpAttribute2 != null) {
					int x = Integer.parseInt(tmpAttribute.getValue());
					int y = Integer.parseInt(tmpAttribute2.getValue());
					aPackage.setPosition(new Point(x, y));
				}
				else {
					aPackage.setPosition(new Point(0, 0));
				}
				attr2Properties(aPackage, curElement);
				List<Element> childrenElements = curElement.elements();
				if(childrenElements.size() > 0) {
					for(Element childElement : childrenElements) {
						decodeChildElement(childElement, aPackage, pDiagram);
					}
				}
				tmpAttribute = curElement.attribute("id");
				if(tmpAttribute != null) {
					namedElementMap.put(Integer.parseInt(tmpAttribute.getValue()), aPackage);
				}
				pDiagram.addNamedElement(aPackage);
			}
			else if(xmiType.equals("DataType")) {
				DataType aDataType = new DataType();
				Attribute tmpAttribute = curElement.attribute("x");
				Attribute tmpAttribute2 = curElement.attribute("y");
				if(tmpAttribute != null && tmpAttribute2 != null) {
					int x = Integer.parseInt(tmpAttribute.getValue());
					int y = Integer.parseInt(tmpAttribute2.getValue());
					aDataType.setPosition(new Point(x, y));
				}
				else {
					aDataType.setPosition(new Point(0, 0));
				}
				attr2Properties(aDataType, curElement);
				tmpAttribute = curElement.attribute("id");
				if(tmpAttribute != null) {
					namedElementMap.put(Integer.parseInt(tmpAttribute.getValue()), aDataType);
				}
				pDiagram.addNamedElement(aDataType);
			}
			else if(xmiType.equals("Enumeration")) {
				Enumeration aEnumeration = new Enumeration();
				Attribute tmpAttribute = curElement.attribute("x");
				Attribute tmpAttribute2 = curElement.attribute("y");
				if(tmpAttribute != null && tmpAttribute2 != null) {
					int x = Integer.parseInt(tmpAttribute.getValue());
					int y = Integer.parseInt(tmpAttribute2.getValue());
					aEnumeration.setPosition(new Point(x, y));
				}
				else {
					aEnumeration.setPosition(new Point(0, 0));
				}
				attr2Properties(aEnumeration, curElement);
				tmpAttribute = curElement.attribute("id");
				if(tmpAttribute != null) {
					namedElementMap.put(Integer.parseInt(tmpAttribute.getValue()), aEnumeration);
				}
				pDiagram.addNamedElement(aEnumeration);
			}
		}
	}
	
	private static void decodeChildElement(Element childElement, Package parent, Diagram pDiagram) {
		if(childElement.getName().equals("packagedElement")) {
			String xmiType = childElement.attribute("type").getValue();
			if(xmiType.equals("BClass")) {
				BClass bClass = new BClass(false, false);
				Attribute tmpAttribute = childElement.attribute("isAbstract");
				if(tmpAttribute != null && tmpAttribute.getValue() == "true") {
					bClass.setIsAbstract(true);
				}
				tmpAttribute = childElement.attribute("isInterface");
				if(tmpAttribute != null && tmpAttribute.getValue() == "true") {
					bClass.setIsInterface(true);
				}
				tmpAttribute = childElement.attribute("x");
				Attribute tmpAttribute2 = childElement.attribute("y");
				if(tmpAttribute != null && tmpAttribute2 != null) {
					int x = Integer.parseInt(tmpAttribute.getValue());
					int y = Integer.parseInt(tmpAttribute2.getValue());
					bClass.setPosition(new Point(x, y));
				}
				else {
					bClass.setPosition(new Point(0, 0));
				}
				attr2Properties(bClass, childElement);
				bClass.setDiagram(pDiagram);
				tmpAttribute = childElement.attribute("id");
				if(tmpAttribute != null) {
					namedElementMap.put(Integer.parseInt(tmpAttribute.getValue()), bClass);
				}
				parent.addChild(bClass);
			}
			else if(xmiType.equals("Package")) {
				Package aPackage = new Package();
				Attribute tmpAttribute = childElement.attribute("x");
				Attribute tmpAttribute2 = childElement.attribute("y");
				if(tmpAttribute != null && tmpAttribute2 != null) {
					int x = Integer.parseInt(tmpAttribute.getValue());
					int y = Integer.parseInt(tmpAttribute2.getValue());
					aPackage.setPosition(new Point(x, y));
				}
				else {
					aPackage.setPosition(new Point(0, 0));
				}
				attr2Properties(aPackage, childElement);
				List<Element> childrenElements = childElement.elements();
				if(childrenElements.size() > 0) {
					for(Element child : childrenElements) {
						decodeChildElement(child, aPackage, pDiagram);
					}
				}
				aPackage.setDiagram(pDiagram);
				tmpAttribute = childElement.attribute("id");
				if(tmpAttribute != null) {
					namedElementMap.put(Integer.parseInt(tmpAttribute.getValue()), aPackage);
				}
				parent.addChild(aPackage);
			}
			else if(xmiType.equals("DataType")) {
				DataType aDataType = new DataType();
				Attribute tmpAttribute = childElement.attribute("x");
				Attribute tmpAttribute2 = childElement.attribute("y");
				if(tmpAttribute != null && tmpAttribute2 != null) {
					int x = Integer.parseInt(tmpAttribute.getValue());
					int y = Integer.parseInt(tmpAttribute2.getValue());
					aDataType.setPosition(new Point(x, y));
				}
				else {
					aDataType.setPosition(new Point(0, 0));
				}
				attr2Properties(aDataType, childElement);
				aDataType.setDiagram(pDiagram);
				tmpAttribute = childElement.attribute("id");
				if(tmpAttribute != null) {
					namedElementMap.put(Integer.parseInt(tmpAttribute.getValue()), aDataType);
				}
				parent.addChild(aDataType);
			}
			else if(xmiType.equals("Enumeration")) {
				Enumeration aEnumeration = new Enumeration();
				Attribute tmpAttribute = childElement.attribute("x");
				Attribute tmpAttribute2 = childElement.attribute("y");
				if(tmpAttribute != null && tmpAttribute2 != null) {
					int x = Integer.parseInt(tmpAttribute.getValue());
					int y = Integer.parseInt(tmpAttribute2.getValue());
					aEnumeration.setPosition(new Point(x, y));
				}
				else {
					aEnumeration.setPosition(new Point(0, 0));
				}
				attr2Properties(aEnumeration, childElement);
				aEnumeration.setDiagram(pDiagram);
				tmpAttribute = childElement.attribute("id");
				if(tmpAttribute != null) {
					namedElementMap.put(Integer.parseInt(tmpAttribute.getValue()), aEnumeration);
				}
				parent.addChild(aEnumeration);
			}
		}
	}
	
	private static void decodeRelationship(Element curElement, Diagram pDiagram) {
		if(curElement.getName().equals("association")) {
			Association association = new Association(false, false);
			Attribute tmpAttribute = curElement.attribute("start");
			if(tmpAttribute != null) {
				NamedElement startNamedElement = namedElementMap.get(Integer.parseInt(tmpAttribute.getValue()));
				association.setStart(startNamedElement);
			}
			tmpAttribute = curElement.attribute("end");
			if(tmpAttribute != null) {
				NamedElement endNamedElement = namedElementMap.get(Integer.parseInt(tmpAttribute.getValue()));
				association.setEnd(endNamedElement);
			}
			attr2Properties(association, curElement);
			pDiagram.addRelationship(association);
		}
		else if(curElement.getName().equals("generalization")) {
			Generalization generalization = new Generalization();
			Attribute tmpAttribute = curElement.attribute("start");
			if(tmpAttribute != null) {
				NamedElement startNamedElement = namedElementMap.get(Integer.parseInt(tmpAttribute.getValue()));
				generalization.setStart(startNamedElement);
			}
			tmpAttribute = curElement.attribute("end");
			if(tmpAttribute != null) {
				NamedElement endNamedElement = namedElementMap.get(Integer.parseInt(tmpAttribute.getValue()));
				generalization.setEnd(endNamedElement);
			}
			attr2Properties(generalization, curElement);
			pDiagram.addRelationship(generalization);
		}
		else if(curElement.getName().equals("composition")) {
			Composition composition = new Composition();
			Attribute tmpAttribute = curElement.attribute("start");
			if(tmpAttribute != null) {
				NamedElement startNamedElement = namedElementMap.get(Integer.parseInt(tmpAttribute.getValue()));
				composition.setStart(startNamedElement);
			}
			tmpAttribute = curElement.attribute("end");
			if(tmpAttribute != null) {
				NamedElement endNamedElement = namedElementMap.get(Integer.parseInt(tmpAttribute.getValue()));
				composition.setEnd(endNamedElement);
			}
			attr2Properties(composition, curElement);
			pDiagram.addRelationship(composition);
		}
	}
	
	private static void attr2Properties(NamedElement pNamedElement, Element curElement) {
		HashMap<String, Property> nProperties = new HashMap<>();
		Attribute tmpAttribute = curElement.attribute("name");
		if(tmpAttribute != null) {
			nProperties.put("name", new Property(tmpAttribute.getValue()));
		}
		
		List<Node> tmpList = null;
		
		if(pNamedElement instanceof BClass) {
			tmpList = curElement.selectNodes("child::ownedAttribute");
			String attrValueString = joinToAttrString(tmpList);
			nProperties.put("attributes", new Property(attrValueString));
			
			tmpList = curElement.selectNodes("child::ownedOperation");
			String methodValueString = joinToMethodString(tmpList);
			nProperties.put("methods", new Property(methodValueString));
		}
		else if(pNamedElement instanceof Enumeration) {
			tmpList = curElement.selectNodes("child::ownedLiteral");
			String literalValueString = joinToLiteralString(tmpList);
			nProperties.put("literals", new Property(literalValueString));
		}
		
		pNamedElement.setProperties(nProperties);
	}
	
	private static void attr2Properties(Relationship pRelationship, Element curElement) {
		HashMap<String, Property> nProperties = new HashMap<>();
		Attribute tmpAttribute = null;
		
		Element tmpElement = (Element)curElement.selectSingleNode("child::lowerValue");
		if(tmpElement != null) {
			tmpAttribute = tmpElement.attribute("value");
			if(tmpAttribute != null) {
				String startLabelValueString = tmpAttribute.getValue();
				nProperties.put("startLabel", new Property(startLabelValueString));
			}
		}
		else {
			nProperties.put("startLabel", new Property(""));
		}
		
		tmpElement = (Element)curElement.selectSingleNode("child::upperValue");
		if(tmpElement != null) {
			tmpAttribute = tmpElement.attribute("value");
			if(tmpAttribute != null) {
				String endLabelValueString = tmpAttribute.getValue();
				nProperties.put("endLabel", new Property(endLabelValueString));
			}
		}
		else {
			nProperties.put("endLabel", new Property(""));
		}
		
		tmpElement = (Element)curElement.selectSingleNode("child::relationshipName");
		if(tmpElement != null) {
			tmpAttribute = tmpElement.attribute("value");
			if(tmpAttribute != null) {
				String midLabelValueString = tmpAttribute.getValue();
				nProperties.put("midLabel", new Property(midLabelValueString));
			}
		}
		else {
			nProperties.put("midLabel", new Property(""));
		}
		
		tmpAttribute = curElement.attribute("direction");
		if(tmpAttribute != null) {
			if(tmpAttribute.getValue().equals("unidirection")) {
				nProperties.put("direction", new Property("UniDirection"));
				((Association)pRelationship).setUniDirection(true);
			}
			else if(tmpAttribute.getValue().equals("bidirection")) {
				nProperties.put("direction", new Property("BiDirection"));
				((Association)pRelationship).setBiDirection(true);
			}
			else {
				nProperties.put("direction", new Property("NoDirection"));
			}
		}
		
		pRelationship.setProperties(nProperties);
	}
	
	private static String joinToAttrString(List<Node> pAttrList) {
		String result = "";
		for(Node node : pAttrList) {
			Attribute tmpAttribute = ((Element)node).attribute("name");
			if(tmpAttribute != null) {
				result += tmpAttribute.getValue();
			}
			tmpAttribute = ((Element)node).attribute("type");
			if(tmpAttribute != null && !tmpAttribute.getValue().equals("")) {
				result += (":" + tmpAttribute.getValue());
			}
			result += ";\n";
		}
		if(result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	
	private static String joinToMethodString(List<Node> pMethodList) {
		String result = "";
		for(Node node : pMethodList) {
			Attribute tmpAttribute = ((Element)node).attribute("name");
			if(tmpAttribute != null) {
				result += tmpAttribute.getValue();
			}
			String parameterString = joinToAttrStringWithComma(((Element)node).elements());
			result += "(" + parameterString + ")";
			tmpAttribute = ((Element)node).attribute("type");
			if(tmpAttribute != null && !tmpAttribute.getValue().equals("")) {
				result += (":" + tmpAttribute.getValue());
			}
			result += ";\n";
		}
		if(result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	
	private static String joinToAttrStringWithComma(List<Element> pAttrList) {
		String result = "";
		for(Node node : pAttrList) {
			Attribute tmpAttribute = ((Element)node).attribute("name");
			if(tmpAttribute != null) {
				result += tmpAttribute.getValue();
			}
			tmpAttribute = ((Element)node).attribute("type");
			if(tmpAttribute != null && !tmpAttribute.getValue().equals("")) {
				result += (":" + tmpAttribute.getValue());
			}
			result += ",";
		}
		if(result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	
	private static String joinToLiteralString(List<Node> pLiteraList) {
		String result = "";
		for(Node node : pLiteraList) {
			Attribute tmpAttribute = ((Element)node).attribute("value");
			if(tmpAttribute != null) {
				result += tmpAttribute.getValue();
			}
			result += ";\n";
		}
		if(result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
}
