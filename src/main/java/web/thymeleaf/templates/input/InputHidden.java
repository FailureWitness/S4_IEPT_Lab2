package web.thymeleaf.templates.input;

public class InputHidden implements Input{
	protected String name;
	protected String value;
	
	public InputHidden(String name, String value) {
		this.name = name;
		this.value = value;
	}
	public InputHidden(String name, Integer value) {
		this(name, value.toString());
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setValue(Integer value) {
		this.value = value.toString();
	}
	public String getType() {
		return InputType.Hidden.toString();
	}
}
