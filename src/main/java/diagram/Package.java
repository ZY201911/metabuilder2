package diagram;

import java.util.ArrayList;

public class Package extends NamedElement {
	
	private ArrayList<NamedElement> children = new ArrayList<>();
	
	
	public ArrayList<NamedElement> getChildren() {
		return children;
	}
	
	public void setChildren(ArrayList<NamedElement> pChildren) {
		children = pChildren;
	}
	public void addChild(int pIndex, NamedElement pNamedElement)
	{
		assert pNamedElement != null;
		assert pIndex >=0 && pIndex <= children.size();
		if(pNamedElement.hasParent())
		{
			pNamedElement.getParent().removeChild(pNamedElement);
		}
		children.add(pIndex, pNamedElement);
		pNamedElement.link(this);
	}

	public void addChild(NamedElement pNamedElement)
	{
		assert pNamedElement != null;
		addChild(children.size(), pNamedElement);
	}

	public void removeChild(NamedElement pNamedElement)
	{
		assert getChildren().contains(pNamedElement);
		assert pNamedElement.getParent() == this;
		children.remove(pNamedElement);
		pNamedElement.unlink();
	}
	public void placeLast(NamedElement pNamedElement)
	{
		assert pNamedElement != null;
		assert getChildren().contains(pNamedElement);
		removeChild(pNamedElement);
		addChild(pNamedElement);
	}
	
	@Override
	public Package clone() {
		Package cloned = (Package) super.clone();
		cloned.children = new ArrayList<>();
		for( NamedElement child : children )
		{
			// We can't use addChild(...) here because of the interaction with the original parent.
			NamedElement clonedChild = child.clone();
			clonedChild.link(cloned);
			cloned.children.add(clonedChild);
		}
		return cloned;
	}
}
