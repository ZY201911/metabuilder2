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
		return startLabel;
	}
	public String getEndLabel() {
		return endLabel;
	}
	public String getMidLabel() {
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
}
