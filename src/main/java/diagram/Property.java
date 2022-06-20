package diagram;

public class Property {
	private String value = "";
	public Property(String value) {
		setValue(value);
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
