package diagram;

import static resources.MetaBuilderResources.RESOURCES;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public abstract class Element implements Cloneable {
	private Diagram diagram = null;
	private Optional<Diagram> aDiagram = Optional.empty();
	private HashMap<String, Property> properties;
	
	Element() {
		buildProperties();
	}
	
	public void buildProperties() {
		properties = new HashMap<>();
	}
	
	public HashMap<String, Property> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, Property> properties) {
		this.properties = properties;
	}
	
	public void putProperty(String key, Property pProperty) {
		properties.put(key, pProperty);
	}
	
	public ArrayList<Property> getPropertiesList() {
		return new ArrayList<>(properties.values());
	}

	public Diagram getDiagram() {
		return diagram;
	}
	
	public void setDiagram(Diagram pDiagram) {
		diagram = pDiagram;
	}
	
	public Element clone() {
		try {
			Element e = (Element)super.clone();
			HashMap<String, Property> oldProperties = e.getProperties();
			e.setProperties(new HashMap<String, Property>());
			for(String key : oldProperties.keySet()) {
				Property p = new Property(oldProperties.get(key));
				e.putProperty(key, p);
			}
			return e;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public Optional<Diagram> getOptionalDiagram() {
		return aDiagram;
	}
	
	public final void attach(Diagram pDiagram)
	{
		assert pDiagram != null;
		aDiagram = Optional.of(pDiagram);
	}
	
	public final void detach()
	{
		aDiagram = Optional.empty();
	}
}
