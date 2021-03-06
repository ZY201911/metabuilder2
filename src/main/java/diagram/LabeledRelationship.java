package diagram;

public abstract class LabeledRelationship extends Relationship {
	private String startLabel = "";
	private String midLabel = "";
	private String endLabel = "";
	
	@Override
	public void buildProperties() {
		super.buildProperties();
		putProperty("startLabel", new Property(""));
		putProperty("midLabel", new Property(""));
		putProperty("endLabel", new Property(""));
	}
	
	public String getStartLabel() {
		setStartLabel(getProperties().get("startLabel").getValue());
		return startLabel;
	}
	public String getEndLabel() {
		setEndLabel(getProperties().get("endLabel").getValue());
		return endLabel;
	}
	public String getMidLabel() {
		setMidLabel(getProperties().get("midLabel").getValue());
		return midLabel;
	}
	
	public void setStartLabel(String pStartLabel) {
		startLabel = pStartLabel;
	}
	public void setEndLabel(String pEndLabel) {
		endLabel = pEndLabel;
	}
	public void setMidLabel(String pMidLabel) {
		midLabel = pMidLabel;
	}
	
	@Override
	public LabeledRelationship clone() {
		LabeledRelationship clone = (LabeledRelationship) super.clone();
		clone.setStartLabel(new String(clone.getStartLabel()));
		clone.setEndLabel(new String(clone.getEndLabel()));
		clone.setMidLabel(new String(clone.getMidLabel()));
		return clone;
	}
}
