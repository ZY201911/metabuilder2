package diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import diagram.manager.DiagramBuilder;
import viewers.diagrams.DiagramViewer;

public class Diagram {
	private String name = "";
	private ArrayList<NamedElement> namedElements = new ArrayList<>();
	private ArrayList<Relationship> relationships = new ArrayList<>();
	private DiagramViewer diagramViewer = new DiagramViewer();
	private ArrayList<Element> protoTypes = createProtoTypes();
	private DiagramBuilder diagramBuilder = new DiagramBuilder(this);
	
	public ArrayList<Element> createProtoTypes() {
		ArrayList<Element> result = new ArrayList<>();
		result.add(new BClass(false, false));
		result.add(new BClass(true, false));
		result.add(new BClass(false, true));
		result.add(new Package());
		result.add(new DataType());
		result.add(new Enumeration());
		result.add(new Association(false, false));
		result.add(new Generalization());
		result.add(new Composition());
		return result;
	}
	
	public String getName() {
		return name;
	}
	public ArrayList<NamedElement> getNamedElements() {
		return namedElements;
	}
	public ArrayList<Relationship> getRelationships() {
		return relationships;
	}
	public DiagramViewer getDiagramViewer() {
		return diagramViewer;
	}
	public ArrayList<Element> getProtoTypes() {
		return protoTypes;
	}
	public DiagramBuilder getDiagramBuilder() {
		return diagramBuilder;
	}

	public void setName(String pName) {
		name = pName;
	}
	public void setNamedElements(ArrayList<NamedElement> pNamedElement) {
		namedElements = pNamedElement;
	}
	public void setRelationships(ArrayList<Relationship> pRelationships) {
		relationships = pRelationships;
	}
	public void setDiagramViewer(DiagramViewer pDiagramViewer) {
		diagramViewer = pDiagramViewer;
	}
	
	public Iterable<Relationship> RelationshipsConnectedTo(NamedElement pNamedElement)
	{
		assert pNamedElement != null && contains(pNamedElement);
		Collection<Relationship> lReturn = new ArrayList<>();
		for( Relationship Relationship : relationships )
		{
			if( Relationship.getStart() == pNamedElement || Relationship.getEnd() == pNamedElement )
			{
				lReturn.add(Relationship);
			}
		}
		return lReturn;
	}
	
	public boolean contains(Element pElement)
	{
		assert pElement != null;
		if( relationships.contains(pElement) )
		{
			return true;
		}
		for( NamedElement NamedElement : namedElements )
		{
			if( containsNamedElements(NamedElement, pElement) )
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean containsNamedElements(NamedElement pTest, Element pTarget)
	{
		if( pTest == pTarget )
		{
			return true;
		}
		if(pTest instanceof Package) {
			for( NamedElement NamedElement : ((Package)pTest).getChildren() )
			{
				if( containsNamedElements(NamedElement, pTarget) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public void addNamedElement(NamedElement pNamedElement)
	{
		assert pNamedElement != null;
		recursiveAttach(pNamedElement);
		namedElements.add(pNamedElement);
	}

	private void recursiveAttach(NamedElement pNamedElement)
	{
		pNamedElement.attach(this);
		if(pNamedElement instanceof Package) {
			((Package)pNamedElement).getChildren().forEach(this::recursiveAttach);
		}
	}

	private void recursiveDetach(NamedElement pNamedElement)
	{
		pNamedElement.detach();
		if(pNamedElement instanceof Package) {
			((Package)pNamedElement).getChildren().forEach(this::recursiveDetach);
		}
	}

	/**
	 * Removes pNamedElement from the list of root NamedElements in this diagram. Callers must ensure that the removal preserves the
	 * integrity of the diagram.
	 * 
	 * @param pNamedElement The NamedElement to remove.
	 * @pre pNamedElement != null && pNamedElement is contained as a root NamedElement.
	 */
	public void removeNamedElement(NamedElement pNamedElement)
	{
		assert pNamedElement != null && namedElements.contains(pNamedElement);
		recursiveDetach(pNamedElement);
		namedElements.remove(pNamedElement);
	}
	
	/**
	 * Adds pRelationship to the diagram. pRelationship should already be connected to its start and end NamedElements. The Relationship is added to the
	 * end of the list of Relationships.
	 * 
	 * @param pRelationship The Relationship to add.
	 * @pre pRelationship != null && pRelationship.getStart() != null && pRelationship.getEnd() != null && pRelationship.getGraph != null
	 */
	public void addRelationship(Relationship pRelationship)
	{
		assert pRelationship != null && pRelationship.getStart() != null && pRelationship.getEnd() != null && pRelationship.getDiagram() != null;
		pRelationship.getStart().addOwnedRealtionship(pRelationship);
		relationships.add(pRelationship);
	}
	
	/**
	 * Adds pRelationship at index pIndex, and shifts the existing Relationships to the right of the list.
	 * 
	 * @param pIndex Where to add the Relationship.
	 * @param pRelationship The Relationship to add.
	 * @pre pRelationship != null && pIndex >=0 && pIndex < relationships.size()
	 */
	public void addRelationship(int pIndex, Relationship pRelationship)
	{
		assert pRelationship != null && pIndex >= 0 && pIndex <= relationships.size();
		relationships.add(pIndex, pRelationship);
	}
	
	/**
	 * Removes pRelationship from this diagram. Callers must ensure that the removal preserves the integrity of the diagram.
	 * 
	 * @param pRelationship The Relationship to remove.
	 * @pre pRelationship != null && pRelationship is contained in the diagram
	 */
	public void removeRelationship(Relationship pRelationship)
	{
		assert pRelationship != null && relationships.contains(pRelationship);
		relationships.remove(pRelationship);
	}
	
	/**
	 * @param pRelationship
	 *            The Relationship to check.
	 * @return The index of pRelationship in the list of Relationships.
	 * @pre contains(pRelationship)
	 */
	public int indexOf(Relationship pRelationship)
	{
		assert contains(pRelationship);
		return relationships.indexOf(pRelationship);
	}
	
	/**
	 * Recursively reorder the node to be on top of its parent's children. If the node is not a child node or the node
	 * does not have a parent, check if the node is a root node of the diagram and place it on top.
	 * 
	 * @param pNamedElement The node to be placed on top
	 * @pre pNamedElement != null
	 */
	public void placeOnTop(NamedElement pNamedElement)
	{
		assert pNamedElement != null;
		// Certain nodes should not have their order changed
		if( pNamedElement.hasParent() )
		{
			Package parent = pNamedElement.getParent();
			// Move the child node to the top of all other children
			parent.placeLast(pNamedElement);
			// Recursively reorder the node's parent
			placeOnTop(parent);
		}
		else if( contains(pNamedElement) )
		{
			removeNamedElement(pNamedElement);
			addNamedElement(pNamedElement);
		}
	}
	
	/**
	 * Creates a copy of the current diagram. The copy is a completely distinct graph of nodes and edges with the same
	 * topology as this diagram.
	 * 
	 * @return A copy of this diagram. Never null.
	 */
	public Diagram duplicate()
	{
		Diagram copy = new Diagram();
		relationships.forEach(edge -> copy.relationships.add((Relationship)edge.clone()));

		for( NamedElement node : namedElements )
		{
			NamedElement nodeCopy = (NamedElement)node.clone();
			copy.namedElements.add(nodeCopy);
			reassignRelationships(copy.relationships, node, nodeCopy);
		}

		// Reassign diagram
		copy.relationships.forEach(edge -> edge.connect(edge.getStart(), edge.getEnd(), copy));
		for( NamedElement node : copy.namedElements )
		{
			copy.attachNamedElement(node);
		}
		return copy;
	}
	
	/*
	 * Recursively attach the node and all its children to this diagram.
	 */
	private void attachNamedElement(NamedElement pNamedElement)
	{
		pNamedElement.attach(this);
		if(pNamedElement instanceof Package) {
			for( NamedElement child : ((Package)pNamedElement).getChildren() )
			{
				attachNamedElement(child);
			}
		}
	}

	/*
	 * For node pOriginal, go through all edges that refer to it and replace it with pCopy in the edge. Do this
	 * recursively for all children of pOriginal, assuming the same topology for pCopy.
	 */
	private static void reassignRelationships(List<Relationship> pRelationships, NamedElement pOriginal, NamedElement pCopy)
	{
		for( Relationship edge : pRelationships )
		{
			if( edge.getStart() == pOriginal )
			{
				edge.connect(pCopy, edge.getEnd(), edge.getDiagram());
			}
			if( edge.getEnd() == pOriginal )
			{
				edge.connect(edge.getStart(), pCopy, edge.getDiagram());
			}
		}
		if( pOriginal instanceof Package ) {
			List<NamedElement> oldChildren = ((Package)pOriginal).getChildren();
			List<NamedElement> newChildren = ((Package)pCopy).getChildren();
			for( int i = 0; i < oldChildren.size(); i++ )
			{
				reassignRelationships(pRelationships, oldChildren.get(i), newChildren.get(i));
			}
		}
	}
}
