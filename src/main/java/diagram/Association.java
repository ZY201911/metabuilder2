package diagram;

public class Association extends LabeledRelationship {
	private boolean uniDirection = false;
	private boolean biDirection = false;
	
	@Override
	public void buildProperties() {
		super.buildProperties();
		putProperty("direction", new Property("NoDirection"));
	}
	
	public boolean getUniDirection() {
		if(getProperties().get("direction").getValue() == "UniDirection") {
			setUniDirection(true);
		}
		return uniDirection;
	}
	public boolean getBiDirection() {
		if(getProperties().get("direction").getValue() == "BiDirection") {
			setBiDirection(true);
		}
		return biDirection;
	}
	
	public void setUniDirection(boolean pUniDirection) {
		uniDirection = pUniDirection;
	}
	public void setBiDirection(boolean pBiDirection) {
		biDirection = pBiDirection;
	}
}
