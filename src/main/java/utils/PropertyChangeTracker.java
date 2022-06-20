package utils;

import java.util.HashMap;
import java.util.Map;

import diagram.Element;
import diagram.Property;
import diagram.manager.CompoundOperation;
import diagram.manager.SimpleOperation;

/**
 * Tracks modification to the properties of a DiagramElement.
 * Should be discarded after a call to stopTracking().
 */
public class PropertyChangeTracker 
{
	private HashMap<String, Property> oldProperties = new HashMap<>();
	private HashMap<String, Property> properties = new HashMap<>();
	
	/**
	 * Creates a new tracker for pEdited.
	 *  
	 * @param pEdited The element to track.
	 * @pre pEdited != null;
	 */
	public PropertyChangeTracker(Element pEdited)
	{
		assert pEdited != null;
		properties = pEdited.getProperties();
	}

	/**
	 * Makes a snapshot of the properties values of the tracked element.
	 */
	public void startTracking()
	{
		for(Map.Entry<String, Property> entry : properties.entrySet())
		{
			oldProperties.put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Creates and returns a CompoundOperation that represents any change
	 * in properties detected between the time startTracking
	 * and stopTracking were called.
	 * 
	 * @return A CompoundOperation describing the property changes.
	 */
	public CompoundOperation stopTracking()
	{
		CompoundOperation operation = new CompoundOperation();
		for( Map.Entry<String, Property> entry : properties.entrySet() )
		{
			if( oldProperties.get(entry.getKey()).getValue() != entry.getValue().getValue())
			{
				final Property newValue = entry.getValue();
				final Property oldValue = oldProperties.get(entry.getKey());
				operation.add(new SimpleOperation(
						()-> entry.setValue(newValue),
						()-> entry.setValue(oldValue)));
			}
		}
		return operation;
	}
}
