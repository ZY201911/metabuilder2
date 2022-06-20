package diagram;

public class Association extends LabeledRelationship {
	private boolean uniDirection = false;
	private boolean biDirection = false;
	
	public boolean getUniDirection() {
		return uniDirection;
	}
	public boolean getBiDirection() {
		return biDirection;
	}
	
	public void setUniDirection(boolean pUniDirection) {
		uniDirection = pUniDirection;
	}
	public void setBiDirection(boolean pBiDirection) {
		biDirection = pBiDirection;
	}
}
