package file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import metabuilder.MetaBuilder;
import diagram.Association;
import diagram.Attribute;
import diagram.BClass;
import diagram.Composition;
import diagram.Diagram;
import diagram.Generalization;
import diagram.Method;
import diagram.Relationship;
import diagram.NamedElement;
import diagram.Package;
import diagram.Property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.jfinal.template.stat.ast.For;
import com.jfinal.template.stat.ast.If;

/**
 * Converts a graph to Xml notation. The notation includes:
 * * The JetUML version
 * * The graph type
 * * An array of NamedElement encodings
 * * An array of relationship encodings
 */
public final class XmlEncoder
{   
	//创建xml文档对象
  	static Document document = DocumentHelper.createDocument();
  	
	//创建根节点
	static Element root = DocumentHelper.createElement("xmi:XMI");
	
	private XmlEncoder() {}
	
	/**
	 * @param pDiagram The diagram to serialize.
	 */
	@SuppressWarnings("static-access")
	public static void encode(Diagram pDiagram, File pFile) throws FileNotFoundException
	{
		
  		document.setRootElement(root);
  		
  		//添加属性
  		root.addAttribute("xmlns:uml", "http://www.omg.org/spec/UML/20131001");
  		root.addAttribute("xmlns:xmi", "http://www.omg.org/spec/XMI/20131001");
  		
  		//创建子节点
  		SerializationContext context = new SerializationContext(pDiagram);
  		encodeNamedElements(context, root);
  		encodeRelationships(context, root);
  		
  		//设置编码格式
  		OutputFormat format = OutputFormat.createPrettyPrint();
  		format.setEncoding("UTF-8");
  		
  		//将xml输出到控制台
  		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(pFile);
			XMLWriter writer = new XMLWriter(fileWriter, format);  
	        writer.write(document);  
	        writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void encodeNamedElements(SerializationContext pContext, Element curElement)
	{
		for( NamedElement NamedElement : pContext )
		{
			encodeNamedElement(NamedElement, pContext, curElement);
		}
	}
	
	private static void encodeNamedElement(NamedElement pNamedElement, SerializationContext pContext, Element curElement)
	{
		if(!pNamedElement.hasParent()) {
			Element aElement = curElement.addElement("packagedElement");
			aElement.addAttribute("xmi:type", pNamedElement.getClass().getSimpleName());
			properties2Attr(pNamedElement.getProperties(), aElement);
			abstractAndInterface(pNamedElement, curElement);
			aElement.addAttribute("id", pContext.getId(pNamedElement) + "");
			aElement.addAttribute("x", pNamedElement.getPosition().getX() + "");
			aElement.addAttribute("y", pNamedElement.getPosition().getY() + "");
			
			if(pNamedElement instanceof Package) {
				for(NamedElement child : ((Package)pNamedElement).getChildren()) {
					encodeChild(child, pContext, aElement);
				}
			}
		}
	}
	
	private static void abstractAndInterface(NamedElement pNamedElement, Element curElement) {
		if(pNamedElement instanceof BClass) {
			if(((BClass) pNamedElement).getIsAbstract()) {
				curElement.addAttribute("isAbstract", "true");
			}
			else if(((BClass)pNamedElement).getIsInterface()) {
				curElement.addAttribute("inInterface", "true");
			}
		}
	}
	
	private static void encodeChild(NamedElement pNamedElement, SerializationContext pContext, Element curElement)
	{
		Element aElement = curElement.addElement("packagedElement");
		aElement.addAttribute("xmi:type", pNamedElement.getClass().getSimpleName());
		properties2Attr(pNamedElement.getProperties(), aElement);
		abstractAndInterface(pNamedElement, curElement);
		aElement.addAttribute("id", pContext.getId(pNamedElement) + "");
		aElement.addAttribute("x", pNamedElement.getPosition().getX() + "");
		aElement.addAttribute("y", pNamedElement.getPosition().getY() + "");
		
		if(pNamedElement instanceof Package) {
			for(NamedElement child : ((Package)pNamedElement).getChildren()) {
				encodeChild(child, pContext, aElement);
			}
		}
	}

	private static void encodeRelationships(AbstractContext pContext, Element curElement)
	{
		for( Relationship relationship : pContext.pDiagram().getRelationships() )
		{
			if(relationship.getStart().hasParent()) {
				curElement = (Element)document.selectSingleNode("//packagedElement[@id='" + pContext.getId(relationship.getStart()) + "']");
			}
			
			Element aElement = null;
			if(relationship instanceof Association) {
				aElement = curElement.addElement("association");
			}
			else if(relationship instanceof Generalization) {
				aElement = curElement.addElement("generalization");
			}
			else if(relationship instanceof Composition) {
				aElement = curElement.addElement("composition");
			}
			aElement.addAttribute("xmi:type", relationship.getClass().getSimpleName());
			aElement.addAttribute("start", pContext.getId(relationship.getStart()) + "");
			aElement.addAttribute("end", pContext.getId(relationship.getEnd()) + "");
			direction(relationship, aElement);
			
			properties2Attr(relationship.getProperties(), aElement);
		}
	}
	
	private static void direction(Relationship relationship, Element curElement) {
		if(relationship instanceof Association) {
			if(((Association) relationship).getUniDirection()) {
				curElement.addAttribute("direction", "unidirection");
			}
			else if(((Association)relationship).getBiDirection()) {
				curElement.addAttribute("direction", "bidirection");
			}
			else {
				curElement.addAttribute("direction", "nodirection");
			}
		}
	}
	
	private static void properties2Attr(HashMap<String, Property> pProperties, Element curElement)
	{
		Property tempProperty = pProperties.get("name");
		if( tempProperty != null )
		{
			curElement.addAttribute("name", tempProperty.getValue());
		}
		tempProperty = pProperties.get("attributes");
		if( tempProperty != null && tempProperty.getValue() != "")
		{
			ArrayList<Attribute> attrList = tempProperty.splitAsAttributes();
			for(Attribute attr : attrList) {
				Element aElement = curElement.addElement("ownedAttribute");
				aElement.addAttribute("name", attr.getName());
				aElement.addAttribute("type", attr.getType());
			}
			
		}
		tempProperty = pProperties.get("methods");
		if( tempProperty != null && tempProperty.getValue() != "")
		{
			ArrayList<Method> methodList = tempProperty.splitAsMethods();
			for(Method method : methodList) {
				Element aElement = curElement.addElement("ownedOperation");
				aElement.addAttribute("name", method.getName());
				aElement.addAttribute("type", method.getType());
				ArrayList<Attribute> paraList = method.getParameters();
				for(Attribute para : paraList) {
					Element aParameter = aElement.addElement("ownedParameter");
					aParameter.addAttribute("name", para.getName());
					aParameter.addAttribute("type", para.getType());
				}
			}
		}
		tempProperty = pProperties.get("literals");
		if( tempProperty != null && tempProperty.getValue() != "")
		{
			Element aElement = curElement.addElement("ownedLiteral");
			aElement.addAttribute("name", tempProperty.getValue());
		}
		tempProperty = pProperties.get("startLabel");
		if( tempProperty != null && tempProperty.getValue() != "")
		{
			Element aElement = curElement.addElement("upperValue");
			aElement.addAttribute("value", tempProperty.getValue());
		}
		tempProperty = pProperties.get("endLabel");
		if( tempProperty != null && tempProperty.getValue() != "")
		{
			Element aElement = curElement.addElement("lowerValue");
			aElement.addAttribute("value", tempProperty.getValue());
		}
		tempProperty = pProperties.get("midLabel");
		if( tempProperty != null && tempProperty.getValue() != "")
		{
			Element aElement = curElement.addElement("midLabel");
			aElement.addAttribute("value", tempProperty.getValue());
		}
	}
}
