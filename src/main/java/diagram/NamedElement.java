package diagram;

import java.util.Optional;

import geom.Point;

public abstract class NamedElement extends Element {
	private String name = "";
	private Optional<Package> parent = Optional.empty();
	private Point position = new Point(0, 0);
	
	@Override
	public void buildProperties() {
		super.buildProperties();
		putProperty("name", new Property(""));
	}
	
	public String getName() {
		setName(getProperties().get("name").getValue());
		return name;
	}
	public Package getParent() {
		assert hasParent();
		return parent.get();
	}
	public Point getPosition() {
		return position;
	}
	public void setName(String pName) {
		name = pName;
	}
	public void setParent(Package pParent) {
		parent = Optional.of(pParent);
	}
	public void setPosition(Point pPosition) {
		position = pPosition;
	}
	
	public boolean hasParent() {
		return parent.isPresent();
	}
	public void translate(int pDeltaX, int pDeltaY)
	{
		position = new Point( position.getX() + pDeltaX, position.getY() + pDeltaY );
	}
	public void link(Package pNamedElement)
	{
		assert pNamedElement instanceof Package && pNamedElement != null;
		parent = Optional.of(pNamedElement);
	}
	public void unlink()
	{
		assert hasParent();
		parent = Optional.empty();
	}
	
	@Override
	public NamedElement clone() {
		NamedElement clone = (NamedElement)super.clone();
		clone.position = position.copy();
		clone.setName(new String(clone.getName()));
		return clone;
	}
}
