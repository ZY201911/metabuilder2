package viewers.namedelements;

 import java.util.IdentityHashMap;
 import java.util.Map;
 import java.util.function.Function;

 import diagram.NamedElement;
 import geom.Rectangle;

 /**
  * Stores the bounds of NamedElements. 
  */
 public class NamedElementStorage 
 {
 	private Map<NamedElement, Rectangle> aNamedElementBounds = new IdentityHashMap<>();
 	private boolean aIsActivated = false;

 	/**
 	 * Returns the bounds of the current NamedElement either from the storage or from the calculator.
 	 * @param pNamedElement the NamedElement of interest.
 	 * @param pBoundCalculator the bound calculator.
 	 * @return the bounds of pNamedElement. 
 	 */
 	public Rectangle getBounds(NamedElement pNamedElement, Function<NamedElement, Rectangle> pBoundCalculator)
 	{
 		if (!aIsActivated)
 		{
 			return pBoundCalculator.apply(pNamedElement);
 		}
 		else if (aIsActivated && aNamedElementBounds.containsKey(pNamedElement))
 		{
 			return aNamedElementBounds.get(pNamedElement);
 		}
 		else
 		{
 			Rectangle computedBounds = pBoundCalculator.apply(pNamedElement);
 			aNamedElementBounds.put(pNamedElement, computedBounds);
 			return computedBounds;
 		}
 	}

 	/**
 	 * Activates the NamedElementStorage.
 	 */
 	public void activate() 
 	{
 		aIsActivated = true;
 	}

 	/**
 	 * Deactivates and clears the NamedElementStorage.
 	 */
 	public void deactivateAndClear() 
 	{
 		aIsActivated = false;
 		aNamedElementBounds.clear();
 	}
 }