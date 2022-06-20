package diagram;

public abstract class Relationship extends Element {
	private NamedElement start = null;
	private NamedElement end = null;
	
	public NamedElement getStart() {
		return start;
	}
	public NamedElement getEnd() {
		return end;
	}
	public void setStart(NamedElement pStart) {
		start = pStart;
	}
	public void setEnd(NamedElement pEnd) {
		end = pEnd;
	}
	
	public void connect(NamedElement pStart, NamedElement pEnd, Diagram pDiagram) {
		setDiagram(pDiagram);
		setStart(pStart);
		setEnd(pEnd);
	}
	
	@Override
	public Relationship clone() {
		return (Relationship)super.clone();
	}
}
